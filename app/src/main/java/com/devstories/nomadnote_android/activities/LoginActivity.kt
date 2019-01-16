package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.JoinAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.kakao.auth.AuthType
import com.kakao.auth.ErrorCode
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.usermgmt.response.model.User
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.log.Logger
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null
    private var userManagement: UserManagement? = null
    private var callback: SessionCallback? = null
    var jointype = -1
    private var kakao_ID: String? = null
    private var sns_name: String? = null

    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.context = this
        progressDialog = ProgressDialog(context)

        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)

        userManagement = UserManagement.getInstance()
        try {
            val info = context.packageManager.getPackageInfo("com.devstories.nomadnote_android", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }

        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }


        loginTV.setOnClickListener {
            val intent = Intent(context, Login2Activity::class.java)
            startActivity(intent)
        }
        joinTV.setOnClickListener {
            val intent = Intent(context, EmailJoinActivity::class.java)
            startActivity(intent)
        }
        kakaoLL.setOnClickListener {
            /*val intent = Intent(context, Login2Activity::class.java)
            jointype = 1
            intent.putExtra("jointype",jointype)
            startActivity(intent)*/
            kakaoLogout()
            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this)
        }
        naverLL.setOnClickListener {
            val intent = Intent(context, Login2Activity::class.java)
            jointype = 2
            intent.putExtra("jointype",jointype)
            startActivity(intent)
        }
        googleLL.setOnClickListener {
            val intent = Intent(context, Login2Activity::class.java)
            jointype = 3
            intent.putExtra("jointype",jointype)
            startActivity(intent)
        }

        facebookLL.setOnClickListener {
            val intent = Intent(context, Login2Activity::class.java)
            jointype = 4
            intent.putExtra("jointype",jointype)
            startActivity(intent)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)

    }

    //카카오톡 시작
    private inner class SessionCallback : ISessionCallback {

        override fun onSessionOpened() {
            requestMe()
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            if (exception != null) {
                //                Logger.e(exception);
                if ("CANCELED_OPERATION" == exception.errorType.toString()) {
                    Toast.makeText(dialogContext, "카카오톡 로그인 취소", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(dialogContext, exception.errorType.toString(), Toast.LENGTH_LONG).show()
                }
            }

            //setContentView(R.layout.activity_login);
        }
    }

    protected fun requestMe() { //유저의 정보를 받아오는 함수
        userManagement!!.requestMe(object : MeResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
                val message = "failed to get user info. msg=" + errorResult!!
                Logger.d(message)
                Toast.makeText(dialogContext, errorResult.errorMessage, Toast.LENGTH_LONG).show()

                val result = ErrorCode.valueOf(errorResult.errorCode)
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish()
                } else {
                    requestMe()
                }
            }

            override fun onSessionClosed(errorResult: ErrorResult) {
                Toast.makeText(context, errorResult.errorMessage, Toast.LENGTH_LONG).show()
                finish()
                //                redirectSignupActivity();
            }

            override fun onNotSignedUp() {
                Toast.makeText(context, "카카오톡 회원이 아닙니다\n가입후 이용해 주시기 바랍니다.", Toast.LENGTH_LONG).show()
                finish()
            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            override fun onSuccess(userProfile: UserProfile) {  //성공 시 userProfile 형태로 반환
                Log.d("유저정보", userProfile.toString())
                kakao_ID = userProfile.id.toString()
                sns_name = userProfile.nickname
                sns_join("", "1", kakao_ID!!, userProfile.nickname)
            }
        })
    }
    fun sns_join(email:String,join_type:String,sns_key:String,name:String) {
        val params = RequestParams()
        params.put("name", name)
        params.put("join_type", join_type)
        params.put("email",email )
        params.put("sns_key",sns_key )



        JoinAction.join(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONObject("member")
                        PrefUtils.setPreference(context, "member_id", Utils.getInt(data, "id"))
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {



                        Toast.makeText(context, response!!.getString("message"), Toast.LENGTH_LONG).show()

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

//                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }
    private fun kakaoLogout() {
        userManagement!!.requestLogout(object : LogoutResponseCallback() {
            override fun onCompleteLogout() {}
        })
    }
    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
