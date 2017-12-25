package com.apm29.kotlinapp.ui

import android.os.Bundle
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter

/**
 * Created by dingzhu on 2017/12/25.
 */
class DingHomeActivity :BaseActivity<DingHomeActivity.HomePresenter>() {
    override fun onNewData(data: Any?) {

    }

    override fun getDefaultLayout(): Int {
       return R.layout.activity_ding_home
    }

    override fun setupViews(savedInstanceState: Bundle?) {

    }

    override fun getPresenter(): HomePresenter {
        return HomePresenter(this)
    }


    class HomePresenter(ui : DingHomeActivity) :BasePresenter(ui){
//        fun loadHomeData(): Disposable {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
    }
}