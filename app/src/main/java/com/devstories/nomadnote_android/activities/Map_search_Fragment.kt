package com.devstories.nomadnote_android.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.PlaceAction
import com.devstories.nomadnote_android.base.GlobalApplication
import com.devstories.nomadnote_android.base.GoogleAnalytics
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.Utils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fra_map_search.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToInt


class Map_search_Fragment : Fragment(), OnLocationUpdatedListener, OnMapReadyCallback {

    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    private lateinit var activity: MainActivity

    private var myLocationSetted = false
    private var selected_item: JSONObject? = null
    val REQUEST_FINE_LOCATION = 100
    val REQUEST_ACCESS_COARSE_LOCATION = 101

    var latitude = 37.5203175
    var longitude = 126.9107831
    private var isShowing = false

    // lateinit var mapView:MapView

    private var googleMap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment

    private val markers = ArrayList<Marker>()
    var places = JSONArray()

    val SELECT_ITEM = 1000


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    internal var ResetReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                initGPS()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        GoogleAnalytics.sendEventGoogleAnalytics(GlobalApplication.getGlobalApplicationContext() as GlobalApplication, "android", "지도검색")

        return inflater.inflate(R.layout.fra_map_search, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity
        activity.titleLL.visibility = View.GONE

        /*
        mapView = MapView(activity)

        val mapPoint2 = MapPoint.mapPointWithGeoCoord(38.5514579595, 126.951949155)
        mapRL.addView(mapView)
        mapView.setMapViewEventListener(this)

        mapView.setCurrentLocationMarker(MapCurrentLocationMarker())
        */

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {

                fusedLocationClient.removeLocationUpdates(locationCallback)

                locationResult ?: return
                onLocationUpdated(locationResult.lastLocation)
            }
        }

        mapFragment = SupportMapFragment.newInstance()
        mapFragment.getMapAsync(this)

        childFragmentManager.beginTransaction().replace(R.id.gmap, mapFragment).commit()

        val filter1 = IntentFilter("UPDATE_TIMELINE")
        activity.registerReceiver(ResetReceiver, filter1)

        val filter2 = IntentFilter("DELETE_TIMELINE")
        activity.registerReceiver(ResetReceiver, filter2)

        isShowing = permissionCheck()

        writeRL.setOnClickListener {
            val intent = Intent(myContext, WriteActivity::class.java)
            startActivity(intent)
        }

        searchIV.setOnClickListener {
            load_place()

            Utils.hideKeyboard(myContext)
        }


        /*   mapView.setMapCenterPoint(mapPoint, true)
        mapRL.addView(mapView)
        val marker = MapPOIItem()
        marker.itemName = "커스텀마크테스트"
        marker.tag = 0
        marker.mapPoint = mapPoint
        marker.customImageResourceId = R.layout.item_map
        marker.markerType = MapPOIItem.MarkerType.CustomImage
        marker.isCustomImageAutoscale = false
        marker.setCustomImageAnchor(0.5f,1.0f)
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
//        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker)
        val marker2 = MapPOIItem()
        marker2.itemName = "테스트"
        marker2.tag = 1
        marker2.mapPoint = mapPoint2
        // 기본으로 제공하는 BluePin 마커 모양.
        marker2.markerType = MapPOIItem.MarkerType.BluePin
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker2.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker2)*/

        keywordET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                load_place()

