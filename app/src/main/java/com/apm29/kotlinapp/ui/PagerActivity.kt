package com.apm29.kotlinapp.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.apm29.guideview.Focus
import com.apm29.guideview.NightVeil
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.utils.showToast
import com.apm29.kotlinapp.view.pager.LazyViewPager
import com.apm29.kotlinapp.view.pager.PagerFragment
import kotlinx.android.synthetic.main.activity_home_layout.*

class PagerActivity : BaseActivity<PagerActivity.PagerPresenter>() {
    override fun onError(error: String?) {
    }

    override fun  onNewData(data: Any?) {
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
            var showCount = 1
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val tab = taber.getTabAt(position)
                tab?.select()
                if (position < taber.tabCount - 1 && showCount < taber.tabCount - 1) {
                    NightVeil.from("pager-" + (position+1), this@PagerActivity)
                            .setBackgroundColor(Color.parseColor("#772f2f2f"))
                            .addFocus(Focus((taber.getChildAt(0) as ViewGroup).getChildAt(position + 1), object : Focus.HitFocusListener {
                                override fun onHit(focus: Focus): Boolean {
                                    focus.view?.performClick()
                                    return true
                                }
                            }))
                            .setUnveilingListener(object : NightVeil.UnveilingListener {
                                override fun onUnveiling(controller: NightVeil.Controller) {
                                    showToast("click position :" + controller.controllerTag )
                                }
                            })
                            .show()
                    showCount++
                } else {
                    NightVeil.removeAllController(this@PagerActivity)
                }
            }

        })
        NightVeil.from("pager-1", this@PagerActivity)
                .setBackgroundColor(Color.parseColor("#772f2f2f"))
                .addFocus(Focus((taber.getChildAt(0) as ViewGroup).getChildAt(1), object : Focus.HitFocusListener {
                    override fun onHit(focus: Focus): Boolean {
                        focus.view?.performClick()
                        return true
                    }
                }))
                .setUnveilingListener(object : NightVeil.UnveilingListener {
                    override fun onUnveiling(controller: NightVeil.Controller) {
                        showToast("click position :" + controller.controllerTag )
                    }
                })
                .show()

    }

    override fun getPresenter(): PagerPresenter {
        return PagerPresenter(this)
    }

    companion object {
        fun starter(context: Context) {
            context.startActivity(Intent(context, PagerActivity::class.java))
        }
    }

    class PagerPresenter(baseUI: BaseUI) : BasePresenter(baseUI)
}
