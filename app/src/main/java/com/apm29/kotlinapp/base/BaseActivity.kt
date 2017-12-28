package com.apm29.kotlinapp.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.utils.SystemBarTintManager
import com.apm29.kotlinapp.utils.showToast
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity<T : BasePresenter> : AppCompatActivity(), BaseUI {
    var statusBarHeight = 0
    var actionBarHeight = 0
    open var drawStatusBar = false
    open var showStatusBar = true
    override var mDisposables:CompositeDisposable= CompositeDisposable()
    protected val baseEmptyContainer: FrameLayout by lazy {
        findViewById<FrameLayout>(R.id.fl_empty_container)
    }
    protected val baseContainer: RelativeLayout by lazy {
        findViewById<RelativeLayout>(R.id.rl_base_container)
    }
    protected val baseLoadingContainer: RelativeLayout by lazy {
        findViewById<RelativeLayout>(R.id.rl_loading_container)
    }
    protected val baseRefreshContainer: RelativeLayout by lazy {
        findViewById<RelativeLayout>(R.id.rl_base_refresh_container)
    }
    protected val baseRefreshLayout: SmartRefreshLayout by lazy {
        findViewById<SmartRefreshLayout>(R.id.srl_refresh_layout)
    }
    protected val ivLoading: ImageView by lazy {
        findViewById<ImageView>(R.id.iv_base_loading)
    }
    protected  var handler=Handler()

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (!mDisposables.isDisposed)
            mDisposables.dispose()
    }

    protected lateinit var mPresenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!showStatusBar)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        //绑定P
        mPresenter = getPresenter()
        ////转场动画
        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        //    window.enterTransition = Explode()
        //    window.reenterTransition = Slide(Gravity.TOP)
        //    window.exitTransition = Slide(Gravity.TOP)
        //    window.returnTransition = Slide(Gravity.BOTTOM)
        //}
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
        layoutInflater.inflate(layoutResID, baseRefreshContainer, true)
        super.setContentView(baseContainer)
    }

    open protected fun setupActionBar(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        initSystemBar()
    }


    /**
     * 设置系统状态栏颜色
     * setSystemUiVisibility(int visibility)方法可传入的实参为：

    1. View.SYSTEM_UI_FLAG_VISIBLE：显示状态栏，Activity不全屏显示(恢复到有状态栏的正常情况)。

    2. View.INVISIBLE：隐藏状态栏，同时Activity会伸展全屏显示。

    3. View.SYSTEM_UI_FLAG_FULLSCREEN：Activity全屏显示，且状态栏被隐藏覆盖掉。

    4. View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。

    5. View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

    6. View.SYSTEM_UI_LAYOUT_FLAGS：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

    7. View.SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键。

    8. View.SYSTEM_UI_FLAG_LOW_PROFILE：状态栏显示处于低能显示状态(low profile模式)，状态栏上一些图标显示会被隐藏。
     */
    private fun initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = resources.getColor(R.color.color_status_bar)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true)
        }
        val tintManager = SystemBarTintManager(this)
        statusBarHeight = tintManager.config.statusBarHeight
        actionBarHeight = tintManager.config.actionBarHeight
        if (!showStatusBar){//不显示statusBar时略过设置
            return
        }
        tintManager.isStatusBarTintEnabled = true
        setStatusBarDarkMode(true, this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //window.statusBarColor = Color.parseColor("#6a2f2f2f")  //这里动态修改颜色
            window.statusBarColor = resources.getColor(R.color.color_status_bar)

            if (drawStatusBar)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View
                        .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else //不绘制statusBar区域
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            tintManager.setStatusBarTintResource(R.color.color_status_bar)
            if (!drawStatusBar) {
                baseContainer.setPadding(0, statusBarHeight, 0, 0)
            }

        }


    }


     /**
     * 为miui设置状态栏颜色
     * @param darkmode 是否黑色
     * @param activity 当前Activity
     */
     @SuppressLint("PrivateApi")
    private fun setStatusBarDarkMode(darkmode: Boolean, activity: Activity) {
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
        baseRefreshLayout.isEnableRefresh = enableRefresh()
        baseRefreshLayout.isEnableLoadmore = enableLoadMore()
        baseRefreshLayout.setOnRefreshListener {
            onStartPullLoad(baseRefreshLayout)
        }
    }
    open protected fun enableRefresh(): Boolean {
        return true
    }

    open protected fun enableLoadMore(): Boolean {
        return false
    }

    open protected fun onStartPullLoad(srlRefreshLayout: SmartRefreshLayout) {
        this.baseRefreshLayout.finishRefresh(200, true)
    }

    @LayoutRes abstract fun getDefaultLayout(): Int

    abstract fun setupViews(savedInstanceState: Bundle?)


    abstract fun getPresenter(): T

    override fun startLoading() {
        if (!enableRefresh()) {
            baseEmptyContainer.visibility = View.GONE
            baseLoadingContainer.visibility = View.VISIBLE
            val rotateAnimation = RotateAnimation(
                    0f, 360f,
                    RotateAnimation.RELATIVE_TO_SELF,
                    0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f
            )
            rotateAnimation.duration = 300
            rotateAnimation.repeatCount = RotateAnimation.INFINITE
            ivLoading?.startAnimation(rotateAnimation)
        }else{
            baseRefreshContainer.visibility = View.VISIBLE
            baseEmptyContainer.visibility=View.GONE
            baseLoadingContainer.visibility = View.GONE
            baseRefreshLayout.autoRefresh(300)
        }
    }

    override fun stopLoading() {
        if (enableRefresh()) {
            handler.postDelayed({
                baseEmptyContainer.visibility = View.GONE
                baseLoadingContainer.visibility = View.GONE
                baseRefreshLayout.finishRefresh(300)
                baseRefreshLayout.finishLoadmore(300)
            },300)
        } else {
            baseRefreshContainer.visibility = View.VISIBLE
            baseEmptyContainer.visibility=View.GONE
            baseLoadingContainer.visibility = View.GONE
            baseRefreshLayout.finishRefresh(300)
            baseRefreshLayout.finishLoadmore(300)
        }
    }

    override fun onEmpty() {
        if (!enableRefresh()) {
            baseEmptyContainer.visibility = View.VISIBLE
            baseLoadingContainer.visibility = View.GONE
            baseRefreshLayout.visibility = View.GONE
            baseRefreshLayout.finishRefresh(300)
            baseRefreshLayout.finishLoadmore(300)
        }else{
            baseRefreshContainer.visibility = View.VISIBLE
            baseEmptyContainer.visibility=View.GONE
            baseLoadingContainer.visibility = View.GONE
            baseRefreshLayout.finishRefresh(300)
            baseRefreshLayout.finishLoadmore(300)
        }
    }

    override fun onError(error: String?) {
        showToast(error?:"加载失败")
        if (enableRefresh()) {
            handler.postDelayed({
                baseEmptyContainer.visibility = View.GONE
                baseLoadingContainer.visibility = View.GONE
                baseRefreshLayout.finishRefresh(300)
                baseRefreshLayout.finishLoadmore(300)
            },300)
        } else {
            baseRefreshContainer.visibility = View.VISIBLE
            baseEmptyContainer.visibility=View.GONE
            baseLoadingContainer.visibility = View.GONE
            baseRefreshLayout.finishRefresh(300)
            baseRefreshLayout.finishLoadmore(300)
        }
    }
}
