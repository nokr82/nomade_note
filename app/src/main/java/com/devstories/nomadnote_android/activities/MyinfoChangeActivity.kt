package com.devstories.nomadnote_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_info_change.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.IOException

class MyinfoChangeActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var name = ""
    var age = 0
    var phone = ""
    var gender = ""

    var MALE = 100
    var FEMALE = 101

    var maleimage: Bitmap? = null
    var femaleimage : Bitmap? = null
    var myprofile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_change)
        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
//        progressDialog = ProgressDialog(context)

        titleBackLL.setOnClickListener {
            finish()
        }

        manLL.setOnClickListener {
            setmenu()
            manIV.visibility = View.VISIBLE
            gender = "M"
        }
        femaleLL.setOnClickListener {
            setmenu()
            femaleckIV.visibility = View.VISIBLE
            gender = "F"
        }

        manLL.setOnLongClickListener {
            permissionmale()
            true
        }

        femaleLL.setOnLongClickListener {
            permissionfemale()
            true
        }

        editTV.setOnClickListener {
            name = Utils.getString(nameET)
            age = Utils.getInt(ageET)
            phone = Utils.getString(phoneET)
            edit_profile()
        }

        val point = PrefUtils.getIntPreference(context, "point")
        mypointTV.setText(getString(R.string.my_points) + " : " + point + "P")

        loadInfo()

    }

    fun setmenu(){
        manIV.visibility= View.INVISIBLE
        femaleckIV.visibility = View.INVISIBLE
    }

    fun edit_profile(){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("name",name)
        params.put("age", age)
        params.put("gender", gender)
        params.put("phone", phone)

        if (myprofile != ""){
//            var uri = Config.url + myprofile
//            val image = Utils.getImage(context.contentResolver,uri)
//            params.put("photo", ByteArrayInputStream(Utils.getByteArray(image)))
        }
        if (maleimage != null){
            params.put("photo", ByteArrayInputStream(Utils.getByteArray(maleimage)))
        }
        if (femaleimage != null){
            params.put("photo", ByteArrayInputStream(Utils.getByteArray(femaleimage)))
        }

        MemberAction.update_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()

                    } else {

                        Toast.makeText(context, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

//                 System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
//                println("--------$errorResponse")
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }
    //사용자정보
    fun loadInfo() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))

        MemberAction.my_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                Log.d("결과",response.toString())

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        var member = response.getJSONObject("member")
                        name =  Utils.getString(member, "name")
                        age =  Utils.getInt(member, "age")
                        gender =  Utils.getString(member, "gender")
                        phone = Utils.getString(member,"phone")
                        myprofile = Utils.getString(member,"profile")
//                        val disk =member.getJSONArray("disk")
                        val payment_sum = Utils.getInt(member,"payment_sum")

                        if (gender == "M"){
                            manIV.visibility = View.VISIBLE
                        }else if (gender =="F"){
                            femaleckIV.visibility = View.VISIBLE
                        }
                        nameET.setText(name)

                        println("-----age----$age , $phone")
                        if (Utils.getInt(member,"age") != -1 && Utils.getInt(member,"age") != 1) {
                            ageET.setText(age.toString())
                        }

                        if (Utils.getInt(member,"phone") != -1 && Utils.getInt(member,"phone") != 1){
                            phoneET.setText(phone)
                        }

                        if (myprofile != ""){
                            if (gender == "M") {
                                var uri = Config.url  + myprofile
                                ImageLoader.getInstance().displayImage(uri, maleIV, Utils.UILoptionsUserProfile)
                            } else {
                                var uri = Config.url + myprofile
                                ImageLoader.getInstance().displayImage(uri, femaleIV, Utils.UILoptionsUserProfile)
                            }
                        }

//                        var disk_byte = 1073741824
                   /*     if (disk.length()>0){
                            for (i in 0 until disk.length()){
                                val disk_item = disk.get(i) as JSONObject
                                val category = Utils.getInt(disk_item,"category")

                                if (category == 1){
                                    disk_byte += 1073741824
                                } else if (category == 2){
                                    disk_byte += 644245094
                                } else {
                                    disk_byte += 21474836
                                }

                            }
                        }*/

//                        PrefUtils.setPreference(context, "disk", disk_byte)
//                        PrefUtils.setPreference(context, "payment_byte", payment_sum)

                    } else {
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {
                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

    private fun permissionmale() {

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                setmenu()
                manIV.visibility = View.VISIBLE
                gender = "M"

                val galleryIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(galleryIntent, MALE)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {

            }

        }

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

    }


    private fun permissionfemale() {

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                setmenu()
                femaleckIV.visibility = View.VISIBLE
                gender = "F"

                val galleryIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(galleryIntent, FEMALE)

            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {

            }

        }

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                MALE -> {
                    if (data != null)
                    {
                        val contentURI = data.data
                        Log.d("uri",contentURI.toString())
                        try
                        {

                            val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)

                            val cursor = context.contentResolver.query(contentURI, filePathColumn, null, null, null)
                            if (cursor!!.moveToFirst()) {
                                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                                val picturePath = cursor.getString(columnIndex)

                                cursor.close()

                                ImageLoader.getInstance().displayImage(contentURI.toString(), maleIV, Utils.UILoptionsProfile)

                                femaleIV.setImageResource(R.mipmap.famal)
                                maleimage = Utils.getImage(context.contentResolver,picturePath)
                                println("-----maleimage---- $maleimage")
                                femaleimage = null

                            }

                        }
                        catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(context, "바꾸기실패", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                FEMALE -> {
                    if (data != null)
                    {
                        val contentURI = data.data
                        Log.d("uri",contentURI.toString())
                        try
                        {

                            val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)

                            val cursor = context.contentResolver.query(contentURI, filePathColumn, null, null, null)
                            if (cursor!!.moveToFirst()) {
                                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                                val picturePath = cursor.getString(columnIndex)

                                cursor.close()

                                ImageLoader.getInstance().displayImage(contentURI.toString(), femaleIV, Utils.UILoptionsProfile)
                                maleIV.setImageResource(R.mipmap.man)

                                maleimage = null
                                femaleimage = Utils.getImage(context.contentResolver,picturePath)

                            }

                        }
                        catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(context, "바꾸기실패", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

            }
        }

    }

    override fun onBackPressed() {
        finish()
        Utils.hideKeyboard(context)
    }

}
