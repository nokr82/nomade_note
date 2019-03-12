package com.devstories.nomadnote_android.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.QuestionAction
import com.devstories.nomadnote_android.actions.TimelineAction
import com.devstories.nomadnote_android.base.*
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_question.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class QuestionActivity : RootActivity() {
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)


        GoogleAnalytics.sendEventGoogleAnalytics(application as GlobalApplication, "android", "문의하기")

        var intent = getIntent()

        titleBackLL.setOnClickListener {
            Utils.hideKeyboard(this)
            finish()
        }

        val time = Utils.timeStr()
        var timesplit = time.split(":")
        var edittime = ""
        if (timesplit.get(0).toInt() >= 12){
            edittime += " PM " + (timesplit.get(0).toInt() - 12).toString() + ":" + timesplit.get(1)
        } else {
            edittime += " AM " + timesplit.get(0)+ ":" + timesplit.get(1)
        }
        pulldateTV.setText(Utils.todayStr() + "" + edittime)

        addcontentLL.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder
                    .setMessage("문의하시겠습니까 ?")

                    .setPositiveButton(getString(R.string.builderyes), DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()


                        addQuestion()

                    })
                    .setNegativeButton(getString(R.string.builderno), DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()



                        Utils.hideKeyboard(this)

                    })
            val alert = builder.create()
            alert.show()
        }



    }

    override fun onBackPressed() {
        Utils.hideKeyboard(context)
        finish()
    }

    fun addQuestion(){
        val title = titleET.text.toString()
        if (title == "" || title == null){
            Toast.makeText(context, "제목은 필수 입력입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val content = contentET.text.toString()
        if (content == "" || content == null){
            Toast.makeText(context, "내용은 필수 입력입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val params = RequestParams()

        params.put("member_id", PrefUtils.getIntPreference(context,"member_id"))
        params.put("title",title)
        params.put("content",content)

        QuestionAction.question(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
                        var intent = Intent()
                        intent.putExtra("reset","reset")
                        setResult(RESULT_OK, intent);

                        Utils.hideKeyboard(context)

                        finish()
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
}
