package com.devstories.nomadnote_android.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_not_disturb.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream

class NotDisturbActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var allblock = false
    var push_block_yn = ""

    var slot = false
    var time_push_yn = ""

    var spinnerItem:ArrayList<String> = ArrayList<String>()

    val SELECT_START = 100
    val SELECT_END = 101

    var startHourMinute = ""
    var startAmpm = ""

    var endHourMinute = ""
    var endAmpm = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_disturb)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        loadInfo()

        titleBackLL.setOnClickListener {
            finish()
            Utils.hideKeyboard(this)
        }

        startRL.setOnClickListener {
            val start_ampm = startampmTV.text.toString()
            val start_time = startTV.text.toString()

            val intent = Intent(context, DlgSelectTimeActivity::class.java)
            intent.putExtra("ampm",start_ampm)
            intent.putExtra("time",start_time)
            startActivityForResult(intent, SELECT_START)
        }

        endRL.setOnClickListener {
            val end_ampm = endampmTV.text.toString()
            val end_time = endTV.text.toString()

            val intent = Intent(context, DlgSelectTimeActivity::class.java)
            intent.putExtra("ampm",end_ampm)
            intent.putExtra("time",end_time)
            startActivityForResult(intent, SELECT_END)
        }

        allblockLL.setOnClickListener {
            allblock = !allblock
            if (allblock) {
                allblockIV.setImageResource(R.mipmap.check_off)
                push_block_yn = "Y"
            } else {
                allblockIV.setImageResource(R.mipmap.check_on)
                push_block_yn = "N"
            }

            setPush("push")

        }

        slotLL.setOnClickListener {
            slot = !slot
            if (slot) {
                slotIV.setImageResource(R.mipmap.check_off)
                time_push_yn = "Y"
            } else {
                slotIV.setImageResource(R.mipmap.check_on)
                time_push_yn = "N"
            }

            setPush("time")

        }

    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                SELECT_START -> {
                    val startHour = data!!.getStringExtra("hourNP")
                    val startMinute = data!!.getStringExtra("minuteNP")
                    startAmpm = data!!.getStringExtra("ampm")
                    startHourMinute = startHour+":"+startMinute

                    startampmTV.setText(startAmpm)
                    startTV.setText(startHourMinute)

                    setAlarm()

                }

                SELECT_END -> {
                    val endHour = data!!.getStringExtra("hourNP")
                    val endMinute = data!!.getStringExtra("minuteNP")
                    endAmpm = data!!.getStringExtra("ampm")
                    endHourMinute = endHour+":"+endMinute

                    endampmTV.setText(endAmpm)
                    endTV.setText(endHourMinute)

                    setAlarm()

                }
            }
        }

    }


    fun setAlarm(){
        startAmpm = startampmTV.text.toString()
        startHourMinute = startTV.text.toString()
        endAmpm = endampmTV.text.toString()
        endHourMinute = endTV.text.toString()

        var cal_starttime = startHourMinute
        var cal_endtime = endHourMinute

        var startSplit = startHourMinute.split(":")
        var endSplit = endHourMinute.split(":")
        var startHour = startSplit.get(0).toInt()
        var endHour = endSplit.get(0).toInt()
        if (startAmpm == "PM"){
            startHour += 12
            cal_starttime = startHour.toString() + ":" + startSplit.get(1)
        }

        if (endAmpm == "PM"){
            endHour += 12
            cal_endtime = endHour.toString() + ":" + endSplit.get(1)
        }

        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("start_ampm",startAmpm)
        params.put("start_time", startHourMinute)
        params.put("end_ampm", endAmpm)
        params.put("end_time", endHourMinute)
        params.put("cal_starttime", cal_starttime)
        params.put("cal_endtime", cal_endtime)

        MemberAction.update_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()

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

    fun setPush(type:String){

        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        if (type == "time"){
            params.put("time_notpush_yn", time_push_yn)
        } else {
            params.put("push_block_yn",push_block_yn)
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
                        startAmpm = Utils.getString(member,"start_ampm")
                        if (startAmpm != null && startAmpm.length > 0){
                            startampmTV.setText(startAmpm)
                        } else {
                            startampmTV.setText("AM")
                        }

                        startHourMinute = Utils.getString(member,"start_time")
                        if (startHourMinute != null && startHourMinute.length > 0){
                            startTV.setText(startHourMinute)
                        } else {
                            startTV.setText("00:00")
                        }

                        endAmpm = Utils.getString(member,"end_ampm")
                        if (endAmpm != null && endAmpm.length > 0){
                            endampmTV.setText(endAmpm)
                        } else {
                            endampmTV.setText("AM")
                        }

                        endHourMinute = Utils.getString(member,"end_time")
                        if (endHourMinute != null && endHourMinute.length > 0){
                            endTV.setText(endHourMinute)
                        } else {
                            endTV.setText("00:00")
                        }

                        push_block_yn = Utils.getString(member,"push_block_yn")
                        time_push_yn = Utils.getString(member,"time_push_yn")
                        if (push_block_yn == "Y"){
                            allblock = true
                            allblockIV.setImageResource(R.mipmap.check_off)
                        } else {
                            allblock = false
                            allblockIV.setImageResource(R.mipmap.check_on)
                        }

                        if (time_push_yn == "Y"){
                            slot = true
                            slotIV.setImageResource(R.mipmap.check_off)
                        } else {
                            slot = false
                            slotIV.setImageResource(R.mipmap.check_on)
                        }


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
