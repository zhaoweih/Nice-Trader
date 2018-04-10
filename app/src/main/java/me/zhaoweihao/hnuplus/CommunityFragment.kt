package me.zhaoweihao.hnuplus

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.util.Collections

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener

import me.zhaoweihao.hnuplus.Adapter.PostAdapter
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Bmob.Post

import android.preference.PreferenceManager

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yoavst.kotlin.`KotlinPackage$SystemServices$69d7d2d0`.connectivityManager

import kotlinx.android.synthetic.main.fragment_community.*
import me.zhaoweihao.hnuplus.Interface.CommunityInterface
import org.jetbrains.anko.toast

/**
 * Created by ZhaoWeihao on 2017/11/9.
 */

class CommunityFragment : Fragment(), CommunityInterface {

    private var layoutManager: LinearLayoutManager? = null
    private var adapter: PostAdapter? = null
    private var userInfo: MyUser? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
         return inflater!!.inflate(R.layout.fragment_community,
                container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        pull_to_refresh!!.setOnRefreshListener { loadData() }

        fb!!.setOnClickListener {
            userInfo = BmobUser.getCurrentUser(MyUser::class.java)
            if (userInfo != null) {
                (activity as MainActivity).gotoPostFragment()
            } else {
                Snackbar.make(fb!!, R.string.not_signin_text, Snackbar.LENGTH_SHORT)
                        .setAction("Sign in") {
                            val intent = Intent(activity, SigninActivity::class.java)
                            startActivity(intent)
                        }.show()

            }
        }

        rv_posts!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0)
                    fb!!.hide()
                else if (dy < 0)
                    fb!!.show()
            }
        })

    }

    /**
     * @networkCode 1 -> 有网络
     *              0 -> 没有网络
     */

    private fun refreshRecyclerView(networkCode: Int) {

        when (networkCode) {
            1 -> {
                pull_to_refresh!!.setRefreshing(true)
                val query = BmobQuery<Post>()
                query.include("author,imageUrls")
                query.findObjects(object : FindListener<Post>() {

                    override fun done(postList: List<Post>, e: BmobException?) {
                        if (postList.isEmpty()){
                            toast("出现了一些错误，请过一会儿再尝试刷新一下")
                            pull_to_refresh!!.setRefreshing(false)
                            return
                        }
                        if (e == null) {
                            Collections.reverse(postList)
                            saveListToPrefs(postList)

                            layoutManager = LinearLayoutManager(activity)
                            rv_posts!!.layoutManager = layoutManager
                            adapter = PostAdapter(postList,1)
                            rv_posts!!.adapter = adapter
                            pull_to_refresh!!.setRefreshing(false)

                        } else {
                            Snackbar.make(rv_posts!!, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
                            pull_to_refresh!!.setRefreshing(false)
                        }
                    }

                })}
            0 -> {
                val appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(activity.applicationContext)
                val gson = Gson()
                val json = appSharedPrefs.getString("MyObject", "")
                val type = object : TypeToken<List<Post>>() {
                }.type
                val postList: List<Post> = gson.fromJson(json,type)

                layoutManager = LinearLayoutManager(activity)
                rv_posts!!.layoutManager = layoutManager
                adapter = PostAdapter(postList,0)
                rv_posts!!.adapter = adapter
                Snackbar.make(rv_posts!!, getString(R.string.check_network_status), Snackbar.LENGTH_SHORT).show()
                pull_to_refresh!!.setRefreshing(false)
            }

        }

    }

    private fun loadData(){
        val conMgr = connectivityManager(activity)
        val activeNetwork = conMgr.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected) {
            refreshRecyclerView(1)
        } else {
            refreshRecyclerView(0)
        }
    }

    private fun saveListToPrefs(postList: List<Post>){
        val appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(activity.applicationContext)
        val prefsEditor = appSharedPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(postList)
        prefsEditor.putString("MyObject", json)
        prefsEditor.commit()
    }

    override fun myMethod() {
        loadData()
    }

    override fun refreshView() {
        refreshRecyclerView(1)
    }

}