package com.apm29.beebubble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;


/**
 * Created by apm29 on 2017/9/18.
 */

public class BeeBubbleSeeker extends android.support.v7.widget.AppCompatSeekBar {

    private VelocityTracker mTracker;
    private int xSpeed;
    private Paint textPaint;
    private Paint bubblePaint;
    private boolean inTouch=false;
    private int indicatorRaduis;

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
        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setColor(Color.WHITE);
        /**
         * 默认参数
         */
        indicatorRaduis = 18;
        setPadding(30,6,30,6);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getTracker(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                inTouch=true;
                if(mTracker==null){
                    mTracker = VelocityTracker.obtain();
                }else{
                    mTracker.clear();
                }
                mTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mTracker.addMovement(event);
                mTracker.computeCurrentVelocity(1000);
                getSpeed();
                break;
            case MotionEvent.ACTION_UP:
                cancelTracker();
                inTouch=false;
                break;
        }
        return super.onTouchEvent(event);
    }

    private void cancelTracker() {
        if(mTracker!=null){
            mTracker.recycle();
            mTracker = null;
        }
    }

    private void getSpeed() {
        mTracker.computeCurrentVelocity(1000);
        xSpeed = (int) mTracker.getXVelocity();
        Log.e("speed",xSpeed+"");
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(l);
    }
    private void getTracker(MotionEvent event){
        if(mTracker ==null){
            mTracker = VelocityTracker.obtain();
            mTracker.addMovement(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        System.out.println("onDraw:"+getMeasuredWidth()+":"+getPaddingLeft()+":"+getPaddingRight());
        float x = getProgress() * 1.0f / getMax() * (getMeasuredWidth()-getPaddingLeft()-getPaddingRight()-2*indicatorRaduis)+getPaddingLeft()+indicatorRaduis;
        int y = getMeasuredHeight() / 2;
        System.out.println("onDraw: x:"+x+" y:"+y);
        System.out.println("onDraw: progress:"+getProgress()+" max:"+getMax());
        if (inTouch) {
            canvas.drawCircle(x + indicatorRaduis, y, indicatorRaduis+5, bubblePaint);
            //canvas.drawText(xSpeed + "", x, y, textPaint);
        }else {
            canvas.drawCircle(x + indicatorRaduis, y, indicatorRaduis, bubblePaint);
            //canvas.drawText(xSpeed + "", x, y, textPaint);
        }
    }


}
