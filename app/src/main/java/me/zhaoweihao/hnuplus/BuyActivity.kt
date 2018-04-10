package me.zhaoweihao.hnuplus

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cn.bmob.v3.BmobUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_buy.*
import me.zhaoweihao.hnuplus.Bmob.MyUser

class BuyActivity : AppCompatActivity() {

    companion object {
       private val TAG = "BuyAcitivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)

        title = "购买(测试版)"

        val intent = intent

        val title = intent.getStringExtra("title")
        val firstImageUrl = intent.getStringExtra("firstImageUrl")
        val price = intent.getStringExtra("price")
        val objectId = intent.getStringExtra("objectId")
        val sellerId = intent.getStringExtra("sellerId")

        Log.d(TAG, "$title $firstImageUrl $price")

        tv_buy_title.text = title
        tv_buy_price.text = price
        tv_buy_price_2.text = price

        Picasso.with(this)
                .load(firstImageUrl)
                .resize(70, 70)
                .centerCrop()
                .into(iv_buy_image)

        btn_buy_sure.setOnClickListener {

            val userInfo = BmobUser.getCurrentUser(MyUser::class.java)
            val buyerId = userInfo.objectId
            val address = et_address.text.toString()

            val intent = Intent(this,OrderActivity::class.java)
            intent.putExtra("buyerId",buyerId)
            intent.putExtra("goodId",objectId)
            intent.putExtra("sellerId",sellerId)
            intent.putExtra("address",address)
            intent.putExtra("price",price)
            intent.putExtra("goodName",title)
            intent.putExtra("imageUrl",firstImageUrl)
            startActivity(intent)

        }


    }

}
