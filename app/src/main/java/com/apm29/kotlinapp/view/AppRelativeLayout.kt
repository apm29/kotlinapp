package com.apm29.kotlinapp.view

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.apm29.kotlinapp.utils.logD

/**
 * Created by dingzhu on 2017/12/22.
 */
@CoordinatorLayout.DefaultBehavior(AppRelativeLayout.Behavior::class)
class AppRelativeLayout(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    class Behavior : CoordinatorLayout.Behavior<AppRelativeLayout>() {

        private var mAppBarHeight=0
        override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppRelativeLayout, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
            //logD("onStartNestedScroll: axes = [${axes}], type = [${type}]")
            if(child.top==0){
                mAppBarHeight=child.bottom
            }
            return true
        }

        override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: AppRelativeLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
            logD("onNestedPreScroll: dx = [${dx}], dy = [${dy}], consumed = [${consumed[0]}:${consumed[1]}], type = [${type}]")
            if (type==0)
            if (child.bottom<=mAppBarHeight){
                child.layout(child.left,child.top-dy,child.right,child.bottom-dy)
            }else{
                child.layout(child.left,child.top,child.right,mAppBarHeight)
            }
        }

        override fun layoutDependsOn(parent: CoordinatorLayout?, child: AppRelativeLayout?, dependency: View?): Boolean {
            //logD("layoutDependsOn:parent = [${parent}], child = [${child}], dependency = [${dependency}]")
            return dependency is AppRelativeLayout
        }

        var scroll = true
        override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppRelativeLayout, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, @ViewCompat.NestedScrollType type: Int) {
            logD("onNestedScroll: dxConsumed = [${dxConsumed}], dyConsumed = [${dyConsumed}], dxUnconsumed = [${dxUnconsumed}], dyUnconsumed = [${dyUnconsumed}], type = [${type}]")

        }

        override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppRelativeLayout, target: View, type: Int) {
            //logD("onStopNestedScroll: type = [${type}]")
            super.onStopNestedScroll(coordinatorLayout, child, target, type)
        }

        override fun onDependentViewChanged(parent: CoordinatorLayout?, child: AppRelativeLayout?, dependency: View?): Boolean {
            return super.onDependentViewChanged(parent, child, dependency)
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
            // Else use the full height (minus the top inset)
            range += childHeight
        }
        return Math.max(0, range)
    }

    fun clamp(value: Int, min: Int, max: Int): Int {
        return if (value < min) min else if (value > max) max else value
    }
}