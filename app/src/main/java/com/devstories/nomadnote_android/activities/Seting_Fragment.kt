package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.ChargeAction
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.Utils
import com.devstories.nomadnote_android.billing.IAPHelper
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.appevents.AppEventsLogger
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.FeedTemplate
import com.kakao.message.template.LinkObject
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.kakao.util.helper.log.Logger
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fra_setting.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class Seting_Fragment : Fragment() {
    lateinit var myContext: Context
    private lateinit var progressDialog: ProgressDialog

    var REQUEST_EXTERNAL_STORAGE_CODE = 1
    var permissionCheck = false
    private lateinit var activity: MainActivity

    var f_type = -1

    var s_type = -1

    var style = ""

    private lateinit var iapHelper: IAPHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext)
        return inflater.inflate(R.layout.fra_setting, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        progressDialog = ProgressDialog(context)

        activity = getActivity() as MainActivity
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(myContext)

        click()
        op_click()
        if (getArguments() != null) {
            s_type = getArguments()!!.getInt("type")
            if (s_type == 1) {
                travelLL.callOnClick()
            }
            if (s_type == 2) {
                memoryLL.callOnClick()
            }
        }

        Log.d("타입", s_type.toString())

        questLL.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO);
            emailIntent.data = Uri.parse("mailto:info@nomadnote.com");
            try {
              startActivity(emailIntent);
            } catch (e:ActivityNotFoundException) {
                e.printStackTrace()
            }
        }

        iapHelper = IAPHelper(activity, object : IAPHelper.BuyListener {

            override fun bought(sku: String, purchaseToken: String) {

                // System.out.println(sku + " bought!!!");

                if ("1GB" == sku) {
                    setCharge(1024*1024*1024, purchaseToken)
                } else if ("600M" == sku) {
                    setCharge(1024*1024*600, purchaseToken)
                }
            }

            override fun failed(e: Exception) {
                e.printStackTrace()

                Utils.alert(context, "구매 중 장애가 발생하였습니다. " + e.localizedMessage)
            }
        })

        buyTV.setOnClickListener {

            if(op_1gbLL.isSelected) {
                iapHelper.buy("1GB")
            } else if(op_600mbLL.isSelected) {
                iapHelper.buy("600M")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        arguments = null
        s_type = -1
    }

    fun op_click() {

        instaIV.setOnClickListener {
            shareInstagram()
        }
        facebookIV.setOnClickListener {
            shareFacebook()
        }
        naverIV.setOnClickListener {

        }
        kakaoIV.setOnClickListener {
            shareKakao()
        }


        //친구추가
        op_idLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                setmenu()
                op_idIV.setImageResource(R.mipmap.icon_check)
                f_type = 2
                var intent = Intent()
                intent.putExtra("type", f_type)
                intent.action = "FRIEND"
                myContext!!.sendBroadcast(intent)
            } else {
                op_idIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_addLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                setmenu()
                op_addIV.setImageResource(R.mipmap.icon_check)
                f_type = 3
                var intent = Intent()
                intent.putExtra("type", f_type)
                intent.action = "FRIEND"
                myContext!!.sendBroadcast(intent)
            } else {
                op_addIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_telLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                setmenu()
                op_telIV.setImageResource(R.mipmap.icon_check)
                f_type = 1
                var intent = Intent()
                intent.putExtra("type", f_type)
                intent.action = "FRIEND"
                myContext!!.sendBroadcast(intent)
            } else {
                op_telIV.setImageResource(R.drawable.circle_background3)
            }
        }

        //결제시스템
        op_1gbLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                setmenu2()
                op_1gbIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_1gbIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_600mbLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                setmenu2()
                op_600mbIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_600mbIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_20kbLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                setmenu2()
                op_20kbIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_20kbIV.setImageResource(R.drawable.circle_background3)
            }
        }


        //여행스타일
        healTV.setOnClickListener {
            style = "1"
            setStyleImage(style)
        }
        hotplaceTV.setOnClickListener {
            style = "2"
            setStyleImage(style)
        }
        cultureTV.setOnClickListener {
            style = "3"
            setStyleImage(style)
        }
        sidmierTV.setOnClickListener {
            style = "4"
            setStyleImage(style)
        }
        museumTV.setOnClickListener {
            style = "5"
            setStyleImage(style)
        }
        artTV.setOnClickListener {
            style = "6"
            setStyleImage(style)
        }
    }

    fun shareKakao() {
        val url = "market://details?id=donggolf.android"
        val imgBuilder = ContentObject.newBuilder("노마드 노트",
                Config.url + "/data/member/5c3cb2dc-c8a8-4351-9b29-16b9ac1f19c8",
                LinkObject.newBuilder().setWebUrl(url).setMobileWebUrl(url).build())
                .setDescrption("노마드 노트")
                .build()


        val builder = FeedTemplate.newBuilder(imgBuilder)
        builder.addButton(ButtonObject("노마드 노트", LinkObject.newBuilder()
                .setWebUrl(url)
                .setMobileWebUrl(url)
                .build()))

        val params = builder.build()

        KakaoLinkService.getInstance().sendDefault(myContext, params, object : ResponseCallback<KakaoLinkResponse>() {
            override fun onFailure(errorResult: ErrorResult) {
                Logger.e(errorResult.toString())
            }

            override fun onSuccess(result: KakaoLinkResponse) {

            }
        })


    }


    fun setstylemenu() {
        museumTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
        museumTV.setTextColor(Color.parseColor("#878787"))
        artTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
        artTV.setTextColor(Color.parseColor("#878787"))
        sidmierTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
        sidmierTV.setTextColor(Color.parseColor("#878787"))
        cultureTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
        cultureTV.setTextColor(Color.parseColor("#878787"))
        hotplaceTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
        hotplaceTV.setTextColor(Color.parseColor("#878787"))
        healTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
        healTV.setTextColor(Color.parseColor("#878787"))
    }


    fun shareFacebook() {
        val content = ShareLinkContent.Builder()

                //링크의 콘텐츠 제목
                .setContentTitle("페이스북 공유 링크입니다.")

                //게시물에 표시될 썸네일 이미지의 URL
                .setImageUrl(Uri.parse("https://lh3.googleusercontent.com/hmVeH1KmKDy1ozUlrjtYMHpzSDrBv9NSbZ0DPLzR8HdBip9kx3wn_sXmHr3wepCHXA=rw"))

                //공유될 링크
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.devstories.nomadnote_android"))

                //게일반적으로 2~4개의 문장으로 구성된 콘텐츠 설명
                .setContentDescription("문장1, 문장2, 문장3, 문장4")
                .build()

        val shareDialog = ShareDialog(this)
        shareDialog.show(content, ShareDialog.Mode.FEED)   //AUTOMATIC, FEED, NATIVE, WEB 등이 있으며 이는 다이얼로그 형식을 말합니다.
    }

    fun shareInstagram() {
        //외부저장 권한 요청(안드로이드 6.0 이후 필수)
        onRequestPermission()

        if (permissionCheck) {
            val bm = BitmapFactory.decodeResource(resources, R.drawable.kakao_default_profile_image)
            val storage = Environment.getExternalStorageDirectory().absolutePath
            val fileName = ".png"

            val folderName = "/nomadnote/"
            val fullPath = storage + folderName
            val filePath: File

            try {
                filePath = File(fullPath)
                if (!filePath.isDirectory) {
                    filePath.mkdirs()
                }
                val fos = FileOutputStream(fullPath + fileName)
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }


            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            val uri = Uri.fromFile(File(fullPath, fileName))
            try {
//                share.putExtra(Intent.EXTRA_STREAM, uri)
                share.putExtra(Intent.EXTRA_TEXT, "텍스트는 지원하지 않음!")
                share.setPackage("com.instagram.android")
                startActivity(share)
            } catch (e: ActivityNotFoundException) {
                Log.d("에러", e.toString())
                Toast.makeText(myContext, "인스타그램이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Log.d("에러", e.toString())
                e.printStackTrace()
            }

        }
    }

    fun onRequestPermission() {
        val permissionReadStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionWriteStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionReadStorage == PackageManager.PERMISSION_DENIED || permissionWriteStorage == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE_CODE)
        } else {
            permissionCheck = true //이미 허용되어 있으므로 PASS
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE_CODE -> for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]
                if (permission == android.Manifest.permission.READ_EXTERNAL_STORAGE) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(myContext, "허용했으니 가능함", Toast.LENGTH_LONG).show()
                        permissionCheck = true
                    } else {
                        Toast.makeText(myContext, "허용하지 않으면 공유 못함 ㅋ", Toast.LENGTH_LONG).show()
                        permissionCheck = false
                    }
                }
            }
        }
    }


    fun setmenu2() {
        op_1gbIV.setImageResource(R.drawable.circle_background3)
        op_600mbIV.setImageResource(R.drawable.circle_background3)
        op_20kbIV.setImageResource(R.drawable.circle_background3)
    }

    fun setmenu() {
        op_idIV.setImageResource(R.drawable.circle_background3)
        op_addIV.setImageResource(R.drawable.circle_background3)
        op_telIV.setImageResource(R.drawable.circle_background3)
    }

    fun click() {

        nationLL.setOnClickListener {
            val intent = Intent(myContext, VisitNationActivity::class.java)
            startActivity(intent)
        }


        logoutLL.setOnClickListener {
            val intent = Intent(myContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            PrefUtils.clear(myContext)
            startActivity(intent)
        }
        myinfochangeLL.setOnClickListener {
            val intent = Intent(myContext, MyinfoChangeActivity::class.java)
            startActivity(intent)
        }

        style = PrefUtils.getIntPreference(context, "style").toString()
        travelLL.setOnClickListener {
            if (op_travelLL.visibility == View.GONE) {
                setStyleImage(style)
                op_travelLL.visibility = View.VISIBLE
                styleIV.rotation = 90f
            } else {
                op_travelLL.visibility = View.GONE
                styleIV.rotation = 0f
            }

        }

        memoryLL.setOnClickListener {
            if (op_memoryLL.visibility == View.GONE) {
                op_memoryLL.visibility = View.VISIBLE
                memoryIV.rotation = 90f

                if (PrefUtils.getIntPreference(context, "payment_byte") != null) {
                    var payment_byte = PrefUtils.getIntPreference(context, "payment_byte")
                    var disk = PrefUtils.getIntPreference(context, "disk")

                    println("------$payment_byte , $disk")

                    var pay_sub = payment_byte.toString().substring(0, 1)
                    if (pay_sub == "-") {
                        var pay_split = payment_byte.toString().split("-")
                        println("---pay-split${pay_split.get(0)}")
                        if (pay_split.get(0) == "-") {
                            payment_byte = pay_split.get(1).toInt()
                            println("-split payment_byte $payment_byte")
                        }
                    }


                    var disk_sub = payment_byte.toString().substring(0, 1)
                    if (disk_sub == "-") {
                        println("---pay-split${disk_sub.get(0)}")
                        var disk_split = disk.toString().split("-")
                        if (disk_split.get(0) == "-") {
                            disk = disk_split.get(1).toInt()
                            println("-split disk $disk")
                        }
                    }

                    var max = Math.round((payment_byte / (1024 * 1024 * 1024) * 10).toDouble()) as Long / 10

                    var rament = Math.round((disk / (1024 * 1024) * 10).toDouble()) as Long / 10

                    var payment = Math.round((payment_byte / (1024 * 1024) * 10).toDouble()) as Long / 10

                    var percent = payment_byte - disk
                    var div = percent / payment_byte
                    progressPB.setProgress(Math.abs(div).toInt())

                    var dif = payment - rament

                    var maxabs = Math.abs(max)
                    var ramentabs = Math.abs(rament)
                    var paymentabs = Math.abs(dif)

                    println("-----rament$rament")

                    mydataTV.setText("총 " + maxabs.toString() + "GB")
                    remantTV.setText(paymentabs.toString() + "MB")
                    useTV.setText(ramentabs.toString() + "MB")
                }

            } else {
                op_memoryLL.visibility = View.GONE
                memoryIV.rotation = 0f
            }

        }

        friendaddLL.setOnClickListener {
            if (op_friendaddLL.visibility == View.GONE) {
                op_friendaddLL.visibility = View.VISIBLE
                friendaddIV.rotation = 90f
            } else {
                op_friendaddLL.visibility = View.GONE
                friendaddIV.rotation = 0f
            }

        }

        payLL.setOnClickListener {
            if (op_payLL.visibility == View.GONE) {
                op_payLL.visibility = View.VISIBLE
                payIV.rotation = 90f
            } else {
                op_payLL.visibility = View.GONE
                payIV.rotation = 0f
            }
        }

        snsLL.setOnClickListener {
            if (op_snsLL.visibility == View.GONE) {
                op_snsLL.visibility = View.VISIBLE
                snsIV.rotation = 90f
            } else {
                op_snsLL.visibility = View.GONE
                snsIV.rotation = 0f
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }


    fun setStyleImage(style_id: String) {
        setstylemenu()

        if (style_id == "1") {
            healTV.setBackgroundResource(R.drawable.background_border_radius10)
            healTV.setTextColor(Color.parseColor("#ffffff"))
        } else if (style_id == "2") {
            hotplaceTV.setBackgroundResource(R.drawable.background_border_radius10)
            hotplaceTV.setTextColor(Color.parseColor("#ffffff"))
        } else if (style_id == "3") {
            cultureTV.setBackgroundResource(R.drawable.background_border_radius10)
            cultureTV.setTextColor(Color.parseColor("#ffffff"))
        } else if (style_id == "4") {
            sidmierTV.setBackgroundResource(R.drawable.background_border_radius10)
            sidmierTV.setTextColor(Color.parseColor("#ffffff"))
        } else if (style_id == "5") {
            museumTV.setBackgroundResource(R.drawable.background_border_radius10)
            museumTV.setTextColor(Color.parseColor("#ffffff"))
        } else if (style_id == "6") {
            artTV.setBackgroundResource(R.drawable.background_border_radius10)
            artTV.setTextColor(Color.parseColor("#ffffff"))
        }

        PrefUtils.setPreference(context, "style", style_id.toInt())
        edit_style()
    }

    fun edit_style() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("style", style)

        MemberAction.update_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

//                        Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()


                    } else {

//                        Toast.makeText(context, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
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

                // System.out.println(responseString);

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


    private fun setCharge(quota: Int, purchaseToken: String) {

        val params = RequestParams()

        ChargeAction.setCharge(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null && !activity.isFinishing()) {
                    progressDialog.dismiss()
                }

                try {

                    // System.out.println(response);

                    val result = Utils.getInt(response, "return")

                    if (result == 1) {
                        // ok

                        iapHelper.consume(purchaseToken)

                    } else if (result == 0) {
                        // error
                        Toast.makeText(context, Utils.getString(response, "error"), Toast.LENGTH_LONG).show()
                        return
                    } else {
                        Toast.makeText(context, "오류가 발생하였습니다.", Toast.LENGTH_LONG).show()
                        return
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {}

            private fun error() {
                if (progressDialog != null && !activity.isFinishing()) {
                    Utils.alert(context, "처리중 장애가 발생하였습니다.")
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null && !activity.isFinishing()) {
                    progressDialog.dismiss()
                }

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null && !activity.isFinishing()) {
                    progressDialog.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null && !activity.isFinishing()) {
                    progressDialog.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null && !activity.isFinishing()) {
                    progressDialog.setMessage("처리 중...")
                    progressDialog.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null && !activity.isFinishing()) {
                    progressDialog.dismiss()
                }
            }
        })
    }


}
