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
import android.widget.LinearLayout
import com.devstories.nomadnote_android.R
import kotlinx.android.synthetic.main.fra_map_search.*
import net.daum.mf.map.api.MapCurrentLocationMarker

class Map_search_Fragment : Fragment()  {
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null
    private var mapView: MapView? = null

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
        mapView = MapView(activity)
        mapLL.addView(mapView)


        click()
    }




fun click(){

    mapLL.setOnClickListener {
        val intent = Intent(myContext, MapSearchActivity::class.java)
        startActivity(intent)
    }
}
    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
