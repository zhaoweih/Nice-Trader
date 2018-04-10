package me.zhaoweihao.hnuplus

import android.app.Fragment
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast

import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.taishi.flipprogressdialog.FlipProgressDialog
import kotlinx.android.synthetic.main.signin_layout.*
import me.zhaoweihao.hnuplus.Utils.Utility
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast


/**
 * Created by ZhaoWeihao on 2017/11/10.
 */


class SigninFragment : Fragment() {

    private var anim: AnimationDrawable? = null

    private var flipProgressDialog:FlipProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.signin_layout,
                container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        anim = container_1!!.background as AnimationDrawable
        anim!!.setEnterFadeDuration(6000)
        anim!!.setExitFadeDuration(2000)

        flipProgressDialog= Utility.myDialog()


        btn_signup_1!!.setOnClickListener { (activity as SigninActivity).toSignupFragment() }

        btn_signin_2!!.setOnClickListener {
            val username = et_username_1!!.text.toString()
            val password = et_password_1!!.text.toString()

            if (username == "" || password == "") {
                Toast.makeText(activity, getString(R.string.can_be_empty), Toast.LENGTH_SHORT).show()
            } else {

                flipProgressDialog!!.show(fragmentManager,"")

                val bu2 = BmobUser()
                bu2.username = username
                bu2.setPassword(password)

                    bu2.login(object : SaveListener<BmobUser?>() {

                        override fun done(bmobUser: BmobUser?, e: BmobException?) {
                            if (e == null) {
                                Toast.makeText(activity, getString(R.string.signin_success), Toast.LENGTH_SHORT).show()
                                flipProgressDialog!!.dismiss()
                                val login = "login"
                                EventBus.getDefault().post(login)
                                activity.finish()
                            } else {
                                toast("登录失败，请检查用户名和密码")
                                flipProgressDialog!!.dismiss()
                            }
                        }
                    })




            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (anim != null && !anim!!.isRunning)
            anim!!.start()
    }

    override fun onPause() {
        super.onPause()
        if (anim != null && anim!!.isRunning) {
            anim!!.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}
