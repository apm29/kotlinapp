# ktapp

### app in kotlin

* 12/11/2017
>加入了极光推送服务,测试可用,未继续深入(jenkins方式集成)

* 12/15/2017
>加入了kotlin-android-extensions,方便在activity中查找id对应的View
http://kotlinlang.org/docs/tutorials/android-plugin.html

* 12/16/2017
>新增了一套引导视图的库 NightVeil 1.0，主要使用NightVeil 、 DarkoLayout、Focus三个类，可以定义点击事件 和 显示类型
>新增了对自定义视图设置后的自定义操作的处理：addTransformer方法，可以定义Layout中包含的view的custom操作
### NightVeil usage
~~~
    val controller1
                   = NightVeil.from("btnLogin",this).addFocus(Focus(btnLogin!!, null, Focus.TYPE.OVAL))

           NightVeil
                   .from("btnSubscribe",this)
                   .addFocus(Focus(btnSubscribe!!, object : Focus.HitFocusListener {
                       override fun onHit(focus: Focus): Boolean {
                           focus.view?.performClick()
                           controller1.show()
                           focus.remoeSelf()
                           return false
                       }
                   }))
                   .addFocus(Focus(
                           RectF(400F,300F,600F,400F),
                           radius = 20F
                   ))
                   .addFocus(Focus(R.id.iv_logo,type = Focus.TYPE.CIRCLE,padding = 20))
                   .setBackgroundColorRes(R.color.guide_bg_color)
                   .setLayout(R.layout.activity_home_guide_layout)
                   .addTransformer {
                       val container=it.findViewById<ConstraintLayout>(R.id.cl_container)
                       val logo = it.findViewById<ImageView>(R.id.iv_logo)
                       val tv = it.findViewById<TextView>(R.id.tv_guide)
                       val arrow = it.findViewById<ImageView>(R.id.arrow)
                       val va=ValueAnimator.ofFloat(0F,200F)
                       va.addUpdateListener {
                           val transition = it.animatedValue as Float
                           logo.x=300+transition
                           logo.y=300+transition
                           arrow.x=logo.x-logo.measuredWidth-20
                           arrow.y=logo.y
                           tv.x=arrow.x-tv.measuredWidth-20
                           tv.y=logo.y+(logo.measuredHeight-tv.measuredHeight)/2
                       }
                       va.duration = 2000
                       va.repeatCount=20
                       va.repeatMode=ValueAnimator.REVERSE
                       va.start()
                   }
                   .show()
         //最好在退出应用或者结束全部引导后调用NightVeil的removeAllController方法移除contrller
~~~
## NightVeil 更新说明
> * NightVeil版本1.2，优化点击事件时机，新增Focus的build方法，新增Controller的addTransformer方法
> * NightVeil 版本1.3 存储结构优化，解决了退出应用后show相同tag的controller不显示的bug，修改了Controller的构造方法
> 将Activity的状态栏和动作条统一设置，添加统一的下拉刷新控件SmartRefreshLayout，编译版本升到v26，添加leakCanary（未完成）

## BaseActivity Usage
> * getPresenter返回对应P,与当前BaseUI绑定
> * getDefaultLayout 返回加入的布局文件
> * setupViews 初始化View操作
> * startLoading/stopLoading/onEmpty是可用被调用的方法,展示不同状态视图层


### 气泡seekbar，气泡随拉取速度偏移

![Alt text](https://github.com/apm29/zsktapp/blob/master/pictures/yjwgif.gif?raw=false)

### 带文字显示的progressBar，带进度增加动画

![Alt text](https://github.com/apm29/zsktapp/blob/master/pictures/increasedProgressBar.gif?raw=false)
