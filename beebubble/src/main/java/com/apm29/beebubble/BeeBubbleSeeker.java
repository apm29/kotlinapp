package com.apm29.beebubble;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.OvershootInterpolator;


/**
 * 气泡seekerBar
 * Created by apm29 on 2017/9/18.
 */

public class BeeBubbleSeeker extends android.support.v7.widget.AppCompatSeekBar {

    public static final double PI = Math.PI;
    public static final int DECELERATE_SPEED = 10;
    private boolean inTouch = false;//控制是否显示bubble
    private VelocityTracker mTracker;
    private int xSpeed;
    private Paint textPaint;
    private Paint bubblePaint;
    private int indicatorRadius;
    private int mPaddingHorizontal;
    private int mPaddingVertical;
    private int speedLimit;
    private int offsetV;
    private int bubbleRadius;
    private int textSize;
    private int indicatorRadiusVariance;
    private int offsetVLimit;
    private ValueAnimator ascendAnim;
    private ValueAnimator descendAnim;
    private String bubbleText;
    private Rect rect;
    private Paint linePaint;
    private RadialGradient shader;

    /**
     * 设置气泡字符
     *
     * @param bubbleText
     */
    public void setBubbleText(String bubbleText) {
        this.bubbleText = bubbleText;
    }

    public BeeBubbleSeeker(Context context) {
        super(context);
    }

    public BeeBubbleSeeker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#ffffff"));
        textSize = toPx(16);
        textPaint.setTextSize(textSize);
        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setColor(Color.parseColor("#51a155"));
        linePaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#99ccf166"));
        linePaint.setStrokeWidth(15);
        /**
         * 默认参数
         */
        //indicator 半径
        indicatorRadius = toPx(6);
        //水平padding
        mPaddingHorizontal = toPx(20);
        //垂直方向padding
        mPaddingVertical = toPx(6);
        //速度限制(也是水平偏移限制之一)
        speedLimit = toPx(600);
        //高度最大偏移
        offsetVLimit = toPx(22);
        //当前高度
        offsetV = toPx(0);
        //bubble 半径
        bubbleRadius = toPx(20);
        // 点击后变大px
        indicatorRadiusVariance = toPx(3);
        //bubble 升起动画
        ascendAnim = ValueAnimator.ofInt(0, offsetVLimit);
        ascendAnim.setDuration(400);
        ascendAnim.setInterpolator(new OvershootInterpolator());
        ascendAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetV = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        //降落动画
        descendAnim = ValueAnimator.ofInt(offsetVLimit, 0);
        descendAnim.setDuration(600);
        descendAnim.setInterpolator(new OvershootInterpolator());
        descendAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetV = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        //减速
        //decelerateHandler.sendEmptyMessage(0);

    }

//    Handler decelerateHandler =new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            if (xSpeed>=0){
//                xSpeed-=DECELERATE_SPEED;
//                if (xSpeed<0)xSpeed=0;
//            }else if (xSpeed<=0){
//                xSpeed+= DECELERATE_SPEED;
//                if (xSpeed>0)xSpeed=0;
//            }
//            invalidate();
//            removeCallbacksAndMessages(null);
//            decelerateHandler.sendEmptyMessageDelayed(0,10);
//        }
//    };
    /**
     * 设置bubble飘起高度
     *
     * @param offsetVLimit 飘起高度限制
     */
    public void setVerticalOffsetLimit(int offsetVLimit) {
        this.offsetVLimit = offsetVLimit;
    }

    private int toPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(),offsetVLimit+indicatorRadius+bubbleRadius+2*mPaddingVertical);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                System.out.println("ACTION_DOWN");
                inTouch = true;
                xSpeed = 0;
                addTracker(event);
                ascendBubble();
                break;
            case MotionEvent.ACTION_MOVE:
//                System.out.println("ACTION_MOVE");
                System.out.println("speed:event");
                mTracker.addMovement(event);
                getSpeed();
                break;
            default:
                inTouch = false;
//                System.out.println("ACTION_OTHER");
                cancelTracker();
                descendBubble();
                break;
        }
        float x = event.getX();
        //计算进度
