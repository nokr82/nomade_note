package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
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

class MainActivity : FragmentActivity() {
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    val Solo_time_Fragment : Solo_time_Fragment = Solo_time_Fragment()
    val Map_search_Fragment : Map_search_Fragment = Map_search_Fragment()
    val Other_time_Fragment : Other_time_Fragment = Other_time_Fragment()
    val Scrap_Fragment : Scrap_Fragment = Scrap_Fragment()
    val Seting_Fragment : Seting_Fragment = Seting_Fragment()
    val Quest_stack_Fragment : Quest_stack_Fragment = Quest_stack_Fragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentFL, Solo_time_Fragment).commit()
        soloIV.setImageResource(R.mipmap.op_solo)
        soloTV.setTextColor(Color.parseColor("#0c6e87"))
        titleLL.visibility = View.GONE
        click()
        context = this

        updateToken()

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

}
