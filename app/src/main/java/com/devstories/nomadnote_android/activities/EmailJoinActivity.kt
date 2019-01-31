package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.R.id.*
import com.devstories.nomadnote_android.actions.JoinAction
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_join)
        this.context = this
        progressDialog = ProgressDialog(context)
        backIV.setOnClickListener {
            finish()
        }




        joinTV.setOnClickListener {
            email = Utils.getString(emailET)
            pw = Utils.getString(pwET)
            pw2 = Utils.getString(pw2ET)
            if (email.equals("")){
                Toast.makeText(context,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pw.equals("")){
                Toast.makeText(context,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pw !=pw2){
                Toast.makeText(context,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            email(email)

        }



    }
    //email중복체크
    fun email(email: String) {
        val params = RequestParams()
        params.put("email", email)

        JoinAction.check_email(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")



                    if ("ok" == result) {
                        val intent = Intent(context, MemberInputActivity::class.java)
                        intent.putExtra("email",email)
                        intent.putExtra("pw",pw)
                        startActivity(intent)


                    } else {
                      Toast.makeText(context,"이미 사용중인 이메일입니다.",Toast.LENGTH_SHORT).show()
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
