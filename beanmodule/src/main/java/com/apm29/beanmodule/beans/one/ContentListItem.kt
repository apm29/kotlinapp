package com.apm29.beanmodule.beans.one

import com.google.gson.annotations.SerializedName

data class ContentListItem(@SerializedName("content_bgcolor")
                           val contentBgcolor: String = "",
                           @SerializedName("ad_type")
                           val adType: Int = 0,
                           @SerializedName("start_video")
                           val startVideo: String = "",
                           val title: String = "",
                           @SerializedName("pic_info")
                           val picInfo: String = "",
                           val number: Int = 0,
                           @SerializedName("video_url")
                           val videoUrl: String = "",
                           @SerializedName("content_type")
                           val contentType: String = "",
                           @SerializedName("serial_id")
                           val serialId: Int = 0,
                           val id: String = "",
                           @SerializedName("ad_closetime")
                           val adClosetime: String = "",
                           @SerializedName("last_update_date")
                           val lastUpdateDate: String = "",
                           val orientation: String = "",
                           @SerializedName("like_count")
                           val likeCount: Int = 0,
                           @SerializedName("item_id")
                           val itemId: String = "",
                           @SerializedName("ad_pvurl_vendor")
                           val adPvurlVendor: String = "",
                           @SerializedName("content_id")
                           val contentId: String = "",
                           val forward: String = "",
                           val author: Author?,
                           @SerializedName("share_info")
                           val shareInfo: ShareInfo?,
                           @SerializedName("words_info")
                           val wordsInfo: String = "",
                           @SerializedName("audio_platform")
                           val audioPlatform: Int = 0,
                           val volume: String = "",
                           @SerializedName("ad_id")
                           val adId: Int = 0,
                           @SerializedName("ad_pvurl")
                           val adPvurl: String = "",
                           @SerializedName("ad_share_cnt")
                           val adShareCnt: String = "",
                           @SerializedName("ad_linkurl")
                           val adLinkurl: String = "",
                           @SerializedName("img_url")
                           val imgUrl: String = "",
                           @SerializedName("post_date")
                           val postDate: String = "",
                           @SerializedName("share_url")
                           val shareUrl: String = "",
                           val subtitle: String = "",
                           @SerializedName("audio_url")
                           val audioUrl: String = "",
                           @SerializedName("movie_story_id")
                           val movieStoryId: Int = 0,
                           val category: String = "",
                           @SerializedName("display_category")
                           val displayCategory: Int = 0,
                           @SerializedName("ad_makettime")
                           val adMakettime: String = "")