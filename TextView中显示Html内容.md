## 在TextView中显示Html格式的文字/图片/超链接

### 简单显示网页形式内容
* Html有fromHtml(String, ImageGetter, TagHandler)等多个方法将String对象转换为Spanned对象,而Spanned可以显示各种Span定义的颜色/行为等
* TextView的setMovementMethod(MovementMethod movement)方法可以设置自动处理url形式的链接,但是默认是跳转浏览器

### 图片显示
* 图片显示需要自己实现Html.ImageGetter类,实现 getDrawable方法把url转换为Drawable返回(这边用了Future/Callable方式请求网络)
```
private fun getDrawable(it: String): Drawable? {
       if (it.isEmpty()) {
           return ColorDrawable()
       }
       var drawable: Drawable? = null
       var future: Callable<Drawable>
       try {
           future = Callable {
               val url = URL(it)
               Drawable.createFromStream(url.openStream(), "")  //获取网路图片
           }
           drawable = executors.submit(future).get()
           drawable.setBounds(0, 0, getWindowWidth() - 200, (getWindowWidth() - 200) / drawable.intrinsicWidth * drawable
                   .intrinsicHeight)//这边吧图片拉伸到屏幕宽度了
       } catch (e: Exception) {
           return ColorDrawable()
       }

       return drawable
   }
```
### 处理自定义的超链接/图片 点击事件
* 先取出TextView中的Spannable(之前用Html.from方法设置的),取出不同的span,自己试了下,图片设置的是ImageSpan,超链接是UrlSpan,
  先取出两个span数组,再把原spannable全部清空Span,然后再把自己定义的Span设置回去(比如ClickableSpan实现类)
  因为清除了所有span,所以这里把图片的Span又设置回去
```
private fun setUrlClickSpan(tv: TextView?) {
        val text = tv?.text
        if (text is Spannable) {
            val urlSpans = text.getSpans(0, text.length, URLSpan::class.java)//取出原有的UrlSpan
            val imgSpans = text.getSpans(0, text.length, ImageSpan::class.java)//取出原有的UrlSpan
            val newSpanStyle = SpannableStringBuilder(text)
            newSpanStyle.clearSpans()
            imgSpans.forEach {
                Log.d("imgSpan", it::class.toString() + it.source)
                newSpanStyle.setSpan(ImageSpan(getDrawable(it.source)), text.getSpanStart(it), text.getSpanEnd(it), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            urlSpans.forEach {//设置
                Log.d("urlSpan", it::class.toString() + it.url)
                val clickSpan = ClickSpan(it.url, this)
                newSpanStyle.setSpan(clickSpan, text.getSpanStart(it), text.getSpanEnd(it), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            tv.text = newSpanStyle
        }
    }
```
### 自己定义Span,很简单,实现其中方法
```
class ClickSpan(var string: String, var context: Context) : URLSpan(string) {
       override fun onClick(widget: View?) {
           showToast(string)
       }
   }
```