//        System.out.println("x:" + x);
        if (x < mPaddingHorizontal) {
            setProgress(0);
        } else if (x > getMeasuredWidth() - mPaddingHorizontal) {
            setProgress(100);
        } else {
            setProgress((int) ((x - mPaddingHorizontal) * 1f / (getMeasuredWidth() - 2 * mPaddingHorizontal) * 100));
        }
        invalidate();
//        System.out.println("x:p" + getProgress());
        return true;
    }

    private void addTracker(MotionEvent event) {
        if (mTracker == null) {
            mTracker = VelocityTracker.obtain();
        } else {
            mTracker.clear();
        }
        mTracker.addMovement(event);
    }

    private void descendBubble() {
        //下降动画
        if (descendAnim != null)
            descendAnim.start();
    }

    private void ascendBubble() {
        //播放动画
        if (ascendAnim != null)
            ascendAnim.start();
    }

    private void cancelTracker() {
        if (mTracker != null) {
            mTracker.recycle();
            mTracker = null;
        }
    }
    int xLastSpeed=0;
    private void getSpeed() {
        if (mTracker==null)return;
        mTracker.computeCurrentVelocity(140);
        xLastSpeed=xSpeed;
//
//        if (xLastSpeed-xSpeed<100&&xLastSpeed-xSpeed>-100&&xLastSpeed!=0){
//            decelerateHandler.sendEmptyMessageDelayed(0,20);
//        }else {
//            xSpeed = (int) mTracker.getXVelocity();
//        }
        xSpeed= (int) mTracker.getXVelocity();
        System.out.println("speed:"+xSpeed);
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
        getSpeed();
        //进度
        float ratio = getProgress() * 1.0f / getMax();
        //去除半径和padding
        int inner = getMeasuredWidth() - 2 * mPaddingHorizontal - 2 * indicatorRadius;
        //计算触点坐标作为indicator圆心
        float x = ratio * inner
                + mPaddingHorizontal + indicatorRadius;
        int y = getMeasuredHeight() - indicatorRadius - mPaddingVertical;
        /**
         * 绘制背景
         */
        canvas.drawColor(Color.parseColor("#2b2b2b"));
        /**
         * 绘制进度
         */
        canvas.drawLine(mPaddingHorizontal, y, x, y, linePaint);
        //移动indicator中
        if (inTouch) {
            System.out.println("inTouch");
            canvas.drawCircle(x, y, indicatorRadius + indicatorRadiusVariance, bubblePaint);
            //vv 速度(200ms内event经过的px)
            int vv;
            if (xSpeed > speedLimit) {
                vv = speedLimit;
            } else if (xSpeed < -speedLimit) {
                vv = -speedLimit;
            } else {
                vv = xSpeed;
            }
            /**
             * 固定高度水平偏移
             * cx cy bubble的中心
             */
            //水平偏移速度的20%
            float cx = x - vv * 0.18f;
            //高度上升offsetV
            float cy = y - offsetV;

            findPath(cx, cy, x, y);
            bubblePaint.setStyle(Paint.Style.FILL);
            //绘制平滑曲线
            canvas.drawPath(bezier, bubblePaint);
            //绘制气泡
            bubblePaint.setShader(shader);
            canvas.drawCircle(cx, cy, bubbleRadius, bubblePaint);
            //绘制气泡文字
            String text;
            if (TextUtils.isEmpty(bubbleText)) {
                text = getProgress() + "";
            } else {
                text = bubbleText;
            }
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text + "", cx+getTextXOffset(), cy+getTextYOffset() , textPaint);
        } else {
            if (offsetV <= 0) {
//                System.out.println("outOfTouch");
                canvas.drawCircle(x, y, indicatorRadius, bubblePaint);
                //canvas.drawText(xSpeed + "", x, y, textPaint);
            }
        }
    }


    public float getTextXOffset() {
        return 0;
    }

    public float getTextYOffset() {
        return textSize / 2;
    }

    Path bezier;

    private void findPath(float cx, float cy, float x, float y) {
//        System.out.println("cx = [" + cx + "], cy = [" + cy + "], x = [" + x + "], y = [" + y + "]");
        //圆心连线方位角
        double beta = Math.atan2(y - cy, x - cx);
        //圆心距
        double dist = (Math.sqrt((cx - x) * (cx - x) + (cy - y) * (cy - y)));
        //半径差距角
        double mew = Math.asin((bubbleRadius - indicatorRadius) / (dist));

//        System.out.println("beta:" + beta * 180 / PI);
//        System.out.println("mew:" + mew * 180 / PI);
        if (bezier == null) {
            bezier = new Path();
        } else {
            bezier.reset();
        }
        bezier.moveTo(x, y);


        /**
         * b1
         */
        float b1x = (float) (cx + Math.cos(beta + PI / 2 - mew) * bubbleRadius);
        float b1y = (float) (cy + Math.sin(beta + PI / 2 - mew) * bubbleRadius);
//        System.out.println("b1x:" + b1x + "b1y" + b1y);

        /**
         * b2
         */
        float b2x = (float) (cx + Math.cos(beta - PI / 2 + mew) * bubbleRadius);
        float b2y = (float) (cy + Math.sin(beta - PI / 2 + mew) * bubbleRadius);
//        System.out.println("b2x:" + b2x + "b2y" + b2y);

        /**
         * i1
         */
        float i1x = (float) (x + Math.cos(beta + PI / 2 - mew) * (indicatorRadius + indicatorRadiusVariance));
        float i1y = (float) (y + Math.sin(beta + PI / 2 - mew) * (indicatorRadius + indicatorRadiusVariance));
//        System.out.println("i1x:" + i1x + "i1y" + i1y);

        /**
         * i2
         */
        float i2x = (float) (x + Math.cos(beta - PI / 2 + mew) * (indicatorRadius + indicatorRadiusVariance));
        float i2y = (float) (y + Math.sin(beta - PI / 2 + mew) * (indicatorRadius + indicatorRadiusVariance));
//        System.out.println("i2x:" + i2x + "i2y" + i2y);
        /**
         * 中间点(bezier曲线控制点)
         */
        //centerMid --> indicator 与 bubble 圆心中点
        float centerMidX = cx / 2 + x / 2;
        float centerMidY = cy / 2 + y / 2;
        //i1与b1中点
        float ib1x = i1x / 2 + b1x / 2;
        float ib1y = i1y / 2 + b1y / 2;
        //i2与b2中点
        float ib2x = i2x / 2 + b2x / 2;
        float ib2y = i2y / 2 + b2y / 2;
        /**
         * i1---b1---b2----i2
         */
//        System.out.println("Bezier"+dist+":"+(bubbleRadius+indicatorRadius));
        if (dist <= (bubbleRadius + indicatorRadius)) {//距离近的时候直接直线链接
            bezier.lineTo(x, y);
            bezier.lineTo(i1x, i1y);
            //bezier.quadTo(centerMidX / 2 + ib1x / 2, centerMidY / 2 + ib1y / 2, b1x, b1y);
            bezier.lineTo(b1x, b1y);
            bezier.lineTo(cx, cy);
            bezier.lineTo(b2x, b2y);
            //bezier.quadTo(centerMidX / 2 + ib2x / 2, centerMidY / 2 + ib2y / 2, i2x, i2y);
            bezier.lineTo(i2x, i2y);
            bezier.close();
        } else {//贝塞尔曲线链接
            bezier.lineTo(x, y);
            bezier.lineTo(i1x, i1y);
            bezier.quadTo(centerMidX / 2 + ib1x / 2, centerMidY / 2 + ib1y / 2, b1x, b1y);
            //bezier.lineTo(b1x, b1y);
            bezier.lineTo(cx, cy);
            bezier.lineTo(b2x, b2y);
            bezier.quadTo(centerMidX / 2 + ib2x / 2, centerMidY / 2 + ib2y / 2, i2x, i2y);
            //bezier.lineTo(i2x, i2y);
            bezier.close();
        }
    }

}
