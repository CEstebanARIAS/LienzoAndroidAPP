package jdc.esteban.lienzopaintandroid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class CircleShape implements DrawShape{

    private float cx, cy, radius;
    private Paint paint;
    private static final float HANDLE_RADIUS = 20f;

    public CircleShape(float cx, float cy, float radius, Paint paint) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.paint = paint;
    }

    @Override
    public void draw(Canvas canvas){
        canvas.drawCircle(cx, cy, radius, paint);
    }

    @Override
    public boolean contains(float x, float y){
        float dx = x - cx;
        float dy = y - cy;
        return (dx * dx + dy * dy) <= (radius * radius);
    }

    @Override
    public void moveBy(float dx, float dy) {
        cx += dx;
        cy += dy;
    }

    public boolean isOnResizeHandle(float x, float y) {
        return Math.hypot(x - (cx + radius), y - cy) <= HANDLE_RADIUS;
    }

    public void resize(float x, float y) {
        radius = Math.max(20, (float) Math.hypot(x - cx, y - cy));
    }


    @Override
    public void drawHighlight(Canvas canvas, Paint paint) {
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx + radius, cy, HANDLE_RADIUS, paint);
        paint.setStyle(Paint.Style.STROKE);
    }
}
