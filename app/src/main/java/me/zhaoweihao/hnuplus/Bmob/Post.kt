package me.zhaoweihao.hnuplus.Bmob

import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.datatype.BmobRelation
import java.util.ArrayList

/**
 * Created by ZhaoWeihao on 2017/11/9.
 */

class Post : BmobObject() {
    var title: String? = null
    var detail: String? = null
    var price: String? = null
    var author: MyUser? = null
    var imageUrls: ArrayList<String>? = null
    var location: String? = null
}
