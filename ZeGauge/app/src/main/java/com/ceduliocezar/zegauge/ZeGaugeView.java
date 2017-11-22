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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.abs;

public class ZeGaugeView extends View {

    private static final String TAG = "ZeGaugeView";
    public static final int TEXT_INCREASE_RANGE = 20;
    private Paint paint;
    private Paint paintBackGround;
    private double arrowAngle = -90;
    private Paint arrowPaint;
    private Paint textPaint;
    private int textSizeInSp = 20;

    public ZeGaugeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZeGaugeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

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
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        drawGradientBackground(canvas);
        drawBackgroundBorder(canvas, cx, cy);
        drawMarkers(canvas, cx, cy);
        drawArrow(canvas, cx, cy);
        drawLabels(canvas, cx, cy);
    }

    private void drawGradientBackground(Canvas canvas) {
        canvas.drawBitmap(makeRadGrad(), 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG));

    }


    private void drawMarkers(Canvas canvas, int cx, int cy) {
        drawSmallMarkers(canvas, cx, cy);
        drawMediumMarkers(canvas, cx, cy);
        drawBiggerMarkers(canvas, cx, cy);
    }

    private void drawSmallMarkers(Canvas canvas, int cx, int cy) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Unit.convertDpToPixels(2, getContext()));

        int markerPadding = Unit.convertDpToPixels(5, getContext());
        Path path = new Path();

        for (int angle = -260; angle <= 0; angle += 2) {
            PointF markerPoint = getPointForAngle(angle, (getWidth() / 2) - markerPadding, cx, cy);

            int markersHeight = Unit.convertDpToPixels(4, getContext());
            PointF pointForAngleLeftBase = getPointForAngle(angle, getViewRadius() - markersHeight - markerPadding, cx, cy);

            path.moveTo(markerPoint.x, markerPoint.y);
            path.lineTo(pointForAngleLeftBase.x, pointForAngleLeftBase.y);

        }
        canvas.drawPath(path, paint);
    }

    private void drawMediumMarkers(Canvas canvas, int cx, int cy) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Unit.convertDpToPixels(3, getContext()));

        int markerPadding = Unit.convertDpToPixels(5, getContext());
        int markersHeight = Unit.convertDpToPixels(14, getContext());
        Path path = new Path();
        for (int angle = -250; angle < 0; angle += 20) {
            PointF markerPoint = getPointForAngle(angle, (getWidth() / 2) - markerPadding, cx, cy);


            PointF pointForAngleLeftBase = getPointForAngle(angle, getViewRadius() - markersHeight - markerPadding, cx, cy);

            path.moveTo(markerPoint.x, markerPoint.y);
            path.lineTo(pointForAngleLeftBase.x, pointForAngleLeftBase.y);

        }
        canvas.drawPath(path, paint);
    }

    private void drawBiggerMarkers(Canvas canvas, int cx, int cy) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Unit.convertDpToPixels(4, getContext()));

        int markerPadding = Unit.convertDpToPixels(5, getContext());
        int markersHeight = getBiggerMarkersHeight();

        Path path = new Path();
        for (int angle = -260; angle <= 0; angle += 20) {
            PointF markerPoint = getPointForAngle(angle, (getWidth() / 2) - markerPadding, cx, cy);


            PointF pointForAngleLeftBase = getPointForAngle(angle, getViewRadius() - markersHeight - markerPadding, cx, cy);

            path.moveTo(markerPoint.x, markerPoint.y);
            path.lineTo(pointForAngleLeftBase.x, pointForAngleLeftBase.y);

        }
        canvas.drawPath(path, paint);
    }

    private int getBiggerMarkersHeight() {
        return Unit.convertDpToPixels(20, getContext());
    }

    private int getViewRadius() {
        return getWidth() / 2;
    }

    private void drawLabels(Canvas canvas, int cx, int cy) {
        for (int textAngle = -260; textAngle <= 0; textAngle += 20) {
            drawTextStep(canvas, cx, cy, textAngle);
        }
    }

    private void drawTextStep(Canvas canvas, int cx, int cy, int textAngle) {
        String text = String.valueOf(textAngle);
        float difference = (float) abs(textAngle - this.arrowAngle);
        if (difference < TEXT_INCREASE_RANGE) {//draw bigger
            float sizeToIncrease = 1 + ((TEXT_INCREASE_RANGE - difference) * 0.05f);
            textPaint.setTextSize(Unit.convertSpToPixels(textSizeInSp * sizeToIncrease, getContext()));
        } else {
            textPaint.setTextSize(Unit.convertSpToPixels(textSizeInSp, getContext()));
        }

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);

        int textMargin = Unit.convertDpToPixels(20, getContext());
        PointF textPoint = getPointForAngle(textAngle, (getViewRadius()) - rect.height() - textMargin - getBiggerMarkersHeight(), cx, cy);
        canvas.drawText(text, textPoint.x - rect.width(), textPoint.y + rect.height(), textPaint);
    }

    private Bitmap makeRadGrad() {
        RectF componentRect = getComponentRect();
        RadialGradient gradient = new RadialGradient(componentRect.centerX(), componentRect.centerY(), getViewRadius() / 2 / 2, 0xFFFFFF,
                0xFF000000, android.graphics.Shader.TileMode.CLAMP);
        Paint p = new Paint();
        p.setDither(true);
        p.setShader(gradient);

        Bitmap bitmap = Bitmap.createBitmap((int) componentRect.width(), (int) componentRect.height(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawCircle(componentRect.centerX(), componentRect.centerY(), getViewRadius() / 2 / 2, p);

        return bitmap;
    }

    private void drawArrow(Canvas canvas, int cx, int cy) {
        Path path = new Path();

        path.moveTo(cx, cy);
        int baseWidth = Unit.convertDpToPixels(5, getContext());


        PointF basePointAngle = getPointForAngle(arrowAngle, getViewRadius(), cx, cy);

        PointF pointForAngleLeft = getPointForAngle(arrowAngle + 90, baseWidth, cx, cy);
        PointF pointForAngleRight = getPointForAngle(arrowAngle - 90, baseWidth, cx, cy);

        path.moveTo(basePointAngle.x, basePointAngle.y);
        path.lineTo(pointForAngleLeft.x, pointForAngleLeft.y);
        path.lineTo(pointForAngleRight.x, pointForAngleRight.y);

        path.close();

        canvas.drawPath(path, arrowPaint);
        canvas.drawCircle(cx, cy, Unit.convertDpToPixels(10, getContext()), arrowPaint);
    }

    private void drawBackgroundBorder(Canvas canvas, int cx, int cy) {

//        canvas.drawCircle(cx, cy, getWidth() / 2, paintBackGround);
        RectF rect = getComponentRect();
        canvas.drawArc(rect, -260, 260, false, paintBackGround);
    }

    @NonNull
    private RectF getComponentRect() {
        return new RectF(0, (getHeight() / 2) - (getWidth() / 2), getWidth(), (getHeight() / 2) + (getWidth() / 2));
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

        double angleForPoint = getAngleForPoint(x, y, getWidth() / 2, getHeight() / 2);

        arrowAngle = angleForPoint;

        Log.d(TAG, "Angle:" + angleForPoint);

        invalidate();

        return true;
    }

    public void setArrowAngle(double arrowAngle) {
        this.arrowAngle = -360 + arrowAngle;
        Log.d(TAG, "Angle set:" + arrowAngle);
        invalidate();
    }
}


