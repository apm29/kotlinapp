package com.apm29.kotlinapp.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.Toolbar
import android.view.Window
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI

/**
 * 带Toolbar和Menu
 */
open class BaseMaterialActivity<P : BasePresenter> : BaseActivity<P>() {

    override fun onNewData(data: Any?) {
    }

    override fun getDefaultLayout() = R.layout.activity_base_material

    override fun setupViews(savedInstanceState: Bundle?) {
        findViewById<Toolbar>(R.id.tool_bar).also {
            setSupportActionBar(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        window.addFlags(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun getPresenter(): P = MaterialDefaultPresenter(this) as P
    open class MaterialDefaultPresenter(ui: BaseUI) : BasePresenter(ui)
}