package me.zhaoweihao.hnuplus.Adapter


import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import me.zhaoweihao.hnuplus.Bmob.Note
import me.zhaoweihao.hnuplus.NoteActivity
import me.zhaoweihao.hnuplus.NoteEditActivity
import me.zhaoweihao.hnuplus.R

/**
 * Created by ZhaoWeihao on 2018/1/10.
 */

class NoteAdapter(private val mNoteList: List<Note>, private val mContext: Context) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    companion object {
        private val TAG = "NoteAdapter"
    }

    class ViewHolder(var noteView: View) : RecyclerView.ViewHolder(noteView) {
        var textView: TextView = noteView.findViewById(R.id.tv_notes)
        var cardView: CardView = noteView.findViewById(R.id.cv_notes)
        var imageView: ImageView = noteView.findViewById(R.id.iv_notes)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.note_item, parent, false)
        val holder = ViewHolder(view)
        holder.noteView.setOnClickListener {
            val position = holder.adapterPosition
            val note = mNoteList[position]

            Log.d(TAG, position.toString() + " " + mNoteList.size.toString())

            val intent = Intent(mContext, NoteEditActivity::class.java)
            if (position == 0) {
                intent.putExtra("data", true)
            } else {
                intent.putExtra("data", false)
                intent.putExtra("objectID", note.objectId)
                intent.putExtra("content", note.content)
                Log.d(TAG, note.content)
            }
            mContext.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: NoteAdapter.ViewHolder, position: Int) {
        val note = mNoteList[position]
        if (position == 0) {
            holder.imageView.visibility = View.VISIBLE
        }

        holder.textView.text = note.content
        holder.noteView.setOnLongClickListener {
            if (position == 0) {

            } else {
                Log.d(TAG, position.toString())
                (mContext as? NoteActivity)?.showDeleteDialog(mNoteList[position].objectId)
            }

            true
        }
    }

    override fun getItemCount(): Int {
        return mNoteList.size
    }

}
