package me.zhaoweihao.hnuplus.Adapter

import android.content.Context

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso
import me.zhaoweihao.hnuplus.Bmob.Order
import me.zhaoweihao.hnuplus.R

/**
 * Show post's data to recyclerview
 */
class OrderAdapter(private val mOrderList: List<Order>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    companion object {
        private val TAG = "OrderAdapter"
    }

    private var mContext: Context? = null


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var goodImage = view.findViewById<ImageView>(R.id.iv_goodimage)
        var goodName = view.findViewById<TextView>(R.id.tv_goodname)
        var goodPrice = view.findViewById<TextView>(R.id.tv_goodprice)
        var address = view.findViewById<TextView>(R.id.tv_address)
        var orderView: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        Log.d(TAG, "onCreateViewHolder")
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.myorder_item,parent, false)
        val holder = ViewHolder(view)
                holder.orderView.setOnClickListener {
                    val position = holder.adapterPosition
                    val post = mOrderList[position]

                }


        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mOrderList[position]

        val imageUrl = post.imageUrl
        val goodName = post.goodName
        val goodPrice = post.price
        val address = post.address

        Picasso.with(mContext)
                .load(imageUrl)
                .resize(70, 70)
                .centerCrop()
                .into(holder.goodImage)

        holder.address.text = "地址："+address
        holder.goodName.text = "商品名称："+goodName
        holder.goodPrice.text = "商品价格:"+goodPrice


        Log.d(TAG, "onBindViewHolder")

    }

    override fun getItemCount(): Int {
        return mOrderList.size
    }


}