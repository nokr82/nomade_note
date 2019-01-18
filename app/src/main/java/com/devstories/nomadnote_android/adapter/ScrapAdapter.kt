package com.devstories.nomadnote_android.activities

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.Utils


open class ScrapAdapter(context: Context, view: Int, data: ArrayList<JSONObject>, scrap_Fragment: Scrap_Fragment) : ArrayAdapter<JSONObject>(context,view, data){

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONObject> = data
    var menu_position = 1
    var scrap_Fragment = scrap_Fragment

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
        var timeline = json.getJSONObject("timeline")
        var place_name = Utils.getString(timeline,"place_name")
        var duration = Utils.getString(timeline,"duration")
        var cost = Utils.getString(timeline,"cost")
        var contents = Utils.getString(timeline,"contents")
        var created = Utils.getString(timeline,"created_at")
        var timeline_id = Utils.getString(timeline,"id")

        var createdsplit = created.split(" ")
        var timesplit = createdsplit.get(1).split(":")

        var style = Utils.getString(timeline,"style_id")

        var member = json.getJSONObject("Member")
        var name = Utils.getString(member,"name")
        var age = Utils.getString(member,"age")
        item.infoTV.setText(name+"/"+age+"세")

        setMenuImage(style.toInt())

        item.placeTV.setText(place_name)
        item.durationTV.setText(duration)
        item.costTV.setText(cost+"$ ")
        item.contentTV.setText(contents)
        if (timesplit.get(0).toInt() >= 12){
            item.createdTV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":"+timesplit.get(1))
        } else {
            item.createdTV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":"+timesplit.get(1))
        }

        var isSel = json.getBoolean("isSelectedOp")

        if (isSel){
            item.trustLL.visibility = View.VISIBLE
            item.trustIV.setImageResource(R.mipmap.op_file)
        } else {
            item.trustLL.visibility = View.GONE
            item.trustIV.setImageResource(R.mipmap.icon_scrap)
        }


        item.trustRL.setOnClickListener {
            isSel = !isSel
            json.put("isSelectedOp",isSel)
            scrap_Fragment.set_scrap(timeline_id)
            removeItem(position)
            notifyDataSetChanged()
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
        var infoTV: TextView
        var placeTV : TextView
        var durationTV : TextView
        var costTV : TextView
        var createdTV:TextView
        var contentTV:TextView
        var healingTV:TextView
        var hotplaceTV:TextView
        var literatureTV:TextView
        var historyTV:TextView
        var museumTV:me.grantland.widget.AutofitTextView
        var iconIV : ImageView
        var trustIV : ImageView
        var trustLL : LinearLayout
        var trustRL : RelativeLayout

        init {
            profileIV = v.findViewById<View>(R.id.profileIV) as ImageView
            infoTV = v.findViewById<View>(R.id.infoTV) as TextView
            placeTV = v.findViewById<View>(R.id.placeTV) as TextView
            durationTV = v.findViewById<View>(R.id.durationTV) as TextView
            costTV = v.findViewById<View>(R.id.costTV) as TextView
            createdTV = v.findViewById<View>(R.id.createdTV) as TextView
            contentTV = v.findViewById<View>(R.id.contentTV) as TextView
            healingTV = v.findViewById<View>(R.id.healingTV) as TextView
            hotplaceTV = v.findViewById<View>(R.id.hotplaceTV) as TextView
            literatureTV = v.findViewById<View>(R.id.literatureTV) as TextView
            historyTV = v.findViewById<View>(R.id.historyTV) as TextView
            museumTV = v.findViewById<View>(R.id.museumTV) as me.grantland.widget.AutofitTextView
            iconIV = v.findViewById<View>(R.id.iconIV) as ImageView
            trustIV = v.findViewById<View>(R.id.trustIV) as ImageView
            trustLL = v.findViewById<View>(R.id.trustLL) as LinearLayout
            trustRL = v.findViewById<View>(R.id.trustRL) as RelativeLayout

        }
    }

    fun menuSetImage(){
        item.healingTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        item.healingTV.setTextColor(Color.parseColor("#878787"))
        item.hotplaceTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        item.hotplaceTV.setTextColor(Color.parseColor("#878787"))
        item.literatureTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        item.literatureTV.setTextColor(Color.parseColor("#878787"))
        item.historyTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        item. historyTV.setTextColor(Color.parseColor("#878787"))
        item.museumTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        item.museumTV.setTextColor(Color.parseColor("#878787"))
    }

    fun setMenuImage(style: Int){
        menuSetImage()
        when(menu_position){
            1 ->{
                item.healingTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                item.healingTV.setTextColor(Color.parseColor("#ffffff"))
            }

            2 ->{
                item.hotplaceTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                item.hotplaceTV.setTextColor(Color.parseColor("#ffffff"))
            }

            3 ->{
                item.literatureTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                item.literatureTV.setTextColor(Color.parseColor("#ffffff"))
            }

            4 ->{
                item.historyTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                item.historyTV.setTextColor(Color.parseColor("#ffffff"))
            }

            5 ->{
                item.museumTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                item.museumTV.setTextColor(Color.parseColor("#ffffff"))
            }
        }
    }


}
