package com.apm29.beanmodule.Init


/**
 * Created by apm29 on 2017/9/5.
 */


data class Data(
		var skuNum: Int,// 17615
		var recommendWTB: List<WTB>,
		var isAdON: Int,// 1
		var ads: List<Ad>,

        var child: List<IndustryChild>
)
data class Meta(
        var code: Int,// 200
        var timestamp: String,// 1474596721
        var desc: String// 获取分类成功
)

data class HomeViewData(
        var meta: Meta,
        var data: Data
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
		var meta: Meta,
		var data: List<Data>
)
data class IndustryChild(
        var id: Int,// 0
        var name: String,// 纯棉纱
        var child: List<IndustryChild>,
        var craftType: Int// 1
)
