package com.ceduliocezar.zegauge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.abs;

public class ZeGaugeView extends View {

    public static final int TEXT_INCREASE_RANGE = 20;
    private Paint paint;
    private Paint paintBackGround;
    private double arrowAngle = -90;
    private Paint arrowPaint;
    private Paint textPaint;
    private int textSizeInSp = 20;
    private Paint smallMarkerPaint;
    private Paint mediumMarkerPaint;
    private Paint biggerMarkerPaint;
    private Paint gradientPaint;

    private int startAngle;
    private int endAngle;

    public ZeGaugeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZeGaugeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        startAngle = -260;
        endAngle = 0;

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(Unit.convertSpToPixels(textSizeInSp, getContext()));

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(Unit.convertSpToPixels(10, getContext()));

        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setColor(Color.parseColor("#EC615D"));
        arrowPaint.setTextSize(Unit.convertSpToPixels(10, getContext()));

        paintBackGround = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackGround.setStyle(Paint.Style.STROKE);
        paintBackGround.setColor(Color.WHITE);
        paintBackGround.setStrokeWidth(Unit.convertDpToPixels(5, getContext()));

        smallMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallMarkerPaint.setColor(Color.GRAY);
        smallMarkerPaint.setStyle(Paint.Style.STROKE);
        smallMarkerPaint.setStrokeWidth(Unit.convertDpToPixels(2, getContext()));

        mediumMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mediumMarkerPaint.setColor(Color.WHITE);
        mediumMarkerPaint.setStyle(Paint.Style.STROKE);
        mediumMarkerPaint.setStrokeWidth(Unit.convertDpToPixels(2, getContext()));

        biggerMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        biggerMarkerPaint.setColor(Color.WHITE);
        biggerMarkerPaint.setStyle(Paint.Style.STROKE);
        biggerMarkerPaint.setStrokeWidth(Unit.convertDpToPixels(4, getContext()));

        gradientPaint = new Paint();
        gradientPaint.setDither(true);

