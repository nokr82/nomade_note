package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.TimelineAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_timeline.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Solo_detail_Activity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var timeline_id = ""
    var menu_position = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            // w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_timeline)
        this.context = this
        progressDialog = ProgressDialog(context)

        click()

        var intent = getIntent()
        timeline_id = intent.getStringExtra("timeline_id")
        detail_timeline()


    }

    fun click(){
        titleBackLL.setOnClickListener {
            finish()
        }

    }

    fun detail_timeline(){
        val params = RequestParams()
//        params.put("member_id", PrefUtils.getIntPreference(context,"member_id"))
        params.put("timeline_id", timeline_id)

        TimelineAction.detail_timeline(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
                        val data = response!!.getJSONObject("timeline")
                        var place_name = Utils.getString(data,"place_name")
                        var duration = Utils.getString(data,"duration")
                        var cost = Utils.getString(data,"cost")
                        var contents = Utils.getString(data,"contents")
                        var created = Utils.getString(data,"created_at")
                        var style = Utils.getString(data,"style_id")

                        var createdsplit = created.split(" ")
                        var timesplit = createdsplit.get(1).split(":")

                        placeTV.setText(place_name)
                        durationTV.setText(duration)
                        costTV.setText(cost + "$")
                        contentTV.setText(contents)

                        val member = response!!.getJSONObject("member")
                        val founder_id = Utils.getString(member,"id")
                        val name = Utils.getString(member,"name")
                        val age = Utils.getString(member,"age")

                        if (founder_id.toInt() != PrefUtils.getIntPreference(context, "member_id")){
                            modifyIV.visibility = View.GONE
                            lockIV.visibility = View.GONE
                        }

                        infoTV.setText(name+"/"+age+"세")


                        if (timesplit.get(0).toInt() >= 12){
                            createdTV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":"+timesplit.get(1))
                        } else {
                            createdTV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":"+timesplit.get(1))
                        }

                        when(style){
                            "1" ->{
                                menu_position = 1
                                setMenuImage(menu_position)
                            }

                            "2" ->{
                                menu_position = 2
                                setMenuImage(menu_position)
                            }

                            "3" ->{
                                menu_position = 3
                                setMenuImage(menu_position)
                            }

                            "4" ->{
                                menu_position = 4
                                setMenuImage(menu_position)
                            }

                            "5" ->{
                                menu_position = 5
                                setMenuImage(menu_position)
                            }
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

    fun menuSetImage(){
        healingTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        healingTV.setTextColor(Color.parseColor("#878787"))
        hotplaceTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        hotplaceTV.setTextColor(Color.parseColor("#878787"))
        literatureTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        literatureTV.setTextColor(Color.parseColor("#878787"))
        historyTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        historyTV.setTextColor(Color.parseColor("#878787"))
        museumTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        museumTV.setTextColor(Color.parseColor("#878787"))
    }

    fun setMenuImage(menu_position: Int){
        menuSetImage()
        when(menu_position){
            1 ->{
                healingTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                healingTV.setTextColor(Color.parseColor("#ffffff"))
            }

            2 ->{
                hotplaceTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                hotplaceTV.setTextColor(Color.parseColor("#ffffff"))
            }

            3 ->{
                literatureTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                literatureTV.setTextColor(Color.parseColor("#ffffff"))
            }

            4 ->{
                historyTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                historyTV.setTextColor(Color.parseColor("#ffffff"))
            }

            5 ->{
                museumTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                museumTV.setTextColor(Color.parseColor("#ffffff"))
            }
        }
    }

}
