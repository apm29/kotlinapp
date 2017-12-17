package com.apm29.guideview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

@SuppressLint("ViewConstructor")
/**
 * 背景layout              Donnie Darko
 * Created by yingjiawei on 2017/12/16.
 */
class DarkoLayout(private var controller:NightVeil.Controller, context: Context?, attrs: AttributeSet?): RelativeLayout(context, attrs){

    var visions: ArrayList<Focus>?= controller.focusList
    var isAdded=false
    var background:Int = controller.backgroundColor
    var mPaint:Paint=Paint(Paint.ANTI_ALIAS_FLAG)
    init {
        this.mPaint.isAntiAlias = true
        val xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        this.mPaint.xfermode = xfermode
        this.mPaint.maskFilter = BlurMaskFilter(10.0f, BlurMaskFilter.Blur.INNER)
        this.setLayerType(1, null as Paint?)
        isClickable=true
        this.setOnTouchListener { v, ev ->
            println("NightVeil "+"ev = ${ev.x}")
            println("NightVeil "+"ev = ${ev.y}")
            if (ev.action==MotionEvent.ACTION_UP) {
                visions?.forEach {
                    println("NightVeil "+"rectF = ${it.rectF}")
                    if (ev != null && ev.x > it.rectF.left && ev.x < it.rectF.right && ev.y > it.rectF.top && ev.y < it.rectF.bottom) {
                        if (it.hitFocusListener?.onHit(it) == true || it.hitFocusListener == null) {
                            it.controller.remove()
                        }
                    }
                }
                if (controller.cancelable) {//如果layout是可以随处点击取消的就不拦截事件，直接remove全部Darko布局，移除Veil
                    controller.remove()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener true
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }
//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        visions?.forEach {
//            if (ev!=null&&ev.x >it.rectF.left&&ev.x <it.rectF.right&&ev.y>it.rectF.top&&ev.y<it.rectF.bottom) {
//                val onHit = it.hitFocusListener?.onHit(it)
//                if (onHit==true||it.hitFocusListener==null){
//                    it.controller.remove()
//                }
//            }
//        }
//        if (controller.cancelable){//如果layout是可以随处点击取消的就不拦截事件，直接remove全部Darko布局，移除Veil
//            controller.remove()
//            return false
//        }
//        return true
//    }
    override fun dispatchDraw(canvas: Canvas?)  {

        canvas?.drawColor(background)
        visions?.forEach {
            it.getRectF(context)
            when(it.type){

                Focus.TYPE.RECTANGULAR->canvas?.drawRoundRect(it.rectF,it.radius,it.radius,mPaint)
                Focus.TYPE.OVAL->canvas?.drawOval(it.rectF,mPaint)
                Focus.TYPE.CIRCLE->canvas?.drawCircle(it.rectF.centerX(),it.rectF.centerY(),getRadius(it.rectF),mPaint)
            }
            println(it.rectF.toString())

        }
        super.dispatchDraw(canvas)
        invalidate()
    }

    private fun getRadius(rectF: RectF): Float {
        return Math.min(rectF.height(),rectF.width())/2
    }

    fun setController(controller: NightVeil.Controller) {
        this.controller=controller
    }
}