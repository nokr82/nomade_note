package com.devstories.nomadnote_android.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.ListView
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.CertificationController
import com.devstories.nomadnote_android.actions.TimelineAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationAccuracy
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.location.providers.LocationManagerProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fra_scrap.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

open class Scrap_Fragment : Fragment(), OnLocationUpdatedListener, AbsListView.OnScrollListener {
    override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {

    }

    val REQUEST_ACCESS_COARSE_LOCATION = 101
    val REQUEST_FINE_LOCATION = 100

    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null
    var data = arrayListOf<Int>()
    var timelineDatas:ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var timelineAdapter: ScrapAdapter
    lateinit var scrapLV: ListView
    private lateinit var activity: MainActivity

    var selectedPostion = -1

    private var page = 1
    private var totalPage = 0
    private var userScrolled = false
    private var lastcount = 0
    private var totalItemCountScroll = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
//        progressDialog = ProgressDialog(myContext)
        return inflater.inflate(R.layout.fra_scrap, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        scrapLV = view.findViewById(R.id.scrapLV)
        timelineAdapter = ScrapAdapter(myContext, R.layout.item_scrap, timelineDatas,this)
        scrapLV.adapter = timelineAdapter
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity = getActivity() as MainActivity
        activity.titleLL.visibility = View.GONE
        click()

        getTimeline()

        scrapLV.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onScrollStateChanged(questLV:AbsListView, newState: Int) {

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    userScrolled = false
                }

                if (!scrapLV.canScrollVertically(-1)) {
                    page=1
                    getTimeline()
                } else if (!scrapLV.canScrollVertically(1)) {
                    if (totalPage > page) {
                        page++
                        lastcount = totalItemCountScroll

                        getTimeline()
                    }
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

    fun click(){

        writeRL.setOnClickListener {
            val intent = Intent(myContext, WriteActivity::class.java)
            startActivity(intent)
        }
        scrapLV.setOnItemClickListener { parent, view, position, id ->
            val item = timelineDatas.get(position)
            val timeline = item.getJSONObject("timeline")
            val timeline_id = Utils.getString(timeline, "id")

            val intent = Intent(myContext, Solo_detail_Activity::class.java)
            intent.putExtra("timeline_id",timeline_id)
            startActivity(intent)
        }

        /*
        searchIV.setOnClickListener {
            search_scrap()
        }
        */

        keywordET.setOnEditorActionListener() { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val srchWd = keywordET.text.toString()
                if (srchWd != null && srchWd != "") {
                    search_scrap()
                }

                if (srchWd == null || srchWd == "") {
                    timelineDatas.clear()
                    getTimeline()
                }

                Utils.hideKeyboard(myContext)
            } else {
            }
            false
        }
    }


    fun getTimeline(){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(myContext,"member_id"))
        params.put("type", "scrap")
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

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
                        if (page == 1){
                            timelineDatas.clear()
                        }

                        val scraps = response!!.getJSONObject("scraps")
                        val datas = scraps.getJSONArray("data")
                        if (datas.length() > 0){
                            for (i in 0 until datas.length()){
                                val timeline = datas.get(i) as JSONObject
                                timelineDatas.add(timeline)
//                                timelineDatas[i].put("isSelectedOp", true)

                            }
                        }

                        totalPage = Utils.getInt(scraps,"last_page")
                        page = Utils.getInt(scraps,"current_page")

                        timelineAdapter.notifyDataSetChanged()
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

    fun search_scrap(){

        var keyword = keywordET.text.toString()
        if (keyword == "" || keyword == null){
            timelineDatas.clear()
            getTimeline()
            return
        }

        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(myContext,"member_id"))
        params.put("type", "scrap")
        params.put("keyword", keyword)

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

                        val datas = response!!.getJSONArray("scraps")
                        if (datas.length() > 0){
                            for (i in 0 until datas.length()){
                                val timeline = datas.get(i) as JSONObject
                                timelineDatas.add(timeline)
                                timelineDatas[i].put("isSelectedOp", true)

                            }
                        }
                        timelineAdapter.notifyDataSetChanged()
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

    fun add_certification(timeline_id: String, latitude: Double, longitude: Double){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(myContext,"member_id"))
        params.put("timeline_id", timeline_id)
        params.put("latitude", latitude)
        params.put("longitude", longitude)

        CertificationController.add_certification(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
                        var point = Utils.getInt(response,"point")
                        PrefUtils.setPreference(myContext, "point", point)


                        var json = timelineDatas.get(selectedPostion)
                        json.put("certification", "2")
                        timelineAdapter.notifyDataSetChanged()

                    } else if ("far_away" == result) {
                        Toast.makeText(myContext, getString(R.string.authenticate_no), Toast.LENGTH_SHORT).show()

                    } else if ("already" == result) {
                        Toast.makeText(myContext, getString(R.string.authenticate_no), Toast.LENGTH_SHORT).show()
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





    fun initGPS(pos:Int) {

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

        selectedPostion = pos

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION)
        } else {
            checkGPs()
        }
    }

