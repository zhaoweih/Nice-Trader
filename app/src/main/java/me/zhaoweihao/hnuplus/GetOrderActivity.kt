package me.zhaoweihao.hnuplus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import kotlinx.android.synthetic.main.activity_get_order.*
import me.zhaoweihao.hnuplus.Adapter.OrderAdapter
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Bmob.Order
import org.jetbrains.anko.toast

class GetOrderActivity : AppCompatActivity() {

    companion object {
        private val TAG = "GetOrderActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_order)

        title = "收到订单"

        val user = BmobUser.getCurrentUser(MyUser::class.java)

        if (user == null){
            toast("你需要登录才能使用此功能")
            return
        }

        val userId = user.objectId

        val query = BmobQuery<Order>()
        query.addWhereEqualTo("sellerId", userId)
        query.setLimit(50)
        query.findObjects(object : FindListener<Order>() {
            override fun done(orders: List<Order>, e: BmobException?) {
                if (e == null) {
                    Log.d(TAG,"查询成功：共" + orders.size + "条数据。")
                    if (orders.isEmpty()){
                        toast("你当前没有任何订单信息")
                        return
                    }
                    val layoutManager = LinearLayoutManager(this@GetOrderActivity)
                    rv_get_order!!.layoutManager = layoutManager
                    val adapter = OrderAdapter(orders)
                    rv_get_order!!.adapter = adapter
                } else {
                    Log.d(TAG, "失败：" + e.message + "," + e.errorCode)
                }
            }
        })
    }
}
