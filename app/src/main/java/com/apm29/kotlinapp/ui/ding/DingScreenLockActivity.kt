package com.apm29.kotlinapp.ui.ding

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.utils.*
import com.apm29.kotlinapp.view.lock.SudokuView
import com.apm29.network.cache.AccountCache
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_ding_screen_lock.*
import java.io.Serializable

class DingScreenLockActivity : BaseActivity<DingScreenLockActivity.SudokuPresenter>() {

    sealed class SudokuType(val type: Int) : Serializable {
        // 1 第一次启动设置密码 2 重设手势密码 3 输入正确手势之后跳转到相应页面 4 遮挡当前页面 5设置/关闭密码
        object FIRST_SET_PASS : SudokuType(1)

        object RESET_PASS : SudokuType(2)
        object ENTER_MAIN : SudokuType(3)
        object LOCK_PAGE : SudokuType(4)
        object SET_OR_CLOSE : SudokuType(5)
    }

    lateinit var lockType: SudokuType
    lateinit var lockPass: String

    override fun enableRefresh() = false
    override var showStatusBar = false
    override fun onNewData(data: Any?) {
    }

    override fun getDefaultLayout() = R.layout.activity_ding_screen_lock

    override fun setupViews(savedInstanceState: Bundle?) {
        lockType = intent.extras[SCREEN_LOCK_TYPE] as SudokuType
        lockPass = getString(SCREEN_LOCK_PASS, "")
        showToast(lockType.type.toString())

        initViews()
        setViewListener()
    }


    private fun initViews() {
        if (AccountCache.isLogin()) {//头像
            Glide.with(this).load(BASE_IMG_URL + AccountCache.userInfo?.avatar).into(iv_app)
        }
        when (lockType.type) {
            SudokuType.FIRST_SET_PASS.type -> {
                // 开启手势或者第一次登录设置手势
                tv_desc.text = "请输入手势密码"
                tv_tip_right.visibility = View.GONE
                tv_skip.text = "跳过"
                tv_skip.visibility = View.VISIBLE
                tv_tip_left.text = ""
                tv_tip_left.visibility = View.GONE
            }
            SudokuType.RESET_PASS.type -> {
                // 2 重设手势密码
                tv_desc.text = "请输入原始密码"
                tv_tip_right.text = "请输入原始密码"
                tv_tip_right.visibility = View.GONE
                tv_skip.visibility = View.GONE
                tv_tip_left.visibility = View.GONE
            }
            SudokuType.ENTER_MAIN.type -> {
                // 3 输入正确手势之后跳转到相应页面
                tv_desc.text = "请输入手势密码"
                tv_tip_right.visibility = View.VISIBLE
                tv_tip_right.text="登录其他账号"
                tv_skip.visibility = View.GONE
                tv_tip_left.visibility = View.VISIBLE
                tv_tip_left.text="管理手势密码"
            }
            SudokuType.LOCK_PAGE.type -> {
                // 4 遮挡当前页面
                tv_desc.text = "请输入手势密码"
                tv_tip_right.visibility = View.GONE
                tv_skip.visibility = View.GONE
                tv_tip_left.visibility = View.GONE
            }
            SudokuType.SET_OR_CLOSE.type -> {
                // 设置/关闭密码
                tv_desc.text = "请输入手势密码"
                tv_tip_right.visibility = View.GONE
                tv_skip.visibility = View.GONE
                tv_tip_left.visibility = View.GONE
            }
        }
    }

    private var pass1: String? = null
    private var pass2: String? = null

    private var errorCount: Int = 0

    private var lockStr: String? =null

    private fun setViewListener() {
        tv_skip.setOnClickListener {
            toMain(this)
        }
        sudoku_view.setOnLockFinishListener(object : SudokuView.OnLockFinishListener {
            override fun finish(lockString: StringBuilder) {
                lockStr=getString(SCREEN_LOCK_PASS,"")
                val set = lockString.toString()
                if (set.length <= 3) {
                    tv_desc.text = "至少连接4个点"
                    sudoku_view.mInvalidate()
                    return
                }
                when (lockType.type) {
                    SudokuType.FIRST_SET_PASS.type// 第一次是设置密码
                    -> if (pass1 == null) {

                        tv_desc.text = "请再次输入手势密码"
                        tv_tip_left.text = "重新设置手势"
                        pass1 = md5(set)
                        sudoku_view.mInvalidate()
                    } else {
                        pass2 = md5(set)
                        if (pass2 == pass1) {
                            tv_desc.text = "设置手势密码成功"
                            putString(SCREEN_LOCK_PASS, set)
                            toMain(this@DingScreenLockActivity)
                            this@DingScreenLockActivity.finish()
                            sudoku_view.mInvalidate()
                        } else {
                            pass1 = null
                            tv_desc.text = "两次输入不一致，请重新输入"
                            sudoku_view.error()
                        }
                    }
                    SudokuType.RESET_PASS.type// 2 重设手势密码
                    -> if (md5(set) != lockStr) {
                        tv_desc.text = "原始密码输入不正确"
                        sudoku_view.error()
                    } else {
                        tv_desc.text = "请输入手势密码"
                        lockType = SudokuType.FIRST_SET_PASS
                        sudoku_view.mInvalidate()
                    }
                    SudokuType.ENTER_MAIN.type// 3 输入正确手势之后跳转到相应页面
                        , SudokuType.LOCK_PAGE.type // 4 遮挡当前页面
                    -> if (md5(set) != lockStr) {
                        tv_desc.text = "输入密码错误"
                        errorCount++
                        sudoku_view.error()
                    } else {
                        if (lockType == SudokuType.ENTER_MAIN) {
                            toMain(this@DingScreenLockActivity)
                        }
                        this@DingScreenLockActivity.finish()
                        sudoku_view.mInvalidate()
                    }
                    SudokuType.SET_OR_CLOSE.type// 设置/关闭密码
                    -> if (TextUtils.isEmpty(lockStr)) {// 设置手势密码
                        if (set.isEmpty()) {
                            tv_desc.text = "请再次输入手势密码"
                            tv_tip_left.text = "重新设置手势"
                            pass1 = md5(set)
                            sudoku_view.mInvalidate()
                        } else {
                            pass2 = md5(set)
                            if (pass2 == set) {
                                tv_desc.text = "设置手势密码成功"
                                putString(SCREEN_LOCK_PASS, set)
                                this@DingScreenLockActivity.finish()
                                sudoku_view.mInvalidate()
                            } else {
                                pass1 = ""
                                tv_desc.text = "两次输入不一致，请重新输入"
                                sudoku_view.error()
                            }
                        }
                    } else { // 关闭密码
                        if (md5(set) != lockStr) { // 输入密码和原密码不一样
                            tv_desc.text = "输入密码错误"
                            sudoku_view.error()
                        } else {// 关闭密码
                            putString(SCREEN_LOCK_PASS, "")
                            toMain(this@DingScreenLockActivity)
                            this@DingScreenLockActivity.finish()
                            sudoku_view.mInvalidate()
                        }
                    }

                }
                sudoku_view.mInvalidate()
            }
        })
    }

    override fun getPresenter() = SudokuPresenter(this)

    class SudokuPresenter(ui: BaseUI) : BasePresenter(ui) {
        //
    }
}
