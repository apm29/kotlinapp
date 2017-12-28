package com.apm29.kotlinapp.view.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.apm29.kotlinapp.utils.clamp
import com.apm29.kotlinapp.utils.logD

/**
 * Created by dingzhu on 2017/12/28.
 */
class DragBehaviorTop(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<NestedScrollView>(context, attrs) {

    var bottom: Int = -1
    var childHeight: Int = -1
    var targetHeight: Int = -1

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: NestedScrollView?, dependency: View?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout?, child: NestedScrollView?, ev: MotionEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(parent: CoordinatorLayout?, child: NestedScrollView?, ev: MotionEvent?): Boolean {
        return super.onTouchEvent(parent, child, ev)
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: NestedScrollView, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return true
    }


    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: NestedScrollView, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        //init
        if (bottom < 0) {
            bottom = coordinatorLayout.bottom
            childHeight = child.measuredHeight
            targetHeight = target.measuredHeight
        }
        println("childHeight = ${child.measuredHeight}")
        println("child.scrollY = ${child.scrollY}")
        println("computeVerticalScrollRange(child) = ${computeVerticalScrollRange(child)}")
        //layout
        if (child.measuredHeight+child.scrollY>=computeVerticalScrollRange(child)) {
            val lastBottom = bottom
            bottom = clamp(bottom - dy, 100, childHeight)
            val top = bottom - childHeight
            consumed[1] = bottom - lastBottom
            child.layout(0, top, child.right, bottom)
        }

    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: NestedScrollView, target: View, velocityX: Float, velocityY: Float): Boolean {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: NestedScrollView, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
    }

    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: NestedScrollView, target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout?, child: NestedScrollView?, dependency: View?) {
        super.onDependentViewRemoved(parent, child, dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: NestedScrollView?, dependency: View?): Boolean {
        return super.onDependentViewChanged(parent, child, dependency)
    }

    private fun isAtBottom(parent: CoordinatorLayout?, child: NestedScrollView?, ev: MotionEvent?) {
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
}