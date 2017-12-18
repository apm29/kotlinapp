package com.example.test

import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by yingjiawei on 2017/12/10.
 */
class  MainTest{
    val propA:String by lazy{

        return@lazy getString()
    }

    fun getString(): String {
        return System.currentTimeMillis().toString()
    }

    var propB :String by Delegates.observable("",this::onChange)

    fun onChange(prop:KProperty<*>, old: String, new: String) {
        println("prop = [${prop}], old = [${old}], new = [${new}]")
    }
    fun onVeto(prop: KProperty<*>,old: String,new: String):Boolean{
        println("prop = [${prop}], old = [${old}], new = [${new}]")
        return new=="ccc"
    }

    var propC:String by Delegates.vetoable("123",this::onVeto)


    var propD:List<String>? by Delegate

    object Delegate : ReadWriteProperty<MainTest, List<String>?> {
        override fun setValue(thisRef: MainTest, property: KProperty<*>, value: List<String>?) {
            println("thisRef = [${thisRef}], property = [${property}], value = [${value}]")

        }

        override fun getValue(thisRef: MainTest, property: KProperty<*>): List<String>? {
            println("thisRef = [${thisRef}], property = [${property}]")
            return "12,122,asd".split(",")
        }

    }

    @Test
    fun main() {
//        assert(getString()!="")
//
//        propD= arrayListOf("12","21","mm")
//        println(propD)
//        Singer(object :Sing{
//            override fun sing() {
//                println("sing by delegate")
//            }
//        }).sing()
        var sdf =SimpleDateFormat("yyyy-MM-dd")
        println("addTime "+sdf.format(Date(1513217824000)))
        println("modifyTime "+sdf.format(Date(1513217824000)))
        println("orderTime "+sdf.format(Date(1513217824000)))
        println("showTime "+sdf.format(Date(1513217824000)))
        println("sortTime "+sdf.format(Date(1513785600000)))

    }

    interface Sing{
        fun sing(): Unit {

        }
    }
    class Singer(delegate:Sing) :Sing by delegate{

    }
}
class Utils {
    object INSTANCE{
        var instance=Utils()
    }
    val time :String by lazy {
        System.currentTimeMillis().toString()
    }
    @Test
    fun test(): Unit {
        println(INSTANCE.instance.time)
        println(INSTANCE.instance.time)
        println(INSTANCE.instance)
        println(INSTANCE.instance)
        assert("123".equals(time))

    }
}