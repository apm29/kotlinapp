package com.apm29.kotlinapp.view.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.math.MathUtils
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.OverScroller
import com.apm29.kotlinapp.utils.clamp
import com.apm29.kotlinapp.utils.logD

/**
 * Created by dingzhu on 2017/12/28.
 */
class DragBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<NestedScrollView>(context, attrs) {

    private var mViewOffsetHelper: ViewOffsetHelper? = null

    private var mTempTopBottomOffset = 0
    private var mTempLeftRightOffset = 0


    override fun onLayoutChild(parent: CoordinatorLayout?, child: NestedScrollView, layoutDirection: Int): Boolean {
        // First let lay the child out
        layoutChild(parent, child, layoutDirection)

        if (mViewOffsetHelper == null) {
            mViewOffsetHelper = ViewOffsetHelper(child)
        }
        mViewOffsetHelper?.onViewLayout()

        if (mTempTopBottomOffset != 0) {
            mViewOffsetHelper?.setTopAndBottomOffset(mTempTopBottomOffset)
            mTempTopBottomOffset = 0
        }
        if (mTempLeftRightOffset != 0) {
            mViewOffsetHelper?.setLeftAndRightOffset(mTempLeftRightOffset)
            mTempLeftRightOffset = 0
        }

        return true
    }

    protected fun layoutChild(parent: CoordinatorLayout?, child: NestedScrollView?, layoutDirection: Int) {
        // Let the parent lay it out by default
        parent?.onLayoutChild(child, layoutDirection)
    }

    fun setTopAndBottomOffset(offset: Int): Boolean {
        if (mViewOffsetHelper != null) {
            return mViewOffsetHelper!!.setTopAndBottomOffset(offset)
        } else {
            mTempTopBottomOffset = offset
        }
        return false
    }

    fun setLeftAndRightOffset(offset: Int): Boolean {
        if (mViewOffsetHelper != null) {
            return mViewOffsetHelper!!.setLeftAndRightOffset(offset)
        } else {
            mTempLeftRightOffset = offset
        }
        return false
    }

    fun getTopAndBottomOffset(): Int {
        return if (mViewOffsetHelper != null) mViewOffsetHelper!!.getTopAndBottomOffset() else 0
    }

    fun getLeftAndRightOffset(): Int {
        return if (mViewOffsetHelper != null) mViewOffsetHelper!!.getLeftAndRightOffset() else 0
    }

    /**
     * 计算可滑动距离
     */
    private fun computeVerticalScrollRange(view: NestedScrollView): Int {
        val count = view.childCount
        val contentHeight = view.height - view.paddingBottom - view.paddingTop
        if (count == 0) {
            return contentHeight
        }

        var scrollRange = view.getChildAt(0).bottom
        val scrollY = view.scrollY
        val overScrollBottom = Math.max(0, scrollRange - contentHeight)
        if (scrollY < 0) {
            scrollRange -= scrollY
        } else if (scrollY > overScrollBottom) {
            scrollRange += scrollY - overScrollBottom
        }

        return scrollRange
    }


    /**
     * Utility helper for moving a [android.view.View] around using
     * [android.view.View.offsetLeftAndRight] and
     * [android.view.View.offsetTopAndBottom].
     *
     *
     * Also the setting of absolute offsets (similar to translationX/Y), rather than additive
     * offsets.
     */
    internal class ViewOffsetHelper(private val mView: View) {

        var layoutTop: Int = 0
            private set
        var layoutLeft: Int = 0
            private set
        private var mOffsetTop: Int = 0
        private var mOffsetLeft: Int = 0

        fun onViewLayout() {
            // Now grab the intended top
            layoutTop = mView.top
            layoutLeft = mView.left

            // And offset it as needed
            updateOffsets()
        }

        private fun updateOffsets() {
            ViewCompat.offsetTopAndBottom(mView, mOffsetTop - (mView.top - layoutTop))
            ViewCompat.offsetLeftAndRight(mView, mOffsetLeft - (mView.left - layoutLeft))
        }

