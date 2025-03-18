package pawz.demo.CLI;

import java.util.Objects;

public final class ImmutablePair<First, Second> {
    private final First first;
    private final Second second;

    public ImmutablePair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public First first() {
        return first;
    }

    public Second second() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ImmutablePair) obj;
        return Objects.equals(this.first, that.first) &&
                Objects.equals(this.second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "ImmutablePair[" +
                "first=" + first + ", " +
                "second=" + second + ']';
    }

}
