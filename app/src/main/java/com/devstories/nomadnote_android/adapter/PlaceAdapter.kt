package donggolf.android.adapters

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.activities.CountryTimelineActivity
import com.devstories.nomadnote_android.activities.MapSearchActivity
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.Utils
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.*


open class PlaceAdapter(context: Context, view:Int, data:ArrayList<JSONObject>, mapSearchActivity: MapSearchActivity?, countryTimelineActivity: CountryTimelineActivity?) : ArrayAdapter<JSONObject>(context,view, data){

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONObject> = data
    var myContext: Context = context
    var mapSearchActivity = mapSearchActivity
    var countryTimelineActivity = countryTimelineActivity

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
        var image = json.getJSONArray("images")
        var member = json.getJSONObject("member")
        var style_id = json.getInt("style_id")
        setMenuImage(style_id)

        var name =  Utils.getString(member,"name")
        var age =  Utils.getString(member,"age")
        var gender =  Utils.getString(member,"gender")
        var profile =  Utils.getString(member,"profile")

        if (image.length() > 0){
            val image_item = image.get(image.length()-1) as JSONObject
            val image_uri = Utils.getString(image_item,"image_uri")
            var uri = Config.url+"/"+ image_uri
            ImageLoader.getInstance().displayImage(uri, item.backgroundIV, Utils.UILoptionsUserProfile)
        } else {
            item.backgroundIV.setImageResource(R.mipmap.time_bg)
        }

        var createdsplit = created.split(" ")
        var timesplit = createdsplit.get(1).split(":")

        if (profile.length < 1) {
            if (gender =="M"){
                item.profileIV.setImageResource(R.mipmap.man)
            }else if (gender == "F"){
                item.profileIV.setImageResource(R.mipmap.famal)
            }
        } else {

            var uri = Config.url + profile

            ImageLoader.getInstance().displayImage(uri, item.profileIV, Utils.UILoptionsUserProfile)
        }

        item.trustLL.visibility = View.GONE
        item.infoTV.setText(name+"/"+age+"세")
        item.placeTV.setText(place_name)
        item.durationTV.setText(duration)
//        item.costTV.setText(cost+"$ ")

        if (cost_str.count() > 0 && cost_str != "") {
            item.costTV.setText(cost_str)
        } else {
            var language = Locale.getDefault().language
            if(language == "en" || language == "ja") {
                item.costTV.setText(mapSearchActivity!!.getString(R.string.unit) + cost)
            } else {
                item.costTV.setText(cost + context.getString(R.string.unit))
            }
        }

