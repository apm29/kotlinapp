package com.apm29.kotlinapp.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.apm29.beanmodule.beans.zs.LoginResult
import com.apm29.beanmodule.beans.zs.UserInfo
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.network.ApiCall
import com.apm29.network.api.API
import com.apm29.network.cache.AccountCache
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : BaseActivity<LoginActivity.LoginPresenter>() {
    override var drawStatusBar=true
    // UI references.
    private var mEmailView: AutoCompleteTextView? = null
    private var mPasswordView: EditText? = null
    private var mProgressView: View? = null
    private var mLoginFormView: View? = null
    private val mLoginButton: Button? by lazy {
        return@lazy (findViewById<Button>(R.id.email_sign_in_button))
    }

    override fun getPresenter(): LoginPresenter = LoginPresenter(this)
    override fun getDefaultLayout(): Int {
        return R.layout.activity_login
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        // Set up the login form.
        mEmailView = findViewById(R.id.email)
        mPasswordView = findViewById(R.id.password)
        mLoginFormView = findViewById(R.id.login_form)
        mProgressView = findViewById(R.id.login_progress)

        mLoginButton?.setOnClickListener {
            disposableLogin = mPresenter.login(mEmailView?.text.toString().trim(), mPasswordView?.text.toString().trim())
        }
    }

    override fun onError(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    var disposableInit: Disposable? = null
    var disposableLogin: Disposable? = null
    override fun onNewData(data: Any?) {
        if (data is LoginResult) {
            if (data.accessToken != null) {
                Toast.makeText(this, " login success ! ", Toast.LENGTH_SHORT).show()
                disposableInit = mPresenter.initUserInfo(data.data.userID)
            } else {
                onError(data.meta.desc)
            }
        } else if (data is UserInfo) {
            //AccountCache.saveUserInfo(this, data)
            Toast.makeText(this, " init user info success ! ", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        //取消订阅
        disposableInit?.dispose()
        disposableLogin?.dispose()
    }

    companion object {
        fun starter(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
//                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context as Activity?).toBundle())
//            else
                context.startActivity(intent)
        }
    }

    class LoginPresenter(ui: BaseUI) : BasePresenter(ui) {
        fun login(mobile: String, password: String): Disposable? {
            return ApiCall.mainApi(ui as Context)
                    .create(API.Login::class.java)
                    .login(mobile, password, 0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                ui.onNewData(it)
                            },
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

        fun initUserInfo(userID: Int): Disposable? {
            return ApiCall.mainApi(ui as Context)
                    .create(API.Login::class.java)
                    .initUserInfo(userID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                //AccountCache.saveUserInfo(ui as Context, it.data)
                                ui.onNewData(it.data)
                            },
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
}



