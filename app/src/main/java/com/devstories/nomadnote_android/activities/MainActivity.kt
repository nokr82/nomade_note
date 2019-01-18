package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.R.id.titleLL
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.Utils
import com.google.firebase.iid.FirebaseInstanceId
import com.kakao.s2.StringSet.count
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : FragmentActivity() {
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    val Solo_time_Fragment : Solo_time_Fragment = Solo_time_Fragment()
    val Map_search_Fragment : Map_search_Fragment = Map_search_Fragment()
    val Other_time_Fragment : Other_time_Fragment = Other_time_Fragment()
    val Scrap_Fragment : Scrap_Fragment = Scrap_Fragment()
    val Seting_Fragment : Seting_Fragment = Seting_Fragment()
    val Quest_stack_Fragment : Quest_stack_Fragment = Quest_stack_Fragment()
    val Friend_Fragment : Friend_Fragment = Friend_Fragment()

    internal var friendReciver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {

                var f_type = intent.getIntExtra("type", -1)

                titleLL.visibility = View.GONE
                var args: Bundle = Bundle()
                args.putInt("type", f_type)
                Friend_Fragment.setArguments(args)
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Solo_time_Fragment).commit()
        soloIV.setImageResource(R.mipmap.op_solo)
        soloTV.setTextColor(Color.parseColor("#0c6e87"))
        titleLL.visibility = View.GONE
        click()
        context = this

        var filter1 = IntentFilter("FRIEND")
        registerReceiver(friendReciver, filter1)

        var filter2 = IntentFilter("UPDATE_TIMELINE")
        registerReceiver(timelineReciver, filter2)

        updateToken()
        loadInfo()

    }

    fun setmenu(){
        soloIV.setImageResource(R.mipmap.solotimeline)
        questIV.setImageResource(R.mipmap.quest_noclk)
        mapsearchIV.setImageResource(R.mipmap.map)
        otherIV.setImageResource(R.mipmap.other_timeline)
        scrapIV.setImageResource(R.mipmap.scrap_time)
        settingIV.setImageResource(R.mipmap.setting)

        soloTV.setTextColor(Color.parseColor("#878787"))
        questTV.setTextColor(Color.parseColor("#878787"))
        mapsearchTV.setTextColor(Color.parseColor("#878787"))
        otherTV.setTextColor(Color.parseColor("#878787"))
        scrapTV.setTextColor(Color.parseColor("#878787"))
        settingTV.setTextColor(Color.parseColor("#878787"))
    }

    fun click(){
        soloLL.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Solo_time_Fragment).commit()
            titleLL.visibility = View.GONE
            setmenu()
            soloIV.setImageResource(R.mipmap.op_solo)
            soloTV.setTextColor(Color.parseColor("#0c6e87"))

        }
        questLL.setOnClickListener {
            logoTV.setText("누적질문보기")
            logoIV.visibility = View.GONE
            titleLL.visibility = View.VISIBLE
            setmenu()
            questIV.setImageResource(R.mipmap.op_quest)
            questTV.setTextColor(Color.parseColor("#0c6e87"))
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Quest_stack_Fragment).commit()
        }
        mapsearchLL.setOnClickListener {
            titleLL.visibility = View.GONE
            setmenu()
            mapsearchIV.setImageResource(R.mipmap.op_mapsearch)
            mapsearchTV.setTextColor(Color.parseColor("#0c6e87"))
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Map_search_Fragment).commit()
        }
        otherLL.setOnClickListener {
            titleLL.visibility = View.GONE
            setmenu()
            otherIV.setImageResource(R.mipmap.op_other)
            otherTV.setTextColor(Color.parseColor("#0c6e87"))
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Other_time_Fragment).commit()
        }
        scrapLL.setOnClickListener {
            logoTV.setText("스크랩 리스트")
            logoIV.visibility = View.GONE
            setmenu()
            titleLL.visibility = View.VISIBLE
            scrapIV.setImageResource(R.mipmap.op_file)
            scrapTV.setTextColor(Color.parseColor("#0c6e87"))
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Scrap_Fragment).commit()
        }
        settingLL.setOnClickListener {
            logoTV.setText("설정")
            setmenu()
            logoIV.visibility = View.GONE
            titleLL.visibility = View.VISIBLE
            settingIV.setImageResource(R.mipmap.op_setting)
            settingTV.setTextColor(Color.parseColor("#0c6e87"))
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Seting_Fragment).commit()
        }

    }

    private fun updateToken() {
        val params = RequestParams()
        val member_id = PrefUtils.getIntPreference(context, "member_id")
        val member_token = FirebaseInstanceId.getInstance().token

        println("-------updatetoken0000")

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

                try {
                    val result = response!!.getString("result")
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
                        var disk = Utils.getInt(member,"disk")
                        var payment_sum = member.getJSONArray("payments")

                        var payment_byte = 1073741824
                        if (payment_sum.length()>0){
                            for (i in 0 until payment_sum.length()){
                                val payment_item = payment_sum.get(i) as JSONObject
                                val category = Utils.getInt(payment_item,"category")

                                if (category == 1){
                                    payment_byte = payment_byte + 1073741824
                                } else if (category == 2){
                                    payment_byte = payment_byte + 644245094
                                } else {
                                    payment_byte = payment_byte + 21474836
                                }

                            }
                        }

                        var diskabs =  Math.abs(disk)
                        var payment_byteabs = Math.abs(payment_byte)

                        PrefUtils.setPreference(context, "disk", diskabs)
                        PrefUtils.setPreference(context, "payment_byte", payment_byteabs)

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



}
