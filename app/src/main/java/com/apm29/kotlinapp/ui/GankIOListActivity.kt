package com.apm29.kotlinapp.ui

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.apm29.beanmodule.beans.gank.ResultsItem
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseListActivity
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.utils.getWindowWidth
import com.apm29.kotlinapp.utils.showToast
import com.apm29.network.ApiCall
import com.apm29.network.api.API
import com.apm29.network.api.GankAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GankIOListActivity : BaseListActivity<ResultsItem, GankIOListActivity.HomePresenter>() {
    companion object {
        val executors: ExecutorService = Executors.newFixedThreadPool(20)
    }

    private val imageGetter: Html.ImageGetter
            by lazy {
                return@lazy Html.ImageGetter {
                    return@ImageGetter getDrawable(it)
                }
            }

    private fun getDrawable(path: String): Drawable? {
        if (path.isEmpty()) {
            return ColorDrawable()
        }
        fixPath(path)
        val drawable: Drawable?
        val callable: Callable<Drawable>
        try {
            callable = Callable {
                val url = URL(path)
                Drawable.createFromStream(url.openStream(), "")  //获取网路图片
            }
            drawable = executors.submit(callable).get()
            drawable.setBounds(0, 0, getWindowWidth() - 200, (getWindowWidth() - 200) / drawable.intrinsicWidth * drawable
                    .intrinsicHeight)
        } catch (e: Exception) {
            return ColorDrawable()
        }

        return drawable
    }

    private fun fixPath(path: String) {
        if (path.contains("imageView")) {
            path.indexOfLast {
                return@indexOfLast it == '?'
            }.also {
                path.subSequence(it, path.length)
            }
        }
        path.plus("?imageView2/0/w/" + (getWindowWidth() - 300))
    }

    private val tagHandler: Html.TagHandler by lazy {
        Html.TagHandler { opening, tag, output, xmlReader ->
            //println("opening = [$opening], tag = [$tag], output = [$output], xmlReader = [$xmlReader]")
        }
    }

    override fun bindItemView(holder: BaseVH, position: Int, itemViewType: Int, item: ResultsItem?) {
        val getter: Html.ImageGetter = imageGetter
        if (holder is HomeVH) {
            holder.tvTitle?.text = item?.title
            holder.tvContent?.text = Html.fromHtml(item?.content, getter, tagHandler)
            holder.tvContent?.movementMethod = LinkMovementMethod.getInstance()
            setUrlClickSpan(holder.tvContent)
        }
    }

    override val enableLoadMore: Boolean
        get() {
            return true
        }

    private fun setUrlClickSpan(tv: TextView?) {
        val text = tv?.text
        if (text is Spannable) {
            val urlSpans = text.getSpans(0, text.length, URLSpan::class.java)//取出原有的UrlSpan
            val imgSpans = text.getSpans(0, text.length, ImageSpan::class.java)//取出原有的UrlSpan
            val newSpanStyle = SpannableStringBuilder(text)
            newSpanStyle.clearSpans()
            imgSpans.forEach {
                //Log.d("imgSpan", it::class.toString() + it.source)
                newSpanStyle.setSpan(ImageSpan(getDrawable(it.source)), text.getSpanStart(it), text.getSpanEnd(it), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                newSpanStyle.setSpan(ClickSpan(it.source, this), text.getSpanStart(it), text.getSpanEnd(it), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            urlSpans.forEach {
                //设置
                //Log.d("urlSpan", it::class.toString() + it.url)
                val clickSpan = ClickSpan(it.url, this)
                newSpanStyle.setSpan(clickSpan, text.getSpanStart(it), text.getSpanEnd(it), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            tv.text = newSpanStyle
        }
    }

    override fun BaseAdapter.getHolder(parent: ViewGroup?, viewType: Int, inflate: View): BaseVH {
        return HomeVH(inflate)
    }

    override fun getResID(): Int {
        return R.layout.item_home_history_content
    }

    override fun getPresenter(): HomePresenter {
        return HomePresenter(this)
    }

    class ClickSpan(var string: String, var context: Context) : URLSpan(string) {
        override fun onClick(widget: View?) {
            showToast(string)
        }
    }

    class HomeVH(itemView: View?) : BaseVH(itemView) {
        var tvTitle: TextView? = itemView?.findViewById(R.id.tv_title_home_item)
        var tvContent: TextView? = itemView?.findViewById(R.id.tv_content_home_item)
    }

    class HomePresenter(ui: BaseUI) : BaseListActivity.ListPresenter(ui) {
        override fun loadData(): Disposable {
            return getDailyContent()
        }

        fun loadHomeViewData(): Disposable {
            return ApiCall.mainApi(ui as Context)
                    .create(API.Home::class.java)
                    .initHomeViewData()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .map {
                        if (it.meta?.code != 200) {
                            ui.onError(it.meta?.desc)
                        }
                        ui.stopLoading()
                        println(it)
                        return@map it
                    }
                    .subscribe(
                            {
                                println("result:" + it)
                                ui.onNewData(it.data)
                            },
                            {
                                println("error:" + it)
                                ui.stopLoading()
                                ui.onError(it.message)
                            },
                            {
                                println("complete")
                                ui.stopLoading()
                            },
                            {
                                println("onSubScribed")
                                ui.startLoading()
                            }
                    )

        }

        fun fetchIndustry(): Disposable {
            return ApiCall.mainApi((ui as Activity))
                    .create(API.Init::class.java)
                    .fetchIndustryCategory()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe()
        }

        fun getDailyContent(): Disposable {
            return ApiCall.gankApi(ui as Context)
                    .create(GankAPI::class.java)
                    .getContent(1, page = (ui as GankIOListActivity).page)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            {
                                println("result:" + it)
                                if (it.error)
                                    ui.onError("请求失败")
                                else
                                    ui.onNewData(it.results)
                            },
                            {
                                println("error:" + it)
                                ui.stopLoading()
                                ui.onError(it.message)
                            },
                            {
                                println("complete")
                                ui.stopLoading()
                            },
                            {
                                println("onSubScribed")
                                ui.startLoading()
                            }
                    )
        }
    }
}

