package me.zhaoweihao.hnuplus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast

import cn.bmob.v3.BmobACL
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import kotlinx.android.synthetic.main.activity_note_edit.*
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Bmob.Note

class NoteEditActivity : AppCompatActivity() {

    private var isAdd: Boolean = false
    private var contentBefore: String? = null
    private var objectID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_edit)
        title = getString(R.string.edit_title)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        isAdd = intent.getBooleanExtra("data", true)
        if (isAdd) {

        } else {
            contentBefore = intent.getStringExtra("content")
            objectID = intent.getStringExtra("objectID")
            et_note!!.setText(contentBefore)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> updateOrSaveData()
        }
        return true
    }

    private fun updateOrSaveData() {
        val contentAfter = et_note!!.text.toString()
        if (isAdd) {
            if (contentAfter == "") {
                finish()
            } else {
                val user = BmobUser.getCurrentUser(MyUser::class.java)
                val note = Note()
                note.content = contentAfter
                note.author = user

                val bmobACL = BmobACL()
                bmobACL.setReadAccess(user, true)
                bmobACL.setWriteAccess(user, true)

                note.acl = bmobACL
                note.save(object : SaveListener<String>() {
                    override fun done(s: String, e: BmobException?) {
                        if (e == null) {
                            Toast.makeText(this@NoteEditActivity, R.string.save_success, Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@NoteEditActivity, R.string.save_failed, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }

        } else {
            if (contentAfter == contentBefore) {
                finish()
            } else {
                val note = Note()
                note.content = contentAfter
                note.update(objectID, object : UpdateListener() {
                    override fun done(e: BmobException?) {
                        if (e == null) {
                            Toast.makeText(this@NoteEditActivity, R.string.update_success, Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@NoteEditActivity, R.string.save_failed, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }

    override fun onBackPressed() {
        updateOrSaveData()
    }
}
