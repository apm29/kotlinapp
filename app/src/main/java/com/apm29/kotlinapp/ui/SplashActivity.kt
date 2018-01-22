package com.apm29.kotlinapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.apm29.beanmodule.beans.ding.ProjectBean
import com.apm29.beanmodule.beans.ding.ProjectCategory
import com.apm29.kotlinapp.R
import com.apm29.kotlinapp.base.BaseActivity
import com.apm29.kotlinapp.base.BasePresenter
import com.apm29.kotlinapp.base.BaseUI
import com.apm29.kotlinapp.ui.adapter.HeaderRVAdapter
import com.apm29.kotlinapp.utils.*
import com.bumptech.glide.Glide
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionNo
import com.yanzhenjie.permission.PermissionYes


class SplashActivity : BaseActivity<SplashActivity.SplashPresenter>() {
    override var drawStatusBar: Boolean = false
    override val enableRefresh: Boolean = false
    override fun onError(error: String?) {
        showToast(error ?: "加载失败")
    }
    override fun onNewData(data: Any?) {
        if (data is ArrayList<*>){
            val arrayList = data as ArrayList<ProjectCategory>
            val titles= Array<String>(arrayList.size,{
                return@Array arrayList[it].categoryName
            })
            val emptyList = emptyList<ProjectBean>()
            val mainData = Array<List<ProjectBean>>(arrayList.size,{
                return@Array arrayList[it].spList ?: emptyList
            })
            adapter = object : HeaderRVAdapter<ProjectBean>(
                    titles,mainData
            ) {
                override fun getHeaderHolder(parent: ViewGroup?): BaseHolder {
                    val view = LayoutInflater.from(this@SplashActivity).inflate(R.layout.list_item_1, parent, false)
                    return DefaultHeaderHolder(view)
                }

                override fun getItemHolder(parent: ViewGroup?): BaseHolder {
                    return DefaultItemHolder(LayoutInflater.from(this@SplashActivity).inflate(R.layout.list_item_1,parent,false))
                }

            }
            recyclerView.adapter=adapter
        }
    }
    lateinit var recyclerView:RecyclerView
    lateinit var adapter:HeaderRVAdapter<ProjectBean>
    override fun getDefaultLayout() = R.layout.activity_splash

    private val imgUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1515994821421&di=58a28998e0ecd9f.gif"

    @SuppressLint("ClickableViewAccessibility")
    override fun setupViews(savedInstanceState: Bundle?) {
        findViewById<TextView>(R.id.tv_gank).setOnClickListener {
            toGankIO(this)
        }
        findViewById<TextView>(R.id.tv_one_api).setOnClickListener {
            toMaterialBase(this)
        }
        findViewById<TextView>(R.id.tv_ding).setOnClickListener {
            toDing(this)
        }
        //toDetail(this)
        findViewById<TextView>(R.id.tv_dollars).setOnClickListener {
            toDollars(this)
        }
        val iv = findViewById<ImageView>(R.id.iv_glide)
        Glide.with(this).load(imgUrl).override(300, 300).into(iv)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)


    }


    override fun getPresenter(): SplashPresenter = SplashPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mPresenter.getPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
        mPresenter.getProgressing()
    }

    // 成功回调的方法，用注解即可，这里的300就是请求时的requestCode。
    @PermissionYes(PERMISSION_REQUEST_CODE)
    private fun getPermissionYes(grantedPermissions: List<String>) {
        // 申请权限成功。
        println("grantedPermissions = [${grantedPermissions}]")
    }

    @PermissionNo(PERMISSION_REQUEST_CODE)
    private fun getPermissionNo(deniedPermissions: List<String>) {
        //申请权限失败。
        println("deniedPermissions = [${deniedPermissions}]")
    }

    class SplashPresenter(ui: BaseUI) : BasePresenter(ui) {
        fun getPermissions(vararg permissions: String) {
            AndPermission.with(ui as Activity)
                    .requestCode(PERMISSION_REQUEST_CODE)
                    .permission(permissions)
                    .rationale { requsetCode, rationale ->
                        // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(ui as Context, rationale).show()
                    }
                    .callback(ui)
                    .start()
        }
        fun getProgressing(){
            DingTasks.queryInProgressProjects( ui as Context,ui)
        }
    }
}
