package com.devstories.nomadnote_android.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.Utils
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import com.devstories.nomadnote_android.R

class AdvertiseAdapter(activity: Activity, data: ArrayList<JSONObject>) : PagerAdapter() {

    private val _activity: Activity = activity
    private val data: ArrayList<JSONObject> = data
    private lateinit var inflater: LayoutInflater

    override fun getCount(): Int {
        return this.data.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        inflater = _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewLayout = inflater.inflate(R.layout.layout_adver_image, container, false)

        val imgDisplay = viewLayout.findViewById(R.id.imgDisplay) as ImageView

        val image_item = data.get(position)
        val image_uri = Utils.getString(image_item, "image_uri")

        var uri = Config.url + image_uri
        ImageLoader.getInstance().displayImage(uri, imgDisplay, Utils.UILoptionsProfile)

        imgDisplay.setOnClickListener {

            var link = Utils.getString(image_item, "link")

            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            _activity.startActivity(intent)

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

