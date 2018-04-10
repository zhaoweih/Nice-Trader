package me.zhaoweihao.hnuplus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import cn.bmob.v3.BmobUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Interface.CommentInterface
import org.jetbrains.anko.toast

class CommentActivity : AppCompatActivity() {

    companion object {
        private val TAG = "CommentActivity"
    }

    private var commentFragment: CommentFragment? = null
    private var listener: CommentInterface? = null
    private var user: MyUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        user = BmobUser.getCurrentUser(MyUser::class.java)

        title = getString(R.string.comment_title)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        val transaction = (fragmentManager)!!.beginTransaction()
        if (commentFragment == null) {
            commentFragment = CommentFragment()
            setListener(commentFragment!!)
            transaction.add(R.id.fl_comment, commentFragment)
        } else {
            transaction.show(commentFragment)
        }
        transaction.commit()

        iv_comment.setOnClickListener { listener!!.myAction(et_comment!!.text.toString()) }

        if (user != null) {
            btn_want.setOnClickListener {
                listener!!.chat()
            }

            setAvatar()
        }
        else{
            btn_want.setOnClickListener {
                toast("你还没有登录，请先登录")
            }
        }


    }

    private fun setListener(listener: CommentInterface) {
        this.listener = listener
    }

    private fun setAvatar() {
        Picasso.with(this)
                .load(user!!.userAvatar)
                .resize(40, 40)
                .centerCrop()
                .into(iv_avatar_down)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}