        /**
         * Set the top and bottom offset for this [ViewOffsetHelper]'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        fun setTopAndBottomOffset(offset: Int): Boolean {
            if (mOffsetTop != offset) {
                mOffsetTop = offset
                updateOffsets()
                return true
            }
            return false
        }

        /**
         * Set the left and right offset for this [ViewOffsetHelper]'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        fun setLeftAndRightOffset(offset: Int): Boolean {
            if (mOffsetLeft != offset) {
                mOffsetLeft = offset
                updateOffsets()
                return true
            }
            return false
        }

        fun getTopAndBottomOffset(): Int {
            return mOffsetTop
        }

        fun getLeftAndRightOffset(): Int {
            return mOffsetLeft
        }
    }

    private var mIsBeingDragged: Boolean = false

    private var mLastMotionY: Int = 0

    private val mTouchSlop: Int = 0

    private var mVelocityTracker: VelocityTracker? = VelocityTracker.obtain()

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: NestedScrollView, ev: MotionEvent): Boolean {


        val action = ev.action

        // Shortcut since we're being dragged
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true
        }

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mIsBeingDragged = false
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                if (canDragView(child) && parent.isPointInChildBounds(child, x, y)) {
                    mLastMotionY = y
                    mVelocityTracker = VelocityTracker.obtain()
                }
            }

            MotionEvent.ACTION_MOVE -> {


                val y = ev.y.toInt()
                val yDiff = Math.abs(y - mLastMotionY)
                if (yDiff > mTouchSlop && !canNestScroll(parent,child,y)) {
                    mIsBeingDragged = true
                    mLastMotionY = y
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                mIsBeingDragged = false
                if (mVelocityTracker != null) {
                    mVelocityTracker?.recycle()
                    mVelocityTracker = null
                }
            }
        }

        mVelocityTracker?.addMovement(ev)
        logD(mIsBeingDragged.toString())
        return mIsBeingDragged
    }

    private fun isScrollUp(y: Int) = (y - mLastMotionY) < 0

    private fun canNestScroll(parent: CoordinatorLayout,child: NestedScrollView, y: Int) :Boolean{
        if (parent.bottom>child.bottom){
            return false
        }else if (parent.bottom==child.bottom){
            if ((computeVerticalScrollRange(child)==child.scrollY+child.height)&&!isScrollUp(y))
                return true
            return (computeVerticalScrollRange(child)!=child.scrollY+child.height)
        }else {
            return false
        }
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: NestedScrollView, ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x.toInt()
                val y = ev.y.toInt()

                if (parent.isPointInChildBounds(child, x, y) && canDragView(child)) {
                    mLastMotionY = y
                    mVelocityTracker = VelocityTracker.obtain()
                } else {
                    return false
                }
            }

            MotionEvent.ACTION_MOVE -> {

                val y = ev.y.toInt()
                var dy = mLastMotionY - y

                if (!mIsBeingDragged && Math.abs(dy) > mTouchSlop) {
                    mIsBeingDragged = true
                    if (dy > 0) {
                        dy -= mTouchSlop
                    } else {
                        dy += mTouchSlop
                    }
                }

                if (mIsBeingDragged) {
                    mLastMotionY = y
                    // We're being dragged so scroll the ABL
                    scroll(parent, child, dy, getMaxDragOffset(child), 0)
                }
            }

            MotionEvent.ACTION_UP -> {
                if (mVelocityTracker != null) {
                    mVelocityTracker!!.addMovement(ev)
                    mVelocityTracker!!.computeCurrentVelocity(1000)
                    val yvel = mVelocityTracker!!.yVelocity
                    fling(parent, child, getMaxDragOffset(child), 0, yvel)
                }
            }
        // $FALLTHROUGH
            MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                if (mVelocityTracker != null) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }
        }

        mVelocityTracker?.addMovement(ev)

        return true
    }

    fun canDragView(child: NestedScrollView): Boolean = true
    /**
     * Returns the maximum px offset when `view` is being dragged.
     */
    fun getMaxDragOffset(view: NestedScrollView): Int {
        return -view.measuredHeight
    }


    private fun scroll(parent: CoordinatorLayout, child: NestedScrollView, dy: Int, minDragOffset: Int, maxDragOffset: Int) {
        //setTopAndBottomOffset(-dy)
        val dyTop = clamp(child.top - dy, minDragOffset, maxDragOffset)
        child.layout(0, dyTop, child.right, dyTop + child.measuredHeight)
    }

    private var mFlingRunnable: Runnable?=null

    private var mScroller: OverScroller? = null

    fun fling(coordinatorLayout: CoordinatorLayout, layout: NestedScrollView, minOffset: Int,
              maxOffset: Int, velocityY: Float): Boolean {
        if (mFlingRunnable != null) {
            layout.removeCallbacks(mFlingRunnable)
            mFlingRunnable = null
        }

        if (mScroller == null) {
            mScroller = OverScroller(layout.context)
        }

        mScroller?.fling(
                0, getTopAndBottomOffset(), // curr
                0, Math.round(velocityY), // velocity.
                0, 0, // x
                minOffset, maxOffset) // y

        if (mScroller?.computeScrollOffset()==true) {
            mFlingRunnable = FlingRunnable(coordinatorLayout, layout)
            ViewCompat.postOnAnimation(layout, mFlingRunnable)
            return true
        } else {
            //onFlingFinished(coordinatorLayout, layout)
            return false
        }
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: NestedScrollView, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return true
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: NestedScrollView, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {

    }

    private inner class FlingRunnable internal constructor(private val mParent: CoordinatorLayout, private val mLayout: NestedScrollView?) : Runnable {

        override fun run() {
            if (mLayout != null && mScroller != null) {
                if (mScroller != null && mScroller!!.computeScrollOffset()) {
                    setHeaderTopBottomOffset(mParent, mLayout, mScroller!!.currY)
                    // Post ourselves so that we run on the next animation
                    ViewCompat.postOnAnimation(mLayout, this)
                } else {
                    //onFlingFinished(mParent, mLayout)
                }
            }
        }
    }

    fun setHeaderTopBottomOffset(parent: CoordinatorLayout, header: NestedScrollView, newOffset: Int): Int {
        return setHeaderTopBottomOffset(parent, header, newOffset,
                Int.MIN_VALUE, Int.MAX_VALUE)
    }

    fun setHeaderTopBottomOffset(parent: CoordinatorLayout, header: NestedScrollView, newOffset: Int,
                                 minOffset: Int, maxOffset: Int): Int {
        var newOffset = newOffset
        val curOffset = getTopAndBottomOffset()
        var consumed = 0

        if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
            // If we have some scrolling range, and we're currently within the min and max
            // offsets, calculate a new offset
            newOffset = MathUtils.clamp(newOffset, minOffset, maxOffset)

            if (curOffset != newOffset) {
                setTopAndBottomOffset(newOffset)
                // Update how much dy we have consumed
                consumed = curOffset - newOffset
            }
        }

        return consumed
    }

}