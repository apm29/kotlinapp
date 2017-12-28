package com.apm29.kotlinapp.view.pager

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val list = inflater.inflate(R.layout.dummy_list,container,false)
        val dummyList = list.findViewById<RecyclerView>(R.id.dummy_list)
        dummyList.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        dummyList.adapter=object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
                return ViewHolder(TextView(parent?.context).also {
                    it.text="1234"
                    it.textSize=80F
                })
            }

            override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            }

            override fun getItemCount(): Int {
                return 28
            }

        }

        return list
    }
    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        //
    }
}