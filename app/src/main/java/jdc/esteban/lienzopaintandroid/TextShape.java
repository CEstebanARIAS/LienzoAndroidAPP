package jdc.esteban.lienzopaintandroid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class TextShape implements DrawShape{

    private String text;
    private float x, y;
    private Paint paint;
    private Rect bounds = new Rect();

    public TextShape(String text, float x, float y, Paint paint) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.paint = paint;
        paint.getTextBounds(text, 0, text.length(), bounds);
    }

    public String getText() {
        return text;
    }

    public void setText(String newText) {
        this.text = newText;
        updateBounds();
    }

    private void updateBounds() {
        paint.getTextBounds(text, 0, text.length(), bounds);
        bounds.offset((int) x, (int) y);
    }

    @Override
    public void draw (Canvas canvas){
        canvas.drawText(text, x, y, paint);
    }

    @Override
    public boolean contains(float px, float py){
        return px >= x &&
                px <= x + bounds.width() &&
                py >= y - bounds.height() &&
                py <= y;
    }

    public void moveBy(float dx, float dy){
        x += dx;
        y += dy;
    }

    @Override
    public void drawHighlight(Canvas canvas, Paint paint) {
    }

}
