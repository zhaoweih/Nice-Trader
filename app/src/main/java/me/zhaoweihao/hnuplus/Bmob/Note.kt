package me.zhaoweihao.hnuplus.Bmob

import cn.bmob.v3.BmobObject

/**
 * Created by ZhaoWeihao on 2018/1/7.
 */

class Note : BmobObject() {
    var content: String? = null
    var author: MyUser? = null
}
