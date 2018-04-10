package me.zhaoweihao.hnuplus

import android.content.Context
import android.util.Log

import com.yalantis.phoenix.util.Logger

import org.greenrobot.eventbus.EventBus

import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.event.OfflineMessageEvent
import cn.bmob.newim.listener.BmobIMMessageHandler

/**
 * Created by Administrator on 2018/2/11.
 */

class DemoMessageHandler(private val context: Context) : BmobIMMessageHandler() {

    override fun onMessageReceive(event: MessageEvent?) {
        Log.d(TAG, "got message" + event!!.message.content)
        EventBus.getDefault().post(event)
    }

    override fun onOfflineReceive(event: OfflineMessageEvent?) {
        val map = event!!.eventMap
        Log.d(TAG, "有" + map.size + "个用户发来离线消息")
        for ((key, list) in map) {
            val size = list.size
            Log.d(TAG, "用户" + key + "发来" + size + "条消息")
            for (i in 0 until size) {

                EventBus.getDefault().post(list[i])
            }
        }
    }

    companion object {

        private val TAG = "DemoMessageHandler"
    }
}
