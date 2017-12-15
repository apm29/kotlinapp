package com.apm29.kotlinapp

import org.junit.Test

/**
 * Created by yingjiawei on 2017/9/16.
 */
class KtTest{
    @Test
    fun main() {
        println("start test")
    }
}
class Task {
    object INSTANCE{
        var instance=Task()
    }
    val time :String by lazy {
        System.currentTimeMillis().toString()
    }
    @Test
    fun main() {
        println(INSTANCE.instance.time)
        println(INSTANCE.instance.time)
        println(INSTANCE.instance)
        println(INSTANCE.instance)

    }
}