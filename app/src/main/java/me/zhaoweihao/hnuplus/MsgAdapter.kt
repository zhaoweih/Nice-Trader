package me.zhaoweihao.hnuplus

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by Administrator on 2018/2/12.
 */

class MsgAdapter(private val mMsgList: List<Msg>) : RecyclerView.Adapter<MsgAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var leftLayout: LinearLayout = view.findViewById(R.id.left_layout)
        var rightLayout: LinearLayout = view.findViewById(R.id.right_layout)
        var leftMsg: TextView = view.findViewById(R.id.left_msg)
        var rightMsg: TextView = view.findViewById(R.id.right_msg)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MsgAdapter.ViewHolder, position: Int) {
        val msg = mMsgList[position]
        if (msg.type == Msg.TYPE_RECEIVED) {
            holder.leftLayout.visibility = View.VISIBLE
            holder.rightLayout.visibility = View.GONE
            holder.leftMsg.text = msg.content
        } else if (msg.type == Msg.TYPE_SENT) {
            holder.rightLayout.visibility = View.VISIBLE
            holder.leftLayout.visibility = View.GONE
            holder.rightMsg.text = msg.content
        }

    }

    override fun getItemCount(): Int {
        return mMsgList.size
    }
}
