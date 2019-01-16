package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.R.id.backIV
import com.devstories.nomadnote_android.R.id.startTV
import com.devstories.nomadnote_android.actions.JoinAction
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_member_infoinput.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MemberInputActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var pw = ""
    var email = ""

    var gender = ""
    var name  = ""
    var age = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_infoinput)
        this.context = this
        progressDialog = ProgressDialog(context)
        setmenu()


        intent = getIntent()
        email = intent.getStringExtra("email")
        pw =  intent.getStringExtra("pw")

        click()



    }

    fun click(){
        manLL.setOnClickListener {
            setmenu()
            gender = "M"
            manckIV.visibility = View.VISIBLE
        }
        femaleLL.setOnClickListener {
            setmenu()
            gender = "F"
            femaleckIV.visibility = View.VISIBLE
        }
        backIV.setOnClickListener {
            finish()
        }


        startTV.setOnClickListener {
            name = Utils.getString(nameET)
            age = Utils.getString(ageET)

            if (name.equals("")){
                Toast.makeText(context, "이름을 입력해주세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (age.equals("")){
                Toast.makeText(context, "나이를 입력해주세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (gender.equals("")){
                Toast.makeText(context, "성별을 선택해주세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            join()
        }
    }
    fun setmenu(){
        manckIV.visibility = View.GONE
        femaleckIV.visibility = View.GONE
    }



    fun join() {
        val params = RequestParams()
        params.put("name", name)
        params.put("email",email )
        params.put("gender", gender)
        params.put("age",age )
        params.put("passwd",pw)


        JoinAction.join(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        Toast.makeText(context, "가입성공", Toast.LENGTH_LONG).show()
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
