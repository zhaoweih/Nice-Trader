package me.zhaoweihao.hnuplus

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast

import butterknife.ButterKnife
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.editarea.*
import kotlinx.android.synthetic.main.explain.*
import kotlinx.android.synthetic.main.query_layout.*
import kotlinx.android.synthetic.main.translate.*
import kotlinx.android.synthetic.main.web.*
import me.zhaoweihao.hnuplus.Gson.Translate
import me.zhaoweihao.hnuplus.Utils.HttpUtil
import me.zhaoweihao.hnuplus.Utils.Utility

import java.io.IOException

import okhttp3.Call
import okhttp3.Response

/**
 * Created by Zhaoweihao on 17/7/6.
 * 如果对我的项目有任何疑问可以给我发邮件或者提issues
 * Email:zhaoweihaochn@gmail.com
 * 如果觉得我的项目写得好可以给我star和fork
 * 谢谢！
 */

class TranslateActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)

        title = "翻译"

        translate_btn!!.visibility = View.INVISIBLE
        iv_clear!!.visibility = View.INVISIBLE
        copy!!.visibility = View.INVISIBLE
        share!!.visibility = View.INVISIBLE
        mixLayout!!.visibility = View.INVISIBLE

        iv_clear!!.setOnClickListener { word_input!!.setText("") }
        word_input!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (word_input!!.editableText.toString().isNotEmpty()) {
                    iv_clear!!.visibility = View.VISIBLE
                    translate_btn!!.visibility = View.VISIBLE
                } else {
                    iv_clear!!.visibility = View.INVISIBLE
                    translate_btn!!.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        translate_btn!!.setOnClickListener {
            if (word_input!!.text.toString().isEmpty()) {
                Snackbar.make(translate_btn!!, R.string.input_empty, Snackbar.LENGTH_SHORT)
                        .show()
            } else {
                progress_bar!!.visibility = View.VISIBLE
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) {
                    imm.hideSoftInputFromWindow(translate_btn!!.windowToken, 0)
                }
                val word = word_input!!.text.toString()
                val url = "http://fanyi.youdao.com/openapi.do?keyfrom=zhaotranslator&key=1681711370&type=data&doctype=json&version=1.1&q="

                HttpUtil.sendOkHttpRequest(url + word, object : okhttp3.Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        //网络请求失败，开启子进程，更新页面
                        runOnUiThread {
                            progress_bar!!.visibility = View.GONE
                            Toast.makeText(this@TranslateActivity, R.string.translate_fail, Toast.LENGTH_SHORT).show()
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val responseData = response.body().string()
                        try {
                            val translate = Utility.handleTranslateResponse(responseData)
                            runOnUiThread {
                                if (translate!!.errorCode == 0) {
                                    showTranslateInfo(translate)
                                } else if (translate.errorCode == 20) {
                                    progress_bar!!.visibility = View.GONE
                                    Toast.makeText(this@TranslateActivity, R.string.translate_overlong, Toast.LENGTH_SHORT).show()
                                } else {
                                    progress_bar!!.visibility = View.GONE
                                    Toast.makeText(this@TranslateActivity, R.string.translate_fail, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            progress_bar!!.visibility = View.INVISIBLE
                        }

                    }
                })
            }
        }
    }


    private fun showTranslateInfo(translate: Translate?) {
        translation_layout!!.removeAllViews()
        explains_layout!!.removeAllViews()
        web_layout!!.removeAllViews()
        translate_title!!.setText(R.string.translate_title)
        explains_title!!.setText(R.string.explains_title)
        web_title!!.setText(R.string.web_title)
        for (i in 0 until translate!!.translation!!.size) {
            val view = LayoutInflater.from(this).inflate(R.layout.translation_item, translation_layout, false)
            val translateText = view.findViewById<View>(R.id.translation_text) as TextView
            translateText.text = translate.translation!![i]
            translateText.setTextColor(resources.getColor(R.color.white))
            translateText.textSize = 25f
            translation_layout!!.addView(view)
        }

        query_text!!.text = translate.query
        if (translate.basic == null) {
            phonetic_text!!.visibility = View.INVISIBLE
            explains_layout!!.visibility = View.INVISIBLE
        } else {

            phonetic_text!!.text = "[" + translate.basic!!.phonetic + "]"
            for (i in 0 until translate.basic!!.explains!!.size) {
                val view = LayoutInflater.from(this).inflate(R.layout.explains_item, explains_layout, false)
                val explainsText = view.findViewById<View>(R.id.expalins_text) as TextView
                explainsText.text = translate.basic!!.explains!![i]
                explains_layout!!.addView(view)
                phonetic_text!!.visibility = View.VISIBLE
                explains_layout!!.visibility = View.VISIBLE
            }
        }


        if (translate.web == null) {
            web_layout!!.visibility = View.INVISIBLE
        } else {

            for (i in 0 until translate.web!!.size) {
                val view = LayoutInflater.from(this).inflate(R.layout.web_item, web_layout, false)
                val keyText = view.findViewById<View>(R.id.key_text) as TextView
                val valueText = view.findViewById<View>(R.id.value_text) as TextView
                keyText.text = translate.web!![i].key
                val values = getFinalValue(translate.web!![i].value)
                valueText.text = values
                web_layout!!.addView(view)
                web_layout!!.visibility = View.VISIBLE
            }
        }
        progress_bar!!.visibility = View.GONE
        copy!!.setOnClickListener {
            val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", translate.translation!![0])
            manager.primaryClip = clipData
            Snackbar.make(translate_btn!!, R.string.copy_success, Snackbar.LENGTH_SHORT)
                    .show()
        }
        share!!.setOnClickListener {
            val intent = Intent()
            intent.setAction(Intent.ACTION_SEND).type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, translate.translation!![0])
            startActivity(Intent.createChooser(intent, getString(R.string.share_choice)))
        }

        copy!!.visibility = View.VISIBLE
        share!!.visibility = View.VISIBLE
        mixLayout!!.visibility = View.VISIBLE
    }

    private fun getFinalValue(value: Array<String>?): String {
        var finalValue = ""
        for (i in value!!.indices) {
            if (i == value.size - 1) {
                finalValue += value[i]
            } else {
                finalValue = finalValue + value[i] + ","
            }
        }
        return finalValue
    }

}
