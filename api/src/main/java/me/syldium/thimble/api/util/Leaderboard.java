package me.syldium.thimble.api.util;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
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
    private List<ThimblePlayerStats> list = new ArrayList<>(Leaderboard.MAX_LENGTH);

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
    }

    /**
     * Copy an existing leaderboard.
     *
     * @param leaderboard The leaderboard to copy.
     */
    public Leaderboard(@NotNull Leaderboard leaderboard) {
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
        int rank = -1;
        for (int i = 0; i < this.list.size(); i++) {
            if (this.list.get(i).equalsPlayer(e)) {
                rank = i;
                break;
            }
        }

        if (rank < 0 && this.list.size() < Leaderboard.MAX_LENGTH) {
            this.list.add(e);
            return true;
        }

        ThimblePlayerStats latest = this.last();
        if (this.comparator.compare(latest, e) > 0) {
            if (rank >= 0) {
                this.list.remove(rank);
            }
            this.list.add(e);
            this.list = this.list.stream()
                    .sorted(this.comparator)
                    .limit(Leaderboard.MAX_LENGTH)
                    .collect(Collectors.toList());
            return true;
        }
        return false;
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
     * Tests if a score is contained.
     *
     * @param score The value to test.
     * @return If so.
     */
    public boolean containsScore(int score) {
        for (ThimblePlayerStats element : this) {
            if (this.getter.applyAsInt(element) == score) {
                return true;
            }
        }
        return false;
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
        return this.list.iterator();
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
        return this.list.remove(o);
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
}
