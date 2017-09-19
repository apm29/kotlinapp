package com.apm29.beebubble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.MainThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import static android.R.attr.controlX2;
import static android.R.attr.logo;
import static android.R.attr.padding;


/**
 * Created by apm29 on 2017/9/18.
 */

public class BeeBubbleSeeker extends android.support.v7.widget.AppCompatSeekBar {

    private VelocityTracker mTracker;
    private int xSpeed;
    private int lastXSpeed;
    private Paint textPaint;
    private Paint bubblePaint;
    private boolean inTouch = false;
    private int indicatorRadius;
    private int mPaddingHorizontal = 0;
    private int mPaddingVertical;
    private int vLimit;
    private int offsetV;
    private int bubbleRadius;
    private int textSize;

    public BeeBubbleSeeker(Context context) {
        super(context);
    }

    public BeeBubbleSeeker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.RED);
        textSize = 36;
        textPaint.setTextSize(textSize);
        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setColor(Color.WHITE);
        /**
         * 默认参数
         */
        indicatorRadius = 18;
        mPaddingHorizontal = 40;
        mPaddingVertical = 10;
        vLimit = 900;

        offsetV = 48;
        bubbleRadius = 40;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                inTouch = true;
                if (mTracker == null) {
                    mTracker = VelocityTracker.obtain();
                } else {
                    mTracker.clear();
                }
                mTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mTracker.addMovement(event);
                getSpeed();
                break;
            default:
                cancelTracker();
                inTouch = false;
                break;
        }
        float x = event.getX();
        System.out.println("x:" + x);
        if (x < mPaddingHorizontal) {
            setProgress(0);
        } else if (x > getMeasuredWidth() - mPaddingHorizontal) {
            setProgress(100);
        } else {
            setProgress((int) ((x - mPaddingHorizontal) * 1f / (getMeasuredWidth() - 2 * mPaddingHorizontal) * 100));
        }
        invalidate();
        System.out.println("x:p" + getProgress());
        return true;
    }

    private void cancelTracker() {
        if (mTracker != null) {
            mTracker.recycle();
            mTracker = null;
        }
    }

    private void getSpeed() {
        mTracker.computeCurrentVelocity(200);
        lastXSpeed = xSpeed;
        xSpeed = (int) mTracker.getXVelocity();
        Log.e("speed", xSpeed + "");
        Log.e("lastSpeed", lastXSpeed + "");
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(l);
    }

    private void getTracker(MotionEvent event) {
        if (mTracker == null) {
            mTracker = VelocityTracker.obtain();
            mTracker.addMovement(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float ratio = getProgress() * 1.0f / getMax();
        int inner = getMeasuredWidth() - 2 * mPaddingHorizontal - 2 * indicatorRadius;
        float x = ratio * inner
                + mPaddingHorizontal + indicatorRadius;
        int y = getMeasuredHeight() - indicatorRadius - mPaddingVertical;


        /**
         * 背景
         */
        canvas.drawColor(Color.parseColor("#883333aa"));
        canvas.drawLine(0 + mPaddingHorizontal, y, x, y, bubblePaint);
        if (inTouch) {
            canvas.drawCircle(x, y, indicatorRadius + 5, bubblePaint);
            //canvas.drawText(xSpeed + "", x, y, textPaint);

            int vv = 0;
            if (xSpeed > vLimit) {
                vv = vLimit;
            } else if (xSpeed < -vLimit) {
                vv = -vLimit;
            } else {
                vv = xSpeed;
            }

            float cx = x - vv * 0.2f;
            float cy = y - offsetV;
            canvas.drawCircle(cx, cy, bubbleRadius, bubblePaint);

            findPath(cx, cy, x, y);
            bubblePaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(bezier, textPaint);
            canvas.drawText(getProgress() + "", cx - textSize / 2, cy + textSize / 2 - 2, textPaint);

        } else {
            canvas.drawCircle(x, y, indicatorRadius, bubblePaint);
            //canvas.drawText(xSpeed + "", x, y, textPaint);
        }
    }

    Path bezier;

    private void findPath(float cx, float cy, float x, float y) {
        double beta = Math.atan2(cx - x, cy - y);

        double dist = (Math.sqrt((cx - x) * (cx - x) + (cy - y) * (cy - y)));
        double mew = Math.asin((bubbleRadius - indicatorRadius) / (dist));
        double alpha = beta>0?(beta-mew):(beta+mew);
        System.out.println("alpha:" + alpha * 180 / Math.PI);
        System.out.println("beta:" + beta * 180 / Math.PI);
        System.out.println("mew:" + mew * 180 / Math.PI);
        if (bezier == null) {
            bezier = new Path();
        } else {
            bezier.reset();
        }
        bezier.moveTo(x, y);
        /**
         * 计算 bubble 1
         */
        float b1x = (float) (cx + Math.cos(alpha) * bubbleRadius);
        float b1y = (float) (cy + Math.sin(alpha) * bubbleRadius);
        System.out.println("b1x:" + b1x + "b1y" + b1y);

        /**
         * b2
         */
        float b2x = cx + (cx - b1x);
        float b2y = cy + (cy - b1y);
        System.out.println("b2x:" + b2x + "b2y" + b2y);

        /**
         * i1
         */
        float i1x = (float) (x + Math.cos(alpha) * indicatorRadius);
        float i1y = (float) (y + Math.sin(alpha) * indicatorRadius);
        System.out.println("i1x:" + i1x + "i1y" + i1y);

        /**
         * i2
         */
        float i2x = 2 * x - i1x;
        float i2y = 2 * y - i1y;
        System.out.println("i2x:" + i2x + "i2y" + i2y);
        /**
         * 中间点
         * ¥
         */
        float xc = cx / 2 + x / 2;
        float yc = cy / 2 + y / 2;
        float ib1x = i1x / 2 + b1x / 2;
        float ib1y = i1y / 2 + b1y / 2;
        float ib2x = i2x / 2 + b2x / 2;
        float ib2y = i2y / 2 + b2y / 2;
        /**
         * i1---b1---b2----i2
         */
        bezier.lineTo(i1x, i1y);
        //bezier.quadTo(xc/2+ib1x/2,yc/2+ib1y/2,b1x,b1y);
        bezier.lineTo(b1x, b1y);

        bezier.lineTo(b2x, b2y);
//        bezier.quadTo(xc/2+ib2x/2,yc/2+ib2y/2,i2x,i2y);
        bezier.lineTo(i2x, i2y);
        bezier.close();
    }


}
