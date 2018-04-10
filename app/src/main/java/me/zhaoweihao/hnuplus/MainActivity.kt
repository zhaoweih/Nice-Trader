package me.zhaoweihao.hnuplus

import android.app.FragmentTransaction
import android.content.Intent
import android.graphics.Color


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import cn.bmob.newim.BmobIM
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener

import kotlinx.android.synthetic.main.activity_main.*
import me.zhaoweihao.hnuplus.Interface.CommunityInterface
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Bmob.Post
import cn.bmob.v3.listener.UploadFileListener
import cn.bmob.v3.datatype.BmobFile
import org.jetbrains.anko.toast
import java.io.File

import com.taishi.flipprogressdialog.FlipProgressDialog
import me.zhaoweihao.hnuplus.Utils.Utility
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import cn.bmob.v3.listener.UploadBatchListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * Created by ZhaoWeihao on 2017/11/9.
 * Github:https://github.com/zhaoweihaoChina
 */
class MainActivity : AppCompatActivity() {

    private var communityFragment: CommunityFragment? = null
    private var moreFragment: MoreFragment? = null
    private var userFragment: UserFragment? = null
    private var messageFragment: MessageFragment? = null

    private var mListener: CommunityInterface? = null

    private var flipProgressDialog: FlipProgressDialog? = null

    companion object {
        private val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        flipProgressDialog = Utility.myDialog()

        bindListener()

        setTabSelection(0)
    }

    private fun bindListener() {
        community_layout!!.setOnClickListener { setTabSelection(0) }
        more_layout!!.setOnClickListener { setTabSelection(1) }
        message_layout!!.setOnClickListener { setTabSelection(2) }
        user_layout!!.setOnClickListener { setTabSelection(3) }
    }

    private fun setTabSelection(index: Int) {
        clearSelection()
        val transaction = fragmentManager!!.beginTransaction()
        hideFragments(transaction)
        when (index) {
            0 -> {
                community_image!!.setImageResource(R.drawable.community)
                community_text!!.setTextColor(Color.WHITE)
                title = "市场"
                if (communityFragment == null) {
                    communityFragment = CommunityFragment()
                    setListener(communityFragment!!)
                    transaction.add(R.id.content, communityFragment)
                } else {
                    transaction.show(communityFragment)
                }
            }
            1 -> {
                more_image!!.setImageResource(R.drawable.more)
                more_text!!.setTextColor(Color.WHITE)
                title = "更多"
                if (moreFragment == null) {
                    moreFragment = MoreFragment()
                    transaction.add(R.id.content, moreFragment)
                } else {
                    transaction.show(moreFragment)
                }
            }
            2 -> {
                message_image!!.setImageResource(R.drawable.user)
                message_text!!.setTextColor(Color.WHITE)
                title = "消息"
                if (messageFragment == null) {
                    messageFragment = MessageFragment()
                    transaction.add(R.id.content, messageFragment)
                } else {
                    transaction.show(messageFragment)
                }
            }
            3 -> {
                user_image!!.setImageResource(R.drawable.user)
                user_text!!.setTextColor(Color.WHITE)
                title = "我的"
                if (userFragment == null) {
                    userFragment = UserFragment()
                    transaction.add(R.id.content, userFragment)
                } else {
                    transaction.show(userFragment)
                }
            }
        }
        transaction.commit()
    }

    private fun clearSelection() {
        community_image!!.setImageResource(R.drawable.community_grey)
        community_text!!.setTextColor(Color.parseColor("#82858b"))
        more_image!!.setImageResource(R.drawable.more_grey)
        more_text!!.setTextColor(Color.parseColor("#82858b"))
        message_image!!.setImageResource(R.drawable.user_grey)
        message_text!!.setTextColor(Color.parseColor("#82858b"))
        user_image!!.setImageResource(R.drawable.user_grey)
        user_text!!.setTextColor(Color.parseColor("#82858b"))
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        if (communityFragment != null) {
            transaction.hide(communityFragment)
        }
        if (moreFragment != null) {
            transaction.hide(moreFragment)
        }
        if (userFragment != null) {
            transaction.hide(userFragment)
        }

        if (messageFragment != null) {
            transaction.hide(messageFragment)
        }
    }

