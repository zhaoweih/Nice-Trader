package me.zhaoweihao.hnuplus

/**
 * Created by Administrator on 2018/2/12.
 */

class Msg(val content: String, val type: Int) {
    companion object {

        val TYPE_RECEIVED = 0

        val TYPE_SENT = 1
    }
}
