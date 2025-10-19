package edu.luc.etl.cs313.android.shapes.model;

/**
 * A point, implemented as a location without a shape.
 */
public class Point extends Location {

    // TODO verify completion

    private final int x, y;

    public Point(final int x, final int y) {
        super(-1, -1, new Circle(0));
        assert x >= 0;
        assert y >= 0;
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
