package com.apm29.kotlinapp.ui.subscription

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.ui.account.LoginActivity
import com.apm29.network.Network
import com.apm29.network.api.Subscription
import com.apm29.network.cache.AccountCache
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SubscriptionManagerActivity : BaseActivity<SubscriptionPresenter>() {
    companion object {
        fun starter(context: Context) {
            if (AccountCache.getUserInfo(context)==null){
                LoginActivity.starter(context)
            }else{
                context.startActivity(Intent(context,SubscriptionManagerActivity::class.java))
            }
        }
    }
    override fun onError(error: String?) {
        Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
    }

    override fun <N : Any?> onNewData(data: N) {
        if (data is List<*>)
        (findViewById(R.id.tv_subscription) as TextView).text=data.toString()
    }

    override fun getPresenter(): SubscriptionPresenter {
        return SubscriptionPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription_manager_layout)

        mPresenter.fetchMySubscription()
    }

}
class SubscriptionPresenter(ui: BaseUI?) : BasePresenter(ui){
    fun fetchMySubscription() {
        Network.mainService(ui as Context)
                .create(Subscription::class.java)
                .fetchMySubscription(AccountCache.userInfo!!.userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {ui.onNewData(it.data)},

                        {
                            ui.stopLoading()
                            ui.onError(it.toString())
                        },
                        {
                            ui.stopLoading()
                        },
                        {
                            ui.startLoading()
                        }
                )
    }
}
