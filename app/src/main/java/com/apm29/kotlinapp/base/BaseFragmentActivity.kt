package com.apm29.kotlinapp.base

import android.os.Bundle
import android.support.v4.app.Fragment
import com.apm29.kotlinapp.R

/**
 * fragment容器Activity,集成一些公共方法,动画等
 */
class BaseFragmentActivity : BaseActivity<BaseFragmentActivity.FragmentPresenter>() {
    override fun onNewData(data: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(error: String?) {

    }

    override fun getDefaultLayout(): Int {
        return  R.layout.activity_base_fragment
    }

    override fun setupViews(savedInstanceState: Bundle?) {
    }



    override fun getPresenter(): FragmentPresenter {
        return FragmentPresenter(this)
    }
    class FragmentPresenter(ui:BaseFragmentActivity):BasePresenter(ui){
        fun addFragment(f: BaseFragment): Unit {

        }
    }

    class BaseFragment :Fragment(){

    }
}
