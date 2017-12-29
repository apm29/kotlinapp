package com.apm29.kotlinapp.view.behavior

import android.animation.ArgbEvaluator
import android.animation.FloatEvaluator
import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.utils.logD
import com.apm29.kotlinapp.utils.px

/**
 * Created by dingzhu on 2017/12/22.
 */
@CoordinatorLayout.DefaultBehavior(AppRelativeLayout.MyBehavior::class)
class AppRelativeLayout(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    class MyBehavior() : CoordinatorLayout.Behavior<AppRelativeLayout>() {


        constructor(context: Context?,attrs: AttributeSet?) : this()

        override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppRelativeLayout, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
            var start=false
            if (type==ViewCompat.TYPE_TOUCH&&axes==ViewCompat.SCROLL_AXIS_VERTICAL){
                start=true
            }
            //logD("onStartNestedScroll: start = $start")
            return start
        }

        override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: AppRelativeLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
//            child.layout(target.left?:0,0,   target?.right ?: 0,  target?.topView?:0)
            logD("onNestedPreScroll")
        }

        override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppRelativeLayout, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
            logD("onStopNestedScroll")
        }

        override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppRelativeLayout, target: View, type: Int) {
            super.onStopNestedScroll(coordinatorLayout, child, target, type)
            logD("onStopNestedScroll")
        }

        override fun onTouchEvent(parent: CoordinatorLayout?, child: AppRelativeLayout?, ev: MotionEvent?): Boolean {
            return super.onTouchEvent(parent, child, ev)
        }

        override fun onMeasureChild(parent: CoordinatorLayout?, child: AppRelativeLayout?, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {
            return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
        }

        override fun onInterceptTouchEvent(parent: CoordinatorLayout?, child: AppRelativeLayout?, ev: MotionEvent?): Boolean {
            return super.onInterceptTouchEvent(parent, child, ev)
        }

        override fun onDependentViewChanged(parent: CoordinatorLayout?, child: AppRelativeLayout?, dependency: View?): Boolean {
            println("dependency?.topView = ${dependency?.top}")
                if (child!=null){
                    child.x=0f
                    child.y=0f+(dependency?.top?: 0).toFloat()-child.measuredHeight
                }
                return true

        }

        override fun layoutDependsOn(parent: CoordinatorLayout?, child: AppRelativeLayout?, dependency: View?): Boolean {
            return dependency is RecyclerView
        }
    }

    /**
     * Returns the scroll range of all children.
     *
     * @return the scroll range in px
     */
    fun getTotalScrollRange(): Int {
        var range = 0
        var i = 0
        val z = childCount
        while (i < z) {
            val child = getChildAt(i)
            val lp = child.layoutParams as LayoutParams
            val childHeight = child.measuredHeight

            range += childHeight + lp.topMargin + lp.bottomMargin

            i++
        }
        return Math.max(0, range)
    }

    internal fun getDownNestedPreScrollRange(): Int {

        var range = 0
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            val lp = child.layoutParams as LayoutParams
            val childHeight = child.measuredHeight

            // First take the margin into account
            range += lp.topMargin + lp.bottomMargin
            // The view has the quick return flag combination...
            // If they're set to enter collapsed, use the minimum height
            //range += ViewCompat.getMinimumHeight(child)
            // Only enter by the amount of the collapsed height
            //range += childHeight - ViewCompat.getMinimumHeight(child)
            // Else use the full height (minus the topView inset)
            range += childHeight
        }
        return Math.max(0, range)
    }


}