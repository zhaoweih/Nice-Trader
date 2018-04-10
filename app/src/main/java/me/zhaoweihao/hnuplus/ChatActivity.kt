package me.zhaoweihao.hnuplus

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View

import com.squareup.picasso.Picasso

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import java.util.ArrayList

import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import cn.bmob.newim.bean.BmobIMMessage
import cn.bmob.newim.bean.BmobIMTextMessage
import cn.bmob.newim.bean.BmobIMUserInfo
import cn.bmob.newim.core.BmobIMClient
import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.event.OfflineMessageEvent
import cn.bmob.newim.listener.ConnectListener
import cn.bmob.newim.listener.MessageSendListener
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import kotlinx.android.synthetic.main.activity_chat.*
import me.zhaoweihao.hnuplus.Adapter.MessageAdapter
import me.zhaoweihao.hnuplus.Bmob.MyUser

class ChatActivity : AppCompatActivity() {

    private val msgList = ArrayList<Msg>()

    private var adapter: MsgAdapter? = null

    private var userName: String? = null
    private var userId: String? = null
    private var price: String? = null
    private var firstImageUrl: String? = null
    private var title: String? = null
    private var objectId: String? = null
    private var code: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initMsgs()

        val layoutManager = LinearLayoutManager(this)
        msg_recycler_view!!.layoutManager = layoutManager

        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = MsgAdapter(msgList)
        msg_recycler_view!!.adapter = adapter

        val intent = intent

        userId = intent.getStringExtra("userId")
        userName = intent.getStringExtra("userName")
        price = intent.getStringExtra("price")
        firstImageUrl = intent.getStringExtra("firstImageUrl")
        title = intent.getStringExtra("title")
        objectId = intent.getStringExtra("objectId")
        code = intent.getIntExtra("code",1)

        if(code == MessageAdapter.FROM_MESSAGE_FRAGMENT_CODE){
            cv_chat_top.visibility = View.GONE
        }


        Log.d(TAG, "传过来的ID是：" + userId!!)

            tv_chat_price!!.text = price

            Picasso.with(this@ChatActivity)
                    .load(firstImageUrl)
                    .resize(60, 60)
                    .centerCrop()
                    .into(iv_chat_image)

            setTitle("与" + userName + "的聊天")

        btn_buy!!.setOnClickListener {
            val intent = Intent(this@ChatActivity, BuyActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("firstImageUrl", firstImageUrl)
            intent.putExtra("price", price)
            intent.putExtra("objectId", objectId)
            intent.putExtra("sellerId", userId)
            startActivity(intent)
        }

        val userInfo = BmobUser.getCurrentUser(MyUser::class.java)
        if (!TextUtils.isEmpty(userInfo.objectId)) {
            BmobIM.connect(userInfo.objectId, object : ConnectListener() {
                override fun done(uid: String, e: BmobException?) {
                    if (e == null) {
                        send!!.setOnClickListener {
                            val content = input_text!!.text.toString()
                            if ("" != content) {
                                val bmobIMUserInfo = BmobIMUserInfo(userId, userName, "")
                                val conversationEntrance = BmobIM.getInstance().startPrivateConversation(bmobIMUserInfo, null)
                                val messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance)
                                sendMessage(messageManager, content)

                            }
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

    private fun sendMessage(mConversationManager: BmobIMConversation, content: String) {
        if (TextUtils.isEmpty(content.trim { it <= ' ' })) {
            Log.d(TAG, "请输入一些东西")
            return
        }
        val msg = BmobIMTextMessage()
        msg.content = content
        mConversationManager.sendMessage(msg, object : MessageSendListener() {
            override fun done(bmobIMMessage: BmobIMMessage, e: BmobException?) {
                if (e == null) {
                    Log.d(TAG, "成功发送信息")
                    val msg = Msg(content, Msg.TYPE_SENT)
                    msgList.add(msg)
                    adapter!!.notifyItemInserted(msgList.size - 1)
                    msg_recycler_view!!.scrollToPosition(msgList.size - 1)
                    input_text!!.setText("")
                } else {
                    Log.d(TAG, "发送信息失败")
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()

    }

    private fun initMsgs() {
        val msg1 = Msg("跟卖家商量一下价格吧", Msg.TYPE_SENT)
        msgList.add(msg1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    @Subscribe
    fun onEventMainThread(event: MessageEvent) {
        Log.d(TAG, "收到一条信息")
        val msg = Msg(event.message.content.toString(), Msg.TYPE_RECEIVED)
        msgList.add(msg)
        adapter!!.notifyItemInserted(msgList.size - 1)
        msg_recycler_view!!.scrollToPosition(msgList.size - 1)
    }

    @Subscribe
    fun onEventMainThread(event: OfflineMessageEvent) {
        Log.d(TAG, "收到离线信息")
    }

    companion object {

        private val TAG = "ChatActivity"
    }

}
