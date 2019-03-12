package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.LoginAction
import com.devstories.nomadnote_android.base.*
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_login2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Login2Activity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null


    var passwd = ""
    var email = ""

    var autoLogin = false

    private var is_push:Boolean = false

    var last_id = ""
    var created = ""
    var timeline_id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        this.context = this
//        progressDialog = ProgressDialog(context)
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)


        GoogleAnalytics.sendEventGoogleAnalytics(application as GlobalApplication, "android", "이메일로 로그인")

        var intent = getIntent()
        is_push = intent.getBooleanExtra("is_push", false)

        if (is_push){
            last_id = intent.getStringExtra("last_id")
            created = intent.getStringExtra("created")

            println("login2:::::::is_push:::::::::::::$is_push last_id::$last_id::::::::created=$created")
        }

        loginTV.setOnClickListener {
            passwd = Utils.getString(passwdET)
            email = Utils.getString(emailET)

            if (email == "") {
                Toast.makeText(context, getString(R.string.enter_email), Toast.LENGTH_LONG).show();
                return@setOnClickListener
            }

            if (email == "") {
                Toast.makeText(context, getString(R.string.enter_password), Toast.LENGTH_LONG).show();
                return@setOnClickListener
            }

            login(email, passwd)
        }

        backIV.setOnClickListener {
            finish()
        }

        autoLoginLL.setOnClickListener {

            autoLogin = !autoLogin

            if (autoLogin) {
                autoCheckIV.setImageResource(R.mipmap.icon_check)
            } else {
                autoCheckIV.setImageResource(0)
            }

        }

    }

    fun login(email: String, passwd: String) {

        val android_id = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        var language = Locale.getDefault().language
        if(language == "zh") {
            language = Locale.getDefault().isO3Country
            if (language == "CHN") {
                language = "zh_rCN"
            } else {
                language = "zh_rTW"
            }
        }

        val params = RequestParams()
        params.put("email", email)
        params.put("passwd", passwd)
        params.put("android_id", android_id)
        params.put("language", language)


        println("login::::::::::autoLogin::::::::::::::::::::::::::::::$autoLogin")

        LoginAction.login(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONObject("member")


                        PrefUtils.setPreference(context, "member_id", Utils.getInt(data, "id"))
                        PrefUtils.setPreference(context, "name", Utils.getString(data, "name"))
                        PrefUtils.setPreference(context, "email", Utils.getString(data, "email"))
                        PrefUtils.setPreference(context, "passwd", Utils.getString(data, "passwd"))
                        PrefUtils.setPreference(context, "gender", Utils.getString(data, "gender"))
                        PrefUtils.setPreference(context, "join_type", Utils.getInt(data, "join_type"))
                        PrefUtils.setPreference(context, "age", Utils.getInt(data, "age"))
                        PrefUtils.setPreference(context, "autoLogin", autoLogin)

                        Utils.hideKeyboard(context)

                        val intent = Intent(context, MainActivity::class.java)
//                        intent.putExtra("is_push",is_push)
//                        intent.putExtra("last_id",last_id)
//                        intent.putExtra("created",created)
//                        intent.putExtra("timeline_id",timeline_id)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
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

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }
}
