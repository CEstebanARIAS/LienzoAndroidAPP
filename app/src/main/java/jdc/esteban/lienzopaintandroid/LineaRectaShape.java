package jdc.esteban.lienzopaintandroid;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LineaRectaShape extends LineShape{
    public LineaRectaShape(float startX, float startY, float endX, float endY, Paint paint) {
        super(startX, startY, endX, endY, paint);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawLine(startX, startY, endX, endY, paint);
    }
}
