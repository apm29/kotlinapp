package com.apm29.kotlinapp.ui.ding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.apm29.beanmodule.beans.ding.ActivityPopupDetail
import com.apm29.beanmodule.beans.ding.AppConfig
import com.apm29.beanmodule.beans.ding.StartupPage
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.utils.*
import com.bigkoo.pickerview.OptionsPickerView
import com.bigkoo.pickerview.TimePickerView
import com.bumptech.glide.Glide
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_ding_splash.*
import java.util.*
import android.content.Intent.ACTION_VIEW
import android.net.Uri


class DingSplashActivity : BaseActivity<DingSplashActivity.Presenter>() {
    override var showStatusBar: Boolean = false
    override val enableRefresh get() = false
    override fun onNewData(data: Any?) {
        if (data is AppConfig) { //appConfig
            if (data.activityPopupSwitch != 0 && data.activityPopup != null) {//有弹窗
                mPresenter.activityPopupMessage(data.activityPopup?.id ?: 0)
            }
        } else if (data is ActivityPopupDetail) {
            //Glide.with(this).load(BASE_IMG_URL+data.image).into(iv_ding)
        } else if (data is StartupPage) {
            val now = System.currentTimeMillis()
            if (data.endTime > now && now > data.startTime)
                Glide.with(this).load(BASE_IMG_URL + data.image).into(iv_ding)
            enterNextPage()
        }
    }

    override fun onError(error: String?) {
        super.onError(error)
        enterNextPage()
    }

    private fun enterNextPage() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (getBoolean(IS_FIRST_INSTALL_APP,true)){//第一次打开app
                //进入新手引导
                toSudoku(this,DingScreenLockActivity.SudokuType.FIRST_SET_PASS)
            }else{
                if (TextUtils.isEmpty(getString(SCREEN_LOCK_PASS,""))){//未设置密码
                    //进入主页
                    toSudoku(this,DingScreenLockActivity.SudokuType.FIRST_SET_PASS)
                }else{
                    //进入锁屏界面
                    toSudoku(this,DingScreenLockActivity.SudokuType.ENTER_MAIN)
                }
            }
            finish()
        },2000)
    }

    override fun startLoading() {
        //do nothing
    }

    override fun getDefaultLayout(): Int {
        return R.layout.activity_ding_splash
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        mPresenter.queryAppConfig()
        mPresenter.getStartupPage()
    }

    private fun showOptionsPicker() {
        val opPicker = OptionsPickerView.Builder(this, OptionsPickerView.OnOptionsSelectListener { options1, options2, options3, v ->
            showToast(options1.toString())
        }).setCyclic(true, true, true).build()
        opPicker.setPicker(arrayListOf("充值", "提现", "投资", "回款", "系统"))
        opPicker.show()
    }

    private fun showTimePicker() {
        //时间选择器
        val pvTime = TimePickerView.Builder(this, TimePickerView.OnTimeSelectListener { date, v ->
            showToast(date.toString())
            showOptionsPicker()
        }).setType(booleanArrayOf(true, true, false, false, false, false)).build()
        pvTime.setDate(Calendar.getInstance())
        //注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show()
    }

    override fun getPresenter(): Presenter {
        return Presenter(this)
    }

    class Presenter(ui: DingSplashActivity) : BasePresenter(ui) {
        fun queryAppConfig(): Disposable {
            return DingTasks.queryAppConfig(ui as Context, ui)
        }

        fun activityPopupMessage(id: Int): Disposable {
            return DingTasks.activityPopupMessage(id, ui as Context, ui)
        }

        fun getStartupPage(): Disposable {
            return DingTasks.getStartupPage(ui as Context, ui)
        }

        fun checkToken(): Disposable {
            return DingTasks.checkToken(ui as Context, ui)
        }
    }
}
