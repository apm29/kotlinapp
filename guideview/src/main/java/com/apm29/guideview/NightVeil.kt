package com.apm29.guideview

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.KeyCharacterMap
import android.view.LayoutInflater
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout

/**
 * Created by yingjiawei on 2017/12/16.
 */
class NightVeil{

    
    companion object {
        fun from(context: Activity): Controller {
            return Controller(context)
        }
        fun getScreenHeight(context: Context): Int {
            val dm = context.resources.displayMetrics
            return dm.heightPixels
        }

        fun getStatusBarHeight(context: Context): Int {
            var height = 38
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                height = context.resources.getDimensionPixelSize(resourceId)
            }

            Log.e("NightVeil", "状态栏的高度:" + height)
            return height
        }

        fun isNavigationBarShow(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 17) {
                val display = activity.windowManager.defaultDisplay
                val size = Point()
                val realSize = Point()
                display.getSize(size)
                display.getRealSize(realSize)
                realSize.y != size.y
            } else {
                val menu = ViewConfiguration.get(activity).hasPermanentMenuKey()
                val back = KeyCharacterMap.deviceHasKey(4)
                !menu && !back
            }
        }

        fun getNavigationBarHeight(activity: Activity): Int {
            return if (!isNavigationBarShow(activity)) {
                0
            } else {
                var height = 0
                val resources = activity.resources
                val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    height = resources.getDimensionPixelSize(resourceId)
                }

                Log.e("NightVeil", "NavigationBar的高度:" + height)
                height
            }
        }
    }


    class Controller(val context: Activity){
        var focusList:ArrayList<Focus> = ArrayList()
        var darko:DarkoLayout?=null
        @LayoutRes var layoutRes:Int=0
        var backgroundColor:Int=Color.parseColor("#882d2d2d")

        fun addFocus(focus: Focus): Controller {
            focus.controller=this
            focusList.add(focus)
            return this
        }
        fun setLayout(@LayoutRes layout: Int):Controller{
            layoutRes=layout
            return this
        }
        fun setBackgroundColor(@ColorInt color: Int):Controller{
            backgroundColor=color
            return this
        }
        fun show() {
            darko = DarkoLayout(this, context, null)
            if(layoutRes != 0){
                val params = RelativeLayout.LayoutParams(-1, -1)
                params.topMargin = getStatusBarHeight(this.context)
                params.bottomMargin = getNavigationBarHeight(this.context)
                val inflate = LayoutInflater.from(context).inflate(layoutRes, darko, false)
                inflate.layoutParams = params
                darko?.addView(inflate)
            }
            val viewGroup = context.window.decorView as FrameLayout
            viewGroup.addView(darko, android.widget.FrameLayout.LayoutParams(-1, -1))
            (darko as DarkoLayout).isAdded=true
        }

        fun remove() {
            val viewGroup = context.window.decorView as ViewGroup
            if (darko?.isAdded==false){
                throw Exception("Veil 还没有显示")
            }
            viewGroup.removeView(darko)
        }

    }

    
}