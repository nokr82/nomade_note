package com.devstories.nomadnote_android.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.GridView
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.actions.TimelineAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import donggolf.android.adapters.SoloTimeAdapter
import kotlinx.android.synthetic.main.fra_solo_time.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

open class Solo_time_Fragment : Fragment() , AbsListView.OnScrollListener{
    override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {

    }

    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    var timelineDatas: ArrayList<JSONArray> = ArrayList<JSONArray>()
    lateinit var timelineAdaper: SoloTimeAdapter

    lateinit var gridGV: GridView

    var SOLO_WRITE = 1000
    var RESET = 1001

    lateinit var activity: MainActivity

    private var page = 1
    private var totalPage = 0
    private var userScrolled = false
    private var lastcount = 0
    private var totalItemCountScroll = 0

    internal var ResetReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                timelineDatas.clear()
                loadData()
            }
        }
    }

    internal var deleteReciver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                timelineDatas.clear()
                loadData()
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
//        progressDialog = ProgressDialog(myContext)

        return inflater.inflate(R.layout.fra_solo_time, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        gridGV = view.findViewById(R.id.gridGV)

        timelineAdaper = SoloTimeAdapter(myContext!!, R.layout.item_solo_grid, timelineDatas,this)
        gridGV.adapter = timelineAdaper

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity

        val filter1 = IntentFilter("UPDATE_TIMELINE")
        activity.registerReceiver(ResetReceiver, filter1)

        val filter2 = IntentFilter("DELETE_TIMELINE")
        activity.registerReceiver(deleteReciver, filter2)

        click()

        loadData()

        loadFriendRequestData()

        gridGV.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onScrollStateChanged(gridGV:AbsListView, newState: Int) {

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    userScrolled = false
                }

                if (!gridGV.canScrollVertically(-1)) {
                    page=1
                    loadData()
                } else if (!gridGV.canScrollVertically(1)) {
                    if (totalPage > page) {
                        page++
                        lastcount = totalItemCountScroll

                        loadData()
                    }
                }
            }
        })

    }

    fun click() {
//        soloRL.setOnClickListener {
//            val intent = Intent(myContext, Solo_detail_Activity::class.java)
//            startActivity(intent)
//        }
        writeRL.setOnClickListener {
            val intent = Intent(myContext, WriteActivity::class.java)
            startActivityForResult(intent, SOLO_WRITE)
        }
        visitstyleRL.setOnClickListener {
            var intent = Intent()
            intent.action = "STYLE_CHANGE"
            myContext.sendBroadcast(intent)
        }
        visitnationRL.setOnClickListener {
            val intent = Intent(myContext, VisitNationActivity::class.java)
            startActivity(intent)
        }



        gridGV.setOnItemClickListener { parent, view, position, id ->

//            val timeline = timelineDatas.get(position) as JSONArray
//
//            val timeline_id = Utils.getString(timeline, "id")
//
//            val intent = Intent(myContext, Solo_detail_Activity::class.java)
//            intent.putExtra("timeline_id",timeline_id)
//            startActivityForResult(intent,RESET)

        }

        keywordET.setOnEditorActionListener() { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                timelineDatas.clear()
                loadData()

                Utils.hideKeyboard(myContext)

            } else {
            }
            false
        }

    }

    fun loadData() {

        var keyword = Utils.getString(keywordET)
        println("----keyword : $keyword")

        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(myContext, "member_id"))
        params.put("keyword", keyword)
        params.put("page", page)

        TimelineAction.my_timeline(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                if(activity == null || !isAdded) {
                    return
                }

                try {

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {
                        if (page == 1){
                            timelineDatas.clear()
                        }
                        totalPage = response!!.getInt("last_page");
                        page = response!!.getInt("current_page");

                        println("-------page $page")
                        println("-------totalpage $totalPage")

                        val friends = response!!.getJSONArray("friend")
                        Log.d("친구", friends.toString())
                        if (friends.length() > 0) {
                            for (i in 0 until friends.length()) {
                                val timeline = friends.get(i) as JSONObject
                                Log.d("친구", timeline.toString())
                                val friend_timeline = timeline.getJSONArray("timelines")
                                Log.d("친구", friend_timeline.toString())
                                if (friend_timeline.length() > 0) {
                                    for (i in 0 until friend_timeline.length()) {
                                        val timeline = friend_timeline.get(i) as JSONArray
                                        Log.d("친구", timeline.toString())
                                        timelineDatas.add(timeline)
                                    }
                                }

                            }
                        }


                        val data = response!!.getJSONArray("data")
                        if (data.length() > 0) {
                            for (i in 0 until data.length()) {
                                val timeline = data.get(i) as JSONArray
                                timelineDatas.add(timeline)
                            }
                        }
                        timelineAdaper.notifyDataSetChanged()
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
                Utils.alert(myContext, "조회중 장애가 발생하였습니다.")
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

                System.out.println(responseString);

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

    fun loadFriendRequestData() {

        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(myContext, "member_id"))

        MemberAction.request_friends(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {

                        val friends = response!!.getJSONArray("friends")

                        for (i in 0 until friends.length()) {
                            val friend = friends[i] as JSONObject
                            val member = friend.getJSONObject("Member")

                            val name = Utils.getString(member, "name")

                            val message = name + "님이 친구 요청을 하였습니다.\n수락하시겠습니까?"

                            val builder = AlertDialog.Builder(context)
                            builder
                                    .setMessage(message)
                                    .setPositiveButton(getString(R.string.builderyes), DialogInterface.OnClickListener { dialog, id ->
                                        confirmFriend(Utils.getInt(friend, "id"), "add")
                                        dialog.cancel()
                                    })
                                    .setNegativeButton(getString(R.string.builderno), DialogInterface.OnClickListener { dialog, id ->
                                        confirmFriend(Utils.getInt(friend, "id"), "del")
                                        dialog.cancel()
                                    })
                            val alert = builder.create()
                            alert.show()

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
                Utils.alert(myContext, "조회중 장애가 발생하였습니다.")
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

                System.out.println(responseString);

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

    fun confirmFriend(id: Int, type: String) {

        val params = RequestParams()
        params.put("friend_id", id)
        params.put("type", type)

        MemberAction.confirm_friend(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = Utils.getString(response, "result")
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
                Utils.alert(myContext, "조회중 장애가 발생하였습니다.")
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

                System.out.println(responseString);

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
                SOLO_WRITE -> {
                    if (data!!.getStringExtra("reset") != null) {
                        println("-ooooooooo")
                        timelineDatas.clear()
                        loadData()
                    }
                }

                RESET -> {
                    if (data!!.getStringExtra("reset") != null) {
                        println("-ooooooooo")
                        timelineDatas.clear()
                        loadData()
                    }
                }
            }
        }

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

        try {
            if (deleteReciver != null) {
                context!!.unregisterReceiver(deleteReciver)
            }
        } catch (e: IllegalArgumentException) {
        }

    }
}

