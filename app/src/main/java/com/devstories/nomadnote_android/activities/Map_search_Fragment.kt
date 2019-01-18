package com.devstories.nomadnote_android.activities

import android.Manifest
import android.app.AlertDialog
import net.daum.mf.map.api.MapView
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.LinearLayout
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.R.id.center
import com.devstories.nomadnote_android.actions.PlaceAction
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
import kotlinx.android.synthetic.main.fra_map_search.*
import kotlinx.android.synthetic.main.fra_map_search.view.*
import net.daum.mf.map.api.MapCurrentLocationMarker
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class Map_search_Fragment : Fragment(), OnLocationUpdatedListener, MapView.MapViewEventListener, MapView.POIItemEventListener {

    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    private lateinit var activity:MainActivity

    private var myLocation = true
    private var selected_item: JSONObject? = null
    val REQUEST_FINE_LOCATION = 100
    val REQUEST_ACCESS_COARSE_LOCATION = 101

    var latitude = 37.5203175
    var longitude = 126.9107831
    private var isShowing = false

    lateinit var mapView:MapView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext)
        return inflater.inflate(R.layout.fra_map_search, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity
        mapView = MapView(activity)

        val mapPoint2 = MapPoint.mapPointWithGeoCoord(38.5514579595, 126.951949155)
        mapRL.addView(mapView)
        mapView.setMapViewEventListener(this)

        mapView.setCurrentLocationMarker(MapCurrentLocationMarker())


        initGPS()
        if (permissionCheck()) {
            Log.d("성공","tktktktk")
            mapView.setShowCurrentLocationMarker(true)
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            isShowing = true
        } else {
            Log.d("실패","tktktktk")
            mapView.setShowCurrentLocationMarker(false)
            val mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
            mapView.setMapCenterPoint(mapPoint, true)
            isShowing = false
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
            val mainGpsSearchCount =0

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
        mapView.setPOIItemEventListener(this)
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
        Log.d("좌표",latitude.toString())
        Log.d("좌표",longitude.toString())




    }

    //장소불러오기
    fun load_place(){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context,"member_id"))


        PlaceAction.load_place(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
//                        mapRL.removeAllViews()
//                        mapView = MapView(activity)
//                        mapRL.addView(mapView)
                        val place = response!!.getJSONArray("place")
// 지도
                        for (i in 0 until place.length()) {
                            val place_o = place.getJSONObject(i)
                            Log.d("플리이스",place_o.toString())

                            val placename = Utils.getString(place_o, "place")

                            val lat = Utils.getDouble(place_o, "lat")
                            val lng = Utils.getDouble(place_o, "lng")

                            val mapPoint = MapPoint.mapPointWithGeoCoord(lat, lng)

                            val marker = MapPOIItem()
                            marker.itemName = placename
                            marker.userObject = place_o
                            marker.mapPoint = mapPoint

                            marker.markerType = MapPOIItem.MarkerType.BluePin

                            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

                            marker.isShowCalloutBalloonOnTouch = true


                            mapView.addPOIItem(marker)
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

                val center = MapPoint.mapPointWithGeoCoord(latitude, longitude)
                mapView.setMapCenterPoint(center, true)
            } else {
                val center = MapPoint.mapPointWithGeoCoord(latitude, longitude)

                mapView.setMapCenterPoint(center, true)
            }

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
     val place_id =Utils.getInt(json,"id")
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

}
