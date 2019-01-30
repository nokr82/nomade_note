package com.devstories.nomadnote_android.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.TimelineAction
import com.devstories.nomadnote_android.base.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationAccuracy
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.location.providers.LocationManagerProvider
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.item_addgoods.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.nostra13.universalimageloader.core.ImageLoader

private val imgSeq = 0

class WriteActivity : RootActivity(), OnLocationUpdatedListener {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var menu_position = 1

    val SELECT_PICTURE = 1000

    var images_path: ArrayList<String> = ArrayList<String>()

    var timeline_id = ""
    var qnas_id = ""
    var created_at = ""

    val REQUEST_ACCESS_COARSE_LOCATION = 101
    val REQUEST_FINE_LOCATION = 100

    var latitude = 37.5203175
    var longitude = 126.9107831

    private var myLocation = true

    private var main_index = -1

    private val timelineData = ArrayList<JSONObject>()

    var block_yn = "N"

    var country = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
//        progressDialog = ProgressDialog(context)

        click()

        var intent = getIntent()

        val time = Utils.timeStr()
        var timesplit = time.split(":")
        timeET.setText(timesplit.get(0))
        minuteET.setText(timesplit.get(1))
        var edittime = ""
        if (timesplit.get(0).toInt() >= 12){
            edittime += " PM " + (timesplit.get(0).toInt() - 12).toString() + ":" + timesplit.get(1)
        } else {
            edittime += " AM " + timesplit.get(0)+ ":" + timesplit.get(1)
        }
        pulldateTV.setText(Utils.todayStr() + "" + edittime)


        if (intent.getStringExtra("timeline_id") != null){
            timeline_id = intent.getStringExtra("timeline_id")
            detail_timeline()
        }

