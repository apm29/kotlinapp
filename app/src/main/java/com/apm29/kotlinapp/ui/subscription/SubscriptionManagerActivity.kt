package com.apm29.kotlinapp.ui.subscription

import android.content.Context
import android.content.Intent
import com.apm29.beanmodule.Init.SubscriptionInfo
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseListActivity
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.ui.account.LoginActivity
import com.apm29.network.ApiCall
import com.apm29.network.api.API
import com.apm29.network.cache.AccountCache
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SubscriptionManagerActivity : BaseListActivity<SubscriptionInfo, SubscriptionManagerActivity.SubscriptionPresenter>() {
    override fun getResID(): Int = R.layout.item_subscription_manager_list_layout

    override fun convertView(helper: BaseHolder?, item: SubscriptionInfo) {
        helper?.setText(R.id.tv_name, item.toString())
    }

    companion object {
        fun starter(context: Context) {
            if (AccountCache.userInfo == null) {
                LoginActivity.starter(context)
            } else {
                context.startActivity(Intent(context, SubscriptionManagerActivity::class.java))
            }
        }
    }

    override fun getPresenter(): SubscriptionPresenter {
        return SubscriptionPresenter(this)
    }

    class SubscriptionPresenter(ui: BaseUI) : BaseListActivity.ListPresenter(ui) {
        override fun loadData(): Disposable {
            return fetchMySubscription()
        }

        private fun fetchMySubscription(): Disposable {
            return ApiCall.mainService(ui as Context)
                    .create(API.Subscription::class.java)
                    .fetchMySubscription(AccountCache.userInfo!!.userID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { ui.onNewData(it.data) },
                            {
                                ui.stopLoading()
                                ui.onError(it.toString())
                            },
                            {
                                //ui.stopLoading()
                            },
                            {
                                ui.startLoading()
                            }
                    )
        }
    }
}

