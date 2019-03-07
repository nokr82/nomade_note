package com.devstories.nomadnote_android.activities

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.Utils
import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.TranslateOptions
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.*


open class OthertimeAdapter(context: Context, view: Int, data: ArrayList<JSONObject>, other_time_Fragment: Other_time_Fragment) : ArrayAdapter<JSONObject>(context,view, data){

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONObject> = data
    var menu_position = 1
    var other_time_Fragment = other_time_Fragment
    var myContext = context

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
        var place_name = Utils.getString(json,"place_name")
        var duration = Utils.getString(json,"duration")
        var cost = Utils.getString(json,"cost")
        var cost_str = Utils.getString(json,"cost_str")
        var contents = Utils.getString(json,"contents")
        var created = Utils.getString(json,"created_at")
        var timeline_id = Utils.getString(json,"id")
        var certification = Utils.getString(json,"certification")

        var createdsplit = created.split(" ")
        var timesplit = createdsplit.get(1).split(":")

        var style = Utils.getString(json,"style_id")

        var member = json.getJSONObject("member")
        var name = Utils.getString(member,"name")
        var age = Utils.getString(member,"age")
        var profile = Utils.getString(member,"profile")
        if (profile != "" && profile != null){
            var uri = Config.url + profile
            ImageLoader.getInstance().displayImage(uri, item.profileIV, Utils.UILoptionsUserProfile)
        } else {
            if (Utils.getString(member, "gender") == "F"){
                item.profileIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.famal)
            }else{
                item.profileIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.man)
            }
        }
        item.infoTV.setText(name+"/"+age+"세")

        setMenuImage(style.toInt())

        var image = json.getJSONArray("images")
        if (image.length() > 0){

//            var uri = ""
//
//            for (i in 0 until image.length()){
//                val image_item = image.get(i) as JSONObject
//                val main_yn = Utils.getString(image_item,"main_yn")
//                val image_uri = Utils.getString(image_item,"image_uri")
//                if (main_yn == "Y"){
//                    uri = Config.url + image_uri
//                }
//            }

            val image_item = image.get(0) as JSONObject
            val image_uri = Utils.getString(image_item, "image_uri")
            var uri = Config.url + "/" + image_uri

            ImageLoader.getInstance().displayImage(uri, item.backgroundIV, Utils.UILoptionsUserProfile)
//            val image_item = image.get(image.length()-1) as JSONObject
//            val image_uri = Utils.getString(image_item,"image_uri")
//            var uri = Config.url + image_uri
//            ImageLoader.getInstance().displayImage(uri, item.backgroundIV, Utils.UILoptionsUserProfile)
        } else {
            item.backgroundIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.time_bg)
        }

        item.placeTV.setText(place_name)
        item.durationTV.setText(duration)
//        item.costTV.setText(cost+"$ ")

        if (cost_str.count() > 0 && cost_str != "") {
            item.costTV.setText(cost_str)
        } else {
            var language = Locale.getDefault().language
            if(language == "en" || language == "ja") {
                item.costTV.setText(other_time_Fragment.getString(R.string.unit) + cost)
            } else {
                item.costTV.setText(cost + other_time_Fragment.getString(R.string.unit))
            }
        }



        item.contentTV.setText(contents)
        if (timesplit.get(0).toInt() >= 12){
            item.createdTV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":"+timesplit.get(1))
        } else {
            item.createdTV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":"+timesplit.get(1))
        }



        var scrap = Utils.getString(json,"scrap")

        if (scrap == "2"){
//            item.trustLL.visibility = View.VISIBLE
            item.trustIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.scrap_ck)
        } else {
//            item.trustLL.visibility = View.GONE
            item.trustIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.icon_scrap)
        }

        item.trustLL.visibility = View.GONE

//        if (certification == "2"){
//            item.textTV.setText("이 정보를 인증하였습니다 !")
//            item.iconIV.setImageResource(R.mipmap.visit_city)
//        }

        item.trustRL.setOnClickListener {

        }

        item.trustIV.tag = position
        item.trustIV.setOnClickListener {
            if (scrap == "1"){
                scrap = "2"
            } else {
                scrap = "1"
            }
            json.put("scrap",scrap)
            notifyDataSetChanged()
            other_time_Fragment.set_scrap(timeline_id)
        }

