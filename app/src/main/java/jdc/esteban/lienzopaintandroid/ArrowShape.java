package jdc.esteban.lienzopaintandroid;

import android.graphics.Canvas;
import android.graphics.Paint;

public class ArrowShape extends LineShape {

    private static final float ARROW_HEAD_ANGLE = 30f;
    private static final float ARROW_HEAD_LENGTH = 40f;

    public ArrowShape(float startX, float startY, float endX, float endY, Paint paint) {
        super(startX, startY, endX, endY, paint);
    }

    @Override
    public void draw(Canvas canvas) {
        // línea principal
        canvas.drawLine(startX, startY, endX, endY, paint);
        drawArrowHead(canvas);
    }

    private void drawArrowHead(Canvas canvas) {
        float angle = (float) Math.atan2(endY - startY, endX - startX);

        float angle1 = angle + (float) Math.toRadians(ARROW_HEAD_ANGLE);
        float angle2 = angle - (float) Math.toRadians(ARROW_HEAD_ANGLE);

        float x1 = endX - ARROW_HEAD_LENGTH * (float) Math.cos(angle1);
        float y1 = endY - ARROW_HEAD_LENGTH * (float) Math.sin(angle1);

        float x2 = endX - ARROW_HEAD_LENGTH * (float) Math.cos(angle2);
        float y2 = endY - ARROW_HEAD_LENGTH * (float) Math.sin(angle2);

        canvas.drawLine(endX, endY, x1, y1, paint);
        canvas.drawLine(endX, endY, x2, y2, paint);
    }
}
