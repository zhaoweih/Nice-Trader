package me.zhaoweihao.hnuplus

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView

import com.lzy.ninegrid.NineGridView
import com.squareup.picasso.Picasso

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

import cn.bmob.newim.BmobIM

/**
 * Created by Administrator on 2018/2/11.
 */

class BmobIMApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (applicationInfo.packageName == myProcessName) {
            BmobIM.init(this)
            BmobIM.registerDefaultMessageHandler(DemoMessageHandler(this))
        }

        NineGridView.setImageLoader(PicassoImageLoader())
    }

    private inner class PicassoImageLoader : NineGridView.ImageLoader {

        override fun onDisplayImage(context: Context, imageView: ImageView, url: String) {
            Picasso.with(context).load(url)
                    .resize(1000,1000)
                    .placeholder(R.drawable.ic_default_image)
                    .error(R.drawable.ic_default_image)
                    .centerCrop()
                    .into(imageView)
        }

        override fun getCacheImage(url: String): Bitmap? {
            return null
        }
    }

    companion object {
        val myProcessName: String?
            get() {
                return try {
                    val file = File("/proc/" + android.os.Process.myPid() + "/" + "cmdline")
                    val mBufferedReader = BufferedReader(FileReader(file))
                    val processName = mBufferedReader.readLine().trim { it <= ' ' }
                    mBufferedReader.close()
                    processName
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

            }
    }
}
