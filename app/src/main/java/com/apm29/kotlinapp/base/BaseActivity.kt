package com.apm29.kotlinapp.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.transition.Explode
import android.transition.Slide
import android.view.*
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.utils.SystemBarTintManager
import com.scwang.smartrefresh.layout.SmartRefreshLayout

abstract class BaseActivity<T : BasePresenter> : AppCompatActivity(), BaseUI {
    var statusBarHeight = 0
    var actionBarHeight = 0
    open var drawStatusBar = false
    protected val flEmptyContainer: FrameLayout by lazy {
        findViewById<FrameLayout>(R.id.fl_empty_container)
    }
    protected val rlBaseContainer: RelativeLayout by lazy {
        findViewById<RelativeLayout>(R.id.rl_base_container)
    }
    protected val rlBaseLoadingContainer: RelativeLayout by lazy {
        findViewById<RelativeLayout>(R.id.rl_loading_container)
    }
    protected val rlBaseRefreshContainer: RelativeLayout by lazy {
        findViewById<RelativeLayout>(R.id.rl_base_refresh_container)
    }
    protected val srlRefreshLayout: SmartRefreshLayout by lazy {
        findViewById<SmartRefreshLayout>(R.id.srl_refresh_layout)
    }

    protected lateinit var mPresenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //绑定P
        mPresenter = getPresenter()
//        //转场动画
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            window.enterTransition = Explode()
//            window.reenterTransition = Slide(Gravity.LEFT)
//            window.exitTransition = Slide(Gravity.LEFT)
//            window.returnTransition = Slide(Gravity.RIGHT)
//        }
        //根布局
        super.setContentView(R.layout.activity_base_layout)
        //加入当前base布局
        setContentView(getDefaultLayout())
        //修改actionBar
        setupActionBar(savedInstanceState)
        //设置下拉刷新
        setupRefresh(savedInstanceState)
        //view初始化
        setupViews(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        layoutInflater.inflate(layoutResID, rlBaseRefreshContainer, true)
        super.setContentView(rlBaseContainer)
    }

    open protected fun setupActionBar(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        initSystemBar()
    }

    /**
     * 为miui设置状态栏颜色
     * @param darkmode 是否黑色
     * @param activity 当前Activity
     */
    fun setStatusBarDarkMode(darkmode: Boolean, activity: Activity) {
        val clazz = activity.window.javaClass
        try {
            var darkModeFlag = 0
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            extraFlagField.invoke(activity.window, if (darkmode) darkModeFlag else 0, darkModeFlag)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 设置系统状态栏颜色,设置手势监测器的回调[.onLeftFling]和[.onRightFling]
     */
    private fun initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true)
        }


        val tintManager = SystemBarTintManager(this)
        statusBarHeight = tintManager.config.statusBarHeight
        actionBarHeight = tintManager.config.actionBarHeight
        tintManager.setStatusBarTintEnabled(true)
        setStatusBarDarkMode(true, this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#6a2f2f2f")  //这里动态修改颜色
            if (drawStatusBar)//全屏，绘制被statusbar占据的部分
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View
                        .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else //不绘制statusbar区域
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            tintManager.setStatusBarTintResource(R.color.color_status_bar)
            if (!drawStatusBar){
                rlBaseContainer.setPadding(0,statusBarHeight,0,0)
            }

        }


    }

    @TargetApi(19)
    private fun setTranslucentStatus(on: Boolean) {
        val win = window
        val winParams = win.attributes
        val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    open protected fun setupRefresh(savedInstanceState: Bundle?) {
        srlRefreshLayout.isEnableRefresh = enableRefresh()
        srlRefreshLayout.setOnRefreshListener {
            onStartPullLoad(srlRefreshLayout)
        }
    }

    open protected fun enableRefresh(): Boolean {
        return true
    }

    open protected fun onStartPullLoad(srlRefreshLayout: SmartRefreshLayout) {
        this.srlRefreshLayout.finishRefresh(200, true)
    }

    @LayoutRes abstract fun getDefaultLayout(): Int

    abstract fun setupViews(savedInstanceState: Bundle?)

    abstract fun getPresenter(): T


    override fun startLoading() {
        flEmptyContainer.visibility = View.GONE
        rlBaseLoadingContainer.visibility = View.VISIBLE
        val tvLoading = findViewById<ImageView>(R.id.iv_base_loading)
        val rotateAnimation = RotateAnimation(
                0f, 360f,
                RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 300
        rotateAnimation.repeatCount = RotateAnimation.INFINITE
        tvLoading?.startAnimation(rotateAnimation)
    }

    override fun stopLoading() {
        flEmptyContainer.visibility = View.GONE
        rlBaseLoadingContainer.visibility = View.GONE
        srlRefreshLayout.finishRefresh(true)
    }

    override fun onEmpty() {
        flEmptyContainer.visibility = View.VISIBLE
        rlBaseLoadingContainer.visibility = View.GONE
        rlBaseContainer.visibility = View.GONE
        srlRefreshLayout.finishRefresh(false)
    }
}
