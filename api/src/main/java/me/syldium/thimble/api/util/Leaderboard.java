package me.syldium.thimble.api.util;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.UUID;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

/**
 * A leaderboard implementation relying on a {@link ArrayList}.
 */
public class Leaderboard implements SortedSet<ThimblePlayerStats> {

    /**
     * The maximum length of the leaderboard.
     *
     * <p>Once this size is reached, new items can only be added if they have a value greater than the last item in the list.</p>
     */
    public static final int MAX_LENGTH = 10;

    private final Comparator<ThimblePlayerStats> comparator;
    private final ToIntFunction<ThimblePlayerStats> getter;
    private final Map<UUID, Integer> positions;
    private final List<ThimblePlayerStats> list;

    /**
     * Constructs a new leaderboard using this criterion.
     *
     * @param ranking The criterion.
     * @return A new leaderboard.
     */
    public static @NotNull Leaderboard of(@NotNull Ranking ranking) {
        return new Leaderboard(ranking.getter());
    }

    private Leaderboard(@NotNull ToIntFunction<ThimblePlayerStats> getter) {
        this(Comparator.comparingInt(getter).reversed(), getter);
    }

    private Leaderboard(@NotNull Comparator<ThimblePlayerStats> comparator, @NotNull ToIntFunction<ThimblePlayerStats> getter) {
        this.comparator = comparator;
        this.getter = getter;
        this.positions = new HashMap<>(Leaderboard.MAX_LENGTH);
        this.list = new ArrayList<>(Leaderboard.MAX_LENGTH);
    }

    /**
     * Copy an existing leaderboard.
     *
     * @param leaderboard The leaderboard to copy.
     */
    public Leaderboard(@NotNull Leaderboard leaderboard) {
        this.comparator = leaderboard.comparator;
        this.getter = leaderboard.getter;
        this.positions = new HashMap<>(leaderboard.positions);
        this.list = new ArrayList<>(leaderboard.list);
    }

    /**
     * Returns the element at the specified position. {@link List#get(int)}
     *
     * @param index Index of the element to return.
     * @return The element at the specified position in this leaderboard.
     * @throws IndexOutOfBoundsException If the index is out of range (index &lt; 0 || index &gt;= size())
     */
    public ThimblePlayerStats get(int index) throws IndexOutOfBoundsException {
        return this.list.get(index);
    }

    /**
     * Adds a new element, if eligible.
     *
     * @param e The element.
     * @return {@code true} if the item has been added.
     */
    @Override
    public boolean add(@NotNull ThimblePlayerStats e) {
        requireNonNull(e, "player stats");
        int existing = this.indexOf(e.uuid());
        int rank = this.insertionIndex(this.getter.applyAsInt(e));
        if (existing == rank) {
            // The ranking does not change, only the previous score must be updated.
            this.list.set(rank, e);
            return true;
        }

        if (existing != -1) {
            // Position change in the ranking.
            this.list.remove(existing);
            this.positions.remove(e.uuid());
        }

        if (rank > MAX_LENGTH) {
            // New position too far.
            this.updatePositions(existing);
            return false;
        }

        // (Re)addition
        this.list.add(rank, e);
        if (existing == -1) {
            this.updatePositions(rank);
        } else if (rank < existing) {
            this.updatePositions(rank, existing + 1);
        } else {
            this.updatePositions(existing, rank + 1);
        }

        if (this.size() > MAX_LENGTH) {
            // Limit the length of the leaderboard.
            this.positions.remove(this.list.remove(MAX_LENGTH).uuid());
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
     * @return The position to insert.
     */
    private int insertionIndex(int score) {
        int low = 0;
        int high = this.list.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = Integer.compare(this.getter.applyAsInt(this.list.get(mid)), score);
            if (cmp >= 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return low;
    }

    /**
     * Returns the position of the given player identifier in this leaderboard.
     *
     * @param playerUniqueId The player unique id.
     * @return The index or {@code -1} if absent.
     */
    public int indexOf(@NotNull UUID playerUniqueId) {
        Integer index = this.positions.get(playerUniqueId);
        return index == null ? -1 : index;
    }

    @Override
    public @NotNull Comparator<? super ThimblePlayerStats> comparator() {
        return this.comparator;
    }

    @Override @Deprecated
    public @NotNull Leaderboard subSet(ThimblePlayerStats e, ThimblePlayerStats e1) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Leaderboard headSet(ThimblePlayerStats e) {
        Leaderboard set = new Leaderboard(this.comparator, this.getter);
        int index = this.list.indexOf(e);
        for (int i = 0; i <= index; i++) {
            set.add(this.list.get(i));
        }
        return set;
    }

    @Override
    public @NotNull Leaderboard tailSet(ThimblePlayerStats e) throws UnsupportedOperationException {
        Leaderboard set = new Leaderboard(this.comparator, this.getter);
        int index = Math.max(0, this.list.indexOf(e));
        for (int i = index; i < this.size(); i++) {
            set.add(this.list.get(i));
        }
        return set;
    }

    @Override
    public @Nullable ThimblePlayerStats first() {
        return this.isEmpty() ? null : this.list.get(0);
    }

    @Override
    public @Nullable ThimblePlayerStats last() {
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
    public @NotNull Iterator<ThimblePlayerStats> iterator() {
        return new LeaderboardIterator(this);
    }

    @Override
    public @NotNull Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    public @NotNull <T> T[] toArray(@NotNull T[] ts) {
        return this.list.toArray(ts);
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = this.list.remove(o);
        if (removed) {
            UUID uuid = ((ThimblePlayerStats) o).uuid();
            this.updatePositions(this.indexOf(uuid));
            this.positions.remove(uuid);
        }
        return removed;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return this.list.containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends ThimblePlayerStats> collection) {
        boolean modified = false;
        for (ThimblePlayerStats element : collection) {
            if (this.add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        boolean changed = this.list.removeAll(collection);
        if (changed) {
            this.updatePositions();
        }
        return changed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        boolean changed = this.list.retainAll(collection);
        if (changed) {
            this.updatePositions();
        }
        return changed;
    }

    private void updatePositions() {
        this.positions.clear();
        for (int i = 0; i < this.list.size(); i++) {
            this.positions.put(this.list.get(i).uuid(), i);
        }
    }

    private void updatePositions(int from, int to) {
        for (int i = from; i < to; i++) {
            this.positions.put(this.list.get(i).uuid(), i);
        }
    }

    private void updatePositions(int from) {
        this.updatePositions(from, this.list.size());
    }

    @Override
    public void clear() {
        this.list.clear();
        this.positions.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leaderboard that = (Leaderboard) o;
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

    private static final class LeaderboardIterator implements Iterator<ThimblePlayerStats> {

        private final Leaderboard leaderboard;
        private final Iterator<ThimblePlayerStats> iterator;
        private ThimblePlayerStats next;
        private int index = -1;

        private LeaderboardIterator(@NotNull Leaderboard leaderboard) {
            this.leaderboard = leaderboard;
            this.iterator = leaderboard.list.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public ThimblePlayerStats next() {
            this.index++;
            return this.next = this.iterator.next();
        }

        @Override
        public void remove() {
            this.iterator.remove();
            this.leaderboard.positions.remove(this.next.uuid());
            this.leaderboard.updatePositions(this.index--);
        }
    }
}
