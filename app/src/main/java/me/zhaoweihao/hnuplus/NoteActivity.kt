package me.zhaoweihao.hnuplus


import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast

import com.taishi.flipprogressdialog.FlipProgressDialog

import java.util.ArrayList
import java.util.Collections
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import kotlinx.android.synthetic.main.activity_note.*
import me.zhaoweihao.hnuplus.Adapter.NoteAdapter
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Bmob.Note
import me.zhaoweihao.hnuplus.Utils.Utility
import org.jetbrains.anko.toast

class NoteActivity : AppCompatActivity() {

    private var noteAdapter: NoteAdapter? = null
    private var flipProgressDialog: FlipProgressDialog? = null

    companion object {
        private val TAG = "NoteActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val user = BmobUser.getCurrentUser(MyUser::class.java)

        if (user == null){
            toast("你需要登录才能使用云笔记功能")
            return
        }

        flipProgressDialog = Utility.myDialog()
        flipProgressDialog!!.show(fragmentManager, "")

        title = getString(R.string.note_title)

        updateData()

    }

    private fun updateData() {
        val bmobQuery = BmobQuery<Note>()
        bmobQuery.findObjects(object : FindListener<Note>() {
            override fun done(list: MutableList<Note>, e: BmobException?) {
                if (e == null) {
                    if (list.size==0){
                        toast("你还没有任何笔记")
                        showOneItem()
                        return
                    }
                    Log.d(TAG, list[0].content)
                    val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    rv_notes!!.layoutManager = layoutManager
                    rv_notes!!.isNestedScrollingEnabled = false
                    val note = Note()
                    note.content = ""
                    list.add(note)
                    Collections.reverse(list)
                    noteAdapter = NoteAdapter(list, this@NoteActivity)
                    rv_notes!!.adapter = noteAdapter
                    ll_note_bottom!!.visibility = View.VISIBLE
                    flipProgressDialog!!.dismiss()
                } else {
                    Log.d(TAG, "发生异常")
                    showOneItem()

                }
            }
        })
    }

    fun showDeleteDialog(objectID: String) {
        val strings = arrayOf(getString(R.string.delete_warn))
        AlertDialog.Builder(this)
                .setItems(strings) { dialogInterface, i ->
                    if (i == 0) {
                        val note = Note()
                        note.objectId = objectID
                        note.delete(object : UpdateListener() {
                            override fun done(e: BmobException?) {
                                if (e == null) {
                                    Toast.makeText(this@NoteActivity, R.string.delete_success, Toast.LENGTH_SHORT).show()
                                    updateData()
                                } else {
                                    Toast.makeText(this@NoteActivity, R.string.delete_failed, Toast.LENGTH_SHORT).show()
                                }

                            }
                        })
                    }
                }
                .create().show()
    }

    fun showOneItem(){
        val noteList = ArrayList<Note>()
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv_notes!!.layoutManager = layoutManager
        rv_notes!!.isNestedScrollingEnabled = false
        val note = Note()
        note.content = ""
        noteList.add(note)
        noteAdapter = NoteAdapter(noteList, this@NoteActivity)
        rv_notes!!.adapter = noteAdapter
        ll_note_bottom!!.visibility = View.VISIBLE
        flipProgressDialog!!.dismiss()
    }

    override fun onRestart() {
        super.onRestart()
        flipProgressDialog!!.show(fragmentManager, "")
        updateData()
        Log.d(TAG, "onRestart")
    }
}
