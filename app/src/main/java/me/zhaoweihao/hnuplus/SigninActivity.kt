package me.zhaoweihao.hnuplus

import android.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.os.Build
import android.view.View
import android.view.ViewGroup


class SigninActivity : AppCompatActivity() {

    private var signinFragment: SigninFragment? = null
    private var signupFragment: SignupFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById<ViewGroup>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        setContentView(R.layout.activity_signin)

        val transaction = (fragmentManager as FragmentManager?)!!.beginTransaction()

        if (signinFragment == null) {
            signinFragment = SigninFragment()
            transaction.add(R.id.fl_signin, signinFragment)
        } else {
            transaction.show(signinFragment)
        }
        transaction.commit()

    }

    open fun toSignupFragment(){

        val transaction = (fragmentManager as FragmentManager?)!!.beginTransaction()

        if (signinFragment != null) {
            transaction.hide(signinFragment)
        }

        if (signupFragment == null) {
            signupFragment = SignupFragment()
            transaction.add(R.id.fl_signin, signupFragment)
        } else {
            transaction.show(signupFragment)
        }
        transaction.commit()

    }





}
