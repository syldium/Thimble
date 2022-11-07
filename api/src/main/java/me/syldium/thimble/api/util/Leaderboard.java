package me.syldium.thimble.api.util;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

/**
 * A leaderboard implementation relying on an {@link ArrayList}.
 *
 * <p>It stores up to {@link #MAX_LENGTH} players with different {@link UUID}s
 * sorted by descending score. This means that only the latest values for the
 * same identifier are retained and that scores lower than the last player in
 * the leaderboard are not used if the maximum capacity has been reached.
 */
public class Leaderboard<T extends ThimblePlayerStats> implements SortedSet<T> {

    /**
     * The maximum length of the leaderboard.
     *
     * <p>Once this size is reached, new items can only be added if they have a value greater than the last item in the list.</p>
     */
    public static final int MAX_LENGTH = 10;

    private final Comparator<T> comparator;
    private final ToIntFunction<T> getter;
    private final List<T> list;

    /**
     * Constructs a new leaderboard using this criterion.
     *
     * @param ranking The criterion.
     * @return A new leaderboard.
     */
    public static @NotNull Leaderboard<ThimblePlayerStats> of(@NotNull Ranking ranking) {
        return new Leaderboard<>(ranking.getter());
    }

    /**
     * Create a new leaderboard sorted by points and jumps.
     *
     * @return A new leaderboard.
     * @since 1.3.0
     */
    @Experimental
    public static @NotNull Leaderboard<ThimblePlayer> byPoints() {
        return new Leaderboard<>(ByPointsComparator.COMPARATOR, ThimblePlayer::points);
    }

    private Leaderboard(@NotNull ToIntFunction<T> getter) {
        this(Comparator.comparingInt(getter).reversed(), getter);
    }

    private Leaderboard(@NotNull Comparator<T> comparator, @NotNull ToIntFunction<T> getter) {
        this.comparator = comparator;
        this.getter = getter;
        this.list = new ArrayList<>(Leaderboard.MAX_LENGTH);
    }

    /**
     * Copy an existing leaderboard.
     *
     * @param leaderboard The leaderboard to copy.
     */
    public Leaderboard(@NotNull Leaderboard<T> leaderboard) {
        this.comparator = leaderboard.comparator;
        this.getter = leaderboard.getter;
        this.list = new ArrayList<>(leaderboard.list);
    }

    /**
     * Returns the element at the specified position. {@link List#get(int)}
     *
     * @param index Index of the element to return.
     * @return The element at the specified position in this leaderboard.
     * @throws IndexOutOfBoundsException If the index is out of range (index &lt; 0 || index &gt;= size())
     */
    public T get(int index) throws IndexOutOfBoundsException {
        return this.list.get(index);
    }

    /**
     * Adds a new element, if eligible.
     *
     * <p>If the {@link UUID} of the player is already present in the leaderboard,
     * then the corresponding element will be replaced according to the new
     * values. Note that if the previously inserted elements are mutable, it is
     * necessary to call this method with the current instance to update the
     * position in the leaderboard.
     *
     * @param e The element.
     * @return {@code true} if the item has been added.
     */
    @Override
    public boolean add(@NotNull T e) {
        requireNonNull(e, "player stats");
        int existing = this.indexOf(e.uuid());
        int limit = -1;
        if (existing != -1 && (limit = this.compareNeighbors(existing, e)) == existing) {
            this.list.set(existing, e);
            return true;
        }

        int rank;
        if (limit == -1) {
            rank = this.insertionIndex(e, 0, this.size() - 1);
        } else if (limit > existing) {
            rank = this.insertionIndex(e, limit, this.size() - 1) - 1;
        } else {
            rank = this.insertionIndex(e, 0, limit);
        }

        if (existing != -1) {
            // Position change in the ranking.
            this.list.remove(existing);
        }

        if (rank > MAX_LENGTH) {
            // New position too far.
            return false;
        }

        // (Re)addition
        this.list.add(rank, e);
        if (this.size() > MAX_LENGTH) {
            // Limit the length of the leaderboard.
            this.list.remove(MAX_LENGTH);
        }
        return true;
    }

    /**
     * Returns the score list.
     *
     * @return The score list.
     */
    public @NotNull List<@NotNull Integer> scores() {
        return this.list.stream().map(this.getter::applyAsInt).collect(Collectors.toList());
    }

