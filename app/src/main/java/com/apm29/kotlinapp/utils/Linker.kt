package com.apm29.kotlinapp.utils

import android.content.Context
import android.content.Intent
import com.apm29.kotlinapp.ui.SampleMaterialActivity
import com.apm29.kotlinapp.ui.GankIOListActivity
import com.apm29.kotlinapp.ui.PagerActivity

/**
 * Created by dingzhu on 2017/12/21.
 */
fun toGankIO(context: Context) {
    context.startActivity(Intent(context, GankIOListActivity::class.java))
}
fun toMaterialBase(context: Context) {
    context.startActivity(Intent(context, SampleMaterialActivity::class.java))
}
fun toPager(context: Context) {
    context.startActivity(Intent(context, PagerActivity::class.java))
}