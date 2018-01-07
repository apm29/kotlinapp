package com.apm29.kotlinapp.ui

import android.os.Bundle
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.view.XWebView


/**
 * 新增一个Dollars页面,方便聊天...
 */
class DollarsActivity : BaseActivity<BasePresenter>() {
    override var showStatusBar: Boolean = false
    override val enableRefresh get() = false
    override fun onNewData(data: Any?) {
    }

    override fun getDefaultLayout(): Int {
        return R.layout.activity_dollars
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        findViewById<XWebView>(R.id.web_view).loadUrl("http://drrr.com/")

    }

    override fun getPresenter(): BasePresenter {
        return BasePresenter(this)
    }


}
