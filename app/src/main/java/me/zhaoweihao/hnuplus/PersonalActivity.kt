package me.zhaoweihao.hnuplus

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import cn.bmob.v3.BmobUser
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.activity_personal.*
import me.zhaoweihao.hnuplus.Bmob.MyUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import cn.bmob.v3.listener.UploadFileListener
import cn.bmob.v3.datatype.BmobFile
import java.io.File


class PersonalActivity : AppCompatActivity() {

    companion object {
        private val TAG = "PersonalActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)

        btn_change_avatar.setOnClickListener {
            // 请求运行时权限申请
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                showImageSelector()
            }
            val user = BmobUser.getCurrentUser(MyUser::class.java)

        }
    }

    private fun showImageSelector(){
        Matisse.from(this)
                .choose(setOf(MimeType.JPEG, MimeType.PNG))
                .countable(true)
                .maxSelectable(1)
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
                Log.d(TAG,"用户拒绝了权限申请")
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            2 -> if (resultCode == RESULT_OK) {
                //uri to path
                var path: String? = null
                val uri = Matisse.obtainResult(data)[0]//你需要转换的uri
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
                if (cursor!!.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    path = cursor.getString(columnIndex)//输出的path
                    Log.d(TAG,"输出的path为："+path)
                    uploadAvatar(path)
                } else {
                    //boooo, cursor doesn't have rows ...
                }
                cursor.close()

            }
        }
    }

    private  fun uploadAvatar(path: String){

        val bmobFile = BmobFile(File(path))
        bmobFile.uploadblock(object : UploadFileListener() {

            override fun done(e: BmobException?) {
                if (e == null) {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Log.d(TAG,"上传文件成功:" + bmobFile.fileUrl)
                    setAvatar(bmobFile.fileUrl)
                } else {
                    Log.d(TAG,"上传文件失败：" + e.message)
                }

            }

            override fun onProgress(value: Int?) {
                // 返回的上传进度（百分比）
            }
        })

    }

    private fun setAvatar(url:String){
        val newUser = MyUser()
        newUser.userAvatar= url
        val bmobUser = BmobUser.getCurrentUser(MyUser::class.java)
        newUser.update(bmobUser.objectId, object : UpdateListener() {
            override fun done(e: BmobException?) {
                if (e == null) {
                    Log.d(TAG,"更新用户信息成功")
                } else {
                    Log.d(TAG,"更新用户信息失败:" + e.message)
                }
            }
        })
    }
}
