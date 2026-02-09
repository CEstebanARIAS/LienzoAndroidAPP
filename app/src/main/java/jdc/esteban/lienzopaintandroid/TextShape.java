package jdc.esteban.lienzopaintandroid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

public class TextShape implements DrawShape {

    private String text;
    private float x, y;
    private Paint paint;
    private RectF bounds = new RectF();

    public TextShape(String text, float x, float y, Paint paint) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.paint = paint;
        updateBounds();
    }

    public String getText() {
        return text;
    }

    public void setText(String newText) {
        this.text = newText;
        updateBounds();
    }

    private void updateBounds() {
        bounds.setEmpty();
        paint.getTextBounds(text, 0, text.length(), new android.graphics.Rect());
        float textWidth = paint.measureText(text);
        Paint.FontMetrics fm = paint.getFontMetrics();

        bounds.left = x;
        bounds.right = x + textWidth;
        bounds.top = y + fm.ascent;
        bounds.bottom = y + fm.descent;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, x, y, paint);
    }

    @Override
    public boolean contains(float px, float py) {
        return bounds.contains(px, py);
    }

    @Override
    public void moveBy(float dx, float dy) {
        x += dx;
        y += dy;
        bounds.offset(dx, dy);
    }

    @Override
    public void drawHighlight(Canvas canvas, Paint highlightPaint) {
        canvas.drawRect(bounds, highlightPaint);
    }

}
