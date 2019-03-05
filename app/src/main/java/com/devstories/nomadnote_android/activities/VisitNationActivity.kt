package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.NationAction
import com.devstories.nomadnote_android.base.GlobalApplication
import com.devstories.nomadnote_android.base.GoogleAnalytics
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_visit_nation.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VisitNationActivity : FragmentActivity(), OnMapReadyCallback {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    lateinit var visitNationAdapter: VisitNationAdapter
    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()

    private lateinit var googleMap: GoogleMap
    private val markers = java.util.ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_nation)
        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
//        progressDialog = ProgressDialog(context)


        GoogleAnalytics.sendEventGoogleAnalytics(application as GlobalApplication, "android", "방문국가기록")

        titleBackLL.setOnClickListener {
            finish()
        }

        visitNationAdapter = VisitNationAdapter(context, R.layout.item_nation, adapterData)
        visitLV.adapter = visitNationAdapter

        val mapFragment =  supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        visitLV.setOnItemClickListener { parent, view, position, id ->
            val item = adapterData.get(position)
            val id = Utils.getInt(item,"id")
            var country = Utils.getString(item,"country")

            var language = Locale.getDefault().language
            if(language == "en" || language == "ja") {
                country = Utils.getString(item, language)
            } else if(language == "zh") {
                language = Locale.getDefault().isO3Country
                if(language == "CHN") {
                    country = Utils.getString(item, "zh_rCN")
                } else if(language == "TWN") {
                    country = Utils.getString(item, "zh_rTW")
                }
            }

            val intent = Intent(context, CountryTimelineActivity::class.java)
            intent.putExtra("country_id",id)
            intent.putExtra("country",country)
            startActivity(intent)

        }




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

    private fun loadData() {

        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))

        NationAction.getcountry(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    adapterData.clear()

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {
                        var countrysDatas = response!!.getJSONArray("countrys")
                        if (countrysDatas.length() > 0){
                            for (i in 0 until countrysDatas.length()){
                                val item = countrysDatas.get(i) as JSONObject
                                adapterData.add(item)
                            }
                        }
                    }

                    visitNationAdapter.notifyDataSetChanged()

                    addMarkers()

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

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.setMaxZoomPreference(2f)
        googleMap.setMinZoomPreference(2f)
        googleMap.getUiSettings().setRotateGesturesEnabled(false)

        googleMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker ->
            System.out.println(marker);

            true
        })

        googleMap.setOnMapClickListener(GoogleMap.OnMapClickListener {

        })

        googleMap.setOnCameraMoveStartedListener(GoogleMap.OnCameraMoveStartedListener {

        })

        googleMap.setOnCameraIdleListener {
            println("g : ${googleMap.cameraPosition.zoom}")
        }

        loadData()
    }


    private fun addMarkers() {


        for (i in 0 until adapterData.size) {
            val place = adapterData.get(i)

            val placename = Utils.getString(place, "country")

            val lat = Utils.getDouble(place, "lat")
            val lng = Utils.getDouble(place, "lng")

            val latlng = LatLng(lat, lng)

//            val marker = googleMap.addMarker(MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.visit_city)))
            val marker = googleMap.addMarker(MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_icon)))
            marker.tag = place

            markers.add(marker)
        }

        fitBounds()

    }


    private fun fitBounds() {

        if(markers.size == 0) {
            return
        }

        val builder = LatLngBounds.Builder()

        for (marker in markers) {
            builder.include(marker.position)
        }

        val bounds = builder.build()

        val padding = 200 // offset from edges of the map in pixels
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        googleMap.moveCamera(cu)
    }

}
