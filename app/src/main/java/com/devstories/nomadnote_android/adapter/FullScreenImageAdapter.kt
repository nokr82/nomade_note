package com.devstories.nomadnote_android.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.VideoView
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.Utils
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject




class FullScreenImageAdapter(activity: Activity, imagePaths: ArrayList<JSONObject>, context: Context) : PagerAdapter() {

    private val _activity: Activity = activity
    private val _imagePaths: ArrayList<JSONObject> = imagePaths
    private lateinit var inflater: LayoutInflater
    private var myContext = context

    override fun getCount(): Int {
        return this._imagePaths.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        inflater = _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewLayout = inflater.inflate(com.devstories.nomadnote_android.R.layout.layout_fullscreen_image, container, false)

        val imgDisplay = viewLayout.findViewById(com.devstories.nomadnote_android.R.id.imgDisplay) as ImageView
        imgDisplay.scaleType = ImageView.ScaleType.CENTER_CROP

        val videoRL = viewLayout.findViewById(com.devstories.nomadnote_android.R.id.videoRL) as RelativeLayout
        val videoDisplay = viewLayout.findViewById(com.devstories.nomadnote_android.R.id.videoDisplay) as VideoView
        val playIV = viewLayout.findViewById(com.devstories.nomadnote_android.R.id.playIV) as ImageView

        val image_item = _imagePaths.get(position)
        val image_uri = Utils.getString(image_item, "image_uri")
        val video_path = Utils.getString(image_item, "video_path")

        var uri = Config.url + image_uri
        ImageLoader.getInstance().displayImage(uri, imgDisplay, Utils.UILoptionsProfile)

        print("video_path : $video_path")

        if(video_path.isEmpty()) {
            var uri = Config.url + image_uri
            ImageLoader.getInstance().displayImage(uri, imgDisplay, Utils.UILoptionsProfile)

            imgDisplay.visibility = View.VISIBLE
            videoRL.visibility = View.GONE

        } else {

            imgDisplay.visibility = View.GONE
            videoRL.visibility = View.VISIBLE

            var uri = Uri.parse(Config.url + video_path)
            videoDisplay.setVideoURI(uri)
            videoDisplay.seekTo(1)

            playIV.setOnClickListener {

//                val uri = Uri.parse(Config.url + video_path)
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.setDataAndType(uri, "video/*")
//                myContext.startActivity(intent)

                playIV.visibility = View.GONE

                videoDisplay.start()

            }


        }


        (container as ViewPager).addView(viewLayout)

        return viewLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as RelativeLayout)

    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}

