package com.apm29.guideview

import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.support.annotation.IdRes
import android.view.View

/**
 * 三种方式建立Focus与高亮区域的对应关系（三种构造）
 * 1）通过直接设置View
 * 2）通过设置BackgroundLayout中的ViewID
 * 3）通过设置RectF
 * Created by yingjiawei on 2017/12/16.
 */
class Focus(var view: View?,
            var hitFocusListener: HitFocusListener? = null,
            var type: TYPE = TYPE.RECTANGULAR,
            var radius: Float = 0F,
            var padding:Int = 0
) {


    var rectF: RectF

    init {
        rectF = RectF()
    }

    constructor(rectF: RectF, hitFocusListener: HitFocusListener? = null, type: TYPE = TYPE.RECTANGULAR, radius: Float = 0F,padding: Int=0) : this(null, hitFocusListener, type, radius,padding) {
        this.rectF = rectF
    }

    private var viewId: Int=0

    constructor(@IdRes viewId: Int, hitFocusListener: HitFocusListener? = null, type: TYPE = TYPE.RECTANGULAR, radius: Float = 0F,padding: Int=0) : this(null, hitFocusListener, type, radius,padding) {
        this.viewId=viewId
        if(view!=null)throw IllegalAccessError("每个Focus只能有一个对应的View、viewID")
    }


    fun getRectF(context: Context): Focus {
        val location = IntArray(2)
        if (view != null) {
            this.view!!.getLocationOnScreen(location)
            rectF.left = location[0].toFloat()-padding
            rectF.top = location[1].toFloat()-padding
            rectF.right = (location[0] + view!!.width).toFloat()+padding
            rectF.bottom = (location[1] + view!!.height).toFloat()+padding
        }
        else if (viewId!=0){
            if (context is Activity){
                val viewById = context.findViewById(viewId)
                viewById?.getLocationOnScreen(location)
                rectF.left = location[0].toFloat()-padding
                rectF.top = location[1].toFloat()-padding
                rectF.right = (location[0] + viewById.width).toFloat()+padding
                rectF.bottom = (location[1] + viewById.height).toFloat()+padding
            }
        }
        return this
    }
    fun setHitFocusListener(hitFocusListener: HitFocusListener?):Focus{
        this.hitFocusListener=hitFocusListener
        return  this
    }

    fun addTransformer(t: (Focus) -> Unit): Focus {
        t.invoke(this)
        return this
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