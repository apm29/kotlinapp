package com.apm29.kotlinapp.view.behavior

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.support.design.widget.CoordinatorLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.apm29.kotlinapp.utils.logD
import com.apm29.kotlinapp.utils.px

/**
 * Created by yingjiawei on 2017/12/24.
 */
class HeaderBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<RelativeLayout>(context, attrs) {
    constructor(context: Context?) : this(context, null)

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: RelativeLayout?, dependency: View?): Boolean {
        println("dependency top = ${dependency?.top}")
//        if (dependency?.top?:0 > 0) {
            if (dependency?.top ?: 0 > 80.px())
                child?.layout(0,  0, dependency?.right ?: 0, dependency?.top ?: 0)
            else
                child?.layout( 0, 0, dependency?.right ?: 0, 80.px())
            logD("left = ${child?.left}")
            logD("top = ${child?.top}")
            logD("right = ${child?.right}")
            logD("bottom = ${child?.bottom}")
            val image = child?.getChildAt(0)
            val w =image?.measuredWidth?:0.also { logD(it.toString()) }
            val h =image?.measuredHeight?:0.also { logD(it.toString()) }
            val ph = child?.bottom?:0.also { logD(it.toString()) }
            val pw = child?.right?:0.also { logD(it.toString()) }
            val padding =child?.paddingTop?:0
            image?.x= (pw/2-w/2).toFloat().also { logD(it.toString()) }
            image?.y= (ph/2-h/2 + padding).toFloat().also { logD(it.toString()) }
            val f = (child!!.bottom-80.px())/(child.measuredHeight -80.px()).toFloat()
            val color = ArgbEvaluator().evaluate(f, Color.parseColor("#99ffffff"), Color.parseColor("#00ffffff"))
            child.setBackgroundColor(color as Int)
            return true
//        }

//        return false
    }

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: RelativeLayout?, dependency: View?): Boolean {
        return dependency is RecyclerView
    }
}