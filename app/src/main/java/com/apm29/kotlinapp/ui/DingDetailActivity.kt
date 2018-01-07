package com.apm29.kotlinapp.ui

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.apm29.guideview.Focus
import com.apm29.guideview.NightVeil
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.utils.showToast
import com.apm29.kotlinapp.view.drag.AutoDragLayout
import com.apm29.kotlinapp.view.pager.LazyViewPager
import com.apm29.kotlinapp.view.pager.PagerFragment
import me.kaelaela.verticalviewpager.transforms.DefaultTransformer

class DingDetailActivity : BaseActivity<BasePresenter>() {
    override val enableRefresh: Boolean
        get() {
            return false
        }

    override fun onNewData(data: Any?) {
    }

    override fun getDefaultLayout(): Int {
        return R.layout.activity_ding_detail
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        findViewById<AutoDragLayout>(R.id.drag_layout).setNextPageListener(object : AutoDragLayout.ShowNextPageNotifier {
            override fun onDragNext() {
                baseRefreshLayout.isEnableRefresh=false
            }
        }).setPrePageListener(object :AutoDragLayout.ShowPrePageNotifier{
            override fun onDragPre() {
                baseRefreshLayout.isEnableRefresh=true
            }
        })


        val pager = findViewById<ViewPager>(R.id.pager)
        val taber = findViewById<TabLayout>(R.id.taber)
        pager.offscreenPageLimit = 3
        pager.adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view ==`object`
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val text = TextView(this@DingDetailActivity)
                text.setText(R.string.app_desc)
                container.addView(text)
                return  text
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }

            override fun getCount(): Int {
                return 4
            }
        }
        pager.setPageTransformer(false, DefaultTransformer())
        taber.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                pager.currentItem = taber.selectedTabPosition
            }
        })
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val tab = taber.getTabAt(position)
                tab?.select()
            }

        })
    }

    override fun getPresenter(): BasePresenter {
        return BasePresenter(this)
    }

}
