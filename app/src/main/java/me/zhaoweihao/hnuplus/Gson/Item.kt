package me.zhaoweihao.hnuplus.Gson

import com.google.gson.annotations.SerializedName

/**
 * Created by ZhaoWeihao on 2018/1/6.
 */

class Item {
    var title: String? = null
    var lat: String? = null
    @SerializedName("long")
    var longtitude: String? = null
    var pubDate: String? = null
    var condition: Condition? = null
    @SerializedName("forecast")
    var forecastList: List<Forecast>? = null
}