    public @NotNull IntStream intStream() {
        return this.list.stream().mapToInt(this.getter);
    }

    /**
     * Tests if a score is present using a binary search.
     *
     * @param score The value to test.
     * @return {@code true} if present.
     */
    public boolean containsScore(int score) {
        int low = 0;
        int high = this.list.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = Integer.compare(this.getter.applyAsInt(this.list.get(mid)), score);
            if (cmp == 0) {
                return true;
            } else if (cmp > 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return false;
    }

    /**
     * Finds the insertion position of the score using a binary search.
     *
     * <p>If an entry has the same score, the insertion position will always be
     * after the existing entries.</p>
     *
     * @param score The score to insert.
     * @param low The smallest index to consider.
     * @param high The largest index to use.
     * @return The position to insert.
     */
    private int insertionIndex(@NotNull T score, int low, int high) {
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = this.comparator.compare(score, this.list.get(mid));
            if (cmp >= 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return low;
    }

    /**
     * Compares an element with its direct neighbors.
     *
     * <p>Return values:</p>
     * <dl>
     *     <dt>index - 1</dt>
     *     <dd>the element should be located before</dd>
     *     <dt>index</dt>
     *     <dd>the element is correctly located relative to its neighbors</dd>
     *     <dt>index + 1</dt>
     *     <dd>the element should be located after</dd>
     * </dl>
     *
     * @param index The index to compare from.
     * @param e The element to use for this index.
     * @return The index where this element should be located locally.
     */
    private int compareNeighbors(int index, @NotNull T e) {
        if (index > 0 && this.comparator.compare(this.list.get(index - 1), e) > 0) {
            return index - 1;
        }
        if (index < (this.size() - 1) && this.comparator.compare(this.list.get(index + 1), e) < 0) {
            return index + 1;
        }
        return index;
    }

    /**
     * Returns the position of the given player identifier in this leaderboard.
     *
     * @param playerUniqueId The player unique id.
     * @return The index or {@code -1} if absent.
     */
    public int indexOf(@NotNull UUID playerUniqueId) {
        for (int i = 0; i < this.list.size(); i++) {
            if (this.list.get(i).uuid().equals(playerUniqueId)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public @NotNull Comparator<? super T> comparator() {
        return this.comparator;
    }

    @Override @Deprecated
    public @NotNull Leaderboard<T> subSet(T e, T e1) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Leaderboard<T> headSet(T e) {
        Leaderboard<T> set = new Leaderboard<>(this.comparator, this.getter);
        int index = this.list.indexOf(e);
        for (int i = 0; i <= index; i++) {
            set.add(this.list.get(i));
        }
        return set;
    }

    @Override
    public @NotNull Leaderboard<T> tailSet(T e) throws UnsupportedOperationException {
        Leaderboard<T> set = new Leaderboard<>(this.comparator, this.getter);
        int index = Math.max(0, this.list.indexOf(e));
        for (int i = index; i < this.size(); i++) {
            set.add(this.list.get(i));
        }
        return set;
    }

    @Override
    public @Nullable T first() {
        return this.isEmpty() ? null : this.list.get(0);
    }

    @Override
    public @Nullable T last() {
        return this.isEmpty() ? null : this.list.get(this.list.size() - 1);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return this.list.iterator();
    }

    @Override
    public @NotNull Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    public @NotNull <U> U[] toArray(@NotNull U[] ts) {
        return this.list.toArray(ts);
    }

    @Override
    public boolean remove(Object o) {
        return this.list.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return this.list.containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> collection) {
        boolean modified = false;
        for (T element : collection) {
            if (this.add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        return this.list.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        return this.list.retainAll(collection);
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leaderboard<?> that = (Leaderboard<?>) o;
        return this.list.equals(that.list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public String toString() {
        return this.list.toString();
    }

    private static final class ByPointsComparator implements Comparator<ThimblePlayer> {

        private static final ByPointsComparator COMPARATOR = new ByPointsComparator();

        @Override
        public int compare(@NotNull ThimblePlayer a, @NotNull ThimblePlayer b) {
            int cmp = b.points() - a.points();
            if (cmp == 0) {
                cmp = b.jumpsForGame() - a.jumpsForGame();
            }
            return cmp;
        }
    }
}
