# ktapp

### app in kotlin

* 12/11/2017
>加入了极光推送服务,测试可用,未继续深入(jenkins方式集成)

* 12/15/2017
>加入了kotlin-android-extensions,方便在activity中查找id对应的View
http://kotlinlang.org/docs/tutorials/android-plugin.html

## BaseActivity Usage
> * getPresenter返回对应P,与当前BaseUI绑定
> * getDefaultLayout 返回加入的布局文件
> * onViewAdded 初始化View操作
> * startLoading/stopLoading/onEmpty是可用被调用的方法,展示不同状态视图层

* 12/16/2017
>新增了一套引导视图的库 guideView，主要使用NightVeil 、 DarkoLayout、Focus三个类，可以定义点击事件 和 显示类型

### 气泡seekbar

![Alt text](https://github.com/apm29/zsktapp/blob/master/pictures/yjwgif.gif?raw=false)

### 文字显示的progressBar

![Alt text](https://github.com/apm29/zsktapp/blob/master/pictures/increasedProgressBar.gif?raw=false)
