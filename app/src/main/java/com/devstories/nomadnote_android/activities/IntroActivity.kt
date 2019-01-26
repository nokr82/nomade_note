package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Base64
import android.util.Log
import com.devstories.nomadnote_android.actions.LoginAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class IntroActivity : RootActivity() {

    protected var _splashTime = 2000 // time to display the splash screen in ms
    private val _active = true
    private var splashThread: Thread? = null

    private lateinit var progressDialog: ProgressDialog

    private lateinit var context: Context

    private var is_push:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.devstories.nomadnote_android.R.layout.activity_intro)
        this.context = this
        progressDialog = ProgressDialog(context)

        // printHashKey()

        splashThread = object : Thread() {
            override fun run() {
                try {
                    var waited = 0
                    while (waited < _splashTime && _active) {
                        Thread.sleep(100)
                        waited += 100
                    }
                } catch (e: InterruptedException) {

                } finally {
                    stopIntro()
                }
            }
        }
        (splashThread as Thread).start()


    }

    private fun stopIntro() {
        handler.sendEmptyMessage(0)
    }

    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            login()
        }
    }

    // 로그인
    private fun login() {

        val email = PrefUtils.getStringPreference(context, "email")
        val passwd = PrefUtils.getStringPreference(context, "passwd")
        val sns_key = PrefUtils.getStringPreference(context, "sns_key")
        val join_type = PrefUtils.getIntPreference(context, "join_type", -1)

        println("join_type : $join_type")

        if (join_type == -1) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            return
        }

        val params = RequestParams()
        params.put("email", email)
        params.put("passwd", passwd)
        params.put("join_type", join_type)
        params.put("sns_key", sns_key)

        println("params : $params")

        LoginAction.validUser(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog.dismiss()
                }

                println("result : $response")

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONObject("member")

                        LoginActivity.processLoginData(context, data)

                        val intent = Intent(context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)

                    } else {

                        PrefUtils.clear(context)

                        val intent = Intent(context, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null) {
                    progressDialog.dismiss()
                }

                System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog.dismiss()
                }
                throwable.printStackTrace()
                error()

                println(errorResponse)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog.dismiss()
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

    private fun printHashKey() {

        try {
            val info = packageManager.getPackageInfo(
                    "-----------PUT YOUR PACKAGE NAME ------------",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

    }
}
