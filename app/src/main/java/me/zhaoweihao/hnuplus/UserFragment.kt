package me.zhaoweihao.hnuplus

import android.app.Fragment
import android.content.Intent

import android.os.Bundle


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import cn.bmob.v3.BmobUser
import com.squareup.picasso.Picasso
import com.taishi.flipprogressdialog.FlipProgressDialog
import kotlinx.android.synthetic.main.fragment_user.*

import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Utils.Utility
import org.greenrobot.eventbus.EventBus


/**
 * Created by ZhaoWeihao on 2017/11/9.
 */

class UserFragment : Fragment() {

    private var userInfo: MyUser? = null

    private var flipProgressDialog:FlipProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_user,
                container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flipProgressDialog = Utility.myDialog()
        btn_signout!!.setOnClickListener {
            BmobUser.logOut()
            val currentUser = BmobUser.getCurrentUser()
            if (currentUser == null) {
                Toast.makeText(activity, getString(R.string.signout_success), Toast.LENGTH_SHORT).show()
                tv_signinstatus!!.text = getString(R.string.not_signin_warn)
                btn_signin_1!!.visibility = View.VISIBLE
                btn_signout!!.visibility = View.GONE
                val signout = "signout"
                EventBus.getDefault().post(signout)
            }
        }

        btn_signin_1!!.setOnClickListener {
            val intent = Intent(activity, SigninActivity::class.java)
            startActivity(intent)
        }
        btn_my_order.setOnClickListener {
            val intent = Intent(activity, MyOrderActivity::class.java)
            startActivity(intent)
        }
        btn_get_order.setOnClickListener {
            val intent = Intent(activity, GetOrderActivity::class.java)
            startActivity(intent)
        }
//        iv_avatar.setOnClickListener {
//            val intent = Intent(activity, PersonalActivity::class.java)
//            startActivity(intent)
//        }


    }

    override fun onResume() {
        super.onResume()
       flipProgressDialog!!.show(fragmentManager,"")

        userInfo = BmobUser.getCurrentUser(MyUser::class.java)
        if (userInfo != null) {
            tv_signinstatus!!.text = "登录账户：" + userInfo!!.username
            btn_signin_1!!.visibility = View.GONE
            btn_signout!!.visibility = View.VISIBLE

            Picasso.with(activity)
                    .load(userInfo!!.userAvatar)
                    .resize(100, 100)
                    .centerCrop()
                    .into(iv_avatar)

            flipProgressDialog!!.dismiss()
        } else {
            tv_signinstatus!!.text = getString(R.string.not_signin_warn)
            btn_signin_1!!.visibility = View.VISIBLE
            btn_signout!!.visibility = View.GONE
            flipProgressDialog!!.dismiss()
        }

    }

}
