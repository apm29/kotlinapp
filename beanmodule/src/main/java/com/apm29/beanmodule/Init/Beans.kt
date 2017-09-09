package com.apm29.beanmodule.Init

import java.io.Serializable


/**
 * Created by apm29 on 2017/9/5.
 */


data class Data(
        var userID: Int,// 28
        var username: String,// 拉克丝
        var emchat: String// 40a1835bf16b
)
data class Meta(
        var code: Int,// 200
        var timestamp: String,// 1474596721
        var desc: String// 获取分类成功
)

data class HomeViewData(
        var skuNum: Int,// 17615
        var recommendWTB: List<WTB>,
        var isAdON: Int,// 1
        var ads: List<Ad>
)
//WTB
data class WTB(
		var id: Int,// 2993
		var name: String,// 包覆
		var branch: String,// 12S
		var craft: String,// 气流纺
		var weight: String,// 8吨
		var status: Int,// 1
		var isExpired: Int,// 0
		var arrival_place: String,// 湖南 长沙市
		var modified_time: String// 今日09:52
)
//HomeViewData
data class Ad(
		var id: Int,// 4
		var title: String,// AppBanner_test1
		var imgURL: String,// http://test.image.zhaosha.com/6f786e32989737805cc3d6ca3c33cd47.jpg
		var linkType: Int,// 1
		var linkURL: String// http://www.zhaosha.com
)
data class IndustryInfo(
		var id:Int,
		var name:String,
        var child: List<IndustryChild>
)
data class IndustryChild(
        var id: Int,// 0
        var name: String,// 纯棉纱
        var child: List<IndustryChild>,
        var craftType: Int// 1
)

data class LoginResult(
		var meta: Meta,
		var accessToken: String,// fcf72b97d3c5c010d4a38f1871e9a7bd
		var data: Data
)


data class UserInfo(
		var userID: Int,// 28
		var username: String,// 拉克是
		var mobile: String,// 18501090502
		var profile_picture: String,// http://test.image.zhaosha.com/358461d414ea8fa346b357c02d8a9881.jpg
		var score: Int,// 642
		var custom_home: List<Int>,
		var company_id: Int,// 27
		var company_name: String,// 宁波市江扬进出口有限公司
		var status: Int,// 1
		var major: String,// 亚麻,锦纶长丝,普梳
		var area_id: Int,// 1279
		var address: String,// 某某XX
		var fav_trade: Int,// 6
		var fav_wtb: Int,// 28
		var fav_shop: Int,// 20
		var anno_num: Int,// 0
		var duibaURL: String,// http://www.duiba.com.cn/autoLogin/autologin?uid=28&credits=642&appKey=44716nTA5BdiqpzzwQuYJCBf931b&sign=7e0b18da3e79b87c8abcca3d71c71ee4&timestamp=1483620759000
		var type: Int,// 1
		var city: String,// 常州市 天宁区
		var cerCompanyInfo: CerCompanyInfo
):Serializable

data class CerCompanyInfo(
		var staff_type: Int,// 0
		var isCertified: Int,// 11
		var staffStatus: Int// 0
)

data class SubscriptionInfo(
		var categoryid: Int,// 18
		var name: String,// 特种工艺纱
		var industryids: List<Industryid>,
		var industryidName: String,// 金银丝,AB,反捻平,股线,其它花式纱,金银丝,金银丝
		var hasNewWTB: Int// 0
)

data class Industryid(
		var industryid: Int,// 494
		var name: String// 金银丝
)
