package com.apm29.kotlinapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.utils.*
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionNo
import com.yanzhenjie.permission.PermissionYes


const val requestCode=998
class SplashActivity : BaseActivity<SplashActivity.SplashPresenter>() {

    override fun onError(error: String?) {
        showToast(error ?: "加载失败")
    }

    override fun onNewData(data: Any?) {
    }

    override fun getDefaultLayout() = R.layout.activity_splash

    override fun setupViews(savedInstanceState: Bundle?) {
        findViewById<TextView>(R.id.tv_gank).also { logD(it.toString()) }.setOnClickListener{
            toGankIO(this)
        }
        findViewById<TextView>(R.id.tv).also {logD(it.toString())}.setOnClickListener{
            toMaterialBase(this)
        }
    }

    override fun getPresenter(): SplashPresenter = SplashPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mPresenter.getPermissions( Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE)
    }

    // 成功回调的方法，用注解即可，这里的300就是请求时的requestCode。
    @PermissionYes(requestCode)
    private fun getPermissionYes(grantedPermissions: List<String>) {
        // 申请权限成功。
        println("grantedPermissions = [${grantedPermissions}]")
    }

    @PermissionNo(requestCode)
    private fun getPermissionNo(deniedPermissions: List<String>) {
        //申请权限失败。
        println("deniedPermissions = [${deniedPermissions}]")
    }
    class SplashPresenter(ui: BaseUI) : BasePresenter(ui){
        fun getPermissions(vararg permissions:String) {
            AndPermission.with(ui as Activity)
                    .requestCode(requestCode)
                    .permission(permissions)
                    .rationale{
                        requsetCode,rationale->
                        // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(ui as Context, rationale).show()
                    }
                    .callback(ui)
                    .start()
        }
    }
}
