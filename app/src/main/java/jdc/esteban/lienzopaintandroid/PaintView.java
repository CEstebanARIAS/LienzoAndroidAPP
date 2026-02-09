package jdc.esteban.lienzopaintandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Magnifier;

import java.util.ArrayList;
import java.util.List;

public class PaintView extends View {

    public enum Tool{
        PEN, ERASER, CIRCLE, RECTANGLE, TEXT, MOVE, LINE
    }
    public enum TipoLinea{
        FLECHA, LINEARECTA
    }
    private enum ResizeMode { NONE, CIRCLE, LINE_START, LINE_END }
    private ResizeMode resizeMode = ResizeMode.NONE;
    private Paint anglePaint;
    private float magnifierWindowX;
    private float magnifierWindowY;


    private Tool currentTool = Tool.PEN;

    private Paint paint;
    private Path currentPath;
    private Float startX, startY;
    private float previewX, previewY;
    private float previewRadius = 0;
    private Paint gridPaint;
    private int gridSize = 50; // tamaño de cada cuadro (px)
    private DrawShape selectedShape;
    private float lastX, lastY;
    private TipoLinea currentTipoLinea = TipoLinea.LINEARECTA;
    private boolean isPreviewingLine = false;
    private Magnifier magnifier;
    private boolean resizing = false;



    private List<DrawShape> shapes = new ArrayList<>();

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaintView(Context context){
        super(context);
        init();
    }

    private void init(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            magnifier = new Magnifier(this);
        }

        /// Para definir las lineas guia del lienzo
        gridPaint = new Paint();
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(1f);
        gridPaint.setStyle(Paint.Style.STROKE);

        /// Define el angulo en las lineas (lineaRecta/ flecha)
        anglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        anglePaint.setColor(Color.DKGRAY);
        anglePaint.setTextSize(32f);
        anglePaint.setStyle(Paint.Style.FILL);


        /// Define el color tamaño y demas propiedades de los objetos creados
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(colorModoOscuroClaroTelefono());
        paint.setStrokeWidth(6f);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        drawGrid(canvas);

        for (DrawShape s: shapes) {
            s.draw(canvas);
        }

        // Dibuja los angulos en las lineas y lo que herede de las lineas
        for (DrawShape s : shapes) {
            s.draw(canvas);

            if (s == selectedShape && s instanceof LineShape) {
                ((LineShape) s).drawAngle(canvas, anglePaint);
            }
        }

        if (currentPath != null){
            canvas.drawPath(currentPath, paint);
        }

        if (currentTool == Tool.CIRCLE && previewRadius > 0) {
            canvas.drawCircle(startX, startY, previewRadius, paint);
        }

        if (selectedShape != null) {
            Paint highlight = new Paint();
            highlight.setStyle(Paint.Style.STROKE);
            highlight.setStrokeWidth(3);
            highlight.setColor(Color.BLUE);

            selectedShape.drawHighlight(canvas, highlight);
        }

        if (isPreviewingLine) {

            if (currentTipoLinea == TipoLinea.LINEARECTA) {
                canvas.drawLine(startX, startY, previewX, previewY, paint);
            } else {
                drawArrowPreview(canvas);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (currentTool == Tool.TEXT && event.getAction() == MotionEvent.ACTION_DOWN) {
            DrawShape shape = findShapeAt(x, y);

            if (shape instanceof TextShape) {
                showEditTextDialog((TextShape) shape);
                return true;
            }
        }

        switch (currentTool) {

            case PEN:
                handlePen(event, x, y);
                break;

            case ERASER:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    eraseAt(x, y);
                }
                break;
            case CIRCLE:
                handleCircle(event, x, y);
                break;
            case LINE:
                handleLine(event, x, y);
                break;
            case TEXT:
                handleText(event, x, y);
                break;
            case MOVE:
               return handleMove(event, x, y);

        }

        invalidate();
        return true;
    }

    private void handlePen(MotionEvent event, float x, float y) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                break;

