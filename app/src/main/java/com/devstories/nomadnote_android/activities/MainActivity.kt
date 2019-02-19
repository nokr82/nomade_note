package com.devstories.nomadnote_android.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.Utils
import com.google.firebase.iid.FirebaseInstanceId
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MainActivity : FragmentActivity() {

    private val RECEIVE_SMS_REQUEST_CODE = 1001
    private val READ_SMS_REQUEST_CODE = 1002
    private val RECEIVE_MMS_REQUEST_CODE = 1003

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    val Solo_time_Fragment : Solo_time_Fragment = Solo_time_Fragment()
    val Map_search_Fragment : Map_search_Fragment = Map_search_Fragment()
    val Other_time_Fragment : Other_time_Fragment = Other_time_Fragment()
    val Scrap_Fragment : Scrap_Fragment = Scrap_Fragment()
    val Seting_Fragment : Seting_Fragment = Seting_Fragment()
    val Quest_stack_Fragment : Quest_stack_Fragment = Quest_stack_Fragment()
    val Friend_Fragment : Friend_Fragment = Friend_Fragment()

    private val BACK_PRESSED_TERM:Long = 1000 * 2
    private var backPressedTime: Long = -1

    var is_push = false
    var last_id = ""
    var created = ""


    internal var datachangeReciver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                logoTV.text = getString(R.string.settings)
                logoTV.visibility  = View.VISIBLE
                setmenu()
                logoIV.visibility = View.GONE
                titleLL.visibility = View.VISIBLE
                settingIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.op_setting)
                settingTV.setTextColor(Color.parseColor("#0c6e87"))

                var args: Bundle = Bundle()
                args.putInt("type", 2)
                Seting_Fragment.arguments = args
                supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Seting_Fragment).commit()
            }
        }
    }

    internal var stylechangeReciver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                logoTV.text = getString(R.string.settings)
                logoTV.visibility  = View.VISIBLE
                setmenu()
                logoIV.visibility = View.GONE
                titleLL.visibility = View.VISIBLE
                settingIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.op_setting)
                settingTV.setTextColor(Color.parseColor("#0c6e87"))

                var args: Bundle = Bundle()
                args.putInt("type", 1)
                Seting_Fragment.arguments = args
                supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Seting_Fragment).commit()
            }
        }
    }


    internal var backReciver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                logoTV.text = getString(R.string.settings)
                logoTV.visibility  = View.VISIBLE
                setmenu()
                logoIV.visibility = View.GONE
                titleLL.visibility = View.VISIBLE
                settingIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.op_setting)
                settingTV.setTextColor(Color.parseColor("#0c6e87"))
                supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Seting_Fragment).commit()
            }
        }
    }
    internal var friendReciver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {

                var f_type = intent.getIntExtra("type", -1)

                titleLL.visibility = View.GONE
                var args: Bundle = Bundle()
                args.putInt("type", f_type)
                Friend_Fragment.arguments = args
                supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Friend_Fragment).commit()
            }
        }
    }

    internal var timelineReciver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {

            }
        }
    }

    internal var myQuotaUpdatedReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                loadInfo()
            }
        }
    }

    var timeline_id = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.devstories.nomadnote_android.R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Solo_time_Fragment).commit()
        soloIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.op_solo)
        soloTV.setTextColor(Color.parseColor("#0c6e87"))
        titleLL.visibility = View.GONE
        click()
        context = this

        progressDialog = ProgressDialog(this, com.devstories.nomadnote_android.R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        var filter1 = IntentFilter("FRIEND")
        registerReceiver(friendReciver, filter1)

        var filter2 = IntentFilter("UPDATE_TIMELINE")
        registerReceiver(timelineReciver, filter2)
        var filter3 = IntentFilter("FRIEND_BACK")
        registerReceiver(backReciver, filter3)
        var filter4 = IntentFilter("STYLE_CHANGE")
        registerReceiver(stylechangeReciver, filter4)
        var filter5 = IntentFilter("DATA_LIMIT")
        registerReceiver(datachangeReciver, filter5)

        val filter6 = IntentFilter("MY_STEP_UPDATED")
        registerReceiver(myQuotaUpdatedReceiver, filter1)

        updateToken()
        loadInfo()

        var intent = getIntent()
        val is_push = PrefUtils.getBooleanPreference(context, "is_push")
        if (is_push) {
            val last_id = PrefUtils.getStringPreference(context,"last_id")
            val created = PrefUtils.getStringPreference(context,"created")
            val intent = Intent(context, WriteActivity::class.java)
            intent.putExtra("qnas_id", last_id)
            intent.putExtra("created_at", created)
            startActivity(intent)
        }

        PrefUtils.removePreference(context, "is_push")
        PrefUtils.removePreference(context, "last_id")
        PrefUtils.removePreference(context, "created")
        PrefUtils.removePreference(context, "timeline_id")

//        timeline_id = intent.getIntExtra("timline_id", -1)
//        is_push = intent.getBooleanExtra("is_push", false)
//
//        if (is_push){
//
//            last_id = intent.getStringExtra("last_id")
//            created = intent.getStringExtra("created")
//
//            println("-------created-----$created")
//
//            if (last_id.length > 0) {
//                val intent = Intent(context, WriteActivity::class.java)
//                intent.putExtra("qnas_id", last_id)
//                intent.putExtra("created_at", created)
//                startActivity(intent)
//            }
//
//        }

        println("--------timeline_id $last_id")

        if (timeline_id > 0) {
            val intent = Intent(context, Solo_detail_Activity::class.java)
            intent.putExtra("timeline_id", timeline_id.toString())
            startActivity(intent)
        }

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), RECEIVE_SMS_REQUEST_CODE)
        }

        val mmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_MMS)
        if (mmsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_MMS), RECEIVE_MMS_REQUEST_CODE)
        }

        val readSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        if (readSmsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), READ_SMS_REQUEST_CODE)
        }

    }

    fun setmenu(){
        soloIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.solotimeline)
        questIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.quest_noclk)
        mapsearchIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.map)
        otherIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.other_timeline)
        scrapIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.scrap_time)
        settingIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.setting)

        soloTV.setTextColor(Color.parseColor("#878787"))
        questTV.setTextColor(Color.parseColor("#878787"))
        mapsearchTV.setTextColor(Color.parseColor("#878787"))
        otherTV.setTextColor(Color.parseColor("#878787"))
        scrapTV.setTextColor(Color.parseColor("#878787"))
        settingTV.setTextColor(Color.parseColor("#878787"))
    }

    fun click(){
        soloLL.setOnClickListener {
            titleBackLL.visibility = View.INVISIBLE
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Solo_time_Fragment).commit()
            titleLL.visibility = View.GONE
            setmenu()
            soloIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.op_solo)
            soloTV.setTextColor(Color.parseColor("#0c6e87"))

        }
        questLL.setOnClickListener {
            titleBackLL.visibility = View.INVISIBLE
            logoTV.text = getString(R.string.seeallquestions)
            logoTV.visibility = View.GONE
            logoIV.visibility = View.VISIBLE
            titleLL.visibility = View.VISIBLE
            setmenu()
            questIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.op_quest)
            questTV.setTextColor(Color.parseColor("#0c6e87"))
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Quest_stack_Fragment).commit()
        }
        mapsearchLL.setOnClickListener {
//            titleLL.visibility = View.GONE
            titleBackLL.visibility = View.INVISIBLE
            setmenu()
            mapsearchIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.op_mapsearch)
            mapsearchTV.setTextColor(Color.parseColor("#0c6e87"))
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Map_search_Fragment).commit()
        }
        otherLL.setOnClickListener {
//            titleLL.visibility = View.GONE
            titleBackLL.visibility = View.INVISIBLE
            setmenu()
            otherIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.op_other)
            otherTV.setTextColor(Color.parseColor("#0c6e87"))
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Other_time_Fragment).commit()
        }
        scrapLL.setOnClickListener {
//            logoTV.setText("스크랩 리스트")
            titleBackLL.visibility = View.INVISIBLE
            logoIV.visibility = View.GONE
            setmenu()
//            titleLL.visibility = View.GONE
            scrapIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.op_file)
            scrapTV.setTextColor(Color.parseColor("#0c6e87"))
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Scrap_Fragment).commit()
        }
        settingLL.setOnClickListener {
            titleBackLL.visibility = View.INVISIBLE
            logoTV.text = getString(R.string.settings)
            logoTV.visibility = View.VISIBLE
            setmenu()
            logoIV.visibility = View.GONE
            titleLL.visibility = View.VISIBLE
            settingIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.op_setting)
            settingTV.setTextColor(Color.parseColor("#0c6e87"))
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Seting_Fragment).commit()
        }

    }

    private fun updateToken() {
        val params = RequestParams()
        val member_id = PrefUtils.getIntPreference(context, "member_id")
        val member_token = FirebaseInstanceId.getInstance().token

        if (member_id == -1 || null == member_token || "" == member_token || member_token.length < 1) {
            return
        }
        params.put("member_id", member_id)
        params.put("token", member_token)
        params.put("device", Config.device)

        println("member_token$member_token")
        println("device${Config.device}")

        MemberAction.regist_token(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                PrefUtils.setPreference(context, "token", member_token)

                try {
//                    val result = response!!.getString("result")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {}

            private fun error() {

                if (progressDialog != null) {
                    Utils.alert(context, "조회중 장애가 발생하였습니다.")
                }
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

//                val member_id = PrefUtils.getIntPreference(context, "member_id")
//                LogAction.log(javaClass.toString(), member_id, responseString)

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


        try {
            if(friendReciver != null) {
                unregisterReceiver(friendReciver)
            }
        } catch (e:Exception) {

        }

        try {
            if(timelineReciver != null) {
                unregisterReceiver(timelineReciver)
            }
        } catch (e:Exception) {

        }

        try {
            if(backReciver != null) {
                unregisterReceiver(backReciver)
            }
        } catch (e:Exception) {

        }

        try {
            if(stylechangeReciver != null) {
                unregisterReceiver(stylechangeReciver)
            }
        } catch (e:Exception) {

        }

        try {
            if(datachangeReciver != null) {
                unregisterReceiver(datachangeReciver)
            }
        } catch (e:Exception) {

        }

        try {
            if(myQuotaUpdatedReceiver != null) {
                unregisterReceiver(myQuotaUpdatedReceiver)
            }
        } catch (e:Exception) {

        }

    }

    fun loadInfo() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))

        MemberAction.my_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        var member = response.getJSONObject("member")
                        var disk = response.getString("disk")
//                        var payment_sum = member.getJSONArray("payments")
                        var point = Utils.getInt(member,"point")
                        var pointdouble = Utils.getDouble(member,"point")
                        var bytedouble = Utils.getDouble(member,"point")
                        var byte = Utils.getInt(member,"bytes")

                        var usebytes = response.getString("usebytes")
                        println("----- usebytes : $usebytes")

                        println("-----memberbyte : $byte")

//                        var payment_byte = 2147483648
                        // var payment_byte = 20480
//                        if (payment_sum.length()>0){
//                            for (i in 0 until payment_sum.length()){
//                                val payment_item = payment_sum.get(i) as JSONObject
//                                val category = Utils.getInt(payment_item,"category")
//
//                                if (category == 1){
//                                    payment_byte = payment_byte + 1073741824
//                                } else if (category == 2){
//                                    payment_byte = payment_byte + 644245094
//                                } else {
//                                    payment_byte = payment_byte + 21474836
//                                }
//
//                            }
//                        }

//                        var diskabs =  Math.abs(disk)
                        // var payment_byteabs = Math.abs(payment_byte)

                        PrefUtils.setPreference(context, "disk", disk.toDouble())
                        // PrefUtils.setPreference(context, "payment_byte", payment_byteabs)
                        val style = Utils.getInt(member, "style_id")
                        PrefUtils.setPreference(context, "style", Utils.getInt(member, "style_id"))
                        PrefUtils.setPreference(context, "point", point)
                        PrefUtils.setPreference(context, "byte", usebytes.toInt())
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

    override fun onBackPressed() {

        if (System.currentTimeMillis() - backPressedTime < BACK_PRESSED_TERM) {
            finish()
        } else {
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            backPressedTime = System.currentTimeMillis()
            Utils.hideKeyboard(context)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && Seting_Fragment != null) {
            Seting_Fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECEIVE_SMS_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    println("SMS Permission has been denied by user")

                } else {

                    println("SMS Permission has been granted by user")

                }

            }
            RECEIVE_MMS_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    println("MMS Permission has been denied by user")

                } else {

                    println("MMS Permission has been granted by user")

                }
            }
            READ_SMS_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    println("READ SMS Permission has been denied by user")

                } else {

                    println("RED SMS Permission has been granted by user")

                }
            }
        }
    }

    override fun onNewIntent(intent2: Intent?) {
        super.onNewIntent(intent2)

        val is_push = PrefUtils.getBooleanPreference(context, "is_push")
        if (is_push) {
            val last_id = PrefUtils.getStringPreference(context,"last_id")
            val created = PrefUtils.getStringPreference(context,"created")
            val intent = Intent(context, WriteActivity::class.java)
            intent.putExtra("qnas_id", last_id)
            intent.putExtra("created_at", created)
            startActivity(intent)
        }

        PrefUtils.removePreference(context, "is_push")
        PrefUtils.removePreference(context, "last_id")
        PrefUtils.removePreference(context, "created")
        PrefUtils.removePreference(context, "timeline_id")

    }

}
