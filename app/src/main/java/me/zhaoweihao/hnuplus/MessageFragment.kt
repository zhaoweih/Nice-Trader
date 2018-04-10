package me.zhaoweihao.hnuplus

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.event.OfflineMessageEvent
import cn.bmob.newim.listener.ConnectListener
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import kotlinx.android.synthetic.main.fragment_message.*
import me.zhaoweihao.hnuplus.Adapter.MessageAdapter
import me.zhaoweihao.hnuplus.Bmob.MyUser
import org.jetbrains.anko.toast
import org.greenrobot.eventbus.ThreadMode



/**
 * Created by Administrator on 2018/2/14.
 */

class MessageFragment : Fragment() {

    private var mMessageAdapter: MessageAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return  inflater!!.inflate(R.layout.fragment_message,
                container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG,"onViewCreated")
        getMessage()

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun getMessage(){
        val userInfo = BmobUser.getCurrentUser(MyUser::class.java)
        if (userInfo==null){
            toast("你需要先登录才能使用消息功能")
            rv_message.visibility = View.GONE
            return
        }
        if (userInfo.objectId!=null) {
            BmobIM.connect(userInfo.objectId, object : ConnectListener() {
                override fun done(uid: String, e: BmobException?) {
                    if (e == null) {
                        //连接成功
                        rv_message.visibility = View.VISIBLE
                        val conversations = BmobIM.getInstance().loadAllConversation()
                        if (conversations.size==0){
                            toast("你还没有任何消息纪录")
                            return
                        }
                        if (conversations[0] != null) {
                            Log.d(TAG, conversations[0].conversationTitle)
                            val layoutManager = LinearLayoutManager(activity)
                            rv_message!!.layoutManager = layoutManager
                            mMessageAdapter = MessageAdapter(conversations, activity)
                            rv_message!!.adapter = mMessageAdapter
                        }
                        Log.d(TAG, "成功连接")
                    } else {
                        //连接失败
                        Log.d(TAG, "连接失败")
                    }
                }
            })
        }
    }

    @Subscribe
    fun onEventMainThread(event: MessageEvent) {
        //重新获取本地消息并刷新列表

        Log.d(TAG, "got a message from MessageFragment")

        val conversations = BmobIM.getInstance().loadAllConversation()

        Log.d(TAG, conversations.size.toString())

        if (conversations[0] != null) {
            mMessageAdapter = MessageAdapter(conversations, activity)
            rv_message!!.adapter = mMessageAdapter
        }


    }

    @Subscribe
    fun helloEventBus(message: String) {
        Log.d(TAG,message)
        if (message == "login"){
            getMessage()
        }
        else if (message == "signout"){
            getMessage()
        }
    }

    @Subscribe
    fun onEventMainThread(event: OfflineMessageEvent) {
        //重新刷新列表
        Log.d(TAG, "got a offline message")
    }

    companion object {

        private val TAG = "MessageFragment"
    }
}