        item.contentTV.setText(contents)
        if (timesplit.get(0).toInt() >= 12){
            item.createdTV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":"+timesplit.get(1))
        } else {
            item.createdTV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":"+timesplit.get(1))
        }

        var isSel = json.getBoolean("isSelectedOp")

        if (isSel){
            item.trustIV.setImageResource(R.mipmap.scrap_ck)
        } else {
            item.trustIV.setImageResource(R.mipmap.icon_scrap)
        }

        item.trustIV.tag = position
        item.trustIV.setOnClickListener {
            var json = data.get(it.tag as Int)
            var isSel = json.getBoolean("isSelectedOp")

            println("isSel : $isSel")

            isSel = !isSel
            json.put("isSelectedOp",isSel)
            var timeline_id = Utils.getString(json,"id")

            if(mapSearchActivity != null) {
                mapSearchActivity!!.set_scrap(timeline_id)
            } else if(countryTimelineActivity != null) {
                countryTimelineActivity!!.set_scrap(timeline_id)
            }

            notifyDataSetChanged()
        }

        val translated = Utils.getString(json, "translated")
        item.translatedTV.text = translated

        item.translateIV.tag = position
        item.translateIV.setOnClickListener {
            var json = data.get(it.tag as Int)
            val task = TranslateAsyncTask(myContext, it, json, this)
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

        var backgroundIV : ImageView
        var contentTV : TextView
        var placeTV: TextView
        var infoTV: TextView
        var durationTV: TextView
        var costTV: TextView
        var createdTV:TextView
        var profileIV : ImageView
        var trustLL: LinearLayout
        var trustIV: ImageView
        var healingTV:me.grantland.widget.AutofitTextView
        var hotplaceTV:me.grantland.widget.AutofitTextView
        var literatureTV:me.grantland.widget.AutofitTextView
        var historyTV:me.grantland.widget.AutofitTextView
        var museumTV:me.grantland.widget.AutofitTextView
        var artTV: me.grantland.widget.AutofitTextView
        var translatedTV:TextView
        var translateIV:ImageView

        init {
            profileIV= v.findViewById<View>(R.id.profileIV) as ImageView
            infoTV = v.findViewById<View>(R.id.infoTV) as TextView
            backgroundIV = v.findViewById<View>(R.id.backgroundIV) as ImageView
            contentTV = v.findViewById<View>(R.id.contentTV) as TextView
            placeTV = v.findViewById<View>(R.id.placeTV) as TextView
            durationTV = v.findViewById<View>(R.id.durationTV) as TextView
            costTV = v.findViewById<View>(R.id.costTV) as TextView
            createdTV = v.findViewById<View>(R.id.createdTV) as TextView
            trustLL = v.findViewById<View>(R.id.trustLL) as LinearLayout
            trustIV = v.findViewById<View>(R.id.trustIV) as ImageView
            healingTV = v.findViewById<View>(R.id.healingTV) as me.grantland.widget.AutofitTextView
            hotplaceTV = v.findViewById<View>(R.id.hotplaceTV) as me.grantland.widget.AutofitTextView
            literatureTV = v.findViewById<View>(R.id.literatureTV) as me.grantland.widget.AutofitTextView
            historyTV = v.findViewById<View>(R.id.historyTV) as  me.grantland.widget.AutofitTextView
            museumTV = v.findViewById<View>(R.id.museumTV) as me.grantland.widget.AutofitTextView
            artTV = v.findViewById<View>(R.id.artTV) as me.grantland.widget.AutofitTextView
            translatedTV = v.findViewById<View>(R.id.translatedTV) as TextView
            translateIV = v.findViewById(R.id.translateIV) as ImageView

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
        item.artTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        item.artTV.setTextColor(Color.parseColor("#878787"))
    }

    fun setMenuImage(style: Int){
        menuSetImage()
        when(style){
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
            6-> {
                item.artTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
                item.artTV.setTextColor(Color.parseColor("#ffffff"))
            }
        }
    }

    companion object {
        class TranslateAsyncTask internal constructor(context: Context, view: View, json: JSONObject, placeAdapter:PlaceAdapter?) : AsyncTask<Void, String, String?>() {

            private val contextReference: WeakReference<Context> = WeakReference(context)
            private val viewReference: WeakReference<View> = WeakReference(view)
            private val jsonReference: WeakReference<JSONObject> = WeakReference(json)
            private val placeAdapterReference: WeakReference<PlaceAdapter?> = WeakReference(placeAdapter)

            override fun onPreExecute() {
                jsonReference.get()!!.put("translated", contextReference.get()!!.getString(R.string.transtrating))

                placeAdapterReference.get()!!.notifyDataSetChanged()
            }

            override fun doInBackground(vararg params: Void?): String? {
                val translate = TranslateOptions.newBuilder().setApiKey("AIzaSyAvs-J-QHV-Ni6sQHAAYzaoSFDlMdq55Fs").build().service

                var contents = Utils.getString(jsonReference.get(),"contents")

                println("contents : $contents")


                var targetLanguage = Locale.getDefault().language

                val translation = translate.translate(
                        contents,
                        Translate.TranslateOption.targetLanguage(targetLanguage))

                return translation.translatedText
            }


            override fun onPostExecute(result: String?) {
                jsonReference.get()!!.put("translated", result)

                println("re : $result")

                placeAdapterReference.get()!!.notifyDataSetChanged()

                // ((viewReference.get()!!.parent.parent.parent.parent.parent as LinearLayout).findViewById(R.id.translatedTV) as TextView).text = result
            }

        }
    }


}
