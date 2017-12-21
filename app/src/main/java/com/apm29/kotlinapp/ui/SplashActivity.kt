package com.apm29.kotlinapp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.utils.*

class SplashActivity : BaseActivity<SplashActivity.SplashPresenter>() {

    override fun onError(error: String?) {
        showToast(error ?: "加载失败")
    }

    override fun onNewData(data: Any?) {
    }

    override fun getDefaultLayout() = R.layout.activity_splash

    override fun setupViews(savedInstanceState: Bundle?) {
        findViewById<TextView>(R.id.tv_gank).also { logD(it.toString()) }.setOnClickListener{
            toGankIO(this)
        }
        findViewById<TextView>(R.id.tv).also {logD(it.toString())}.setOnClickListener{
            toMaterialBase(this)
        }
    }

    override fun getPresenter(): SplashPresenter = SplashPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    class SplashPresenter(ui: BaseUI) : BasePresenter(ui)
}
