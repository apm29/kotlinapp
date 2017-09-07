package com.apm29.kotlinapp.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.apm29.kotlinapp.R

abstract class  BaseActivity<P:BasePresenter> : AppCompatActivity(),BaseUI<BasePresenter> {

    val flEmptyContainer: FrameLayout by lazy {
        findViewById(R.id.fl_empty_container) as FrameLayout
    }
    val rlBaseContainer:RelativeLayout by lazy {
        findViewById(R.id.rl_base_container) as RelativeLayout
    }

    private lateinit var mPresenter: BasePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //绑定P
        mPresenter=getPresenter()

        super.setContentView(R.layout.activity_base_layout)

    }
    abstract fun   getPresenter(): BasePresenter

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutInflater.inflate(layoutResID,rlBaseContainer,true))
    }
}
