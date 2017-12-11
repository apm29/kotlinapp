package com.apm29.kotlinapp.view.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.apm29.kotlinapp.R;


/**
 * Created by yingjiawei on 2017/12/11.
 */


public class IncreaseProgressBar extends View {
    private int mProgress = 50;
    private Paint mRedPaint;
    private Paint mGrayPaint;
    private Paint mTextPaint;
    private int w;
    private int h;
    private int paddingLeft;
    private int paddingBottom;
    private int paddingTop;
    private int paddingRight;
    private RectF redRect;
    private RectF grayRect;
    private RectF textRect;
    private int mTextHeight;
    private int mTextWidth;
    private float density;
    private int mProgressHeight = 5;
    private int start;

    public IncreaseProgressBar(Context context) {
        this(context, null);
    }

    public IncreaseProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IncreaseProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IncreaseProgressBar);
        int mainColor = typedArray.getColor(R.styleable.IncreaseProgressBar_progressMainColor, Color.RED);
        int secColor = typedArray.getColor(R.styleable.IncreaseProgressBar_progressSecondaryColor, Color.GRAY);
        int textColor = typedArray.getColor(R.styleable.IncreaseProgressBar_progressTextColor, Color.RED);
        int textSize = typedArray.getInt(R.styleable.IncreaseProgressBar_progressTextSize, 12);
        int progress = typedArray.getInt(R.styleable.IncreaseProgressBar_progressInt, 0);
        typedArray.recycle();
        mProgress = progress;
        mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRedPaint.setColor(mainColor);
        mGrayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGrayPaint.setColor(secColor);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(toPX(textSize));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        redRect = new RectF();
        grayRect = new RectF();
        textRect = new RectF();

        mTextHeight = toPX(30);
        mProgressHeight = toPX(5);
    }

    public int toPX(int dp) {
        if (density == 0)
            density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        //包裹内容
        int measuredHeight = mTextHeight + mProgressHeight + getPaddingBottom() + getPaddingTop();
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), measuredHeight);
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setProgress(int progress) {
        if (progress >= 100) progress = 100;
        start = 0;
        mTextWidth = (int) mTextPaint.measureText(mProgress + "%");

        final int finalProgress = progress;
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start++;
                if (start <= finalProgress) {
                    mProgress = start;
                    handler.postDelayed(this, 5);
                    invalidate();
                } else {
                    handler.removeCallbacksAndMessages(null);
                    start = 0;
                }
            }
        }, 5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        super.onSizeChanged(w, h, oldw, oldh);

        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();


    }

    private Handler handler = new Handler();

    @Override
    protected void onDraw(Canvas canvas) {
        grayRect.bottom = redRect.bottom = h - paddingBottom;
        redRect.left = paddingLeft;
        textRect.bottom = paddingTop + mTextHeight;
        grayRect.top = redRect.top = h - paddingBottom - mProgressHeight;
        grayRect.left = redRect.right = (w - paddingLeft - paddingRight) * mProgress / 100 + paddingLeft;
        grayRect.right = w - paddingRight;

        textRect.top = paddingTop;
        textRect.left = redRect.right - mTextWidth / 2;
        textRect.right = redRect.right + mTextWidth / 2;

//        System.out.println("grayRect = " + grayRect);
//        System.out.println("textRect = " + textRect);
//        System.out.println("redRect = " + redRect);
        canvas.drawRect(redRect, mRedPaint);
        canvas.drawRect(grayRect, mGrayPaint);
        if (textRect.right > w) {
            textRect.right = w - paddingRight;
            textRect.left = w - mTextWidth - paddingRight;
        }
        if (textRect.left < 0) {
            textRect.left = paddingLeft;
            textRect.right = paddingRight + mTextWidth;
        }
        System.out.println("mProgress = " + mProgress);

        canvas.drawText(mProgress + "%", textRect.centerX(), textRect.centerY(), mTextPaint);
    }
}

