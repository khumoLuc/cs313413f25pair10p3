package edu.luc.etl.cs313.android.shapes.android;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import edu.luc.etl.cs313.android.shapes.model.*;

/**
 * A Visitor for drawing a shape to an Android canvas.
 */
public class Draw implements Visitor<Void> {


    private final Canvas canvas;

    private final Paint paint;

    public Draw(final Canvas canvas, final Paint paint) {
        this.canvas = canvas;
        this.paint = paint;
        paint.setStyle(Style.STROKE);
    }

    @Override
    public Void onCircle(final Circle c) {
        canvas.drawCircle(0, 0, c.getRadius(), paint);
        return null;
    }

    @Override
    public Void onStrokeColor(final StrokeColor c) {
        // change the stroke color to the new color then reset it to the original one
        final int prevColor = paint.getColor();
        paint.setColor(c.getColor());
        c.getShape().accept(this);
        paint.setColor(prevColor);
        return null;
    }

    @Override
    public Void onFill(final Fill f) {;
        // change the style to Fill, then reset it to the original one
        final Style prevStyle = paint.getStyle();
        paint.setStyle(Style.FILL_AND_STROKE);
        f.getShape().accept(this);
        paint.setStyle(prevStyle);
        return null;
    }

    @Override
    public Void onGroup(final Group g) {
        // loops thru all Shapes in the group and tells each shape to accept the visitor
        for (Shape s : g.getShapes()) {
            s.accept(this);
        }
        return null;
    }

    @Override
    public Void onLocation(final Location l) {
        // move it to designated position of l.x and l.y and then back to where it was initially
        canvas.translate(l.getX(), l.getY());
        l.getShape().accept(this);
        canvas.translate(-l.getX(), -l.getY());
        return null;
    }

    @Override
    public Void onRectangle(final Rectangle r) {
        // draws a rectangle following onCircle's example
        canvas.drawRect(0,0, r.getWidth(), r.getHeight(), paint) ;
        return null;
    }

    @Override
    public Void onOutline(Outline o) {
        // change the style to Stroke, then reset it to the original one
        final Style prevStyle = paint.getStyle();
        paint.setStyle(Style.STROKE);
        o.getShape().accept(this);
        paint.setStyle(prevStyle);
        return null;
    }

    @Override
    public Void onPolygon(final Polygon s) {
        //  draws a polygon
        final java.util.List<? extends Point> points = s.getPoints();
        int size = points.size();
        // as discussed in class, we need 4 vals for each line -> x1,y1,x2,y2
        final float[] pts = new float[size*4];
        int i = 0;
        for (int j = 0; j < size; j++) {
            // for each line, we need x,y of both starting and ending points
            Point str = points.get(j);
            Point end;
            if (j == size - 1) {
                // adding the last line to connect it to the first point
                end = points.get(0);
            } else {
                end = points.get(j + 1);
            }

            pts[i++] = str.getX();
            pts[i++] = str.getY();
            pts[i++] = end.getX();
            pts[i++] = end.getY();
        }
        // draws all lines in pts
        canvas.drawLines(pts, paint);
        return null;
    }
}
