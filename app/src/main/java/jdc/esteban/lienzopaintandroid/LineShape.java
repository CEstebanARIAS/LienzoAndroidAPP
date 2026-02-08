package jdc.esteban.lienzopaintandroid;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class LineShape implements DrawShape {

    protected float startX, startY, endX, endY;
    protected Paint paint;

    public LineShape(float startX, float startY, float endX, float endY, Paint paint) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.paint = paint;
    }

    @Override
    public void moveBy(float dx, float dy) {
        startX += dx;
        startY += dy;
        endX += dx;
        endY += dy;
    }

    @Override
    public boolean contains(float x, float y) {
        float tolerance = 20f;

        float dx = endX - startX;
        float dy = endY - startY;

        float lengthSquared = dx * dx + dy * dy;
        if (lengthSquared == 0) return false;

        float t = ((x - startX) * dx + (y - startY) * dy) / lengthSquared;
        t = Math.max(0, Math.min(1, t));

        float px = startX + t * dx;
        float py = startY + t * dy;

        float distance = (float) Math.hypot(x - px, y - py);
        return distance <= tolerance;
    }

    @Override
    public void drawHighlight(Canvas canvas, Paint highlightPaint) {
        canvas.drawLine(startX, startY, endX, endY, highlightPaint);
    }
}
