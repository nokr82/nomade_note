package com.devstories.nomadnote_android.activities

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.PlaceAction
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
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationAccuracy
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.location.providers.LocationManagerProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fra_map_search.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToInt


class Map_search_Fragment : Fragment(), OnLocationUpdatedListener, MapView.MapViewEventListener, MapView.POIItemEventListener, OnMapReadyCallback {

    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    private lateinit var activity: MainActivity

    private var myLocation = true
    private var selected_item: JSONObject? = null
    val REQUEST_FINE_LOCATION = 100
    val REQUEST_ACCESS_COARSE_LOCATION = 101

    var latitude = 37.5203175
    var longitude = 126.9107831
    private var isShowing = false

    // lateinit var mapView:MapView

    private lateinit var googleMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    private val markers = ArrayList<Marker>()
    var places = JSONArray()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        return inflater.inflate(R.layout.fra_map_search, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        mapFragment = SupportMapFragment.newInstance()
        mapFragment.getMapAsync(this)

        childFragmentManager.beginTransaction().replace(R.id.gmap, mapFragment).commit()


        initGPS()

        if (permissionCheck()) {
            Log.d("성공", "tktktktk")
            // mapView.setShowCurrentLocationMarker(true)
            // mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            isShowing = true
        } else {
            Log.d("실패", "tktktktk")
            // mapView.setShowCurrentLocationMarker(false)
            // val mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
            // mapView.setMapCenterPoint(mapPoint, true)
            isShowing = false
        }

        writeRL.setOnClickListener {
            val intent = Intent(myContext, WriteActivity::class.java)
            startActivity(intent)
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

        keywordET.setOnEditorActionListener() { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                load_place()

                Utils.hideKeyboard(context)
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
        if (Utils.availableLocationService(context)) {
//            startLocation()
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



                val builder = AlertDialog.Builder(context)
                builder.setTitle("확인")
                builder.setMessage("위치 서비스 이용이 제한되어 있습니다.\n설정에서 위치 서비스 이용을 허용해주세요.")
                builder.setCancelable(true)
                builder.setNegativeButton("취소") { dialog, id ->
                    dialog.cancel()

                    latitude = 37.5203175
                    longitude = 126.9107831

                }
                builder.setPositiveButton("설정") { dialog, id ->
                    dialog.cancel()
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                val alert = builder.create()
                alert.show()
            }
        }
    }

    private fun startLocation() {

        if (progressDialog != null) {
            // show dialog
            //progressDialog.setMessage("현재 위치 확인 중...");
            progressDialog!!.show()
        }

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

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewInitialized(p0: MapView?) {
        // mapView.setPOIItemEventListener(this)
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(mapView: MapView?, mapPoint: MapPoint?) {
        if (isShowing) {
            if (mapView != null) {
                if (mapView.isShowingCurrentLocationMarker) {
                    if (mapView != null) {
                        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
                    }
                }
            }
        }

        myLocation = false
        /*    val mapPoint = MapPoint.mapPointWithGeoCoord(37.5514579595, 126.951949155)
            val marker = MapPOIItem()
            marker.itemName = "테스트"
            marker.mapPoint = mapPoint
            marker.markerType = MapPOIItem.MarkerType.BluePin
            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
            marker.isShowCalloutBalloonOnTouch = true
            mapView.addPOIItem(marker)
            val mapPoint2 = MapPoint.mapPointWithGeoCoord(37.48906326293945, 126.75608825683594)
            val marker2 = MapPOIItem()
            marker2.itemName = "테스트"
            marker2.mapPoint = mapPoint2
            marker2.markerType = MapPOIItem.MarkerType.BluePin
            marker2.selectedMarkerType = MapPOIItem.MarkerType.RedPin
            marker2.isShowCalloutBalloonOnTouch = true
            mapView.addPOIItem(marker2)*/
        val gc = mapPoint!!.getMapPointGeoCoord()
        load_place()
        latitude = gc.latitude
        longitude = gc.longitude
        Log.d("좌표", latitude.toString())
        Log.d("좌표", longitude.toString())


    }

    //장소불러오기
    fun load_place() {

        val keyword = Utils.getString(keywordET)

        val params = RequestParams()
        params.put("keyword", keyword)
        params.put("member_id", PrefUtils.getIntPreference(myContext, "member_id"))


        PlaceAction.load_place(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {
                        places = response!!.getJSONArray("place")

                        addMarkers()
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


    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }


    override fun onLocationUpdated(location: Location?) {
        stopLocation()
        if (location != null) {
            if (myLocation) {
                latitude = location.getLatitude()
                longitude = location.getLongitude()

            }

            val latlng = LatLng(latitude, longitude)

            val cu = CameraUpdateFactory.newLatLng(latlng)
            googleMap.animateCamera(cu)

            if (progressDialog != null) {
                progressDialog!!.dismiss()
            }
        }

    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {

    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }

    override fun onPOIItemSelected(MapView: MapView?, marker: MapPOIItem?) {
        selected_item = marker!!.userObject as JSONObject
        showplace(selected_item!!)

    }

    fun showplace(json: JSONObject) {
        val place_id = Utils.getInt(json, "id")
        val intent = Intent(myContext, MapSearchActivity::class.java)
        intent.putExtra("place_id", place_id)
        startActivity(intent)

    }


    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.getUiSettings().setRotateGesturesEnabled(false)

        googleMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker ->
            // System.out.println(marker);

            val item = marker.tag as JSONObject?
            val type = Utils.getString(item, "type")

            // System.out.println("type : " + type);

            val place_id = Utils.getInt(item,"id")
            val intent = Intent(myContext, MapSearchActivity::class.java)
            intent.putExtra("place_id", place_id)
            startActivity(intent)

            true
        })

        googleMap.setOnMapClickListener(GoogleMap.OnMapClickListener {

        })

        googleMap.setOnCameraMoveStartedListener(GoogleMap.OnCameraMoveStartedListener {

        })

        load_place()
    }


    private fun addMarkers() {

        googleMap.clear()

        for (i in 0 until places.length()) {
            val place = places.getJSONObject(i)

            val placename = Utils.getString(place, "place")

            val lat = Utils.getDouble(place, "lat")
            val lng = Utils.getDouble(place, "lng")

            val latlng = LatLng(lat, lng)

            var bitmap = BitmapFactory.decodeResource(myContext.getResources(), R.mipmap.pin2);

            // draw

            val scale = resources.displayMetrics.density

            var bitmapConfig = bitmap.config;
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

            val place_name = Utils.getString(place, "place")

            // draw text to the Canvas center
            val total_durations = Utils.getInt(place, "total_durations")
            val hour = total_durations / 60
            val min = total_durations % 60
            val duration = "$hour:$min"

            val total_costs = Utils.getInt(place, "total_costs")
            val costs = "$total_costs$"

            println("duration : $duration, costs : $costs")

            //draw the first text
            val bounds1 = Rect()
            val paint1 = Paint()
            paint1.color = Color.WHITE
            // text size in pixels
            paint1.textSize = (14 * scale).roundToInt().toFloat()
            paint1.getTextBounds(place_name, 0, place_name.length, bounds1)

            var x = (bitmap.width - bounds1.width()) / 2f - duration.length
            var y = 28f * scale
            canvas.drawText(place_name, x, y, paint1)

            //draw the first text
            val bounds2 = Rect()
            val paint2 = Paint()
            paint2.color = Color.WHITE
            // text size in pixels
            paint2.textSize = (14 * scale).roundToInt().toFloat()
            paint2.getTextBounds(duration, 0, duration.length, bounds2)

            x = (bitmap.width - bounds2.width()) / 2f - duration.length
            y = 48f * scale
            canvas.drawText(duration, x, y, paint2)

            //draw the second text
            val bounds3 = Rect()
            val paint3 = Paint(Paint.ANTI_ALIAS_FLAG)
            paint3.color = Color.WHITE
            // text size in pixels
            paint3.textSize = (14 * scale).roundToInt().toFloat()
            paint3.getTextBounds(costs, 0, costs.length, bounds3)

            x = (bitmap.width - bounds3.width()) / 2f - costs.length
            y = 74f * scale
            canvas.drawText(costs, x, y, paint3)




            // draw


            val marker = googleMap.addMarker(MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
            marker.tag = place

            markers.add(marker)
        }

        fitBounds()

    }


    private fun fitBounds() {

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
