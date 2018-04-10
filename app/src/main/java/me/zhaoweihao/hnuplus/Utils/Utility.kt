package me.zhaoweihao.hnuplus.Utils

import android.graphics.Color

import com.google.gson.Gson
import com.taishi.flipprogressdialog.FlipProgressDialog

import java.util.ArrayList

import me.zhaoweihao.hnuplus.Gson.Translate
import me.zhaoweihao.hnuplus.Gson.Weather
import me.zhaoweihao.hnuplus.R


/**
 * Created by Zhaoweihao on 2018/1/6.
 */

object Utility {
    fun handleWeatherResponse(response: String): Weather? {
        try {
            val gson = Gson()
            return gson.fromJson(response, Weather::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun handleTranslateResponse(response: String): Translate? {
        try {
            val gson = Gson()
            return gson.fromJson(response, Translate::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null

    }

    fun myDialog(): FlipProgressDialog {
        val imageList = ArrayList<Int>()
        imageList.add(R.drawable.ic_favorite_border_white_24dp)
        imageList.add(R.drawable.ic_favorite_white_24dp)

        val flipY = FlipProgressDialog()
        flipY.setImageList(imageList)
        flipY.setCanceledOnTouchOutside(false)
        flipY.setOrientation("rotationY")
        flipY.setBackgroundColor(Color.parseColor("#FF4081"))
        flipY.setDimAmount(0.3f)
        flipY.setCornerRadius(32)

        return flipY

    }


}
