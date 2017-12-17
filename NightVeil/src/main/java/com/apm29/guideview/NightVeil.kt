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
import android.view.*
import android.widget.FrameLayout
import android.widget.RelativeLayout

/**
 * controller管理类
 * 管理着activity对应的controller
 * Created by yingjiawei on 2017/12/16.
 */
class NightVeil {


    companion object {
        private var controllersMap: HashMap<Class<out Activity>, HashMap<String,Controller>> = HashMap()
        /**
         * 构造controller的方法
         */
        fun from(tag:String,context: Activity): Controller {

            return Controller(tag,context).also {
                val map = controllersMap[context::class.java] ?: HashMap()
                controllersMap.put(context::class.java, map)
                map.put(it.controllerTag,it)

            }
        }

        fun removeAll(): Unit {
            controllersMap=HashMap()
        }

        /**
         * 移除所有图层
         * @return 移除的个数
         */
        fun removeAllController(currentActivity: Activity): Int {
            val i = controllersMap[currentActivity::class.java]
            return i?.keys?.filter { i[it]?.remove() == true }?.count()?:0
        }

        /**
         * 移除单个controller图层
         */
        fun remove(controllerTag: String, currentActivity: Activity): Boolean {
            return controllersMap[currentActivity::class.java]?.get(controllerTag).let { it?.remove() } ?: false
        }

        /**
         * 显示单个
         */
        fun show(tag: String, currentActivity: Activity): Boolean {
//            return controllersMap[currentActivity::class.java]?.find {
//                it.controllerTag == tag
//            }.let { it?.show() } ?: false
            return controllersMap[currentActivity::class.java]?.get (tag)?.show()?:false
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


    class Controller(var controllerTag: String,val context: Activity) :Comparable<Controller>{
        override fun compareTo(other: Controller): Int {
            if (this.controllerTag==other.controllerTag)return 0
            return 1
        }

        var focusList: ArrayList<Focus> = ArrayList()
        var darko: DarkoLayout? = null
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

        override fun hashCode(): Int {
            return 100
        }

        override fun equals(other: Any?): Boolean {
            return this.controllerTag.equals((other as Controller).controllerTag)
        }
//        fun setControllerTag(tag: String): Controller {
//            controllerTag = tag
//            return this
//        }


        fun setBackgroundColor(@ColorInt color: Int): Controller {
            backgroundColor = color
            return this
        }

        fun setBackgroundColorRes(@ColorRes color: Int): Controller {
            this.backgroundColor = context.resources.getColor(color)
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

        /**
         * 在setLayout之后调用，为视图添加一些自定义操作，比如动画
         * transform携带的View为DarkoLayout，其中的子View为setLayout后填充的自定义View
         */
        fun addTransformer(transformer: (View) -> Unit): Controller {
            darko?.let {
                transformer.invoke(it)
            } ?: throw IllegalAccessError("addTransformer方法调用时机在setLayout调用之后")
            return this
        }

        fun show(): Boolean {
            isShow = false
            try {
                if (darko == null) {
                    darko = DarkoLayout(this, context, null)
                } else
                    darko?.setController(this)
                val viewGroup = context.window.decorView as FrameLayout
                if (darko?.isAdded == false) {
                    viewGroup.removeView(darko)
                    viewGroup.addView(darko, android.widget.FrameLayout.LayoutParams(-1, -1))
                    (darko as DarkoLayout).isAdded = true
                    isShow = true
                }
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
            darko?.isAdded = false
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