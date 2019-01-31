package com.devstories.nomadnote_android.activities

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.Config
import com.nostra13.universalimageloader.core.ImageLoader
import com.devstories.nomadnote_android.base.Utils


open class VisitNationAdapter(context: Context, view: Int, data: ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context,view, data){

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONObject> = data
    var menu_position = 1

    override fun getView(position: Int, convertView: View?, parent : ViewGroup?): View {

        lateinit var retView: View

        if (convertView == null) {
            retView = View.inflate(context, view, null)
            item = ViewHolder(retView)
            retView.tag = item
        } else {
            retView = convertView
            item = convertView.tag as ViewHolder
            if (item == null) {
                retView = View.inflate(context, view, null)
                item = ViewHolder(retView)
                retView.tag = item
            }
        }

        var json = data.get(position)
        val countryName = Utils.getString(json,"country")
//        val timeline = json.getJSONObject("timeline")
        val created_at = Utils.getString(json,"created_at")
        val count = Utils.getInt(json,"count")
        val image_uri = Utils.getString(json,"image_uri")
        var uri = Config.url + image_uri
        ImageLoader.getInstance().displayImage(uri, item.flagIV, Utils.UILoptionsUserProfile)

        item.countryTV.setText(countryName)
        var created_split = created_at.split(" ")
        item.createdTV.setText(created_split.get(0))
        item.countTV.setText(count.toString() + " 개")


        return retView

    }

    override fun getItem(position: Int): JSONObject {

        return data.get(position)
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getCount(): Int {

        return data.count()
    }

    fun removeItem(position: Int){
        data.removeAt(position)
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) {

        var flagIV : ImageView
        var countryTV : TextView
        var createdTV: TextView
        var countTV: TextView

        init {
            flagIV = v.findViewById<View>(R.id.flagIV) as ImageView
            countryTV = v.findViewById<View>(R.id.countryTV) as TextView
            createdTV = v.findViewById<View>(R.id.createdTV) as TextView
            countTV = v.findViewById<View>(R.id.countTV) as TextView
        }
    }



}
