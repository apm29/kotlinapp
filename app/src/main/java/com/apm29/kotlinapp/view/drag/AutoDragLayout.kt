package com.apm29.kotlinapp.view.drag

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ScrollingView
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.apm29.kotlinapp.utils.Utils
import com.apm29.kotlinapp.utils.getWindowWidth

/**
 * 自动分隔滑动
 * Created by apm29 on 2017/12/29.
 */
class AutoDragLayout(context: Context, attributes: AttributeSet) : ViewGroup(context, attributes) {

    var dragHelper: ViewDragHelper
    var gestureDetector: GestureDetectorCompat
    /**滑动状态*/
    var state = State.IDLE
    /**页面状态 第一页/第二页*/
    var page = Page.FIRST
    /**速度阈值*/
    private val velocityThreshold: Int = 100
    /**距离阈值*/
    private val distanceThreshold: Int = 100

    /**阻力系数*/
    private val resistanceCoefficient = 3
    /**两个子View*/
    lateinit var topView: View
    lateinit var bottomView: View
    /**view的高度*/
    var topHeight: Int = 0
    var bottomHeight: Int = 0
    /**是否向下滑动*/
    var scrollDown = true
    var scrollHorizontal =true

    private var flingDetector: GestureDetectorCompat

    init {
        dragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return true
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                //不越界
                if (child == topView) {
                    if (top > 0) return 0

                } else if (child == bottomView) {
                    if (top < 0) return 0
                }
                //滑动阻力加大 resistanceCoefficient阻力系数,越大越难拉
                return child.top + (top - child.top) / resistanceCoefficient
            }

            override fun getViewVerticalDragRange(child: View): Int {
                return 1
            }

            override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                viewPositionChanged(changedView, left, top, dx, dy)
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                smoothScrollTopBottom(releasedChild, yvel)
            }
        })
        gestureDetector = GestureDetectorCompat(this.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                println("distanceX = $distanceX")
                scrollDown = distanceY > 0
                scrollHorizontal = Math.abs(distanceX) > Math.abs(distanceY)
                return Math.abs(distanceX) < Math.abs(distanceY)
            }

        })
        flingDetector = GestureDetectorCompat(this.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                //左右滑动
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX < 0)
                        onRightFling()
                    else
                        onLeftFling()
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })

    }
    var onScrollHorizontalOverListener: OnScrollHorizontalOverListener?=null

    interface OnScrollHorizontalOverListener{
        fun left()
        fun right()
    }
    private fun onLeftFling() {
        onScrollHorizontalOverListener?.left()
    }

    private fun onRightFling() {
        onScrollHorizontalOverListener?.right()
    }

    private val SCREEN_WIDTH = getWindowWidth()
    private val WIDTH = SCREEN_WIDTH / 4
    fun scrollToBottom() {
        smoothScrollTopBottom(topView, (-(velocityThreshold+1)).toFloat())
    }

    /**
     * 滑动到最顶端或最低端
     */
    private fun smoothScrollTopBottom(releasedChild: View, yvel: Float) {
        var scrollTop = 0
        if (releasedChild == topView) {
            if (yvel < -velocityThreshold || releasedChild.top < -distanceThreshold) {
                scrollTop = -topHeight//滑到下一页
                page = Page.SECOND
                nextPageListener?.onDragNext()
            }

        } else if (releasedChild == bottomView) {
            if (yvel > velocityThreshold * 5 || releasedChild.top > distanceThreshold) {
                scrollTop = topHeight//滑到上一页
                page = Page.FIRST
                prePageListener?.onDragPre()
            }
        }
        if (dragHelper.smoothSlideViewTo(releasedChild, 0, scrollTop)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun viewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
        if (changedView == topView) {
            val offsetTopBottom = topHeight + topView.top - bottomView.top
            bottomView.offsetTopAndBottom(offsetTopBottom)
        } else if (changedView == bottomView) {
            val offsetTopBottom = (bottomView.top - topHeight
                    - topView.top)
            topView.offsetTopAndBottom(offsetTopBottom)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val maxWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val maxHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(
                resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0))
    }

    var nextPageListener: ShowNextPageNotifier? = null
    var prePageListener: ShowPrePageNotifier? = null

    //    fun setNextPageListener(nextPageListener: ShowNextPageNotifier): AutoDragLayout {
//        this.nextPageListener = nextPageListener
//        return this
//    }
//
//    fun setPrePageListener(prePageListener: ShowPrePageNotifier): AutoDragLayout {
//        this.prePageListener = prePageListener
//        return this
//    }
    interface ShowNextPageNotifier {
        fun onDragNext()

    }

    interface ShowPrePageNotifier {
        fun onDragPre()

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        topView = getChildAt(0)
        bottomView = getChildAt(1)
        if (topView.top == 0) {
            topView.layout(l, 0, r, b - t)
            bottomView.layout(l, 0, r, b - t)
            topHeight = topView.measuredHeight
            bottomHeight = bottomView.measuredHeight
            bottomView.offsetTopAndBottom(topHeight)
        } else {
            topView.layout(l, topView.top, r, topView.bottom)
            bottomView.layout(l, topView.bottom, r, bottomView.bottom)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        flingDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }
    var requestNotIntercept = false//不拦截
    var isFirstMoveEvent=true
    @SuppressLint("RestrictedApi")
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val verticalScroll = gestureDetector.onTouchEvent(ev)
        var interceptTouchEvent = ev.action == MotionEvent.ACTION_DOWN
        dragHelper.processTouchEvent(ev)
        //自动滑动的时候返回
        if (state == State.AUTO_SCROLL || requestNotIntercept)
            return false
        //处理滑动拦截,拦截后会交给dragHelper,做出滑动阻尼效果
        if (page == Page.FIRST && topView is ScrollingView) {
            val atBottom = topView.scrollY + topHeight == (topView as NestedScrollView).computeVerticalScrollRange()
            interceptTouchEvent = atBottom && scrollDown//在底端上滑时拦截
        } else if (page == Page.SECOND) {
            val atTop = bottomView.scrollY == 0
            interceptTouchEvent = atTop && !scrollDown//在顶端下滑时拦截
            println("interceptTouchEvent = ${interceptTouchEvent}")
        }
        if (interceptTouchEvent) state = State.DRAG//边界滑动处理
        //其他的layout没有阻尼效果,需要自己加,只能通过滑动速度来下拉上滑的


        //误差修正
        if (state != State.DRAG)
            if (page == Page.FIRST) {
                topView.layout(0, 0, topView.right, topHeight)
                bottomView.layout(0, topHeight, bottomView.right, topHeight + bottomHeight)
            } else if (page == Page.SECOND) {
                bottomView.layout(0, 0, bottomView.right, bottomHeight)
                topView.layout(0, -topHeight, topView.right, 0)
            }
        return interceptTouchEvent && verticalScroll
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    enum class State {
        DRAG, AUTO_SCROLL, IDLE
    }

    enum class Page {
        FIRST, SECOND
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
            state = State.AUTO_SCROLL
        } else {
            state = State.IDLE
        }
    }
}