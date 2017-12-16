package com.apm29.guideview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout

/**
 * 背景layout
 * Created by yingjiawei on 2017/12/16.
 */
class DarkoLayout(controller:NightVeil.Controller, context: Context?, attrs: AttributeSet?): RelativeLayout(context, attrs){

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

    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        visions?.forEach {
            if (ev!=null&&ev.x >it.rectF.left&&ev.x <it.rectF.right&&ev.y>it.rectF.top&&ev.y<it.rectF.bottom) {
                val onHit = it.hitFocusListener?.onHit(it)
                if (onHit==true||it.hitFocusListener==null){
                    it.controller.remove()
                }
            }
        }
        return true
    }
    override fun dispatchDraw(canvas: Canvas?)  {

        canvas?.drawColor(background)
        println("onDraw")
        visions?.forEach {
            it.getRectF()
            when(it.type){

                Focus.TYPE.RECTANGULAR->canvas?.drawRoundRect(it.rectF,it.radius,it.radius,mPaint)
                Focus.TYPE.OVAL->canvas?.drawOval(it.rectF,mPaint)
                Focus.TYPE.CIRCLE->canvas?.drawCircle(it.rectF.centerX(),it.rectF.centerY(),getRadius(it.rectF),mPaint)
            }
            println(it.rectF.toString())

        }
        super.dispatchDraw(canvas)
    }

    private fun getRadius(rectF: RectF): Float {
        return Math.min(rectF.height(),rectF.width())/2
    }
}