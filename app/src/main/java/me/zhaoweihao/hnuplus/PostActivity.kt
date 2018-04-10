package me.zhaoweihao.hnuplus

import android.app.FragmentManager
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_post.*
import me.zhaoweihao.hnuplus.Interface.PostInterface


class PostActivity : AppCompatActivity() {

    private var postFragment: PostFragment? = null

    private var listener: PostInterface? = null

    companion object {
        private val TAG = "PostActivity"
    }

    var paths: Array<String?> = arrayOfNulls(9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        setSupportActionBar(my_toolbar)
        title = "发布二手商品"
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        val transaction = (fragmentManager as FragmentManager?)!!.beginTransaction()
        if (postFragment == null) {
            postFragment = PostFragment()
            setListener(postFragment!!)
            transaction.add(R.id.fl_post, postFragment)
        } else {
            transaction.show(postFragment)
        }
        transaction.commit()

    }

    fun setListener(listener: PostInterface) {
        this.listener = listener
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar,menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            2 -> if (resultCode == RESULT_OK) {
                //uri to path
                val uris = Matisse.obtainResult(data)
                val uriLength = uris.size
                paths = arrayOfNulls<String>(uriLength)
                listener!!.showImage(uris)
                var i = 0
                for (uri in uris) {
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
                    if (cursor!!.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                        paths[i] = cursor.getString(columnIndex)
                        Log.d(TAG,paths[i]+" "+i.toString()+" "+paths.size.toString())
                        i++

                    } else {
                        //boooo, cursor doesn't have rows ...
                    }
                    cursor.close()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_submit -> listener!!.myMethod()
            android.R.id.home -> finish()
        }
        return true
    }

}
