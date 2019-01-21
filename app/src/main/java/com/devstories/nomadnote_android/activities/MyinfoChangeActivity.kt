package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_info_change.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream

class MyinfoChangeActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null


    var name = ""
    var age = 0
    var phone = 0
    var gender = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_change)
        this.context = this
        progressDialog = ProgressDialog(context)


        loadInfo()



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
     editTV.setOnClickListener {
         name = Utils.getString(nameET)
         age = Utils.getInt(ageET)
         phone = Utils.getInt(phoneET)
         edit_profile()
     }



        titleBackLL.setOnClickListener {
            finish()
        }




    }

    fun setmenu(){
        manIV.visibility= View.GONE
        femaleckIV.visibility = View.GONE
    }

    fun edit_profile(){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("name",name)
        params.put("age", age)
        params.put("gender", gender)
        params.put("phone", phone)

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
    //사용자정보
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
                        name =  Utils.getString(member, "name")
                        age =  Utils.getInt(member, "age")
                        gender =  Utils.getString(member, "gender")
                        phone = Utils.getInt(member,"phone")
                        val disk =member.getJSONArray("disk")
                        val payment_sum = Utils.getInt(member,"payment_sum")

                        if (gender == "M"){
                            manIV.visibility = View.VISIBLE
                        }else if (gender =="F"){
                            femaleckIV.visibility = View.VISIBLE
                        }
                        nameET.setText(name)
                        ageET.setText(age.toString())
                        phoneET.setText(phone.toString())
                        var disk_byte = 1073741824
                        if (disk.length()>0){
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
                        }

                        PrefUtils.setPreference(context, "disk", disk_byte)
                        PrefUtils.setPreference(context, "payment_byte", payment_sum)




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


}
