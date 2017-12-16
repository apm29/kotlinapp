package com.apm29.guideview

import android.graphics.RectF
import android.view.View

/**
 * Created by yingjiawei on 2017/12/16.
 */
class Focus(var view: View?,
            var hitFocusListener: HitFocusListener? = null,
            var type: TYPE = TYPE.RECTANGULAR,
            var radius: Float = 0F
) {


    var rectF: RectF

    init {
        rectF = RectF()
    }

    constructor(rectF: RectF, hitFocusListener: HitFocusListener? = null, type: TYPE = TYPE.RECTANGULAR, radius: Float = 0F) : this(null, hitFocusListener, type, radius) {
        this.rectF = rectF
    }


    fun getRectF() {
        if (view != null) {
            val location = IntArray(2)
            this.view!!.getLocationOnScreen(location)
            rectF.left = location[0].toFloat()
            rectF.top = location[1].toFloat()
            rectF.right = (location[0] + view!!.width).toFloat()
            rectF.bottom = (location[1] + view!!.height).toFloat()
        }
    }

    /**
     * 点击监听
     * @return 是否移除当前Focus
     */
    open interface HitFocusListener {
        fun onHit(focus: Focus): Boolean {
            println("hit " + focus.view)
            focus.view?.performClick()
            return true
        }
    }

    enum class TYPE {
        RECTANGULAR, CIRCLE, OVAL
    }

    lateinit var controller: NightVeil.Controller
}