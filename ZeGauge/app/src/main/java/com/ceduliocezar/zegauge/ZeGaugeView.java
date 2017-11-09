package com.ceduliocezar.zegauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ZeGaugeView extends View {

    private Paint paint;
    private Paint paintBackGround;
    private double angle = -90;

    private static final String TAG = "ZeGaugeView";

    public ZeGaugeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZeGaugeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(Unit.convertSpToPixels(10, getContext()));


        paintBackGround = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackGround.setColor(Color.RED);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        drawBackground(canvas, cx, cy);
        drawArrow(canvas, cx, cy);
        drawSteps(canvas, cx, cy);
    }

    private void drawSteps(Canvas canvas, int cx, int cy) {
        for (int angle = -220; angle <= 40; angle += 10) {
            drawTextStep(canvas, cx, cy, angle);
        }
    }

    private void drawTextStep(Canvas canvas, int cx, int cy, int angle) {
        String text = String.valueOf(angle);

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);

        int textMargin = Unit.convertDpToPixels(10, getContext());
        PointF textPoint = getPointForAngle(angle, (getWidth() / 2) - rect.height() - textMargin, cx, cy);

        canvas.rotate(angle + 90, textPoint.x, textPoint.y);
        canvas.drawText(text, textPoint.x - rect.centerX(), textPoint.y, paint);

        canvas.rotate(-(angle + 90), textPoint.x, textPoint.y);
    }

    private void drawArrow(Canvas canvas, int cx, int cy) {
        Path path = new Path();

        path.moveTo(cx, cy);
        int baseWidth = Unit.convertDpToPixels(5, getContext());


        PointF basePointAngle = getPointForAngle(angle, getWidth() / 2, cx, cy);

        PointF pointForAngleLeft = getPointForAngle(angle + 90, baseWidth, cx, cy);
        PointF pointForAngleRight = getPointForAngle(angle - 90, baseWidth, cx, cy);

        path.moveTo(basePointAngle.x, basePointAngle.y);
        path.lineTo(pointForAngleLeft.x, pointForAngleLeft.y);
        path.lineTo(pointForAngleRight.x, pointForAngleRight.y);

        path.close();

        canvas.drawPath(path, paint);
        canvas.drawCircle(cx, cy, Unit.convertDpToPixels(10, getContext()), paint);
    }

    private void drawBackground(Canvas canvas, int cx, int cy) {

        canvas.drawCircle(cx, cy, getWidth() / 2, paintBackGround);
    }


    private PointF getPointForAngle(double angle, double radius, float cx, float cy) {

        double x = cx + radius * Math.cos(Math.toRadians(angle));
        double y = cy + radius * Math.sin(Math.toRadians(angle));

        return new PointF((float) x, (float) y);
    }

    private double getAngleForPoint(float x, float y, float cx, float cy) {

        return Math.toDegrees(Math.atan2(y - cy, x - cx));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        float x = event.getX();
        float y = event.getY();

        double angleForPoint = getAngleForPoint(x, y, getWidth() / 2, getHeight() / 2);

        angle = angleForPoint;

        Log.d(TAG, "Angle:" + angleForPoint);

        invalidate();

        return true;
    }
}
