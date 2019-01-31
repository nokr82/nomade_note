package com.devstories.nomadnote_android.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.view.ViewPager
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.QnasAction
import com.devstories.nomadnote_android.actions.TimelineAction
import com.devstories.nomadnote_android.adapter.FullScreenImageAdapter
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.facebook.FacebookSdk
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.FeedTemplate
import com.kakao.message.template.LinkObject
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_timeline.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL

class Solo_detail_Activity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var timeline_id = ""
    var qnas_id = ""
    var menu_position = 1

    var MODIFY = 100
    var block = ""

    var share_image_uri = ""
    var share_contents = ""
    var share_image_path = ""

    var adPosition = 0
    private lateinit var fullScreenAdapter: FullScreenImageAdapter
    var imagePaths: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            // w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_timeline)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
//        progressDialog = ProgressDialog(context)


        FacebookSdk.sdkInitialize(applicationContext)

        click()

        fullScreenAdapter = FullScreenImageAdapter(this, imagePaths)
        pagerVP.adapter = fullScreenAdapter
        pagerVP.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                adPosition = position
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {

                for (i in imagePaths.indices) {
                    if (i == adPosition) {
//                        addDot(circleLL, true)
                    } else {
//                        addDot(circleLL, false)
                    }
                }
            }
        })


        var intent = getIntent()
        if (intent.getStringExtra("timeline_id") != null) {
            timeline_id = intent.getStringExtra("timeline_id")
            detail_timeline()
        }


        if (intent.getStringExtra("qnas_id") != null) {
            timeline_id = intent.getStringExtra("qnas_id")
            detail_timeline()
//            detail_qnas()
        }

    }

    fun click() {
        titleBackLL.setOnClickListener {
            finish()
        }

        modifyIV.setOnClickListener {
            val intent = Intent(context, WriteActivity::class.java)
            intent.putExtra("timeline_id", timeline_id)
            startActivityForResult(intent, MODIFY)
        }

        deleteIV.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder

                    .setMessage(getString(R.string.builderdelete))

                    .setPositiveButton(getString(R.string.builderyes), DialogInterface.OnClickListener { dialog, id ->
                        delete_timeline()
                        dialog.cancel()

                    })
                    .setNegativeButton(getString(R.string.builderno), DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })

            val alert = builder.create()
            alert.show()
        }

        lockIV.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            if (block == "N") {
                builder.setMessage("게시물을 비공개 하시겠습니까?")
            } else {
                builder.setMessage("게시물을 공개 하시겠습니까?")
            }
            builder
                    .setPositiveButton(getString(R.string.builderyes), DialogInterface.OnClickListener { dialog, id ->
                        if (block == "N") {
                            change_block("Y")
                            lockIV.setImageResource(R.mipmap.lock)
                        } else {
                            change_block("N")
                            lockIV.setImageResource(R.mipmap.shiels_r)
                        }
                        dialog.cancel()

                    })
                    .setNegativeButton(getString(R.string.builderno), DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })


            val alert = builder.create()
            alert.show()

        }

        instaIV.setOnClickListener {
            shareInstagram()
        }
        facebookIV.setOnClickListener {
            shareFacebook()
        }
        naverIV.setOnClickListener {
            shareNaverBlog()
        }
        kakaoIV.setOnClickListener {
            shareKakaoStory()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

    fun detail_timeline() {
        val params = RequestParams()
//        params.put("member_id", PrefUtils.getIntPreference(context,"member_id"))
        params.put("timeline_id", timeline_id)

        TimelineAction.detail_timeline(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {
                        val data = response!!.getJSONObject("timeline")
                        var place_name = Utils.getString(data, "place_name")
                        var duration = Utils.getString(data, "duration")
                        var cost = Utils.getString(data, "cost")
                        var contents = Utils.getString(data, "contents")
                        var created = Utils.getString(data, "created_at")
                        var style = Utils.getString(data, "style_id")
                        block = Utils.getString(data, "block_yn")

                        if (block == "N") {
                            lockIV.setImageResource(R.mipmap.shiels_r)
                        } else {
                            lockIV.setImageResource(R.mipmap.lock)
                        }

                        var createdsplit = created.split(" ")
                        var timesplit = createdsplit.get(1).split(":")

                        placeTV.setText(place_name)
                        durationTV.setText(duration)
                        costTV.setText(cost + "$")
                        contentTV.setText(contents)

                        share_contents = contents

                        val member = data.getJSONObject("member")
                        val founder_id = Utils.getString(member, "id")
                        val name = Utils.getString(member, "name")
                        val age = Utils.getString(member, "age")
                        val profile = Utils.getString(member, "profile")
                        if (profile != null && profile != "") {
                            var uri = Config.url + profile
                            ImageLoader.getInstance().displayImage(uri, profileIV, Utils.UILoptionsUserProfile)
                        }

                        val image = data.getJSONArray("images")
                        if (image.length() > 0) {
                            if (imagePaths != null) {
                                imagePaths.clear()
                            }

                            pagerVP.visibility = View.VISIBLE
                            logoIV.visibility = View.GONE

//                            val image_item = image.get(image.length()-1) as JSONObject
//                            val image_uri = Utils.getString(image_item,"image_uri")
//                            var uri = Config.url + image_uri
//                            println("-------uri ---------")
//                            ImageLoader.getInstance().displayImage(uri, logoIV, Utils.UILoptionsUserProfile)

                            for (i in 0 until image.length()) {
                                val image_item = image.get(i) as JSONObject
                                val image_uri = Utils.getString(image_item, "image_uri")
                                val main_yn = Utils.getString(image_item, "main_yn")
                                var uri = Config.url + image_uri
                                if (main_yn == "Y") {
                                    var uri = Config.url + image_uri
//                                    ImageLoader.getInstance().displayImage(uri, logoIV, Utils.UILoptionsUserProfile)
//                                    imagePaths.add(uri)
                                }
                                imagePaths.add(uri)
                                share_image_uri = image_uri

                            }
                            fullScreenAdapter.notifyDataSetChanged()
                        } else {

                        }

                        if (founder_id.toInt() != PrefUtils.getIntPreference(context, "member_id")) {
                            soloLL.visibility = View.GONE
                            modifyIV.visibility = View.GONE
                            lockIV.visibility = View.GONE
                            deleteIV.visibility = View.GONE
                        }

                        infoTV.setText(name + "/" + age + "세")


                        if (timesplit.get(0).toInt() >= 12) {
                            createdTV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":" + timesplit.get(1))
                        } else {
                            createdTV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":" + timesplit.get(1))
                        }

                        when (style) {
                            "1" -> {
                                menu_position = 1
                                setMenuImage(menu_position)
                            }

                            "2" -> {
                                menu_position = 2
                                setMenuImage(menu_position)
                            }

                            "3" -> {
                                menu_position = 3
                                setMenuImage(menu_position)
                            }

                            "4" -> {
                                menu_position = 4
                                setMenuImage(menu_position)
                            }

                            "5" -> {
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

    fun menuSetImage() {
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
        artmuseumTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        artmuseumTV.setTextColor(Color.parseColor("#878787"))
    }

    fun setMenuImage(menu_position: Int) {
        menuSetImage()
        when (menu_position) {
            1 -> {
                healingTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                healingTV.setTextColor(Color.parseColor("#ffffff"))
            }

            2 -> {
                hotplaceTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                hotplaceTV.setTextColor(Color.parseColor("#ffffff"))
            }

            3 -> {
                literatureTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                literatureTV.setTextColor(Color.parseColor("#ffffff"))
            }

            4 -> {
                historyTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                historyTV.setTextColor(Color.parseColor("#ffffff"))
            }

            5 -> {
                museumTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                museumTV.setTextColor(Color.parseColor("#ffffff"))
            }

            6 -> {
                artmuseumTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                artmuseumTV.setTextColor(Color.parseColor("#ffffff"))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                MODIFY -> {
                    if (data!!.getStringExtra("reset") != null) {
                        timeline_id = data!!.getStringExtra("timeline_id")
                        detail_timeline()
                    }
                }
            }
        }


    }

    fun delete_timeline() {
        val params = RequestParams()
        params.put("timeline_id", timeline_id)

        TimelineAction.delete_timeline(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {
                        var intent = Intent()
                        intent.putExtra("reset", "reset")
                        setResult(RESULT_OK, intent);
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

    fun change_block(block_yn: String) {
        val params = RequestParams()
        params.put("timeline_id", timeline_id)
        params.put("block_yn", block_yn)

        TimelineAction.change_block(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {
                        var intent = Intent()
                        intent.putExtra("reset", "reset")
                        setResult(RESULT_OK, intent);
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

    fun detail_qnas() {
        val params = RequestParams()
//        params.put("member_id", PrefUtils.getIntPreference(context,"member_id"))
        params.put("qnas_id", qnas_id)

        QnasAction.detail_qnas(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = Utils.getString(response, "result")
                    if ("ok" == result) {
                        val data = response!!.getJSONObject("qnas")
                        val member = data.getJSONObject("member")
                        val name = Utils.getString(member, "name")
                        val style_id = Utils.getInt(member, "style_id")
                        val answer = Utils.getString(data, "answer")
                        contentTV.setText(answer)

                        setMenuImage(style_id)

                        val founder_id = Utils.getString(member, "id")
                        val age = Utils.getString(member, "age")
                        val profile = Utils.getString(member, "profile")
                        if (profile != null && profile != "") {
                            var uri = Config.url + profile
                            ImageLoader.getInstance().displayImage(uri, profileIV, Utils.UILoptionsUserProfile)
                        }

                        soloLL.visibility = View.GONE
                        modifyIV.visibility = View.GONE
                        lockIV.visibility = View.GONE
                        deleteIV.visibility = View.GONE

                        val created = Utils.getString(data, "answer_dt")
                        locationLL.visibility = View.GONE

                        infoTV.setText(name + "/" + age + "세")

                        var createdsplit = created.split(" ")
                        var timesplit = createdsplit.get(1).split(":")

                        if (timesplit.get(0).toInt() >= 12) {
                            createdTV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":" + timesplit.get(1))
                        } else {
                            createdTV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":" + timesplit.get(1))
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


    private fun shareInstagram() {

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        try {

            var image_uri = Config.url + "/storage/images/2019/02/01/Q2kXN56SHwcO8ZTxB7SM8CRlItFNwLHEMmSGxTYd.jpeg"

            var uri:Uri

            if (share_image_uri != "") {

                uri = Uri.parse("file://" + share_image_path)

            } else {
                uri = Uri.parse("android.resource://" + context.packageName + "/mipmap/ic_launcher");
            }

            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("android.resource://" + context.packageName + "/mipmap/ic_launcher"));
            shareIntent.putExtra(Intent.EXTRA_TEXT, "텍스트는 지원하지 않음!")
            shareIntent.setPackage("com.instagram.android")
            startActivity(shareIntent)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "인스타그램이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // 이미지 다운로드(포트폴리오)
    private inner class DownloadFilesTask() : AsyncTask<String, Int, Bitmap>() {

        lateinit var bitmap: Bitmap

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg url: String): Bitmap? {
            var bitmap: Bitmap? = null

            try {
                bitmap = downloadUrl(url[0])
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            share_image_path = Utils.saveBitmap(context, bitmap)

        }

        // Image downloading method.
        @Throws(IOException::class)
        private fun downloadUrl(imageUrl: String): Bitmap {
            val `is` = URL(imageUrl).content as InputStream

            return BitmapFactory.decodeStream(`is`)
        }

    }

    private fun getImageUri(context: Context, inImage: Bitmap) :Uri {
        var bytes: ByteArrayOutputStream = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        var path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private fun shareFacebook() {
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {

            val url = Config.url + "/share/open?id=" + timeline_id + "&image_uri=" + share_image_uri + "&contents=" + share_contents
            val content = ShareLinkContent.Builder().setContentUrl(Uri.parse(url)).build()

            val shareDialog = ShareDialog(this)
            shareDialog.show(content)   //AUTOMATIC, FEED, NATIVE, WEB 등이 있으며 이는 다이얼로그 형식을 말합니다.
        }

    }

    private fun shareNaverBlog() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        try {

            val title = "노마드노트"
            val post = share_contents
            val appId = context.packageName
            val appName = "노마드노트"
            val url = String.format("naverblog://write?title=%s&content=%s", title, post);

            val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            shareIntent.setPackage("com.nhn.android.blog")
            startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "네이버 블로그앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareKakaoStory() {

        try {

            val url = Config.url + "/share/open_app"

            val imgBuilder = ContentObject.newBuilder("노마드노트",
                    Config.url + share_image_uri,
                    LinkObject.newBuilder().setWebUrl(url).setMobileWebUrl(url).build())
                    .setDescrption(share_contents)
                    .build()

            val builder = FeedTemplate.newBuilder(imgBuilder)
            builder.addButton(ButtonObject("노마드노트 바로가기", LinkObject.newBuilder()
                    .setAndroidExecutionParams("timeline_id=$timeline_id")
                    .setIosExecutionParams("timeline_id=$timeline_id")
                    .setWebUrl(url)
                    .setMobileWebUrl(url)
                    .build()))

            val params = builder.build()

            KakaoLinkService.getInstance().sendDefault(this, params, object : ResponseCallback<KakaoLinkResponse>() {
                override fun onFailure(errorResult: ErrorResult) {

                }

                override fun onSuccess(result: KakaoLinkResponse) {

                }
            })

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "카카오톡이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        finish()
        Utils.hideKeyboard(context)
    }
}
