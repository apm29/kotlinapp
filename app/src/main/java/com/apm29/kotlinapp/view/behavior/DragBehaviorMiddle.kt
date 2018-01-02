package com.apm29.kotlinapp.view.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View

/**
 * Created by yingjiawei on 2017/12/29.
 */
class DragBehaviorMiddle(context: Context, attributeSet: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attributeSet) {
    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        child.layout(0, parent.measuredHeight - child.measuredHeight, parent.right, parent.bottom)
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        var dependent = false
        if (Math.abs(parent.indexOfChild(child) - parent.indexOfChild(dependency)) == 1) {
            dependent = true
        }
        return dependent
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if ((parent.indexOfChild(child) - parent.indexOfChild(dependency)) == 1)
            child.layout(0, dependency.bottom, dependency.right, dependency.bottom + child.measuredHeight)
        return true
    }
}