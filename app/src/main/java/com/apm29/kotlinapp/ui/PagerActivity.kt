package com.apm29.kotlinapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.apm29.guideview.NightVeil
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.view.pager.LazyViewPager
import com.apm29.kotlinapp.view.pager.PagerFragment
import kotlinx.android.synthetic.main.activity_home_layout.*

class PagerActivity : BaseActivity<PagerActivity.PagerPresenter>() {
    override fun onError(error: String?) {
    }

    override fun <N> onNewData(data: N) {
    }

    override fun getDefaultLayout(): Int {
        return R.layout.activity_pager
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        val pager = findViewById<LazyViewPager>(R.id.pager)
        val taber = findViewById<TabLayout>(R.id.taber)
        pager.offscreenPageLimit = 3
        pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                val pagerFragment = PagerFragment()
                val bundle = Bundle()
                bundle.putInt("data", position)
                pagerFragment.arguments = bundle
                return pagerFragment
            }


            override fun getCount(): Int {
                return 4
            }
        }
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
                //这时不会show，因为NightVeil中controller和Activity有对应关系
                NightVeil.show("btnLogin", this@PagerActivity)
            }

        })
    }

    override fun getPresenter(): PagerPresenter {
        return  PagerPresenter(this)
    }

    companion object {
        fun starter(context: Context) {
            context.startActivity(Intent(context, PagerActivity::class.java))
        }
    }

    class PagerPresenter(baseUI: BaseUI):BasePresenter(baseUI)
}