        if (intent.getStringExtra("qnas_id") != null){
            qnas_id = intent.getStringExtra("qnas_id")
            created_at = intent.getStringExtra("created_at")


            val now = System.currentTimeMillis()
            val date = Date(now)
            val sdf = SimpleDateFormat("yy-MM-dd HH:mm:ss")
            val getTime = sdf.format(date)

            if(created_at == null || created_at.isEmpty()) {
                created_at = getTime
            }

            var d1 = sdf.parse(created_at);
            var d2 = sdf.parse(getTime);

            val diff = d2.time - d1.time
            val days = TimeUnit.MILLISECONDS.toDays(diff);
            val remainingHoursInMillis = diff - TimeUnit.DAYS.toMillis(days);
            val hours = TimeUnit.MILLISECONDS.toHours(remainingHoursInMillis);
            val remainingMinutesInMillis = remainingHoursInMillis - TimeUnit.HOURS.toMillis(hours);
            val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMinutesInMillis);
            timeET.setText(hours.toString())
            minuteET.setText(minutes.toString())
            timetakeTV.setText("소요시간")
            logoIV.setText("답변하기")
        }

        initGPS()

    }
    fun click(){
        blockIV.setOnClickListener {
            if (block_yn == "N"){

//                val builder = AlertDialog.Builder(context)
//                builder
//
//                        .setMessage("글을 숨기시겠습니까 ?")
//
//                        .setPositiveButton("예", DialogInterface.OnClickListener { dialog, id ->
//                            blockIV.setImageResource(R.mipmap.lock_icon)
//                            block_yn = "Y"
//                            dialog.cancel()
//
//                        })
//                        .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, id ->
//                            dialog.cancel()
//                        })
//
//                val alert = builder.create()
//                alert.show()
                block_yn = "Y"
                blockIV.setImageResource(R.mipmap.lock_icon)
            } else {
                blockIV.setImageResource(R.mipmap.shield)
                block_yn = "N"
            }
//            blockIV.setImageResource(R.mipmap.lock_icon)
        }

        titleBackLL.setOnClickListener {
            finish()
            Utils.hideKeyboard(context)
        }

        //힐링
        healingTV.setOnClickListener {
            menuSetImage()
            healingTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            healingTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 1
        }

        //핫플레이스
        hotplaceTV.setOnClickListener {
            menuSetImage()
            hotplaceTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            hotplaceTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 2
        }

        //문학 스타일
        literatureTV.setOnClickListener {
            menuSetImage()
            literatureTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            literatureTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 3
        }

        //역사 스타일
        historyTV.setOnClickListener {
            menuSetImage()
            historyTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            historyTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 4
        }

        //박물관 스타일
        museumTV.setOnClickListener {
            menuSetImage()
            museumTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            museumTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 5
        }

        artTV.setOnClickListener {
            menuSetImage()
            artTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            artTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 5
        }

        addcontentLL.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder

                    .setMessage(getString(R.string.builderwanttopost))

                    .setPositiveButton(getString(R.string.builderyes), DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                        if (timeline_id == "") {
                            addContent()
//                            testaddtimeline()
                        } else {
                            modify()
                        }
                    })
                    .setNegativeButton(getString(R.string.builderno), DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })

            val alert = builder.create()
            alert.show()

        }

        //이미지추가
        addpictureLL.setOnClickListener {
            permission()
        }


    }



    fun addContent(){
        val bytes:ArrayList<Int> = ArrayList<Int>()

        timelineData.clear()

        val location = locationET.text.toString()
        if (location == "" || location == null){
            Toast.makeText(context, "지역은 필수입력 입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val time = timeET.text.toString()
        val minute = minuteET.text.toString()
        if (time == "" || time == null || minute == "" || minute == null){
            Toast.makeText(context, "시간은 필수입력 입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val contents = contentET.text.toString()
        if (contents == "" || contents == null){
            Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val fulltime = time +":"+ minute
        val money = moneyET.text.toString()
        if (money.length == 0){
            Toast.makeText(context, "금액을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }



        val params = RequestParams()
        params.put("member_id",PrefUtils.getIntPreference(context,"member_id"))
        params.put("place_name",location)
        params.put("duration",fulltime)
        params.put("cost",money)
        params.put("contents",contents)
        params.put("place_id","1")
        params.put("country_id","1")
        params.put("style_id",menu_position)
        params.put("token",PrefUtils.getStringPreference(context,"token"))
        params.put("block_yn",block_yn)
        params.put("country",country)

        val content_byte = contents.toByteArray()
        val content_size = content_byte.size
        bytes.add(content_size)

        var seq = 0
        if (addPicturesLL != null){
            for (i in 0 until addPicturesLL!!.childCount) {
                val o = JSONObject()
                val v = addPicturesLL?.getChildAt(i)
                val imageIV = v?.findViewById<ImageView>(R.id.addedImgIV)
                val firstLL = v?.findViewById<LinearLayout>(R.id.firstLL)
                if (imageIV is ImageView) {
                    val bitmap = imageIV.drawable as BitmapDrawable
                    params.put("files[$seq]", ByteArrayInputStream(Utils.getByteArray(bitmap.bitmap)))
//                    o.put("files",ByteArrayInputStream(Utils.getByteArray(bitmap.bitmap)))
                    var image = Utils.getByteArray(bitmap.bitmap)
                    var image_size = image.size
                    bytes.add(image_size)
                    seq++
                }

                if (firstLL!!.visibility == View.VISIBLE){
//                    params.put("main_yn", "Y")
//                    o.put("position",i)
                    params.put("position",i+1)
                }
//                else {
//                    o.put("main_yn","N")
//                    params.put("main_yn", "N")
//                }
//                timelineData.add(o)
            }
        }



        params.put("timelineData", timelineData)
        var sum = 0
        var disk_sum:Double = 0.0

        for (i in 0 until bytes.size){
            sum += bytes.get(i)
        }

        if (PrefUtils.getIntPreference(context,"byte") != null) {
            var byte = PrefUtils.getIntPreference(context, "byte")
            var disk = PrefUtils.getDoublePreference(context, "disk")

            disk_sum = byte.toDouble() + sum
            Log.d("현재 사용량",byte.toString())
            Log.d("용량",disk_sum.toString())
            if (disk_sum > disk){
//                Toast.makeText(context, "데이터 초과입니다.", Toast.LENGTH_SHORT).show()
                Utils.alert(context,"무료 서비스 용량을 초과하였습니다.", object: AlertListener {
                    override fun before(): Boolean {
                        return true
                    }

                    override fun after() {
                        /*    var intent = Intent()
                            intent.action = "DATA_LIMIT"
                            sendBroadcast(intent)*/
                    }
                })
                return

            }
        }
        var disk_sumabs =  Math.abs(disk_sum)
        PrefUtils.setPreference(context, "disk", disk_sumabs)
        params.put("disk_data",sum)
        params.put("qnas_id",qnas_id)

        TimelineAction.addtimeline(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
                        var intent = Intent()
                        intent.putExtra("reset","reset")
                        setResult(RESULT_OK, intent);

                        Utils.hideKeyboard(context)

                        finish()
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

    fun modify(){

        val location = locationET.text.toString()
        if (location == "" || location == null){
            Toast.makeText(context, "지역은 필수입력 입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val time = timeET.text.toString()
        val minute = minuteET.text.toString()
        if (time == "" || time == null || minute == "" || minute == null){
            Toast.makeText(context, "시간은 필수입력 입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val contents = contentET.text.toString()
        if (contents == "" || contents == null){
            Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val fulltime = time +":"+ minute
        val money = moneyET.text.toString()
        if (money.length == 0){
            Toast.makeText(context, "금액을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val params = RequestParams()
        params.put("member_id",PrefUtils.getIntPreference(context,"member_id"))
        params.put("timeline_id",timeline_id)
        params.put("place_name",location)
        params.put("duration",fulltime)
        params.put("cost",money)
        params.put("contents",contents)
        params.put("place_id","1")
        params.put("country_id","1")
        params.put("style_id",menu_position)

        var seq = 0
        if (addPicturesLL != null){
            for (i in 0 until addPicturesLL!!.childCount) {
                val o = JSONObject()
                val v = addPicturesLL?.getChildAt(i)
                val imageIV = v?.findViewById<ImageView>(R.id.addedImgIV)
                val firstLL = v?.findViewById<LinearLayout>(R.id.firstLL)
                if (imageIV is ImageView) {
                    val bitmap = imageIV.drawable as BitmapDrawable
                    params.put("files[$seq]", ByteArrayInputStream(Utils.getByteArray(bitmap.bitmap)))
//                    o.put("files",ByteArrayInputStream(Utils.getByteArray(bitmap.bitmap)))
                    var image = Utils.getByteArray(bitmap.bitmap)
                    var image_size = image.size
//                    bytes.add(image_size)
                    seq++
                }

                if (firstLL!!.visibility == View.VISIBLE){
//                    params.put("main_yn", "Y")
//                    o.put("position",i)
                    params.put("position",i+1)
                }
//                else {
//                    o.put("main_yn","N")
//                    params.put("main_yn", "N")
//                }
//                timelineData.add(o)
            }
        }

        TimelineAction.update_timeline(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result =   Utils.getString(response,"result")
                    if ("ok" == result) {
                        var intent = Intent()
                        intent.putExtra("reset","reset")
                        intent.putExtra("timeline_id",timeline_id)
                        intent.action = "UPDATE_TIMELINE"
                        sendBroadcast(intent)
                        setResult(RESULT_OK, intent);

                        finish()

                        Utils.hideKeyboard(context)
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

                        locationET.setText(place_name)
                        timeET.setText(timesplit.get(0))
                        minuteET.setText(timesplit.get(1))
                        moneyET.setText(cost)
                        contentET.setText(contents)

                        val member = data.getJSONObject("member")
                        val founder_id = Utils.getString(member,"id")
                        val name = Utils.getString(member,"name")
                        val age = Utils.getString(member,"age")

                        val images = data.getJSONArray("images")
                        if (images.length() > 0){
                            for (i in 0 until images.length()){
                                val image_item = images.get(i) as JSONObject
                                var path = Config.url+  Utils.getString(image_item, "image_uri")
                                reset(path,i)
                            }
                        }

                        if (timesplit.get(0).toInt() >= 12){
                            pulldateTV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":"+timesplit.get(1))
                        } else {
                            pulldateTV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":"+timesplit.get(1))
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

    private fun permission() {

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {

                var intent = Intent(context, FindPictureGridActivity::class.java)
                startActivityForResult(intent, SELECT_PICTURE)

            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {

            }

        }

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                SELECT_PICTURE -> {

                    var item = data?.getStringArrayExtra("images")//photoPath
                    //var name = data?.getStringArrayExtra("displayname")

                    for (i in 0..(item!!.size - 1)) {
                        val str = item[i]

                        images_path!!.add(str)

//                        var add_file = Utils.getImage(context.contentResolver, str)
//
//                        var imageView = View.inflate(context, R.layout.item_addgoods, null)
//                        val imageIV: ImageView = imageView.findViewById(R.id.addedImgIV)
//                        val delIV: ImageView = imageView.findViewById(R.id.delIV)
//                        imageIV.setImageBitmap(add_file)
//                        addPicturesLL?.addView(imageView)
//
//                        delIV.setOnClickListener {
//                            if (addPicturesLL != null) {
//                                addPicturesLL!!.removeView(imageView)
//                            }
//                        }

                        reset(str,i)

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

    fun reset(str: String,i :Int) {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(str, options)
        options.inJustDecodeBounds = false
        options.inSampleSize = 1
        if (options.outWidth > 96) {
            val ws = options.outWidth / 96 + 1
            if (ws > options.inSampleSize) {
                options.inSampleSize = ws
            }
        }
        if (options.outHeight > 96) {
            val hs = options.outHeight / 96 + 1
            if (hs > options.inSampleSize) {
                options.inSampleSize = hs
            }
        }

        images_path.add(str)
        var add_file = Utils.getImage(context.contentResolver, str)
        val bitmap = BitmapFactory.decodeFile(str)
        var v = View.inflate(context, R.layout.item_addgoods, null)
        val imageIV = v.findViewById(R.id.addedImgIV) as ImageView
        val delIV = v.findViewById<View>(R.id.delIV) as ImageView
        val first = v.findViewById<View>(R.id.firstLL) as LinearLayout
        val fullImageLL = v.findViewById<View>(R.id.fullImageLL) as RelativeLayout
        if (timeline_id.length > 0) {
            ImageLoader.getInstance().displayImage(str, v.addedImgIV, Utils.UILoptionsUserProfile)
        }
        imageIV.setImageBitmap(add_file)
        delIV.tag = i
        fullImageLL.setTag(i)
        delIV.setOnClickListener {
            if (first.visibility == View.VISIBLE){
                if (addPicturesLL?.getChildAt(0) != null) {
                    val v0 = addPicturesLL?.getChildAt(0)
                    val firstLL = v0?.findViewById<View>(R.id.firstLL) as LinearLayout
                    firstLL.visibility = View.VISIBLE
                }
            }
            addPicturesLL!!.removeView(v)
        }
        if (imgSeq == 0) {
            addPicturesLL!!.addView(v)
        }

        for (i in 0 until addPicturesLL.childCount) {
            val v = addPicturesLL?.getChildAt(i)
            val imageIV = v?.findViewById<ImageView>(R.id.addedImgIV)
            val firstLL = v?.findViewById<View>(R.id.firstLL) as LinearLayout
            val childView = addPicturesLL.getChildAt(i)
            firstLL.visibility = View.GONE
        }
        val v0 = addPicturesLL?.getChildAt(0)
        val firstLL = v0?.findViewById<View>(R.id.firstLL) as LinearLayout
        firstLL.visibility = View.VISIBLE

    }

    fun clickMethodMain(v: View) {
        val tag = v.tag as Int
//        if (images_path.size != 0) {
//            main_index = tag + images_path.size
//        } else {
//            main_index = tag
//        }

//        var v = View.inflate(context, R.layout.item_addgoods, null)

        for (i in 0 until addPicturesLL.childCount) {
            val v = addPicturesLL?.getChildAt(i)
            val imageIV = v?.findViewById<ImageView>(R.id.addedImgIV)
            val firstLL = v?.findViewById<View>(R.id.firstLL) as LinearLayout
            val childView = addPicturesLL.getChildAt(i)
            firstLL.visibility = View.GONE
            println("-----gone")
        }
        val firstLL = v.findViewById<View>(R.id.firstLL) as LinearLayout
        firstLL.visibility = View.VISIBLE
    }


    override fun onBackPressed() {
        Utils.hideKeyboard(context)
        finish()
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

    }

    private fun stopLocation() {
        SmartLocation.with(context).location().stop()
    }

    private fun permissionCheck(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            !(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED)
        } else {
            false
        }
    }

    private fun loadPermissions(perm: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(context, perm) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm), requestCode)
        } else {
            if (Manifest.permission.ACCESS_FINE_LOCATION == perm) {
                loadPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_ACCESS_COARSE_LOCATION)
            } else if (Manifest.permission.ACCESS_COARSE_LOCATION == perm) {
                checkGPs()
            }
        }
    }

    override fun onLocationUpdated(location: Location?) {
        if (location != null) {
            if (myLocation) {
                latitude = location.getLatitude()
                longitude = location.getLongitude()
            }


            var systemLocale = getApplicationContext().getResources().getConfiguration().locale
            val strLanguage = systemLocale.language
            var geocoder: Geocoder = Geocoder(context, Locale.getDefault());

            var list:List<Address> = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1);
            if(list.size > 0){
                println("list ---- ${list}")
                println("list.admin ${list.get(0).adminArea}")

                country = list.get(0).countryName

                locationET.setText(list.get(0).adminArea)
            }

            if (progressDialog != null) {
                progressDialog!!.dismiss()
            }
        }

        stopLocation()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_FINE_LOCATION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_ACCESS_COARSE_LOCATION)
            }
            REQUEST_ACCESS_COARSE_LOCATION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkGPs()
            }
        }

    }





}
