package jdc.esteban.lienzopaintandroid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class FreePathShape implements DrawShape {

    private Path path;
    private Paint paint;
    private RectF bounds;

    public FreePathShape(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
        bounds = new RectF();
        path.computeBounds(bounds, true);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    @Override
    public void moveBy(float dx, float dy) {
        path.offset(dx, dy);
        bounds.offset(dx, dy);
    }

    @Override
    public void drawHighlight(Canvas canvas, Paint paint) {
        canvas.drawRect(bounds, paint);
    }

}
