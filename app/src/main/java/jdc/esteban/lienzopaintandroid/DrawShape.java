package jdc.esteban.lienzopaintandroid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public interface DrawShape {
    void draw(Canvas canva);

    boolean contains(float x, float y);

    void moveBy(float dx, float dy);

    void drawHighlight(Canvas canvas, Paint paint);
}
