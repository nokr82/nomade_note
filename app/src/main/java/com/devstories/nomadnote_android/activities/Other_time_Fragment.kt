package com.devstories.nomadnote_android.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.ListView
import android.widget.RelativeLayout
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.CertificationController
import com.devstories.nomadnote_android.actions.TimelineAction
import com.devstories.nomadnote_android.base.*
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fra_other_time.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

open class Other_time_Fragment : Fragment() , AbsListView.OnScrollListener {
    override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {

    }
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null
    lateinit var OthertimeAdapter: OthertimeAdapter
    var data = arrayListOf<Int>()
    lateinit var otherLV:ListView
    private lateinit var activity: MainActivity

    var timelineDatas:ArrayList<JSONObject> = ArrayList<JSONObject>()

    private var page = 1
    private var totalPage = 0
    private var userScrolled = false
    private var lastcount = 0
    private var totalItemCountScroll = 0

    var timeline_position = 0

    private var visibleThreshold = 2

    val SELECT_TIMELINE = 1000

    internal var ResetReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                var timeline_id = intent.getStringExtra("timeline_id")
                detail_timeline(timeline_id)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        GoogleAnalytics.sendEventGoogleAnalytics(GlobalApplication.getGlobalApplicationContext() as GlobalApplication, "android", "타인타임라인")
//        progressDialog = ProgressDialog(myContext)
        return inflater.inflate(R.layout.fra_other_time, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        otherLV = view.findViewById(R.id.otherLV)
        OthertimeAdapter = OthertimeAdapter(myContext, R.layout.item_scrap, timelineDatas, this)
        otherLV.adapter = OthertimeAdapter

        super.onViewCreated(view, savedInstanceState)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity
        activity.titleLL.visibility = View.GONE

        val filter1 = IntentFilter("UPDATE_TIMELINE")
        activity.registerReceiver(ResetReceiver, filter1)

        val filter2 = IntentFilter("DELETE_TIMELINE")
        activity.registerReceiver(ResetReceiver, filter2)

        click()

        getTimeline()

        otherLV.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (userScrolled && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold && page < totalPage && totalPage > 0) {
                    userScrolled = false

                    if (totalPage > page) {
                        page++
                        getTimeline()
                    }
                }
            }

            override fun onScrollStateChanged(questLV:AbsListView, newState: Int) {

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    userScrolled = false
                }

                /*
                if (!otherLV.canScrollVertically(-1)) {
                    page=1
                    getTimeline()
                } else if (!otherLV.canScrollVertically(1)) {
                    if (totalPage > page) {
                        page++
                        lastcount = totalItemCountScroll

                        getTimeline()
                    }
                }
                */
            }
        })

    }

    fun click(){
        writeRL.setOnClickListener {
            val intent = Intent(myContext, WriteActivity::class.java)
            startActivity(intent)
        }


        otherLV.setOnItemClickListener { parent, view, position, id ->
            val timeline = timelineDatas.get(position)
            val timeline_id = Utils.getString(timeline, "id")
            var scrap = Utils.getString(timeline, "scrap")

            var view: View = View.inflate(myContext, R.layout.item_scrap, null)
            var trustRL: RelativeLayout = view.findViewById(R.id.trustRL) as RelativeLayout

            trustRL.setOnClickListener {
                if (scrap == "1"){
                    timelineDatas[position].put("scrap", "1")
                } else {
                    timelineDatas[position].put("scrap", "2")
                }
                OthertimeAdapter.notifyDataSetChanged()
            }

            val intent = Intent(myContext, Solo_detail_Activity::class.java)
            intent.putExtra("timeline_id",timeline_id)
            startActivityForResult(intent,SELECT_TIMELINE)
        }

        searchIV.setOnClickListener {
            page = 1
            getTimeline()
        }

        keywordET.setOnEditorActionListener() { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Utils.hideKeyboard(myContext)
                page = 1
                getTimeline()

            } else {
            }
            false
        }

    }

    fun set_scrap(timeline_id: String){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(myContext,"member_id"))
        params.put("timeline_id", timeline_id)


        TimelineAction.set_scrap(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {

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
                Utils.alert(myContext, getString(R.string.error))
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

    fun getTimeline(){

        var keyword = keywordET.text.toString()

        val params = RequestParams()
        params.put("keyword", keyword)
        params.put("member_id", PrefUtils.getIntPreference(myContext,"member_id"))
        params.put("type", "all")
        params.put("page", page)

        TimelineAction.my_timeline(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
                        if (timelineDatas != null && page == 1){
                            timelineDatas.clear()
                        }

                        val timeline = response!!.getJSONObject("timeline")
                        val datas = timeline!!.getJSONArray("data")
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
                        totalPage = Utils.getInt(timeline,"last_page")
                        page = Utils.getInt(timeline,"current_page")

                        println("totalPage : $totalPage, page : $page")

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
                Utils.alert(myContext, getString(R.string.error))
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
    fun add_certification(timeline_id: String){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(myContext,"member_id"))
        params.put("timeline_id", timeline_id)
        params.put("point", "500")

        CertificationController.add_certification(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {

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
                Utils.alert(myContext, getString(R.string.error))
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

    override fun onPause() {
        super.onPause()
        keywordET.setText("")
    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

        try {
            if (ResetReceiver != null) {
                context!!.unregisterReceiver(ResetReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

    }

    fun detail_timeline(timeline_id:String) {
        val params = RequestParams()
        params.put("timeline_id", timeline_id)

        TimelineAction.detail_timeline(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {
                        var timeline = response!!.getJSONObject("timeline")
                        val data_id = Utils.getString(timeline,"id")
                        for (i in 0 until timelineDatas.size){
                            val item = timelineDatas.get(i)
                            val id = Utils.getString(item,"id")
                            if (data_id == id){
                                timelineDatas.set(i,timeline)
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

        })

    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                SELECT_TIMELINE-> {
                    if (data!!.getStringExtra("timeline_id") != null) {
                        val timeline_id = data!!.getStringExtra("timeline_id")
                        for (i in 0 until timelineDatas.size){
                            val item = timelineDatas.get(i)
                            val id = Utils.getString(item,"id")
                            if (timeline_id == id){
                                timelineDatas.removeAt(i)
                                break
                            }
                        }
                        OthertimeAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    }

}
