package com.devstories.nomadnote_android.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.QnasAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Quest_stack_Fragment : Fragment()  {
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    lateinit var QuestAdapter: QuestAdapter
    var data = arrayListOf<Int>()
    var adapterData:ArrayList<JSONObject> = ArrayList<JSONObject>()

    var DETAIL = 500
    var WRITE = 501

    lateinit var questLV: ListView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext)
        return inflater.inflate(R.layout.fra_stack_quest, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        questLV = view.findViewById(R.id.questLV)
        QuestAdapter = QuestAdapter(myContext, R.layout.item_stack_quest, adapterData)
        questLV.adapter = QuestAdapter

        super.onViewCreated(view, savedInstanceState)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        questLV.setOnItemClickListener { parent, view, position, id ->
            val data = adapterData.get(position)
            val answer = Utils.getString(data,"answer")
            val qnas_id = Utils.getString(data,"id")
            val created_at = Utils.getString(data,"created_at")
            if (answer != null && answer != ""){
                val intent = Intent(myContext, Solo_detail_Activity::class.java)
                intent.putExtra("qnas_id",answer)
                startActivityForResult(intent,DETAIL)
            } else {
                val intent = Intent(myContext, WriteActivity::class.java)
                intent.putExtra("qnas_id",qnas_id)
                intent.putExtra("created_at",created_at)
                startActivityForResult(intent,WRITE)
            }
        }

        get_qnas()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }


    fun get_qnas(){
        val params = RequestParams()
        val member_id = PrefUtils.getIntPreference(context, "member_id")
        params.put("member_id", member_id)


        QnasAction.get_qnas(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }


                try {

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {
                        adapterData.clear()
                        val qnas = response!!.getJSONArray("qnas")
                        if (qnas.length() > 0){
                            for (i in 0 until qnas.length()){
                                val item = qnas.get(i) as JSONObject
                                adapterData.add(item)
                            }
                        }
                        QuestAdapter.notifyDataSetChanged()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {}

            private fun error() {

                if (progressDialog != null) {
                    Utils.alert(context, "조회중 장애가 발생하였습니다.")
                }
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

//                val member_id = PrefUtils.getIntPreference(context, "member_id")
//                LogAction.log(javaClass.toString(), member_id, responseString)

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

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                DETAIL -> {
                    if (data!!.getStringExtra("reset") != null) {
                        adapterData.clear()
                        get_qnas()
                    }
                }

                WRITE -> {
                    if (data!!.getStringExtra("reset") != null) {
                        adapterData.clear()
                        get_qnas()
                    }
                }
            }
        }

    }

}
