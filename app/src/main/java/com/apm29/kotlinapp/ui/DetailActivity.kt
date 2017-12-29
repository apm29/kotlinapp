package com.apm29.kotlinapp.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.view.pager.DragLayout

class DetailActivity : BaseActivity<BasePresenter>() {
    override fun enableRefresh(): Boolean {
        return true
    }

    override fun onNewData(data: Any?) {
    }

    override fun getDefaultLayout(): Int {
        return R.layout.activity_detail
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        findViewById<DragLayout>(R.id.drag_layout)
                .setPrePageListener {
                    baseRefreshLayout.isEnableRefresh = true
                }.setNextPageListener {
                    baseRefreshLayout.isEnableRefresh = false
                }
    }

    override fun getPresenter(): BasePresenter {
        return BasePresenter(this)
    }

}