        gradientPaint.setStyle(Paint.Style.STROKE);
        gradientPaint.setStrokeWidth(Unit.convertDpToPixels(20, getContext()));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackgroundBorder(canvas);
        drawGradientBackground(canvas);
        drawMarkers(canvas);
        drawArrow(canvas);
        drawLabels(canvas);
    }

    private void drawGradientBackground(Canvas canvas) {
        canvas.drawBitmap(makeRadialGradient(), 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG));
    }

    private void drawMarkers(Canvas canvas) {
        drawSmallMarkers(canvas);
        drawMediumMarkers(canvas);
        drawBiggerMarkers(canvas);
    }

    private void drawSmallMarkers(Canvas canvas) {
        float centerX = getCenterX();
        int centerY = getCenterY();
        int markerPadding = Unit.convertDpToPixels(5, getContext());
        int viewRadius = getViewRadius();

        Path path = new Path();

        for (int angle = startAngle; angle <= endAngle; angle += 2) {
            PointF markerPoint = getPointForAngle(angle, viewRadius - markerPadding, centerX, centerY);

            int markersHeight = Unit.convertDpToPixels(4, getContext());
            PointF pointForAngleLeftBase = getPointForAngle(angle, viewRadius - markersHeight - markerPadding, centerX, centerY);

            path.moveTo(markerPoint.x, markerPoint.y);
            path.lineTo(pointForAngleLeftBase.x, pointForAngleLeftBase.y);

        }
        canvas.drawPath(path, smallMarkerPaint);
    }

    private void drawMediumMarkers(Canvas canvas) {

        float centerX = getCenterX();
        int centerY = getCenterY();
        int markerPadding = Unit.convertDpToPixels(5, getContext());
        int markersHeight = Unit.convertDpToPixels(14, getContext());
        int viewRadius = getViewRadius();

        Path path = new Path();
        for (int angle = startAngle; angle < endAngle; angle += 10) {
            PointF markerPoint = getPointForAngle(angle, viewRadius - markerPadding, centerX, centerY);
            PointF pointForAngleLeftBase = getPointForAngle(angle, viewRadius - markersHeight - markerPadding, centerX, centerY);

            path.moveTo(markerPoint.x, markerPoint.y);
            path.lineTo(pointForAngleLeftBase.x, pointForAngleLeftBase.y);

        }

        canvas.drawPath(path, mediumMarkerPaint);
    }

    private void drawBiggerMarkers(Canvas canvas) {

        float centerX = getCenterX();
        int centerY = getCenterY();

        int markerPadding = Unit.convertDpToPixels(5, getContext());
        int markersHeight = getBiggerMarkersHeight();
        int viewRadius = getViewRadius();

        Path path = new Path();
        for (int angle = startAngle; angle <= endAngle; angle += 20) {
            PointF markerPoint = getPointForAngle(angle, viewRadius - markerPadding, centerX, centerY);

            PointF pointForAngleLeftBase = getPointForAngle(angle, viewRadius - markersHeight - markerPadding, centerX, centerY);

            path.moveTo(markerPoint.x, markerPoint.y);
            path.lineTo(pointForAngleLeftBase.x, pointForAngleLeftBase.y);

        }
        canvas.drawPath(path, biggerMarkerPaint);
    }

    private int getBiggerMarkersHeight() {
        return Unit.convertDpToPixels(20, getContext());
    }

    private int getViewRadius() {
        return getViewDiameter() / 2;
    }

    private int getViewDiameter() {

        int diameter = getWidth();

        if (getHeight() < diameter) {
            diameter = getHeight();
        }

        return (int) (diameter - paintBackGround.getStrokeWidth());
    }

    private void drawLabels(Canvas canvas) {
        int centerX = getCenterX();
        int centerY = getCenterY();

        for (int textAngle = startAngle; textAngle <= endAngle; textAngle += 20) {
            drawTextStep(canvas, centerX, centerY, textAngle);
        }
    }

    private void drawTextStep(Canvas canvas, int cx, int cy, int textAngle) {
        String text = String.valueOf(textAngle);

        float difference = (float) abs(textAngle - this.arrowAngle);
        if (difference < TEXT_INCREASE_RANGE) {
            drawBiggerText(difference);
        } else {
            drawNormalText();
        }

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);

        int textMargin = Unit.convertDpToPixels(20, getContext());

        PointF textPoint = getPointForAngle(textAngle, (getViewRadius()) - rect.height() - textMargin - getBiggerMarkersHeight(), cx, cy);
        canvas.drawText(text, textPoint.x - rect.width(), textPoint.y + rect.height(), textPaint);
    }

    private void drawNormalText() {
        textPaint.setTextSize(Unit.convertSpToPixels(textSizeInSp, getContext()));
        textPaint.setColor(Color.LTGRAY);
    }

    private void drawBiggerText(float difference) {
        float sizeToIncrease = 1 + ((TEXT_INCREASE_RANGE - difference) / TEXT_INCREASE_RANGE * 0.5f);
        textPaint.setTextSize(Unit.convertSpToPixels(textSizeInSp * sizeToIncrease, getContext()));
        textPaint.setColor(Color.WHITE);
    }

    private Bitmap makeRadialGradient() {
        RectF componentRect = getComponentRect();

        RadialGradient gradient = new RadialGradient(componentRect.centerX(), componentRect.centerY(), getViewRadius() / 2 / 2, 0xFF000000,
                0xFFFFFF, Shader.TileMode.CLAMP);

        gradientPaint.setShader(gradient);

        Bitmap bitmap = Bitmap.createBitmap((int) componentRect.width(), (int) componentRect.height(), Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawCircle(componentRect.centerX(), componentRect.centerY(), getViewRadius() / 2 / 2, gradientPaint);

        return bitmap;
    }

    private void drawArrow(Canvas canvas) {
        Path path = new Path();

        float centerX = getCenterX();
        int centerY = getCenterY();

        path.moveTo(centerX, centerY);

        int baseWidth = Unit.convertDpToPixels(5, getContext());
        PointF basePointAngle = getPointForAngle(arrowAngle, getViewRadius(), centerX, centerY);

        PointF pointForAngleLeft = getPointForAngle(arrowAngle + 90, baseWidth, centerX, centerY);
        PointF pointForAngleRight = getPointForAngle(arrowAngle - 90, baseWidth, centerX, centerY);

        path.moveTo(basePointAngle.x, basePointAngle.y);
        path.lineTo(pointForAngleLeft.x, pointForAngleLeft.y);
        path.lineTo(pointForAngleRight.x, pointForAngleRight.y);

        path.close();

        canvas.drawPath(path, arrowPaint);
        canvas.drawCircle(centerX, centerY, Unit.convertDpToPixels(10, getContext()), arrowPaint);
    }

    private void drawBackgroundBorder(Canvas canvas) {

        RectF rect = getComponentRect();

        canvas.drawArc(rect, startAngle, Math.abs(Math.abs(startAngle) - Math.abs(endAngle)), false, paintBackGround);
    }

    @NonNull
    private RectF getComponentRect() {

        int viewRadius = getViewRadius();
        int centerY = getCenterY();
        int centerX = getCenterX();
        int viewDiameter = getViewDiameter();

        int left = centerX - viewRadius;
        int top = centerY - viewRadius;
        int right = left + viewDiameter;
        int bottom = top + viewDiameter;

        return new RectF(left, top, right, bottom);
    }

    private int getCenterX() {
        return getWidth() / 2;
    }

    private int getCenterY() {
        return getHeight() / 2;
    }


    private PointF getPointForAngle(double angle, double radius, float cx, float cy) {

        double x = cx + radius * Math.cos(Math.toRadians(angle));
        double y = cy + radius * Math.sin(Math.toRadians(angle));

        return new PointF((float) x, (float) y);
    }

    private double getAngleForPoint(float x, float y, float cx, float cy) {
        double angle;
        double rawAngle = Math.toDegrees(Math.atan2(y - cy, x - cx));
        if (rawAngle > 0) {
            angle = -180 - (180 - rawAngle);
        } else {
            angle = rawAngle;
        }
        return angle;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        if (!isTouchInsideGauge(x, y)) {
            return true;
        }

        arrowAngle = getAngleForPoint(x, y, getCenterX(), getCenterY());

        invalidate();

        return true;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
        invalidate();
    }

    public void setEndAngle(int endAngle) {
        this.endAngle = endAngle;
        invalidate();
    }

    public void setArrowAngle(double arrowAngle) {
        this.arrowAngle = -360 + arrowAngle;

        invalidate();
    }

    private boolean isTouchInsideGauge(float x, float y) {
        return Math.pow(x - getCenterX(), 2) + Math.pow(y - getCenterY(), 2) < Math.pow(getViewRadius(), 2);
    }
}


