package com.apm29.kotlinapp.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.apm29.kotlinapp.R

abstract class  BaseActivity< T:BasePresenter> : AppCompatActivity(),BaseUI {

    protected val flEmptyContainer: FrameLayout by lazy {
        findViewById(R.id.fl_empty_container) as FrameLayout
    }
    protected val rlBaseContainer:RelativeLayout by lazy {
        findViewById(R.id.rl_base_container) as RelativeLayout
    }
    protected val rlBaseLoadingContainer:RelativeLayout by lazy {
        findViewById(R.id.rl_loading_container) as RelativeLayout
    }

    protected lateinit var mPresenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //绑定P
        mPresenter=getPresenter()

        super.setContentView(R.layout.activity_base_layout)
        //加入当前base布局
        setContentView(getDefaultLayout())
        onViewAdded()
    }

    @LayoutRes abstract fun getDefaultLayout():  Int

    abstract fun onViewAdded()

    abstract fun   getPresenter(): T

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutInflater.inflate(layoutResID,rlBaseContainer,true))
    }

    override fun startLoading() {
        flEmptyContainer.visibility=View.GONE
        rlBaseLoadingContainer.visibility=View.VISIBLE
        val tvLoading=findViewById(R.id.iv_base_loading)
        val rotateAnimation = RotateAnimation(
                0f, 360f,
                RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration=300
        rotateAnimation.repeatCount=RotateAnimation.INFINITE
        tvLoading?.startAnimation(rotateAnimation)
    }

    override fun stopLoading() {
        flEmptyContainer.visibility=View.GONE
        rlBaseLoadingContainer.visibility=View.GONE
    }

    override fun onEmpty() {
        flEmptyContainer.visibility=View.VISIBLE
        rlBaseLoadingContainer.visibility=View.GONE
        rlBaseContainer.visibility=View.GONE
    }
}
