package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.JoinAction
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.GlobalApplication
import com.devstories.nomadnote_android.base.GoogleAnalytics
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_find_idand_passwd.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.facebook.internal.Utility.arrayList




class FindIDAndPasswdActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null
    var type = ""
    var notice_type = -1
    var spinnerItem:ArrayList<String> = ArrayList<String>()
    var authNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_idand_passwd)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        GoogleAnalytics.sendEventGoogleAnalytics(application as GlobalApplication, "android", "아이디,비밀번호찾기")

        spinnerItem.add(getString(R.string.direct_input))
        get_emaillist()
        var spinnerAdpater = ArrayAdapter(context, R.layout.spiner_item, spinnerItem)
        spinnerS.adapter = spinnerAdpater
        spinnerAdpater.setDropDownViewResource(R.layout.spiner_item)
        spinnerS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                val email = spinnerItem.get(pos)
                if (email == getString(R.string.direct_input)){
                    emailET.visibility = View.VISIBLE
                    emailTV.visibility = View.GONE
                    emailET.setHint(getString(R.string.direct_input))
                    emailET.setText("")
                    emailTV.setText("")
                } else {
                    emailET.visibility = View.GONE
                    emailTV.visibility = View.VISIBLE
                    emailET.setText("")
                    emailTV.setText(email)
                }
            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }

        }

        var intent = getIntent()
        type = intent.getStringExtra("type")
        if (type == "password"){
            logoTV.text = getString(R.string.find_password)
            idV.visibility = View.GONE
            passwordV.visibility = View.VISIBLE
            idTV.setTextColor(Color.parseColor("#878787"))
            passwordTV.setTextColor(Color.parseColor("#000000"))
            snsLL.visibility = View.VISIBLE
            snsV.visibility = View.VISIBLE
            idandpasswordTV.visibility = View.GONE
            idandpasswordLL.visibility = View.VISIBLE
            emailidLL.visibility = View.VISIBLE
            phoneidLL.visibility = View.VISIBLE
            sendphoneTV.setText(getString(R.string.send_code))
            sendemailTV.setText(getString(R.string.send_code))
        }

        titleBackLL.setOnClickListener {
            finish()
            Utils.hideKeyboard(this)
        }

        privacyTV.setOnClickListener {
            notice_type=2
            val intent = Intent(context, NoticeActivity::class.java)
            intent.putExtra("type",notice_type)
            startActivity(intent)
        }
        serviceTV.setOnClickListener {
            notice_type=3
            val intent = Intent(context, NoticeActivity::class.java)
            intent.putExtra("type",notice_type)
            startActivity(intent)
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
            logoTV.text = getString(R.string.find_password)
            idV.visibility = View.VISIBLE
            passwordV.visibility = View.GONE
            idTV.setTextColor(Color.parseColor("#000000"))
            passwordTV.setTextColor(Color.parseColor("#878787"))
            snsLL.visibility = View.GONE
            snsV.visibility = View.GONE
            idandpasswordTV.visibility = View.VISIBLE
            idandpasswordLL.visibility = View.GONE
            emailidLL.visibility = View.GONE
            phoneidLL.visibility = View.GONE
            phoneidET.setText("")
            idET.setText("")
            sendphoneTV.setText(getString(R.string.confirm))
            sendemailTV.setText(getString(R.string.confirm))
            type = "id"
        }

        passwordLL.setOnClickListener {
            logoTV.text = getString(R.string.find_password)
            idV.visibility = View.GONE
            passwordV.visibility = View.VISIBLE
            idTV.setTextColor(Color.parseColor("#878787"))
            passwordTV.setTextColor(Color.parseColor("#000000"))
            snsLL.visibility = View.VISIBLE
            snsV.visibility = View.VISIBLE
            idandpasswordTV.visibility = View.GONE
            idandpasswordLL.visibility = View.VISIBLE
            emailidLL.visibility = View.VISIBLE
            phoneidLL.visibility = View.VISIBLE
            sendphoneTV.setText(getString(R.string.send_code))
            sendemailTV.setText(getString(R.string.send_code))
            type = "password"
        }

        sendphoneTV.setOnClickListener {
            findPhone()
        }

        sendemailTV.setOnClickListener {
            send_mail()
        }




    }

    fun findPhone(){
        val id = phoneidET.text.toString()
        if (type == "password"){
            if (id == null || id == ""){
                Toast.makeText(context, getString(R.string.enter_id), Toast.LENGTH_SHORT).show()
                return
            }
        }

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
        params.put("type",type)
        if (type == "password"){
            params.put("id",id)
        }

        JoinAction.check_phone(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {

                    } else if ("empty" == result){
                        Toast.makeText(context,getString(R.string.nomember), Toast.LENGTH_SHORT).show()
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


    fun get_emaillist(){
        val params = RequestParams()

        JoinAction.get_emaillist(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
                        var emails = response!!.getJSONArray("emails")
                        for (i in 0 until emails.length()){
                            val item = emails.get(i) as JSONObject
                            val email = Utils.getString(item,"email")
                            spinnerItem.add(email)
                        }
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

        })

    }

    fun send_mail(){

        val id = idET.text.toString()
        if (type == "password") {
            if (id == null || id == "") {
                Toast.makeText(context, getString(R.string.enter_id), Toast.LENGTH_SHORT).show()
                return
            }
        }

        val name = emailnameET.text.toString()
        if (name == null || name == ""){
            Toast.makeText(context, getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
            return
        }

        var email = useremailET.text.toString()
        if (email == null || email == ""){
            Toast.makeText(context, getString(R.string.enter_email), Toast.LENGTH_SHORT).show()
            return
        }

        var selectemail = emailET.text.toString()
        var selectemailtv = emailTV.text.toString()
        if (selectemail == "" && selectemailtv == null){
            Toast.makeText(context, getString(R.string.enter_email), Toast.LENGTH_SHORT).show()
            return
        }

        if (selectemail != "" || selectemail != null){
            email = email + "@" + selectemail
        } else if (selectemailtv != "" || selectemailtv != null){
            email = email + "@" + selectemailtv
        }

        val params = RequestParams()
        if (type == "password") {
            params.put("id", id)
        }

        params.put("name",name)
        params.put("email",email)
        params.put("type",type)

        JoinAction.send_mail(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
                        if (type == "password"){
                            val authnumber = Utils.getString(response,"authNumber")
                            authNumber = authnumber
                        }

                    } else if ("empty" == result) {
                        Toast.makeText(context, getString(R.string.nomember), Toast.LENGTH_SHORT).show()
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
