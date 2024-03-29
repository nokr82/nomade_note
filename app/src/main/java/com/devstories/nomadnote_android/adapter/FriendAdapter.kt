package com.devstories.nomadnote_android.activities

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.R.id.*
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.Utils


open class FriendAdapter(context: Context, view:Int, data:ArrayList<JSONObject>,Friend_add_Fragment:Friend_add_Fragment) : ArrayAdapter<JSONObject>(context,view, data){

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONObject> = data
    var menu_position = 1
    var Friend_add_Fragment:Friend_add_Fragment = Friend_add_Fragment

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
        var member = json.getJSONObject("Member")
        var name = Utils.getString(member,"name")
        var age = Utils.getString(member,"age")
        var gender = Utils.getString(member,"gender")
        var profile = Utils.getString(member,"profile")
        var pem_id = Utils.getInt(member,"id")
        var updated_at = Utils.getString(json,"updated_at")

        if (profile.length < 1) {
            if (gender=="F"){
                item.profileIV.setImageResource(R.mipmap.famal)
            }else{
                item.profileIV.setImageResource(R.mipmap.man)
            }
        } else {
            ImageLoader.getInstance().displayImage(Config.url + profile, item.profileIV, Utils.UILoptionsUserProfile)
        }


        item.nameTV.setText(name)

        item.deleteTV.setOnClickListener {
            Friend_add_Fragment.friend_del(pem_id)
        }



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

        var profileIV : ImageView
        var shareTV: TextView
        var nameTV : TextView
        var deleteTV : TextView

        init {
            profileIV = v.findViewById<View>(R.id.profileIV) as ImageView
            shareTV = v.findViewById<View>(R.id.shareTV) as TextView
            nameTV = v.findViewById<View>(R.id.nameTV) as TextView
            deleteTV = v.findViewById<View>(R.id.deleteTV) as TextView

        }
    }



}
