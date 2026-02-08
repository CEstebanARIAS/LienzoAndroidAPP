package jdc.esteban.lienzopaintandroid;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CircleShape implements DrawShape{

    private float cx, cy, radius;
    private Paint paint;

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
        return (dx * dy + dy * dy) <= (radius * radius);
    }

    @Override
    public void moveBy(float dx, float dy) {
        cx += dx;
        cy += dy;
    }

    @Override
    public void drawHighlight(Canvas canvas, Paint paint) {
        canvas.drawCircle(cx, cy, radius + 5, paint);
    }

}
