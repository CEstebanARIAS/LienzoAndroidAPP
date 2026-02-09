package jdc.esteban.lienzopaintandroid;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public abstract class LineShape implements DrawShape {

    protected float startX, startY, endX, endY;
    protected Paint paint;
    private static final float HANDLE_RADIUS = 20f;
    private enum Handle {START, END, NONE}
    private Handle activeHandle = Handle.NONE;

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

    public boolean isOnStartHandle(float x, float y) {
        return Math.hypot(x - startX, y - startY) <= HANDLE_RADIUS;
    }

    public boolean isOnEndHandle(float x, float y) {
        return Math.hypot(x - endX, y - endY) <= HANDLE_RADIUS;
    }
    public void resizeStart(float x, float y) {
        startX = x;
        startY = y;
    }

    public void resizeEnd(float x, float y) {
        endX = x;
        endY = y;
    }

    public float getAngleDegrees() {
        float angle = (float) Math.toDegrees(
                Math.atan2(startY - endY, endX - startX)
        );

        if (angle < 0) angle += 360;

        return angle;
    }

    protected float getCenterX() {
        return (startX + endX) / 2f;
    }

    protected float getCenterY() {
        return (startY + endY) / 2f;
    }

    public void drawAngle(Canvas canvas, Paint textPaint) {
        float angle = getAngleDegrees();
        @SuppressLint("DefaultLocale") String text = String.format("%.1f°", angle);

        float cx = getCenterX();
        float cy = getCenterY();

        canvas.drawText(text, cx + 10, cy - 10, textPaint);
    }



    @Override
    public void drawHighlight(Canvas canvas, Paint highlightPaint) {
        // Línea resaltada
        canvas.drawLine(startX, startY, endX, endY, highlightPaint);

        // Handles
        highlightPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(startX, startY, HANDLE_RADIUS, highlightPaint);
        canvas.drawCircle(endX, endY, HANDLE_RADIUS, highlightPaint);
        highlightPaint.setStyle(Paint.Style.STROKE);
    }
}
