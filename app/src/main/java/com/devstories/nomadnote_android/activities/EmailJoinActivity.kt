package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.R.id.*
import com.devstories.nomadnote_android.actions.JoinAction
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.*
import com.google.firebase.iid.FirebaseInstanceId
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_email_join.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class EmailJoinActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var pw = ""
    var pw2 = ""
    var email = ""
    var notice_type = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_join)
        this.context = this
//        progressDialog = ProgressDialog(context)
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        backIV.setOnClickListener {
            finish()
            Utils.hideKeyboard(this)
        }

        GoogleAnalytics.sendEventGoogleAnalytics(application as GlobalApplication, "android", "이메일회원가입")

        companyinfoTV.setOnClickListener {
            var intent = Intent(context, CompanyInfomationActivity::class.java)
            startActivity(intent)
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
        joinTV.setOnClickListener {
            email = Utils.getString(emailET)
            pw = Utils.getString(pwET)
            pw2 = Utils.getString(pw2ET)
            if (email.equals("")){
                Toast.makeText(context,getString(R.string.enter_email),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Utils.isValidEmail(email)) {
                Toast.makeText(context,getString(R.string.check_email),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pw.equals("")){
                Toast.makeText(context, getString(R.string.enter_password),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pw !=pw2){
                Toast.makeText(context,getString(R.string.mismatch_password),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fillIV.visibility == View.GONE){
                Toast.makeText(context,getString(R.string.info_check),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            email(email)

        }

        chkinfoLL.setOnClickListener {
            if (fillIV.visibility == View.GONE) {
                fillIV.visibility = View.VISIBLE
             } else {
                fillIV.visibility = View.GONE
            }
        }



    }
    //email중복체크
    fun email(email: String) {

        val android_id = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        val params = RequestParams()
        params.put("email", email)
        params.put("android_id", android_id)

        JoinAction.check_email(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    when (result) {
                        "ok" -> {
                            val intent = Intent(context, MemberInputActivity::class.java)
                            intent.putExtra("email", email)
                            intent.putExtra("pw", pw)
                            startActivity(intent)
                        }
                        "joined" -> Toast.makeText(context,getString(R.string.already_joined),Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(context,getString(R.string.already_email),Toast.LENGTH_SHORT).show()
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
                Utils.alert(context, getString(R.string.error))
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

    override fun onBackPressed() {
        finish()
        Utils.hideKeyboard(context)
    }

}
