package me.zhaoweihao.hnuplus

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Fragment
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import com.bumptech.glide.Glide
import com.lzy.ninegrid.ImageInfo
import com.lzy.ninegrid.preview.NineGridViewClickAdapter
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.yoavst.kotlin.`KotlinPackage$Toasts$53212cf1`.toast
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.activity_buy.*
import kotlinx.android.synthetic.main.fragment_post.*
import me.zhaoweihao.hnuplus.Interface.PostInterface
import org.jetbrains.anko.toast

/**
 * Created by ZhaoWeihao on 2017/11/9.
 */

class PostFragment :  Fragment(), PostInterface,TencentLocationListener{

    private var addressStr: String? = null


    companion object {
        private val TAG  = "PostFragment"
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_post, container,
                false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (activity.checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 0)
            } else {
                val request = TencentLocationRequest.create()
                val locationManager = TencentLocationManager.getInstance(activity)
                val error = locationManager.requestLocationUpdates(request, this)
            }
        }

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_pic!!.setOnClickListener {
            // 请求运行时权限申请
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                showImageSelector()
            }

        }
    }

    private fun showImageSelector(){
        Matisse.from(activity)
                .choose(setOf(MimeType.JPEG,MimeType.PNG))
                .countable(true)
                .maxSelectable(9)
                .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .forResult(2)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageSelector()
            } else {
                toast(activity, "denied")
            }
        }
    }

    override fun myMethod() {
        val intent = Intent()

        when {
            et_post_title.text.isEmpty() -> {
                toast("标题不能为空")
                return
            }
            et_post_price.text.isEmpty() -> {
                toast("价格不能为空")
                return
            }
            et_post_detail.text.isEmpty() -> {
                toast("细节不能为空")
                return
            }
            tv_location.text.isEmpty() -> {
                toast("地址不能为空")
                return
            }

            (activity as PostActivity).paths[0] == null -> {
                intent.putExtra("post_title", et_post_title.text.toString())
                intent.putExtra("post_detail", et_post_detail.text.toString())
                intent.putExtra("post_location", tv_location.text.toString())
                intent.putExtra("post_price", et_post_price.text.toString())
                activity.setResult(RESULT_OK,intent)
                activity.finish()
            }
            else -> {
                Log.d(TAG,"have photo")
                intent.putExtra("post_title", et_post_title.text.toString())
                intent.putExtra("post_detail", et_post_detail.text.toString())
                intent.putExtra("post_location", tv_location.text.toString())
                intent.putExtra("post_price", et_post_price.text.toString())
                intent.putExtra("data_return_2",(activity as PostActivity).paths)
                activity.setResult(RESULT_OK,intent)
                activity.finish()
            }
        }
    }

    override fun showImage(imageUris: List<Uri>) {
        btn_pic.visibility = View.INVISIBLE
        val imageInfo = ArrayList<ImageInfo>()

        val length = imageUris.size

        Log.d(TAG,length.toString())

        for (imageUri in imageUris) {
            Log.d(TAG, imageUri.toString())
            val info = ImageInfo()
            info.setThumbnailUrl(imageUri.toString())
            info.setBigImageUrl(imageUri.toString())
            imageInfo.add(info)
        }

        iv_show_pic.setAdapter(NineGridViewClickAdapter(activity, imageInfo))
        iv_show_pic.setOnClickListener { showImageSelector() }
    }

    override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {

    }

    override fun onLocationChanged(tencentLocation: TencentLocation?, error: Int, reason: String?) {
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            Log.d(TAG, tencentLocation!!.address)
            addressStr = tencentLocation.address
            tv_location.text = addressStr
            //删除注册的监听器
            val locationManager = TencentLocationManager.getInstance(activity)
            locationManager.removeUpdates(this)
        } else {
            // 定位失败
        }
    }



}