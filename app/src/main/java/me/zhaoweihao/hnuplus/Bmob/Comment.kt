package me.zhaoweihao.hnuplus.Bmob

import cn.bmob.v3.BmobObject

/**
 * Created by ZhaoWeihao on 2017/11/9.
 */

class Comment : BmobObject() {
    var content: String? = null
    var user: MyUser? = null
    var post: Post? = null
}
