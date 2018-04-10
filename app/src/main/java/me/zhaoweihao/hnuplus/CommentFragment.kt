package me.zhaoweihao.hnuplus

import android.app.Fragment
import android.app.ProgressDialog

import android.content.DialogInterface
import android.content.Intent

import android.os.Bundle

import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.lzy.ninegrid.ImageInfo
import com.lzy.ninegrid.NineGridView
import com.lzy.ninegrid.preview.NineGridViewClickAdapter
import com.taishi.flipprogressdialog.FlipProgressDialog

import java.util.ArrayList
import java.util.Collections

import butterknife.BindView
import butterknife.ButterKnife
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobPointer
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comment_layout.*
import kotlinx.android.synthetic.main.comment_top.*
import me.zhaoweihao.hnuplus.Adapter.CommentAdapter
import me.zhaoweihao.hnuplus.Interface.CommentInterface
import me.zhaoweihao.hnuplus.Bmob.Comment
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Bmob.Post
import me.zhaoweihao.hnuplus.Utils.Utility

/**
 * Created by ZhaoWeihao on 2017/11/10.
 */

class CommentFragment : Fragment(), CommentInterface {

    companion object {
        private val TAG = "CommentFragment"
    }

    private var layoutManager: LinearLayoutManager? = null
    private var adapter: CommentAdapter? = null
    private var objectID: String? = null
    private var authorObjectID: String? = null
    private var userName: String? = null
    private var price: String? = null
    private var firstImageUrl: String? = null
    private var title: String? = null
    private var detail: String? = null
    private var avatarUrl: String? = null
    private var user: MyUser? = null
    private var flipProgressDialog: FlipProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.comment_layout,
                container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = BmobUser.getCurrentUser(MyUser::class.java)
        val intent = activity.intent
        userName = intent.getStringExtra("author")
        price = intent.getStringExtra("price")
        title = intent.getStringExtra("title")
        detail = intent.getStringExtra("detail")
        avatarUrl = intent.getStringExtra("avatarUrl")

        Picasso.with(activity)
                .load(avatarUrl)
                .resize(40, 40)
                .centerCrop()
                .into(iv_avatar)

        tv_author!!.text = userName
        tv_content!!.text = title + " " + detail
        tv_time!!.text = intent.getStringExtra("time")
        tv_price!!.text = price
        tv_location!!.text = intent.getStringExtra("location")
        objectID = intent.getStringExtra("objectID")
        authorObjectID = intent.getStringExtra("authorObjectID")

        if (intent.getStringArrayListExtra("imageUrls") == null) {
            Log.d(TAG, "no photo")
        } else {
            val imageInfo = ArrayList<ImageInfo>()
            val imageUrls = intent.getStringArrayListExtra("imageUrls")
            firstImageUrl = imageUrls[0]
            for (imageUrl in imageUrls) {
                Log.d(TAG, imageUrl)
                val info = ImageInfo()
                info.setThumbnailUrl(imageUrl)
                info.setBigImageUrl(imageUrl)
                imageInfo.add(info)
            }
            ninegridview!!.setAdapter(NineGridViewClickAdapter(activity, imageInfo))
        }

        when {
            user == null -> fl_delete!!.visibility = View.GONE
            user!!.objectId == authorObjectID -> fl_delete!!.visibility = View.VISIBLE
            else -> fl_delete!!.visibility = View.GONE
        }

        fl_delete!!.setOnClickListener {
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("你确定要删除这个商品吗")
            dialog.setMessage(R.string.comment_delete_message)
            dialog.setCancelable(false)
            dialog.setPositiveButton(R.string.comment_sure) { _, _ ->
                val p = Post()
                p.objectId = objectID
                p.delete(object : UpdateListener() {
                    override fun done(e: BmobException?) {
                        if (e == null) {
                            Toast.makeText(activity, R.string.delete_success, Toast.LENGTH_SHORT).show()
                            activity.finish()
                        } else {
                            Toast.makeText(activity, R.string.delete_failed, Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
            dialog.setNegativeButton(R.string.delete_cancel) { _, _ -> }

            dialog.show()
        }

        flipProgressDialog = Utility.myDialog()
        flipProgressDialog!!.show(fragmentManager, "")

        refreshCommentData(objectID)
    }

    private fun refreshCommentData(objectID: String?) {

        val query = BmobQuery<Comment>()
        val post = Post()
        post.objectId = objectID
        query.addWhereEqualTo("post", BmobPointer(post))
        query.include("user,post.author")
        query.findObjects(object : FindListener<Comment>() {

            override fun done(objects: List<Comment>, e: BmobException?) {
                if (objects.isEmpty()) {
                    flipProgressDialog!!.dismiss()
                } else {
                    Collections.reverse(objects)
                    rv_comments!!.isNestedScrollingEnabled = false
                    layoutManager = LinearLayoutManager(activity)
                    rv_comments!!.layoutManager = layoutManager
                    adapter = CommentAdapter(objects)
                    rv_comments!!.adapter = adapter
                    flipProgressDialog!!.dismiss()
                }
            }
        })
    }

    override fun myAction(data: String) {

        when {
            user == null -> Snackbar.make(rv_comments!!, R.string.not_signin_text, Snackbar.LENGTH_SHORT)
                    .setAction("Sign in") {
                        val intent = Intent(activity, SigninActivity::class.java)
                        startActivity(intent)
                    }.show()
            data == "" -> Toast.makeText(activity, R.string.empty_text, Toast.LENGTH_SHORT).show()
            else -> {

                flipProgressDialog!!.show(fragmentManager, "")

                val post = Post()
                post.objectId = objectID
                val comment = Comment()
                comment.content = data
                comment.post = post
                comment.user = user
                comment.save(object : SaveListener<String>() {

                    override fun done(objectId: String, e: BmobException?) {
                        if (e == null) {
                            refreshCommentData(objectID)
                            Toast.makeText(activity, R.string.add_commit_success, Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(activity, R.string.add_commit_failed, Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
        }

    }

    override fun chat() {
        val intent = Intent(activity, ChatActivity::class.java)
        intent.putExtra("userId", authorObjectID)
        intent.putExtra("userName", userName)
        intent.putExtra("price", price)
        intent.putExtra("firstImageUrl", firstImageUrl)
        intent.putExtra("title", title)
        intent.putExtra("objectId", objectID)
        startActivity(intent)
    }

}
