package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.JoinAction
import com.devstories.nomadnote_android.base.GlobalApplication
import com.devstories.nomadnote_android.base.GoogleAnalytics
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.auth.AuthType
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_find_idand_passwd.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.kakao.usermgmt.UserManagement
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.util.exception.KakaoException
import java.util.logging.Logger
import com.kakao.auth.ErrorCode
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import java.lang.ref.WeakReference
import java.util.*


class FindIDAndPasswdActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    private var userManagement: UserManagement? = null
    private var callback: SessionCallback? = null
    private var callbackManager: CallbackManager? = null
    private var accessToken: AccessToken? = null
    private val RC_SIGN_IN = 1000
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var mAuth: FirebaseAuth

    private lateinit var mOAuthLoginModule: OAuthLogin

    private lateinit var findIDAndPasswdActivity: FindIDAndPasswdActivity

    var type = ""
    var notice_type = -1
    var spinnerItem:ArrayList<String> = ArrayList<String>()
    var authNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_idand_passwd)

        this.findIDAndPasswdActivity = this
        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        GoogleAnalytics.sendEventGoogleAnalytics(application as GlobalApplication, "android", "아이디,비밀번호찾기")

        callback = SessionCallback()

        Session.getCurrentSession().addCallback(callback)

        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        userManagement = UserManagement.getInstance()

        mOAuthLoginModule = OAuthLogin.getInstance()
        mOAuthLoginModule.init(
                context
                , getString(R.string.OAUTH_CLIENT_ID)
                , getString(R.string.OAUTH_CLIENT_SECRET)
                , getString(R.string.OAUTH_CLIENT_NAME)
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        )

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        /* mGoogleApiClient = GoogleApiClient.Builder(this)
                 .enableAutoManage(this, this)
                 .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                 .build()*/
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()


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
            logoTV.text = getString(R.string.find_id)
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

        naverLL.setOnClickListener {
            naverLogin()
        }

        kakaoLL.setOnClickListener {
            kakaoLogout()
            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this@FindIDAndPasswdActivity)
        }

        facebookLL.setOnClickListener {
            disconnectFromFacebook()
        }

        googleLL.setOnClickListener {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

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
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
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

            if (id != null) {
                isMember(3, id)
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,link, email")
        request.parameters = parameters
        request.executeAsync()
    }

    fun naverLogin() {
        mOAuthLoginModule.startOauthLoginActivity(this, mOAuthLoginHandler)
    }

    private var mOAuthLoginHandler = object : OAuthLoginHandler() {
        override fun run(success: Boolean) {
            print("success : $success")

            if (success) {
                val accessToken = mOAuthLoginModule.getAccessToken(context)
                val refreshToken = mOAuthLoginModule.getRefreshToken(context)
                val expiresAt = mOAuthLoginModule.getExpiresAt(context)
                val tokenType = mOAuthLoginModule.getTokenType(context)

                RequestApiTask(findIDAndPasswdActivity, mOAuthLoginModule).execute()

            } else {
                val errorCode = mOAuthLoginModule.getLastErrorCode(context).code
                val errorDesc = mOAuthLoginModule.getLastErrorDesc(context)
                Toast.makeText(context, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class RequestApiTask(findIDAndPasswdActivity: FindIDAndPasswdActivity, mOAuthLoginModule: OAuthLogin) : AsyncTask<Void, Void, String?>() {

        private var activityRef: WeakReference<FindIDAndPasswdActivity> = WeakReference(findIDAndPasswdActivity)
        private var mOAuthLoginModuleRef: WeakReference<OAuthLogin> = WeakReference(mOAuthLoginModule)

        override fun onPreExecute() {

        }

        override fun doInBackground(vararg params: Void): String? {

            val url = "https://openapi.naver.com/v1/nid/me"
            val at = mOAuthLoginModuleRef.get()?.getAccessToken(activityRef.get())

            val result = mOAuthLoginModuleRef.get()?.requestApi(activityRef.get(), at, url)

            return result
        }

        override fun onPostExecute(content: String?) {

            val me = JSONObject(content)


            val resultcode = Utils.getString(me, "resultcode")

            if (resultcode == "00") {
                val response = me.getJSONObject("response")
                val sns_key = Utils.getString(response, "id")
                val nickname = Utils.getString(response, "nickname")
                val profile_image = Utils.getString(response, "profile_image")
                val gender = Utils.getString(response, "gender")
                val email = Utils.getString(response, "email")
                val name = Utils.getString(response, "name")

                activityRef.get()?.isMember(1, sns_key)

                // activityRef.get()?.sns_join(email, "2", sns_key, name)

            } else {
                val message = Utils.getString(me, "message")

                Toast.makeText(activityRef.get(), message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun kakaoLogout() {
        userManagement!!.requestLogout(object : LogoutResponseCallback() {
            override fun onCompleteLogout() {}
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
                // Logger.d(message)
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
                isMember(2, userProfile.id.toString())
            }
        })
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
        if (phone == null || phone == "") {
            Toast.makeText(context, getString(R.string.enter_phonenumber), Toast.LENGTH_SHORT).show()
            return
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

                        if (type == "password"){
                            Toast.makeText(context,getString(R.string.find_pw_send_email), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context,getString(R.string.find_id_send_email), Toast.LENGTH_SHORT).show()
                        }


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

                            Toast.makeText(context, getString(R.string.find_pw_send_email), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, getString(R.string.find_id_send_email), Toast.LENGTH_LONG).show()
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

    fun isMember(type: Int, sns_key: String){

        val android_id = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        val params = RequestParams()
        params.put("type", type)
        params.put("android_id", android_id)
        params.put("sns_key", sns_key)

        JoinAction.find_pw_by_sns(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {

                        Toast.makeText(context, getString(R.string.find_pw_send_email), Toast.LENGTH_LONG).show()

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

    override fun onDestroy() {
        super.onDestroy()


        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        // System.out.println("acct : " + acct);

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser

                            // System.out.println("user : "  + user);

                            if (user != null) {
                                isMember(4, user.uid)
                            }
                            //                            isMember(user.getEmail(), "4", user.getUid(), user.getDisplayName());
                        } else {

                            // Utils.alert(context, "Exception 2 : " + task.getException().getLocalizedMessage());

                            var errorMag = "로그인에 실패하였습니다."
                            val errorCode = (task.exception as FirebaseAuthException).errorCode

                            println("errorCode : $errorCode")

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d("구글띠", task.toString())
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("구글띠", account.toString())
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                Log.d("에러", e.toString())
            }

        }

        callbackManager!!.onActivityResult(requestCode, resultCode, data)

    }

}
