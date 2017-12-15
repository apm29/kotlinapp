package com.apm29.kotlinapp.view.pager

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.apm29.kotlinapp.R
import kotlinx.android.synthetic.main.activity_pager.*
import kotlinx.android.synthetic.main.pager.*

/**
 * Created by yingjiawei on 2017/12/12.
 */
class PagerFragment : Fragment() {
    var position: Int = -1
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        println("create view" + position)
        val inflate = LayoutInflater.from(container?.context).inflate(R.layout.pager, container, false)
        return inflate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        position = arguments.getInt("data")
        super.onCreate(savedInstanceState)
        println("create " + position)
    }

    val handler: Handler = Handler()
    override fun onResume() {
        super.onResume()
        tv.text = "text set by ktAndroidExt"
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser)
            handler.postDelayed({
                println("new " + position)
                val textView = view?.findViewById(R.id.tv) as TextView?
                textView?.text = getString(R.string.string_pager_data)
            }, 1000)
        else
            handler.removeCallbacksAndMessages(null)
    }
}