package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_find_idand_passwd.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class FindIDAndPasswdActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null
    var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_idand_passwd)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        var intent = getIntent()
        type = intent.getStringExtra("type")
        if (type == "id"){
            idV.visibility = View.VISIBLE
            passwordV.visibility = View.GONE
            idTV.setTextColor(Color.parseColor("#000000"))
            passwordTV.setTextColor(Color.parseColor("#878787"))
            snsLL.visibility = View.GONE
            snsV.visibility = View.GONE
            idandpasswordTV.visibility = View.VISIBLE
            idandpasswordLL.visibility = View.GONE
        } else {
            idV.visibility = View.GONE
            passwordV.visibility = View.VISIBLE
            idTV.setTextColor(Color.parseColor("#878787"))
            passwordTV.setTextColor(Color.parseColor("#000000"))
            snsLL.visibility = View.VISIBLE
            snsV.visibility = View.VISIBLE
            idandpasswordTV.visibility = View.GONE
            idandpasswordLL.visibility = View.VISIBLE
        }

        titleBackLL.setOnClickListener {
            finish()
            Utils.hideKeyboard(this)
        }

        companyinfoTV.setOnClickListener {
            var intent = Intent(context, CompanyInfomationActivity::class.java)
            startActivity(intent)
        }

        findphoneLL.setOnClickListener {
            if (op_phoneLL.visibility == View.GONE){
                op_phoneLL.visibility = View.VISIBLE
                phoneIV.rotation = 90f
                phoneV.visibility = View.GONE
            } else {
                op_phoneLL.visibility = View.GONE
                phoneIV.rotation = 0f
                phoneV.visibility = View.VISIBLE
            }
        }

        findemailLL.setOnClickListener {
            if (op_emailLL.visibility == View.GONE){
                op_emailLL.visibility = View.VISIBLE
                emailIV.rotation = 90f
                emailV.visibility = View.GONE
            } else {
                op_emailLL.visibility = View.GONE
                emailIV.rotation = 0f
                emailV.visibility = View.VISIBLE
            }
        }

        findsnsLL.setOnClickListener {
            if (op_snsLL.visibility == View.GONE){
                op_snsLL.visibility = View.VISIBLE
                snsIV.rotation = 90f
                snsV.visibility = View.GONE
            } else {
                op_snsLL.visibility = View.GONE
                snsIV.rotation = 0f
                snsV.visibility = View.VISIBLE
            }
        }

        idLL.setOnClickListener {
            idV.visibility = View.VISIBLE
            passwordV.visibility = View.GONE
            idTV.setTextColor(Color.parseColor("#000000"))
            passwordTV.setTextColor(Color.parseColor("#878787"))
            snsLL.visibility = View.GONE
            snsV.visibility = View.GONE
            idandpasswordTV.visibility = View.VISIBLE
            idandpasswordLL.visibility = View.GONE
        }

        passwordLL.setOnClickListener {
            idV.visibility = View.GONE
            passwordV.visibility = View.VISIBLE
            idTV.setTextColor(Color.parseColor("#878787"))
            passwordTV.setTextColor(Color.parseColor("#000000"))
            snsLL.visibility = View.VISIBLE
            snsV.visibility = View.VISIBLE
            idandpasswordTV.visibility = View.GONE
            idandpasswordLL.visibility = View.VISIBLE
        }

        sendphoneTV.setOnClickListener {
            findPhone()
        }


    }

    fun findPhone(){

        val name = nameET.text.toString()
        if (name == null || name == ""){
            Toast.makeText(context, getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
            return
        }

        val phone = phoneET.text.toString()
        if (type == "phone") {
            if (phone == null || phone == "") {
                Toast.makeText(context, getString(R.string.enter_phonenumber), Toast.LENGTH_SHORT).show()
                return
            }
        }

        val params = RequestParams()
        params.put("name",name)
        params.put("phone",phone)

        MemberAction.find_phone(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {

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

                println(responseString);

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

                println(errorResponse)

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

                println(errorResponse)
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
