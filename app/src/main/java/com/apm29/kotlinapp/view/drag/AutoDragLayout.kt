package com.apm29.kotlinapp.view.drag

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * Created by dingzhu on 2017/12/29.
 */
class AutoDragLayout(context: Context, attributes: AttributeSet) : ViewGroup(context, attributes) {

    var dragHelper: ViewDragHelper
    var gestureDetector: GestureDetectorCompat
    /**滑动状态*/
    var state = State.IDLE
    var page = Page.FIRST
    /**速度阈值*/
    var velocityThreshold: Int = 100
    /**距离阈值*/
    var distanceThreshold: Int = 100

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
                //滑动阻力加大
                return child.top + (top - child.top) / 3
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
                scrollDown = distanceY > 0
                return Math.abs(distanceY) > Math.abs(distanceX)
            }
        })
    }

    var scrollDown = false

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
            if (yvel > velocityThreshold || releasedChild.top > distanceThreshold) {
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
    fun setNextPageListener(nextPageListener: ShowNextPageNotifier): AutoDragLayout {
        this.nextPageListener = nextPageListener
        return this
    }

    fun setPrePageListener(prePageListener: ShowPrePageNotifier): AutoDragLayout {
        this.prePageListener = prePageListener
        return this
    }

    interface ShowNextPageNotifier {
        fun onDragNext()
    }

    interface ShowPrePageNotifier {
        fun onDragPre()
    }

    lateinit var topView: View
    lateinit var bottomView: View
    var topHeight: Int = 0
    var bottomHeight: Int = 0
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

    @SuppressLint("RestrictedApi")
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val verticalScroll = gestureDetector.onTouchEvent(ev)
        var interceptTouchEvent = false
        dragHelper.processTouchEvent(ev)
        //自动滑动的时候返回
        if (state == State.AUTO_SCROLL)
            return false
        //处理滑动拦截,拦截后会交给dragHelper,做出滑动阻尼效果
        if (page == Page.FIRST && topView is NestedScrollView) {
            val scroll = topView as NestedScrollView
            val atBottom = topView.scrollY + topHeight == scroll.computeVerticalScrollRange()
            interceptTouchEvent = atBottom && scrollDown//在底端时拦截
        } else if (page == Page.SECOND && bottomView is NestedScrollView) {
            val scroll = bottomView as NestedScrollView
            val atTop = scroll.scrollY == 0
            interceptTouchEvent = atTop && !scrollDown//在底端时拦截
        }
        //其他的layout没有阻尼效果,需要自己加,通过滑动速度来下拉上滑的

        //误差修正
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
        DRAG, SCROLL_TOP, SCROLL_BOTTOM, AUTO_SCROLL, IDLE
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