            case MotionEvent.ACTION_UP:
                shapes.add(new FreePathShape(
                        new Path(currentPath),
                        new Paint(paint)
                ));
                currentPath = null;
                break;
        }
    }

    private void handleCircle(MotionEvent event, float x, float y) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                previewRadius = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                previewRadius = (float) Math.hypot(
                        x - startX,
                        y - startY
                );
                break;

            case MotionEvent.ACTION_UP:
                float radius = (float) Math.hypot(
                        x - startX,
                        y - startY
                );

                shapes.add(new CircleShape(
                        startX,
                        startY,
                        radius,
                        new Paint(paint)
                ));

                previewRadius = 0;
                break;
        }
    }

    private void handleText(MotionEvent event, float x, float y) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            Paint textPaint = new Paint(paint);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTextSize(48f);

            TextShape textShape = new TextShape(
                    "Texto",
                    x,
                    y,
                    textPaint
            );

            shapes.add(textShape);
            showEditTextDialog(textShape);
        }
    }

    private boolean handleMove(MotionEvent event, float x, float y) {
        if (currentTool == Tool.MOVE) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    selectedShape = findShapeAt(x, y);
                    lastX = x;
                    lastY = y;
                    resizeMode = ResizeMode.NONE;
                    if (selectedShape instanceof CircleShape) {
                        if (((CircleShape) selectedShape).isOnResizeHandle(x, y)) {
                            resizeMode = ResizeMode.CIRCLE;
                        }
                    }

                    if (selectedShape instanceof LineShape) {
                        LineShape line = (LineShape) selectedShape;

                        if (line.isOnStartHandle(x, y)) {
                            resizeMode = ResizeMode.LINE_START;
                        } else if (line.isOnEndHandle(x, y)) {
                            resizeMode = ResizeMode.LINE_END;
                        }
                    }


                    invalidate();
                    return selectedShape != null;

                case MotionEvent.ACTION_MOVE:
                    if (selectedShape == null) return false;

                    if (resizeMode == ResizeMode.CIRCLE && selectedShape instanceof CircleShape) {
                        ((CircleShape) selectedShape).resize(x, y);

                    } else if (resizeMode == ResizeMode.LINE_START && selectedShape instanceof LineShape) {
                        showMagnifier(x, y);
                        ((LineShape) selectedShape).resizeStart(x, y);

                    } else if (resizeMode == ResizeMode.LINE_END && selectedShape instanceof LineShape) {
                        showMagnifier(x, y);
                        ((LineShape) selectedShape).resizeEnd(x, y);

                    } else {
                        float dx = x - lastX;
                        float dy = y - lastY;
                        selectedShape.moveBy(dx, dy);
                        lastX = x;
                        lastY = y;
                    }


                    invalidate();
                    return true;

                case MotionEvent.ACTION_UP:
                    resizeMode = ResizeMode.NONE;
                    hideMagnifier();
                    selectedShape = null;
                    return true;
            }
        }
        return false;
    }

    private void handleLine(MotionEvent event, float x, float y) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                previewX = x;
                previewY = y;
                isPreviewingLine = true;
                showMagnifier(x,y);
                break;

            case MotionEvent.ACTION_MOVE:
                previewX = x;
                previewY = y;
                showMagnifier(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:

                hideMagnifier();
                if (currentTipoLinea == TipoLinea.LINEARECTA){
                    shapes.add(new LineaRectaShape(
                            startX, startY, x, y, new Paint(paint)
                    ));
                }else{
                    shapes.add(new ArrowShape(
                            startX, startY, x, y, new Paint(paint)
                    ));
                }
                isPreviewingLine = false;
                break;
        }
    }

    private DrawShape findShapeAt(float x, float y) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(x, y)) {
                return shapes.get(i);
            }
        }
        return null;
    }

    private void eraseAt(float x, float y) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(x, y)) {
                shapes.remove(i);
                break;
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        for (int x = 0; x <= width; x += gridSize) {
            gridPaint.setStrokeWidth(x % (gridSize * 5) == 0 ? 2f : 1f);
            canvas.drawLine(x, 0, x, height, gridPaint);
        }

        for (int y = 0; y <= height; y += gridSize) {
            gridPaint.setStrokeWidth(y % (gridSize * 5) == 0 ? 2f : 1f);
            canvas.drawLine(0, y, width, y, gridPaint);
        }
    }


    public void setTool(Tool tool) {
        currentTool = tool;
    }

    public Tool getTool(){
        return currentTool;
    }

    public interface OnTextEditListener {
        void onEditText(TextShape textShape);
    }

    private OnTextEditListener textEditListener;

    public void setOnTextEditListener(OnTextEditListener listener) {
        this.textEditListener = listener;
    }

    private void showEditTextDialog(TextShape textShape) {
        if (textEditListener != null) {
            textEditListener.onEditText(textShape);
        }
    }

    public void setTipoLinea(TipoLinea linea){
        currentTipoLinea = linea;
    }

    private void drawArrowPreview(Canvas canvas) {

        canvas.drawLine(startX, startY, previewX, previewY, paint);

        float angle = (float) Math.atan2(previewY - startY, previewX - startX);

        float angle1 = angle + (float) Math.toRadians(30);
        float angle2 = angle - (float) Math.toRadians(30);

        float length = 40f;

        float x1 = previewX - length * (float) Math.cos(angle1);
        float y1 = previewY - length * (float) Math.sin(angle1);

        float x2 = previewX - length * (float) Math.cos(angle2);
        float y2 = previewY - length * (float) Math.sin(angle2);

        canvas.drawLine(previewX, previewY, x1, y1, paint);
        canvas.drawLine(previewX, previewY, x2, y2, paint);
    }

    private boolean modoOscuro(){
        int modoOscuro = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return modoOscuro == Configuration.UI_MODE_NIGHT_YES;
    }

    private int colorModoOscuroClaroTelefono(){
        if (modoOscuro()) {
            return Color.parseColor("#F5C542");
        } else {
            return Color.parseColor("#1E3A5F");
        }
    }

    private void showMagnifier(float x, float y) {

        if (magnifier != null && android.os.Build.VERSION.SDK_INT >= 28) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                magnifier.show(
                        x,
                        y,
                        magnifierWindowX,
                        magnifierWindowY  // aparece estatico en la pantalla
                );
            }
        }
    }

    private void hideMagnifier() {

        if (magnifier != null && android.os.Build.VERSION.SDK_INT >= 28) {
            magnifier.dismiss();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Margen desde el borde
        float margin = 40f;

        magnifierWindowX = w - margin;
        magnifierWindowY = margin;
    }


}
