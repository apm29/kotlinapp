package com.apm29.guideview

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.KeyCharacterMap
import android.view.LayoutInflater
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout

/**
 * controller管理类
 * 管理着activity对应的controller
 * Created by yingjiawei on 2017/12/16.
 */
class NightVeil {


    companion object {
        private var controllersMap: HashMap<Class<out Activity>, ArrayList<Controller>> = HashMap()
        /**
         * 构造controller的方法
         */
        fun from(context: Activity): Controller {

            return Controller(context).also {
                val arrayList = controllersMap[context::class.java] ?: ArrayList()
                controllersMap.put(context::class.java,arrayList)
                arrayList.add(it)
            }
        }

        /**
         * 移除所有图层
         * @return 移除的个数
         */
        fun removeAllController(currentActivity: Activity): Int {
            return controllersMap[currentActivity::class.java]?.filter {
                it.remove()
            }?.count() ?: 0
        }

        /**
         * 移除单个controller图层
         */
        fun remove(controllerTag: String, currentActivity: Activity): Boolean {
            return controllersMap[currentActivity::class.java]?.find {
                it.controllerTag == controllerTag
            }.let { it?.remove() } ?: false
        }

        /**
         * 显示单个
         */
        fun show(tag: String,currentActivity: Activity): Boolean {
            return controllersMap[currentActivity::class.java]?.find {
                it.controllerTag == tag
            }.let { it?.show() } ?: false
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
            if (BuildConfig.DEBUG)
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
                if (BuildConfig.DEBUG)
                    Log.e("NightVeil", "NavigationBar的高度:" + height)
                height
            }
        }
    }


    class Controller(val context: Activity) {
        var focusList: ArrayList<Focus> = ArrayList()
        var darko: DarkoLayout? = null
        var controllerTag: String = ""
        @LayoutRes
        var layoutRes: Int = 0
        var cancelable: Boolean = false
        var isShow = false
        var backgroundColor: Int = Color.parseColor("#882d2d2d")
        var unveilingListener: NightVeil.UnveilingListener? = null
        fun addFocus(focus: Focus): Controller {
            focus.controller = this
            focusList.add(focus)
            return this
        }

        fun setLayout(@LayoutRes layout: Int): Controller {
            layoutRes = layout
            darko = DarkoLayout(this, context, null)
            if (layoutRes != 0) {
                val params = RelativeLayout.LayoutParams(-1, -1)
                params.topMargin = getStatusBarHeight(this.context)
                params.bottomMargin = getNavigationBarHeight(this.context)
                val inflate = LayoutInflater.from(context).inflate(layoutRes, darko, false)
                inflate.layoutParams = params
                darko?.addView(inflate)
            }
            return this
        }

        fun setControllerTag(tag: String): Controller {
            controllerTag = tag
            return this
        }


        fun setBackgroundColor(@ColorInt color: Int): Controller {
            backgroundColor = color
            return this
        }
        fun setBackgroundColorRes(@ColorRes color: Int): Controller {
            this.backgroundColor=context.resources.getColor(color)
            return this
        }

        fun setUnveilingListener(listener: UnveilingListener): Controller {
            this.unveilingListener = listener
            return this
        }

        fun setCancelableAnyWhere(cancelable: Boolean): Controller {
            this.cancelable = cancelable
            return this
        }

        fun show(): Boolean {
            try {
                if(darko==null){
                    darko= DarkoLayout(this,context,null)
                }
                val viewGroup = context.window.decorView as FrameLayout
                if (darko?.isAdded == false)
                    viewGroup.addView(darko, android.widget.FrameLayout.LayoutParams(-1, -1))
                (darko as DarkoLayout).isAdded = true
                isShow = true
            } catch (e: Exception) {
                isShow = false
            }

            return isShow
        }

        fun remove(): Boolean {
            val viewGroup = context.window.decorView as ViewGroup
            if (darko == null || darko?.isAdded == false) {
                if (BuildConfig.DEBUG)
                    Log.d("NightVeil", "Veil 还没有添加View或者view已经显示")
                return false
            }
            viewGroup.removeView(darko)
            darko?.isAdded=false
            isShow = false
            unveilingListener?.onUnveiling(this)
            return true
        }

    }

    /**
     * guide 层移除监听
     */
    interface UnveilingListener {
        fun onUnveiling(controller: Controller) {
            if (BuildConfig.DEBUG)
                Log.d("NightVeil", "controller removed")
        }
    }

}