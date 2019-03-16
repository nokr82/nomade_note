package com.devstories.nomadnote_android.activities

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.Utils
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import java.util.*


open class VisitNationAdapter(context: Context, view: Int, data: ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context,view, data){

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONObject> = data
    var menu_position = 1
    var myContext: Context = context

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
        var countryName = Utils.getString(json,"country")

        var language = Locale.getDefault().language
        if(language == "en" || language == "ja") {
            countryName = Utils.getString(json, language)
        } else if(language == "zh") {
            language = Locale.getDefault().isO3Country
            if(language == "CHN") {
                countryName = Utils.getString(json, "zh_rCN")
            } else {
                countryName = Utils.getString(json, "zh_rTW")
            }
        }


//        val timeline = json.getJSONObject("timeline")
        val created_at = Utils.getString(json,"created_at")
        val count = Utils.getInt(json,"count")
        val image_uri = Utils.getString(json,"image_uri")

        var uri = Config.url + image_uri
        ImageLoader.getInstance().displayImage(uri, item.flagIV, Utils.UILoptionsUserProfile)

        item.countryTV.setText(countryName)
        var created_split = created_at.split(" ")
        item.createdTV.setText(created_split.get(0))
        item.countTV.setText(count.toString() + " " + myContext.getString(com.devstories.nomadnote_android.R.string.post_count))


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
            flagIV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.flagIV) as ImageView
            countryTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.countryTV) as TextView
            createdTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.createdTV) as TextView
            countTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.countTV) as TextView
        }
    }



}