//        item.trustLL.setOnClickListener {
//            if (certification == "1") {
//                isSel = !isSel
//                json.put("certification", "2")
//                notifyDataSetChanged()
//                other_time_Fragment.add_certification(timeline_id)
//            }
//        }

        val translated = Utils.getString(json, "translated")
        item.translatedTV.text = translated

        item.translateIV.tag = position
        item.translateIV.setOnClickListener {
            var json = data.get(it.tag as Int)
            val task = TranslateAsyncTask(myContext, it, json, this, null)
            task.execute()
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
        var translatedTV:TextView
        var healingTV:me.grantland.widget.AutofitTextView
        var hotplaceTV:me.grantland.widget.AutofitTextView
        var literatureTV:me.grantland.widget.AutofitTextView
        var historyTV:me.grantland.widget.AutofitTextView
        var museumTV:me.grantland.widget.AutofitTextView
        var iconIV : ImageView
        var trustIV : ImageView
        var trustLL : LinearLayout
        var trustRL : RelativeLayout
        var backgroundIV : ImageView
        var textTV: TextView
        var artTV: me.grantland.widget.AutofitTextView
        var translateIV:ImageView

        init {
            profileIV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.profileIV) as ImageView
            infoTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.infoTV) as TextView
            placeTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.placeTV) as TextView
            durationTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.durationTV) as TextView
            costTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.costTV) as TextView
            createdTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.createdTV) as TextView
            contentTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.contentTV) as TextView
            translatedTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.translatedTV) as TextView
            healingTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.healingTV) as me.grantland.widget.AutofitTextView
            hotplaceTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.hotplaceTV) as me.grantland.widget.AutofitTextView
            literatureTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.literatureTV) as me.grantland.widget.AutofitTextView
            historyTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.historyTV) as  me.grantland.widget.AutofitTextView
            museumTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.museumTV) as me.grantland.widget.AutofitTextView
            iconIV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.iconIV) as ImageView
            trustIV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.trustIV) as ImageView
            trustLL = v.findViewById<View>(com.devstories.nomadnote_android.R.id.trustLL) as LinearLayout
            trustRL = v.findViewById<View>(com.devstories.nomadnote_android.R.id.trustRL) as RelativeLayout
            backgroundIV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.backgroundIV) as ImageView
            textTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.textTV) as TextView
            artTV = v.findViewById<View>(com.devstories.nomadnote_android.R.id.artTV) as me.grantland.widget.AutofitTextView
            translateIV = v.findViewById(R.id.translateIV) as ImageView

        }
    }

    fun menuSetImage(){
        item.healingTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius8_000000)
        item.healingTV.setTextColor(Color.parseColor("#878787"))
        item.hotplaceTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius8_000000)
        item.hotplaceTV.setTextColor(Color.parseColor("#878787"))
        item.literatureTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius8_000000)
        item.literatureTV.setTextColor(Color.parseColor("#878787"))
        item.historyTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius8_000000)
        item. historyTV.setTextColor(Color.parseColor("#878787"))
        item.museumTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius8_000000)
        item.museumTV.setTextColor(Color.parseColor("#878787"))
        item.artTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius8_000000)
        item.artTV.setTextColor(Color.parseColor("#878787"))
    }

    fun setMenuImage(style: Int){
        menuSetImage()
        when(style){
            1 ->{
                item.healingTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius7_000000)
                item.healingTV.setTextColor(Color.parseColor("#ffffff"))
            }

            2 ->{
                item.hotplaceTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius7_000000)
                item.hotplaceTV.setTextColor(Color.parseColor("#ffffff"))
            }

            3 ->{
                item.literatureTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius7_000000)
                item.literatureTV.setTextColor(Color.parseColor("#ffffff"))
            }

            4 ->{
                item.historyTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius7_000000)
                item.historyTV.setTextColor(Color.parseColor("#ffffff"))
            }

            5 ->{
                item.museumTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius7_000000)
                item.museumTV.setTextColor(Color.parseColor("#ffffff"))
            }
            6-> {
                item.artTV.setBackgroundResource(com.devstories.nomadnote_android.R.drawable.background_border_radius7_000000)
                item.artTV.setTextColor(Color.parseColor("#ffffff"))
            }
        }
    }

    companion object {
        class TranslateAsyncTask internal constructor(context: Context, view: View, json: JSONObject, othertimeAdapter: OthertimeAdapter?, scrapAdapter:ScrapAdapter?) : AsyncTask<Void, String, String?>() {

            private val contextReference: WeakReference<Context> = WeakReference(context)
            private val viewReference: WeakReference<View> = WeakReference(view)
            private val jsonReference: WeakReference<JSONObject> = WeakReference(json)
            private val othertimeAdapterReference: WeakReference<OthertimeAdapter?> = WeakReference(othertimeAdapter)
            private val scrapAdapterReference: WeakReference<ScrapAdapter?> = WeakReference(scrapAdapter)

            override fun onPreExecute() {
                jsonReference.get()!!.put("translated", contextReference.get()!!.getString(R.string.transtrating))

                if(othertimeAdapterReference.get() != null) {
                    othertimeAdapterReference.get()!!.notifyDataSetChanged()
                } else if(scrapAdapterReference.get() != null) {
                    scrapAdapterReference.get()!!.notifyDataSetChanged()
                }
            }

            override fun doInBackground(vararg params: Void?): String? {
                val translate = TranslateOptions.newBuilder().setApiKey("AIzaSyAvs-J-QHV-Ni6sQHAAYzaoSFDlMdq55Fs").build().service

                var contents = Utils.getString(jsonReference.get(),"contents")

                println(contents)


                var targetLanguage = Locale.getDefault().language

                val translation = translate.translate(
                        contents,
                        TranslateOption.targetLanguage(targetLanguage))

                return translation.translatedText
            }


            override fun onPostExecute(result: String?) {
                jsonReference.get()!!.put("translated", result)

                println(result)

                if(othertimeAdapterReference.get() != null) {
                    othertimeAdapterReference.get()!!.notifyDataSetChanged()
                } else if(scrapAdapterReference.get() != null) {
                    scrapAdapterReference.get()!!.notifyDataSetChanged()
                }

                // ((viewReference.get()!!.parent.parent.parent.parent.parent as LinearLayout).findViewById(R.id.translatedTV) as TextView).text = result
            }

        }
    }

}
