
package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.TimelineAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import donggolf.android.adapters.PlaceAdapter
import kotlinx.android.synthetic.main.activity_country_timeline.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CountryTimelineActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null
    lateinit var countryAdapter: PlaceAdapter
    var country_id = -1
    var keyword = ""
    var timelineDatas:ArrayList<JSONObject> = ArrayList<JSONObject>()
    var country = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_timeline)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        titleBackLL.setOnClickListener {
            finish()
            Utils.hideKeyboard(this)
        }

        var intent = getIntent()
        country_id = intent.getIntExtra("country_id",-1)
        country = intent.getStringExtra("country")
        logoTV.setText(country)

        countryAdapter = PlaceAdapter(context, R.layout.item_scrap, timelineDatas)
        scrapLV.adapter = countryAdapter
        country_timeline()
        click()
    }

    fun click(){
        scrapLV.setOnItemClickListener { parent, view, position, id ->
            val item = timelineDatas.get(position)
            val timeline_id = Utils.getString(item, "id")
            val intent = Intent(context, Solo_detail_Activity::class.java)
            intent.putExtra("timeline_id",timeline_id)
            startActivity(intent)
        }

        keywordET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyword = keywordET.text.toString()
                country_timeline()
            }

            Utils.hideKeyboard(context)
                false
        }
    }

    fun country_timeline(){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context,"member_id"))
        params.put("country_id",country_id)
        params.put("keyword",keyword)

        TimelineAction.country_timeline(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
                        if (timelineDatas != null){
                            timelineDatas.clear()
                        }

                        val datas = response!!.getJSONArray("timeline")
                        if (datas.length() > 0){
                            for (i in 0 until datas.length()){
                                val timeline = datas.get(i) as JSONObject
                                timelineDatas.add(timeline)
                                var scrap = Utils.getString(timeline,"scrap")
                                if (scrap == "1"){
                                    timelineDatas[i].put("isSelectedOp", false)
                                } else {
                                    timelineDatas[i].put("isSelectedOp", true)
                                }
                            }
                        }
                        countryAdapter.notifyDataSetChanged()
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

    override fun onBackPressed() {
        finish()
        Utils.hideKeyboard(context)
    }
}
