package com.apm29.kotlinapp.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.apm29.kotlinapp.ui.*
import com.apm29.kotlinapp.ui.ding.DingScreenLockActivity
import com.apm29.kotlinapp.ui.ding.DingSplashActivity

/**
 * Created by dingzhu on 2017/12/21.
 */
fun toGankIO(context: Context) {
    context.startActivity(Intent(context, GankIOListActivity::class.java))
}

fun toMaterialBase(context: Context) {
    context.startActivity(Intent(context, SampleMaterialActivity::class.java))
}

fun toMain(context: Context) {
    context.startActivity(Intent(context, PagerActivity::class.java))
}

fun toDing(context: Context) {
    context.startActivity(Intent(context, DingSplashActivity::class.java))
}

fun toSudoku(context: Context, type: DingScreenLockActivity.SudokuType) {

    val intent = Intent(context, DingScreenLockActivity::class.java)
    intent.putExtra(SCREEN_LOCK_TYPE, type)
    context.startActivity(intent)
}

fun toDetail(context: Context) {
    context.startActivity(Intent(context, DetailActivity::class.java))
}