    fun gotoPostFragment() {
        val intent = Intent(this, PostActivity::class.java)
        startActivityForResult(intent, 1)
    }

    private fun setListener(listener: CommunityInterface) {
        this.mListener = listener
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val postTitle = data!!.getStringExtra("post_title")
                val postDetail = data.getStringExtra("post_detail")
                val postLocation = data.getStringExtra("post_location")
                val postPrice = data.getStringExtra("post_price")
                val paths = data.getStringArrayExtra("data_return_2")
                for (path in paths) {
                    Log.d(TAG, path)
                }

                if (paths == null) {
                    if (postTitle == "" || postDetail == "" || postLocation == "" || postPrice == "") {
                        Toast.makeText(this, getString(R.string.empty_text_warn), Toast.LENGTH_SHORT).show()
                    } else {
                        val user = BmobUser.getCurrentUser(MyUser::class.java)
                        val post = Post()
                        flipProgressDialog!!.show(getFragmentManager(), "")

                        post.title = postTitle
                        post.detail = postDetail
                        post.price = postPrice
                        post.location = postLocation
                        post.author = user

                        post.save(object : SaveListener<String>() {

                            override fun done(objectId: String, e: BmobException?) {
                                if (e == null) {
                                    flipProgressDialog!!.dismiss()
                                    Toast.makeText(this@MainActivity, getString(R.string.post_success), Toast.LENGTH_SHORT).show()
                                    viewKonfetti.build()
                                            .addColors(Color.parseColor("#fce18a"), Color.parseColor("#ff726d"), Color.parseColor("#b48def"), Color.parseColor("#f4306d"))
                                            .setDirection(0.0, 359.0)
                                            .setSpeed(1f, 5f)
                                            .setFadeOutEnabled(true)
                                            .setTimeToLive(600L)
                                            .addShapes(Shape.RECT, Shape.CIRCLE)
                                            .addSizes(Size(12))
                                            .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
                                            .stream(300, 5000L)
                                    mListener!!.myMethod()
                                } else {
                                    Toast.makeText(this@MainActivity, getString(R.string.post_failed), Toast.LENGTH_SHORT).show()
                                }
                            }
                        })

                    }
                } else {
                    if (postTitle == "" || postDetail == "" || postLocation == "" || postPrice == "") {
                        Toast.makeText(this, getString(R.string.empty_text_warn), Toast.LENGTH_SHORT).show()
                    } else {
                        val user = BmobUser.getCurrentUser(MyUser::class.java)
                        val post = Post()

                        flipProgressDialog!!.show(fragmentManager, "")

                        toast("正在发布帖子...")

                        BmobFile.uploadBatch(paths, object : UploadBatchListener {

                            override fun onSuccess(files: List<BmobFile>, urls: List<String>) {
                                if (urls.size == paths.size) {//如果数量相等，则代表文件全部上传完成
                                    post.title = postTitle
                                    post.detail = postDetail
                                    post.price = postPrice
                                    post.location = postLocation
                                    post.author = user
                                    post.imageUrls = ArrayList<String>(urls)
                                    post.save(object : SaveListener<String>() {

                                        override fun done(objectId: String, e: BmobException?) {
                                            if (e == null) {
                                                Toast.makeText(this@MainActivity, "发布商品成功", Toast.LENGTH_SHORT).show()
                                                flipProgressDialog!!.dismiss()
                                                mListener!!.refreshView()
                                            } else {
                                                Toast.makeText(this@MainActivity,  "发布商品失败", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    })
                                }
                            }

                            override fun onError(statuscode: Int, errormsg: String) {
                                Log.d(TAG,"Error")
                            }

                            override fun onProgress(curIndex: Int, curPercent: Int, total: Int, totalPercent: Int) {
                                //1、curIndex--表示当前第几个文件正在上传
                                //2、curPercent--表示当前上传文件的进度值（百分比）
                                //3、total--表示总的上传文件数
                                //4、totalPercent--表示总的上传进度（百分比）
//                                Log.d(TAG, curIndex.toString())

                            }
                        })


                    }
                }

            }
        }

    }

}
