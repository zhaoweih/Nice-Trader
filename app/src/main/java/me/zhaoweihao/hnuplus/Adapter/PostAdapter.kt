package me.zhaoweihao.hnuplus.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.TextView

import me.zhaoweihao.hnuplus.CommentActivity
import me.zhaoweihao.hnuplus.Bmob.Post
import me.zhaoweihao.hnuplus.MainActivity

import me.zhaoweihao.hnuplus.R
import com.lzy.ninegrid.ImageInfo
import com.lzy.ninegrid.NineGridView
import com.lzy.ninegrid.preview.NineGridViewClickAdapter
import com.squareup.picasso.Picasso
import com.yoavst.kotlin.`KotlinPackage$Toasts$53212cf1`.toast


/**
 * Created by ZhaoWeihao on 2017/11/9.
 */

/**
 * Show post's data to recyclerview
 */
class PostAdapter(private val mPostList: List<Post>,private val disableCode: Int) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    companion object {
        private val TAG = "PostAdapter"
    }

    private var mContext: Context? = null

    private val disable = disableCode

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var postAuthor = view.findViewById<TextView>(R.id.tv_author)
        var postContent = view.findViewById<TextView>(R.id.tv_content)
        var postTime = view.findViewById<TextView>(R.id.tv_time)
        var postPrice = view.findViewById<TextView>(R.id.tv_price)
        var postLocation = view.findViewById<TextView>(R.id.tv_location)
        var nineGrid = view.findViewById<NineGridView>(R.id.ninegridview)
        var avatar = view.findViewById<ImageView>(R.id.iv_avatar)
        var postView: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        Log.d(TAG, "onCreateViewHolder")
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.posts_item, parent, false)
        val holder = ViewHolder(view)
        when (disable) {
            1 -> {
                holder.postView.setOnClickListener {
                    val position = holder.adapterPosition
                    val post = mPostList[position]
                    val intent = Intent(mContext, CommentActivity::class.java)
                    intent.putExtra("author", post.author!!.username)
                    intent.putExtra("title", post.title)
                    intent.putExtra("detail", post.detail)
                    intent.putExtra("price", "¥ " + post.price)
                    intent.putExtra("time", post.createdAt)
                    intent.putExtra("location", post.location)
                    intent.putExtra("objectID", post.objectId)
                    intent.putExtra("authorObjectID", post.author!!.objectId)
                    intent.putExtra("avatarUrl", post.author!!.userAvatar)
                    if (post.imageUrls == null){

                    }else{
                        intent.putStringArrayListExtra("imageUrls", post.imageUrls)
                    }
                    (mContext as MainActivity).startActivity(intent)
                }

            }

            0 -> {
                holder.postView.setOnClickListener {
                    toast(mContext, "Please check your network status")
                }
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mPostList[position]
        holder.postAuthor.text = post.author!!.username
        holder.postContent.text = post.title + " " + post.detail
        holder.postPrice.text = "¥ " + post.price
        holder.postTime.text = post.createdAt
        holder.postLocation.text = post.location

        Picasso.with(mContext)
                .load(post.author!!.userAvatar)
                .resize(40, 40)
                .centerCrop()
                .into(holder.avatar)


        Log.d(TAG, "onBindViewHolder")

        val imageInfo = ArrayList<ImageInfo>()

        val length = post.imageUrls!!.size

        for (i in 0..(length - 1)) {
            Log.d(TAG, post.imageUrls!!.get(i))
            val info = ImageInfo()
            info.setThumbnailUrl(post.imageUrls!!.get(i))
            info.setBigImageUrl(post.imageUrls!!.get(i))
            imageInfo.add(info)
        }

        holder.nineGrid.setAdapter(NineGridViewClickAdapter(mContext, imageInfo))

    }

    override fun getItemCount(): Int {
        return mPostList.size
    }


}
