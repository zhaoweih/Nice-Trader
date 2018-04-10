package me.zhaoweihao.hnuplus.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

import me.zhaoweihao.hnuplus.Bmob.Comment
import me.zhaoweihao.hnuplus.R

/**
 * Created by ZhaoWeihao on 2017/11/10.
 */

/**
 * Show comment's data to recyclerview
 */
class CommentAdapter(private val mCommentList: List<Comment>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    companion object {
        private val TAG = "CommentAdapter"
    }

    private var mContext: Context? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var commentatorName = view.findViewById<TextView>(R.id.tv_commentator_name)
        var commentatorContent = view.findViewById<TextView>(R.id.tv_commentator_content)
        var commentAvatar = view.findViewById<ImageView>(R.id.iv_avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.comments_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = mCommentList[position]
        holder.commentatorName.text = comment.user!!.username
        holder.commentatorContent.text = comment.content
        Picasso.with(mContext)
                .load(comment.user!!.userAvatar)
                .resize(40, 40)
                .centerCrop()
                .into(holder.commentAvatar)
    }

    override fun getItemCount(): Int {
        return mCommentList.size
    }
}
