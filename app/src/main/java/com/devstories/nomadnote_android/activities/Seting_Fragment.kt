package com.devstories.nomadnote_android.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
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
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fra_setting.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Seting_Fragment : Fragment() {
    lateinit var myContext: Context
    private lateinit var progressDialog: ProgressDialog

    var REQUEST_EXTERNAL_STORAGE_CODE = 1
    var permissionCheck = false
    private lateinit var activity: MainActivity
    val Solo_time_Fragment : Solo_time_Fragment = Solo_time_Fragment()
    var f_type = -1

    var s_type = -1

    var style = ""

    private lateinit var iapHelper: IAPHelper



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
//        progressDialog = ProgressDialog(myContext)
        progressDialog = ProgressDialog(myContext, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
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
//                stylebackIV.visibility = View.VISIBLE
                activity.titleBackLL.visibility = View.VISIBLE
            }
            if (s_type == 2) {
                memoryLL.callOnClick()
            }
        }

        activity.titleBackLL.setOnClickListener {
            if (s_type == 1) {
                activity.supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Solo_time_Fragment).commit()
//            stylebackIV.visibility = View.GONE
                op_travelLL.visibility = View.GONE
                activity.soloIV.setImageResource(R.mipmap.op_solo)
                activity.soloTV.setTextColor(Color.parseColor("#0c6e87"))

                activity.settingIV.setImageResource(R.mipmap.setting)
                activity.settingTV.setTextColor(Color.parseColor("#878787"))

                activity.logoIV.visibility = View.VISIBLE
                activity.titleLL.visibility = View.GONE
                activity.titleBackLL.visibility = View.GONE
                s_type = -1
            }
        }

        Log.d("타입", s_type.toString())

        settingquestLL.setOnClickListener {
            val intent = Intent(context, QuestionActivity::class.java)
            startActivity(intent)
        }

        iapHelper = IAPHelper(activity, object : IAPHelper.BuyListener {

            override fun bought(sku: String, purchaseToken: String) {

                println("$sku bought!!!");

                if ("1gb" == sku) {
                    setCharge(1024*1024*1024, purchaseToken)
                } else if ("600mb" == sku) {
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
                iapHelper.buy("1gb")
            } else if(op_600mbLL.isSelected) {
                iapHelper.buy("600mb")
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
            shareNaverBlog()
        }
        kakaoIV.setOnClickListener {
            shareKakaoStory()
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

            op_600mbLL.isSelected = false
            op_20kbLL.isSelected = false

            if (it.isSelected) {
                setmenu2()
                op_1gbIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_1gbIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_600mbLL.setOnClickListener {
            it.isSelected = !it.isSelected

            op_1gbLL.isSelected = false
            op_20kbLL.isSelected = false

            if (it.isSelected) {
                setmenu2()
                op_600mbIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_600mbIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_20kbLL.setOnClickListener {
            it.isSelected = !it.isSelected

            op_1gbLL.isSelected = false
            op_600mbLL.isSelected = false

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
                    var disk = PrefUtils.getDoublePreference(context, "disk")
                    var byte = PrefUtils.getIntPreference(context, "byte")

                    println("------$payment_byte , $disk")

//                    var pay_sub = payment_byte.toString().substring(0, 1)
//                    if (pay_sub == "-") {
//                        var pay_split = payment_byte.toString().split("-")
//                        println("---pay-split${pay_split.get(0)}")
//                        if (pay_split.get(0) == "-") {
//                            payment_byte = pay_split.get(1).toInt()
//                            println("-split payment_byte $payment_byte")
//                        }
//                    }


                    var disk_sub = payment_byte.toString().substring(0, 1)
                    if (disk_sub == "-") {
                        println("---pay-split${disk_sub.get(0)}")
                        var disk_split = disk.toString().split("-")
                        if (disk_split.get(0) == "-") {
                            disk = disk_split.get(1).toDouble()
                            println("-split disk $disk")
                        }
                    }

//                    var max = Math.round((payment_byte / (1024 * 1024 * 1024) * 10).toDouble()) as Long / 10
                    var max = Math.round((disk / (1024 * 1024 * 1024) * 10).toDouble()) as Long / 10
                    var maxgb = Math.round((disk / (1024 * 1024) * 10).toDouble()) as Long / 10
                    var maxkb = Math.round((disk / (1024) * 10).toDouble()) as Long / 10


//                    var rament = Math.round((disk / (1024 * 1024) * 10).toDouble()) as Long / 10
                    var rament = Math.round((byte / (1024 * 1024) * 10).toDouble()) as Long / 10
                    var ramentkb = Math.round((byte / (1024) * 10).toDouble()) as Long / 10

//                    var payment = Math.round((payment_byte / (1024 * 1024) * 10).toDouble()) as Long /
                    var payment = Math.round((disk / (1024 * 1024) * 10).toDouble()) as Long / 10
                    var paymentkb =  Math.round((disk / (1024) * 10).toDouble()) as Long / 10

//                    var percent = payment_byte - disk
                    var percent = disk - byte

//                    var div = percent / payment_byte
                    var div = percent / (byte*100)

                    progressPB.setMax(disk.toInt())
//                    progressPB.setProgress(Math.abs(div).toInt())
                    progressPB.setProgress(byte)

                    var dif = payment - rament
                    var difkb = paymentkb - ramentkb

                    var maxabs = Math.abs(max)
                    var ramentabs = Math.abs(rament)
                    var ramentabskb = Math.abs(ramentkb)
                    var paymentabs = Math.abs(dif)
                    var paymentabskb = Math.abs(difkb)

                    println("-----rament$rament")

                    if (maxabs.toInt() == 0){
                        if (maxgb.toInt() == 0){
                            mydataTV.setText("총 " + Math.abs(maxkb).toString() + "KB")
                            progressPB.setMax(maxkb.toInt())
                            progressPB.setProgress(ramentabskb.toInt())
                        } else {
                            mydataTV.setText("총 " + Math.abs(maxgb).toString() + "MB")
                        }
                    } else {
                        mydataTV.setText("총 " + maxabs.toString() + "GB")
                    }

                    println("--------------paymentsabs $paymentabs")
                    println("--------------ramentabs $ramentabs")

                    if (paymentabs == 0.toLong()){
                        remantTV.setText(paymentabskb.toString()  + "KB")
                    } else {
                        remantTV.setText(paymentabs.toString() + "MB")
                    }

                    if (ramentabs == 0.toLong()){
                        useTV.setText(ramentabskb.toString() + "KB")
                    } else {
                        useTV.setText(ramentabs.toString() + "MB")
                    }
//                    mydataTV.setText("총 " + maxabs.toString() + "GB")
//                    remantTV.setText(paymentabs.toString() + "MB")
//                    useTV.setText(ramentabs.toString() + "MB")
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

        deleteLL.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder
                    .setMessage("정말로 탈퇴 하시겠습니까 ?")

                    .setPositiveButton("예", DialogInterface.OnClickListener { dialog, id ->
                        delete_member()
                        Utils.hideKeyboard(context)
                    })
                    .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                        Utils.hideKeyboard(context)
                    })
            val alert = builder.create()
            alert.show()
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
        params.put("quota", quota)
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))

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

                        val intent = Intent()
                        intent.action = "MY_QUOTA_UPDATED"
                        context?.sendBroadcast(intent)

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

    fun delete_member() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("del_yn", "Y")

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        iapHelper?.onActivityResult(requestCode, resultCode, data)
    }



    private fun shareInstagram() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        try {
            val uri = Uri.parse("android.resource://" + myContext.packageName + "/mipmap/ic_launcher");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "텍스트는 지원하지 않음!")
            shareIntent.setPackage("com.instagram.android")
            startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            Log.d("에러", e.toString())
            Toast.makeText(myContext, "인스타그램이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.d("에러", e.toString())
            e.printStackTrace()
        }
    }

    private fun shareFacebook() {

        // val image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        // val photo = SharePhoto.Builder().setBitmap(image).setCaption("노마드노트").build();
        // val content = SharePhotoContent.Builder().addPhoto(photo).build();

        val content = ShareLinkContent.Builder().setContentUrl(Uri.parse(Config.url + "/share")).build()

        val shareDialog = ShareDialog(this)
        shareDialog.show(content, ShareDialog.Mode.FEED)   //AUTOMATIC, FEED, NATIVE, WEB 등이 있으며 이는 다이얼로그 형식을 말합니다.
    }

    private fun shareNaverBlog() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        try {

            val title = "노마드노트"
            val post = "내용은 나만의 여행추억을 실시간으로 간편하게 기록하는 여행기록서비스"
            val appId = myContext.packageName
            val appName = "노마드노트"
            val url = String.format("naverblog://write?title=%s&content=%s", title, post);

            val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            shareIntent.setPackage("com.nhn.android.blog")
            startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            Log.d("에러", e.toString())
            Toast.makeText(myContext, "네이버 블로그앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.d("에러", e.toString())
            e.printStackTrace()
        }
    }

    private fun shareKakaoStory() {
        try {

            val post = "내용은 나만의 여행추억을 실시간으로 간편하게 기록하는 여행기록서비스"
            val appId = myContext.packageName
            val appName = "노마드노트"
            val url = String.format("storylink://posting?post=%s&appid=%s&appver=1.0.0&apiver=1.0&appname=%s", post, appId, appName);

            val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            shareIntent.setPackage("com.kakao.story")
            startActivity(shareIntent)

        } catch (e: ActivityNotFoundException) {
            Log.d("에러", e.toString())
            Toast.makeText(myContext, "카카오스토리앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.d("에러", e.toString())
            e.printStackTrace()
        }
    }



}
