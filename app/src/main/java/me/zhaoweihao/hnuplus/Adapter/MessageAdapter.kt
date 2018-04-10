package me.zhaoweihao.hnuplus.Adapter


import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import cn.bmob.newim.bean.BmobIMConversation
import me.zhaoweihao.hnuplus.Bmob.Note
import me.zhaoweihao.hnuplus.ChatActivity
import me.zhaoweihao.hnuplus.NoteActivity
import me.zhaoweihao.hnuplus.NoteEditActivity
import me.zhaoweihao.hnuplus.R
import java.text.SimpleDateFormat
import java.util.*
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.listener.QueryListener
import com.squareup.picasso.Picasso
import me.zhaoweihao.hnuplus.Bmob.MyUser


/**
 * Created by ZhaoWeihao on 2018/1/10.
 */

class MessageAdapter(private val mMessageList: List<BmobIMConversation>, private val mContext: Context) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(var messageView: View) : RecyclerView.ViewHolder(messageView) {
        var messageTitle: TextView = messageView.findViewById(R.id.tv_message_title)
        var messageConetent: TextView = messageView.findViewById(R.id.tv_message_content)
        var messageTime: TextView = messageView.findViewById(R.id.tv_updatetime)
        var messageAvatar: ImageView = messageView.findViewById(R.id.iv_avatar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.message_item, parent, false)
        val holder = ViewHolder(view)

        holder.messageView.setOnClickListener {
            val position = holder.adapterPosition
            val conversation = mMessageList[position]
            Log.d(TAG, conversation.conversationId)
            val intent = Intent(mContext, ChatActivity::class.java)
            intent.putExtra("userId", conversation.conversationId)
            intent.putExtra("userName", conversation.conversationTitle)
            intent.putExtra("code", FROM_MESSAGE_FRAGMENT_CODE)
            mContext.startActivity(intent)
        }



        return holder
    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        val conversation = mMessageList[position]
        holder.messageTitle.text = conversation.conversationTitle
        holder.messageConetent.text = conversation.messages[0].content
        holder.messageTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date(conversation.updateTime))
        Log.d(TAG,conversation.conversationId)
        val query = BmobQuery<MyUser>()
        query.getObject(conversation.conversationId, object : QueryListener<MyUser>() {

            override fun done(user: MyUser, e: BmobException?) {
                if (e == null) {
                    Picasso.with(mContext)
                            .load(user.userAvatar)
                            .resize(50, 50)
                            .centerCrop()
                            .into(holder.messageAvatar)
                } else {
                    Log.d(TAG, "失败：" + e.message + "," + e.errorCode)
                }
            }

        })
    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    companion object {

        private val TAG = "MessageAdapter"

         val FROM_MESSAGE_FRAGMENT_CODE = 0
    }

}
