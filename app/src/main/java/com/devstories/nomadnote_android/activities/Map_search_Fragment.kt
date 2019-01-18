package com.devstories.nomadnote_android.activities

import net.daum.mf.map.api.MapView
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devstories.nomadnote_android.R
import kotlinx.android.synthetic.main.fra_map_search.*
import kotlinx.android.synthetic.main.fra_map_search.view.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint


class Map_search_Fragment : Fragment(), MapView.POIItemEventListener,MapView.CurrentLocationEventListener {
    override fun onCurrentLocationUpdateFailed(p0: MapView?) {

    }

    override fun onCurrentLocationUpdate(p0: MapView?, p1: MapPoint?, p2: Float) {

    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {

    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {

    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

    }

    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    private lateinit var activity:MainActivity


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

        val mapView = MapView(activity)
        val mapPoint = MapPoint.mapPointWithGeoCoord(37.5514579595, 126.951949155)
        val mapPoint2 = MapPoint.mapPointWithGeoCoord(38.5514579595, 126.951949155)
        mapView.setMapCenterPoint(mapPoint, true)
        mapRL.addView(mapView)
        val marker = MapPOIItem()
        marker.itemName = "한세사이버보안고등학교"
        marker.tag = 0
        marker.mapPoint = mapPoint
        // 기본으로 제공하는 BluePin 마커 모양.
        marker.markerType = MapPOIItem.MarkerType.BluePin
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker)
        val marker2 = MapPOIItem()
        marker2.itemName = "테스트"
        marker2.tag = 1
        marker2.mapPoint = mapPoint2
        // 기본으로 제공하는 BluePin 마커 모양.
        marker2.markerType = MapPOIItem.MarkerType.BluePin
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker2.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker2)

    }




    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