                Utils.hideKeyboard(myContext)
            } else {
            }
            false
        }

    }

    private fun initGPS() {
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
                latitude = -1.0
                longitude = -1.0



                val builder = AlertDialog.Builder(myContext)
                builder.setTitle("확인")
                builder.setMessage("위치 서비스 이용이 제한되어 있습니다.\n설정에서 위치 서비스 이용을 허용해주세요.")
                builder.setCancelable(true)
                builder.setNegativeButton("취소") { dialog, id ->
                    dialog.cancel()

                    latitude = 37.5203175
                    longitude = 126.9107831

                }
                builder.setPositiveButton(myContext.getString(R.string.settings)) { dialog, id ->
                    dialog.cancel()
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                val alert = builder.create()
                alert.show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocation() {

        if (progressDialog != null) {
            // show dialog
            //progressDialog.setMessage("현재 위치 확인 중...");
            progressDialog!!.show()
        }

        /*
        val smartLocation = SmartLocation.Builder(context).logging(true).build()
        val locationControl = smartLocation.location(LocationManagerProvider()).oneFix()

        if (SmartLocation.with(context).location(LocationManagerProvider()).state().isGpsAvailable) {
            val locationParams = LocationParams.Builder().setAccuracy(LocationAccuracy.MEDIUM).build()
            locationControl.config(locationParams)
        } else if (SmartLocation.with(context).location(LocationManagerProvider()).state().isNetworkAvailable) {
            val locationParams = LocationParams.Builder().setAccuracy(LocationAccuracy.LOW).build()
            locationControl.config(locationParams)
        }

        smartLocation.location().oneFix().start(this)
        */

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000  /* 10 secs */
        locationRequest.fastestInterval = 2000 /* 2 sec */

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

    }

    private fun stopLocation() {
        SmartLocation.with(myContext).location().stop()
    }

    private fun permissionCheck(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            !(checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED)
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

    //장소불러오기
    fun load_place() {

        val keyword = Utils.getString(keywordET)

        val params = RequestParams()
        params.put("keyword", keyword)
        params.put("member_id", PrefUtils.getIntPreference(myContext, "member_id"))

        println("-----load_place----")

        PlaceAction.load_place(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {

                        places = response!!.getJSONArray("place")

                        println("----------places-----$response")

                        addMarkers(keyword)
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

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

    override fun onLocationUpdated(location: Location?) {
        stopLocation()

        if(activity == null || !isAdded) {
            return
        }

        println("lo : $location")

        if (location != null) {

            latitude = location.latitude
            longitude = location.longitude

            myLocationSetted = true

            if(googleMap != null) {
                val cu = CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 14.1f)
                googleMap!!.animateCamera(cu)
            }


            if (progressDialog != null) {
                progressDialog!!.dismiss()
            }
        }

        load_place()
    }

    fun showplace(json: JSONObject) {
        val place_id = Utils.getInt(json, "id")
        val intent = Intent(myContext, MapSearchActivity::class.java)
        intent.putExtra("place_id", place_id)
        startActivity(intent)

    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

        try {
            if (ResetReceiver != null) {
                activity.unregisterReceiver(ResetReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap!!.uiSettings.isRotateGesturesEnabled = false

        googleMap!!.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker ->
            // System.out.println(marker);

            val keyword = Utils.getString(keywordET)

            val item = marker.tag as JSONObject?
            val type = Utils.getString(item, "type")

            // System.out.println("type : " + type);

            val place_id = Utils.getInt(item,"id")
            val intent = Intent(myContext, MapSearchActivity::class.java)
            intent.putExtra("place_id", place_id)
            intent.putExtra("keyword", keyword)

            startActivityForResult(intent,SELECT_ITEM)

            true
        })

        googleMap!!.setOnMapClickListener(GoogleMap.OnMapClickListener {

        })

        googleMap!!.setOnCameraMoveStartedListener(GoogleMap.OnCameraMoveStartedListener {

        })

        googleMap!!.setOnCameraIdleListener {
            val zoom = googleMap!!.cameraPosition.zoom

            println("zoom : $zoom")

            if(zoom >= 14) {
                for (marker in markers) {
                    marker.isVisible = true
                }
            } else {
                for (marker in markers) {
                    marker.isVisible = false
                }
            }
        }

        if(myLocationSetted) {
            val cu = CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 14.1f)
            googleMap?.animateCamera(cu)
        }


        initGPS()

    }


    private fun addMarkers(keyword: String) {

        if(activity == null || !isAdded) {
            return
        }

        googleMap?.clear()
        markers.clear()

        for (i in 0 until places.length()) {
            val place = places.getJSONObject(i)

            val placename = Utils.getString(place, "place")

            val lat = Utils.getDouble(place, "lat")
            val lng = Utils.getDouble(place, "lng")

            val latlng = LatLng(lat, lng)

            var bitmap = BitmapFactory.decodeResource(myContext.resources, R.mipmap.pin2)

            // draw

            val scale = resources.displayMetrics.density

            var bitmapConfig = bitmap.config
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true)

            val canvas = Canvas(bitmap)
            // new antialised Paint
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.WHITE
            // text size in pixels
            paint.textSize = (14 * scale).roundToInt().toFloat()

            var place_name = Utils.getString(place, "place")

            var language = Locale.getDefault().language
            if(language == "en" || language == "ja") {
                place_name = Utils.getString(place, language)
            } else if(language == "zh") {
                language = Locale.getDefault().isO3Country
                if(language == "CHN") {
                    place_name = Utils.getString(place, "zh_rCN")
                } else {
                    place_name = Utils.getString(place, "zh_rTW")
                }
            }


            // draw text to the Canvas center
            val total_durations = Utils.getInt(place, "total_durations")
            val hour = total_durations / 60
            val min = total_durations % 60
            val duration = "$hour:$min"

            val total_costs = Utils.getInt(place, "total_costs")
            var costs = "$total_costs$"
            if(language == "en" || language == "ja") {
                costs = getString(R.string.unit) + total_costs.toString()
            } else if (language == "zh"){
                costs = total_costs.toString() +"\n" + getString(R.string.unit)
            } else {        // 코리아
                costs = total_costs.toString() + getString(R.string.unit)
            }

            println("duration : $duration, costs : $costs")

            //draw the first text
            val bounds1 = Rect()
            val paint1 = Paint()
            paint1.color = Color.WHITE
            // text size in pixels
            paint1.textSize = (11 * scale).roundToInt().toFloat()
            paint1.getTextBounds(place_name, 0, place_name.length, bounds1)

            var x = (bitmap.width - bounds1.width()) / 2f - duration.length
            var y = 36f * scale
            canvas.drawText(place_name, x, y, paint1)

            //draw the first text
            val bounds2 = Rect()
            val paint2 = Paint()
            paint2.color = Color.WHITE
            // text size in pixels
            paint2.textSize = (11 * scale).roundToInt().toFloat()
            paint2.getTextBounds(duration, 0, duration.length, bounds2)

            x = (bitmap.width - bounds2.width()) / 2f - duration.length
            y = 49f * scale
            canvas.drawText(duration, x, y, paint2)

            //draw the second text
            val bounds3 = Rect()
            val paint3 = Paint(Paint.ANTI_ALIAS_FLAG)
            paint3.color = Color.WHITE
            // text size in pixels
            paint3.textSize = (11 * scale).roundToInt().toFloat()
            paint3.getTextBounds(costs, 0, costs.length, bounds3)

            x = (bitmap.width - bounds3.width()) / 2f - costs.length
            y = 74f * scale
            canvas.drawText(costs, x, y, paint3)




            // draw


            val marker = googleMap!!.addMarker(MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
            marker.tag = place
            val zoom = googleMap!!.cameraPosition.zoom
            marker.isVisible = zoom >= 14

            markers.add(marker)

            /*
            if(keyword.isNotEmpty() && i == 0) {
                val cu = CameraUpdateFactory.newLatLngZoom(latlng, 14.1f)
                googleMap!!.animateCamera(cu)
            }
            */

        }

        // fitBounds()

    }


    private fun fitBounds() {

        val builder = LatLngBounds.Builder()

        for (marker in markers) {
            builder.include(marker.position)
        }

        val bounds = builder.build()

        val padding = 200 // offset from edges of the map in pixels
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        googleMap?.moveCamera(cu)
    }

    override fun onPause() {
        super.onPause()
        keywordET.setText("")
    }

}
