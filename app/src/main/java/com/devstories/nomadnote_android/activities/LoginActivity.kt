package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.JoinAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.kakao.auth.AuthType
import com.kakao.auth.ErrorCode
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.StringSet.email
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.Utility.getPackageInfo
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
import java.util.*

class LoginActivity : FragmentActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null
    private var userManagement: UserManagement? = null
    private var callback: SessionCallback? = null
    private var callbackManager: CallbackManager? = null
    private var accessToken: AccessToken? = null

    var jointype = -1
    private var kakao_ID: String? = null
    private var sns_name: String? = null
    private var facebook_ID: String? = null
    private var facebook_NAME: String? = null
    private val RC_SIGN_IN = 1000
    private lateinit var mAuth: FirebaseAuth;
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.context = this
        progressDialog = ProgressDialog(context)

        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)

        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        userManagement = UserManagement.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
       /* mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()*/
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance()


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
            kakaoLogout()
            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this)
        }
        naverLL.setOnClickListener {
            val intent = Intent(context, Login2Activity::class.java)
            jointype = 2
            intent.putExtra("jointype", jointype)
            startActivity(intent)
        }
        googleLL.setOnClickListener {
            /* val intent = Intent(context, Login2Activity::class.java)
             jointype = 3
             intent.putExtra("jointype",jointype)
             startActivity(intent)*/
//            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            val signInIntent =mGoogleSignInClient!!.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        facebookLL.setOnClickListener {
            /*  val intent = Intent(context, Login2Activity::class.java)
              jointype = 4
              intent.putExtra("jointype",jointype)
              startActivity(intent)*/
            disconnectFromFacebook()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d("구글띠",task.toString())
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("구글띠",account.toString())
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Log.d("에러",e.toString())
            }

        }


        /*if (requestCode == RC_SIGN_IN){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.d("구글띠",result.toString())
            if (result.isSuccess) {
                //구글 로그인 성공해서 파베에 인증
                val account = result.signInAccount

                Log.d("구글띠",account.toString())

                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        // System.out.println("acct : " + acct);

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult>{
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser

                            // System.out.println("user : "  + user);

                            if (user != null) {
                                sns_join(user.getUid(), user.getDisplayName()!!, user.getEmail(), "google")
                            }
                            //                            isMember(user.getEmail(), "4", user.getUid(), user.getDisplayName());
                        } else {

                            // Utils.alert(context, "Exception 2 : " + task.getException().getLocalizedMessage());

                            var errorMag = "로그인에 실패하였습니다."
                            val errorCode = (task.exception as FirebaseAuthException).getErrorCode()
                            when (errorCode) {

                                "ERROR_OPERATION_NOT_ALLOWED", "ERROR_REQUIRES_RECENT_LOGIN", "ERROR_USER_MISMATCH", "ERROR_INVALID_CUSTOM_TOKEN", "ERROR_CUSTOM_TOKEN_MISMATCH" -> errorMag = "구글 로그인 중 장애가 발생하였습니다."

                                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL", "ERROR_INVALID_CREDENTIAL" -> errorMag = "로그인 정보가 일치하지 않거나 유효기간이 지났습니다."

                                "ERROR_INVALID_EMAIL" -> errorMag = "이메일 주소가 형식에 맞지 않습니다."

                                "ERROR_WRONG_PASSWORD" -> errorMag = "비밀번호가 일치하지 않습니다."

                                "ERROR_EMAIL_ALREADY_IN_USE" -> errorMag = "이메일은 이미 다른 사용자가 사용중입니다."

                                "ERROR_CREDENTIAL_ALREADY_IN_USE" -> errorMag = "로그인 정보는 이미 다른 계저에 등록되어 있습니다."

                                "ERROR_USER_DISABLED" -> errorMag = "사용자 계정이 관리자에 의해 비활성화 되어있습니다."

                                "ERROR_INVALID_USER_TOKEN", "ERROR_USER_TOKEN_EXPIRED" -> errorMag = "로그인 정보가 유효하지 않습니다. 다시 로그인해주세요."

                                "ERROR_USER_NOT_FOUND" -> errorMag = "해당 계정이 존재하지 않습니다."

                                "ERROR_WEAK_PASSWORD" -> errorMag = "비밀번호가 유효하지 않습니다."
                            }

                            Utils.alert(context, "$errorMag\n계정을 확인한 뒤 다시 시도해주세요.")
                        }

                    }
                })
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
                    Toast.makeText(context, "카카오톡 로그인 취소", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, exception.errorType.toString(), Toast.LENGTH_LONG).show()
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
                Toast.makeText(context, errorResult.errorMessage, Toast.LENGTH_LONG).show()

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
                email = ""
                sns_join(email, "1", kakao_ID!!, userProfile.nickname)
            }
        })
    }

    fun sns_join(email: String, join_type: String, sns_key: String?, name: String?) {
        val params = RequestParams()
        params.put("name", name)
        params.put("join_type", join_type)
        params.put("email", email)
        params.put("sns_key", sns_key)



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

    // 페이스북 로그아웃
    fun disconnectFromFacebook() {
        //        LoginManager.getInstance().logOut();
        //        if (accessToken == null) {
        //            doStartWithFacebook();
        //            return;
        //        }
        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback {
            LoginManager.getInstance().logOut()
            doStartWithFacebook()
        }).executeAsync()
    }

    private fun doStartWithFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            this.accessToken = AccessToken.getCurrentAccessToken()
            fetchUserData()
        } else {
            LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, Arrays.asList("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(loginResult: LoginResult) {
                            accessToken = loginResult.accessToken
                            fetchUserData()
                        }

                        override fun onCancel() {
                            Toast.makeText(context, "페이스북 로그인 취소", Toast.LENGTH_LONG).show()
                        }

                        override fun onError(exception: FacebookException) {
                            Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                        }
                    })
        }
    }

    private fun fetchUserData() {
        val request = GraphRequest.newMeRequest(
                accessToken
        ) { `object`, response ->
            var id: String? = null
            var name: String? = null
            var eamil: String? = null

            try {
                if (`object`.has("id") && !`object`.isNull("id")) {
                    id = `object`.getString("id")
                }

                if (`object`.has("name") && !`object`.isNull("name")) {
                    name = `object`.getString("name")
                }

                if (`object`.has("email") && !`object`.isNull("email")) {
                    eamil = `object`.getString("email")
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            facebook_ID = id
            facebook_NAME = name

            sns_join(eamil!!, "4", facebook_ID, facebook_NAME)
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,link, email")
        request.parameters = parameters
        request.executeAsync()
    }


    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
