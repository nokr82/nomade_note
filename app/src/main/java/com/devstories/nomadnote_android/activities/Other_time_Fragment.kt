package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ListView
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.TimelineAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fra_other_time.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Other_time_Fragment : Fragment()  {
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null
    lateinit var OthertimeAdapter: OthertimeAdapter
    var data = arrayListOf<Int>()
    lateinit var otherLV:ListView

    var timelineDatas:ArrayList<JSONObject> = ArrayList<JSONObject>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext)

        getTimeline()

        return inflater.inflate(R.layout.fra_other_time, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        otherLV = view.findViewById(R.id.otherLV)
        OthertimeAdapter = OthertimeAdapter(myContext, R.layout.item_scrap, timelineDatas)
        otherLV.adapter = OthertimeAdapter

        super.onViewCreated(view, savedInstanceState)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        click()
        super.onActivityCreated(savedInstanceState)
    }

    fun click(){
        otherLV.setOnItemClickListener { parent, view, position, id ->
            val timeline = timelineDatas.get(position)
            val timeline_id = Utils.getString(timeline, "id")

            val intent = Intent(myContext, Solo_detail_Activity::class.java)
            intent.putExtra("timeline_id",timeline_id)
            startActivity(intent)
        }
    }

    fun getTimeline(){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context,"member_id"))
        params.put("type", "all")


        TimelineAction.my_timeline(params, object : JsonHttpResponseHandler() {

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
                            }
                        }
                        OthertimeAdapter.notifyDataSetChanged()
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

}