    private fun checkGPs() {
        if (Utils.availableLocationService(myContext)) {
            startLocation()
        } else {
            gpsCheckAlert.sendEmptyMessage(0)
        }
    }

    internal var gpsCheckAlert: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val mainGpsSearchCount = 0

            if (mainGpsSearchCount == 0) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        }
    }

    private fun startLocation() {

        val smartLocation = SmartLocation.Builder(myContext).logging(true).build()
        val locationControl = smartLocation.location(LocationManagerProvider()).oneFix()

        if (SmartLocation.with(myContext).location(LocationManagerProvider()).state().isGpsAvailable) {
            val locationParams = LocationParams.Builder().setAccuracy(LocationAccuracy.MEDIUM).build()
            locationControl.config(locationParams)
        } else if (SmartLocation.with(myContext).location(LocationManagerProvider()).state().isNetworkAvailable) {
            val locationParams = LocationParams.Builder().setAccuracy(LocationAccuracy.LOW).build()
            locationControl.config(locationParams)
        }

        smartLocation.location().oneFix().start(this)

    }

    private fun stopLocation() {
        SmartLocation.with(myContext).location().stop()
    }

    private fun permissionCheck(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            !(ContextCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED)
        } else {
            false
        }
    }

    private fun loadPermissions(perm: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(myContext, perm) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(perm), requestCode)
        } else {
            if (Manifest.permission.ACCESS_FINE_LOCATION == perm) {
                loadPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_ACCESS_COARSE_LOCATION)
            } else if (Manifest.permission.ACCESS_COARSE_LOCATION == perm) {
                checkGPs()
            }
        }
    }


    override fun onLocationUpdated(location: Location?) {
        stopLocation()

        if(activity == null || !isAdded) {
            return
        }

        println("lo : $location")

        if (location != null) {

            val latitude = location.getLatitude()
            val longitude = location.getLongitude()

            var json = timelineDatas.get(selectedPostion)
            var timeline = json.getJSONObject("timeline")
            var timeline_id = Utils.getString(timeline,"id")

            add_certification(timeline_id, latitude, longitude)

        } else {
            if (progressDialog != null) {
                progressDialog!!.dismiss()
            }

            Toast.makeText(myContext, "Invalid request[1]", Toast.LENGTH_SHORT).show()
        }

    }

    fun onLocationUpdatedOld(location: Location?) {
        stopLocation()

        println("lo : $location")

        if (location != null) {

            val latitude = location.getLatitude()
            val longitude = location.getLongitude()

            var geocoder: Geocoder = Geocoder(myContext, Locale.KOREAN);

            var adminArea = ""
            var list:List<Address> = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 10);
            if(list.size > 0){
                adminArea = list.get(0).adminArea
            }

            println("adminArea : $adminArea")

            geocoder = Geocoder(myContext);

            list = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 10);

            println(list)

            if(list.size > 0) {
                if(selectedPostion >= 0) {
                    var json = timelineDatas.get(selectedPostion)
                    var timeline = json.getJSONObject("timeline")
                    var timeline_id = Utils.getString(timeline,"id")
                    var admin_area_kr = Utils.getString(timeline,"admin_area_kr")

                    println("admin_area_kr : $admin_area_kr")

                    if(admin_area_kr == adminArea) {

                        json.put("certification", "2")
                        timelineAdapter.notifyDataSetChanged()
                        add_certification(timeline_id, latitude, longitude)

                    } else {
                        if (progressDialog != null) {
                            progressDialog!!.dismiss()
                        }

                        Toast.makeText(myContext, "Invalid request[4]", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (progressDialog != null) {
                        progressDialog!!.dismiss()
                    }

                    Toast.makeText(myContext, "Invalid request[3]", Toast.LENGTH_SHORT).show()
                }

            } else {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                Toast.makeText(myContext, "Invalid request[2]", Toast.LENGTH_SHORT).show()
            }

        } else {
            if (progressDialog != null) {
                progressDialog!!.dismiss()
            }

            Toast.makeText(myContext, "Invalid request[1]", Toast.LENGTH_SHORT).show()
        }

    }

}
