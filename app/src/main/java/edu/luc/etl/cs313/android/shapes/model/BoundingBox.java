package edu.luc.etl.cs313.android.shapes.model;

/**
 * A shape visitor for calculating the bounding box, that is, the smallest
 * rectangle containing the shape. The resulting bounding box is returned as a
 * rectangle at a specific location.
 */
public class BoundingBox implements Visitor<Location> {

    // TODO verify completion with testing

    @Override
    public Location onCircle(final Circle c) {
        final int radius = c.getRadius();
        return new Location(-radius, -radius, new Rectangle(2 * radius, 2 * radius));
    }

    @Override
    public Location onFill(final Fill f) {
        return f.getShape().accept(this);
    }

    @Override
    public Location onGroup(final Group g) {
        int max_x = 0;
        int max_y = 0;
        int min_x = Integer.MAX_VALUE;
        int min_y = Integer.MAX_VALUE;

        // scan all shapes for top-left and bottom-right corners of this group
        for(Shape shape : g.getShapes()) {
            Location box = shape.accept(this);
            Rectangle boundary = (Rectangle) box.getShape();

            int x = box.getX();
            int y = box.getY();
            // not pretty, but it works just fine
            // reuse of getW/getH is like this for readability
            if (x + boundary.getWidth() > max_x) max_x = x + boundary.getWidth();
            if (y + boundary.getHeight() > max_y) max_y = y + boundary.getHeight();
            if (x < min_x) min_x = x;
            if (y < min_y) min_y = y;
        }

        int w = max_x - min_x;
        int h = max_y - min_y;
        return new Location(min_x, min_y, new Rectangle(w, h));
    }

    @Override
    public Location onLocation(final Location l) {
        // the previous implementation resulted in error "Location cannot be cast to class Rectangle"
        // it returned a Location object like Location(Location(Rectangle)), not the Rectangle as was expecting
        // -> Location(Rectangle).

        // get the child bounding box Location(Rectangle)
        final Location box = l.getShape().accept(this);
        // get the rectangle from the child's box by unpacking not just re-wrapping like before
        final Rectangle rect = (Rectangle) box.getShape();

        final int newX = l.getX() + box.getX();
        final int newY = l.getY() + box.getY();

        return new Location(newX, newY, rect);
    }

    @Override
    public Location onRectangle(final Rectangle r) {
        // NOT -w, -h per onCircle; instead, 0, 0 per expectations in testBoundingBox
        return new Location(0, 0, r);
    }

    @Override
    public Location onStrokeColor(final StrokeColor c) {
        return c.getShape().accept(this);
    }

    @Override
    public Location onOutline(final Outline o) {
        return o.getShape().accept(this);
    }

    @Override
    public Location onPolygon(final Polygon s) {
        // get the points of the polygon
        final java.util.List<? extends Point> points = s.getPoints();

        int max_x = 0;
        int max_y = 0;
        int min_x = Integer.MAX_VALUE;
        int min_y = Integer.MAX_VALUE;

        // loop thru all points to find the real min and max
        for (final Point p : points) {
            if (p.getX() > max_x) max_x = p.getX();
            if (p.getY() > max_y) max_y = p.getY();
            if (p.getX() < min_x) min_x = p.getX();
            if (p.getY() < min_y) min_y = p.getY();
        }

        // create the bounding box
        int w = max_x - min_x;
        int h = max_y - min_y;
        return new Location(min_x, min_y, new Rectangle(w, h));
    }
}
