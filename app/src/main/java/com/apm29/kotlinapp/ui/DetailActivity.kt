package com.apm29.kotlinapp.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter

class DetailActivity : BaseActivity<BasePresenter>() {
    override fun enableRefresh(): Boolean {
        return false
    }
    override fun onNewData(data: Any?) {
    }

    override fun getDefaultLayout(): Int {
        return R.layout.activity_detail
    }

    override fun setupViews(savedInstanceState: Bundle?) {
    }

    override fun getPresenter(): BasePresenter {
        return BasePresenter(this)
    }

}
