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
import com.devstories.nomadnote_android.actions.JoinAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_member_infoinput.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.IOException

class MemberInputActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var pw = ""
    var email = ""

    var gender = ""
    var name  = ""
    var age = ""
    var phone = ""

    var FEMALE = 100
    var MALE = 101

    var maleimage: Bitmap? = null
    var femaleimage : Bitmap? = null
    var myprofile = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_infoinput)
        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
//        progressDialog = ProgressDialog(context)
        setmenu()


        intent = getIntent()
        email = intent.getStringExtra("email")
        pw =  intent.getStringExtra("pw")

        click()



    }

    fun click(){
        manLL.setOnClickListener {
            setmenu()
            gender = "M"
            manckIV.visibility = View.VISIBLE
        }
        femaleLL.setOnClickListener {
            setmenu()
            gender = "F"
            femaleckIV.visibility = View.VISIBLE
        }
        backIV.setOnClickListener {
            finish()
        }

        manLL.setOnLongClickListener {
            permissionmale()
            true
        }

        femaleLL.setOnLongClickListener {
            permissionfemale()
            true
        }


        startTV.setOnClickListener {
            name = Utils.getString(nameET)
            age = Utils.getString(ageET)
            phone = Utils.getString(phoneET)

            if (name.equals("")){
                Toast.makeText(context, getString(R.string.enteryourname), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (age.equals("")){
                Toast.makeText(context, "나이를 입력해주세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (gender.equals("")){
                Toast.makeText(context, getString(R.string.chooseyourgender), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (phone.equals("")){
                Toast.makeText(context, getString(R.string.enter_phonenumber), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            join()
        }
    }
    fun setmenu(){
        manckIV.visibility = View.INVISIBLE
        femaleckIV.visibility = View.INVISIBLE
    }



    fun join() {
        val params = RequestParams()
        params.put("name", name)
        params.put("email",email )
        params.put("gender", gender)
        params.put("age",age )
        params.put("passwd",pw)
        params.put("phone",phone)
        params.put("join_type", 5)

        if (maleimage != null){
            params.put("photo", ByteArrayInputStream(Utils.getByteArray(maleimage)))
        }
        if (femaleimage != null){
            params.put("photo", ByteArrayInputStream(Utils.getByteArray(femaleimage)))
        }


        JoinAction.join(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONObject("member")
                        PrefUtils.setPreference(context, "member_id", Utils.getInt(data, "id"))

                        Utils.hideKeyboard(context)

                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        Toast.makeText(context, "가입성공", Toast.LENGTH_LONG).show()
                        startActivity(intent)
                    } else {



                        Toast.makeText(context, response!!.getString("message"), Toast.LENGTH_LONG).show()

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

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    throwable: Throwable,
                    errorResponse: JSONObject?
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    throwable: Throwable,
                    errorResponse: JSONArray?
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
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
                manckIV.visibility = View.VISIBLE
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
