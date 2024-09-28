package ru.tensor.sbis.scanner.data.model;

/**
 * @author am.boldinov
 */
public class CornerPoint {
    public float x;
    public float y;

    public CornerPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CornerPoint that = (CornerPoint) o;

        return Float.compare(that.x, x) == 0 && Float.compare(that.y, y) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}
