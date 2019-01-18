package donggolf.android.adapters

import android.content.Context
import android.media.Image
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.Utils


open class SoloTimeAdapter(context: Context, view:Int, data:ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context,view, data){

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONObject> = data

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
        var contents = Utils.getString(json,"contents")
        var created = Utils.getString(json,"created_at")
        var image = json.getJSONArray("images")

        if (image.length() > 0){
            val image_item = image.get(image.length()-1) as JSONObject
            val image_uri = Utils.getString(image_item,"image_uri")
            var uri = Config.url+"/" + image_uri
            ImageLoader.getInstance().displayImage(uri, item.backgroundIV, Utils.UILoptionsUserProfile)
        }


        var createdsplit = created.split(" ")
        var timesplit = createdsplit.get(1).split(":")


        item.placeTV.setText(place_name)
        item.durationTV.setText(duration)
        item.costTV.setText(cost+"$ ")
        item.contentTV.setText(contents)
        if (timesplit.get(0).toInt() >= 12){
            item.createdTV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":"+timesplit.get(1))
        } else {
            item.createdTV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":"+timesplit.get(1))
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
        var durationTV: TextView
        var costTV: TextView
        var createdTV:TextView


        init {
            backgroundIV = v.findViewById<View>(R.id.backgroundIV) as ImageView
            contentTV = v.findViewById<View>(R.id.contentTV) as TextView
            placeTV = v.findViewById<View>(R.id.placeTV) as TextView
            durationTV = v.findViewById<View>(R.id.durationTV) as TextView
            costTV = v.findViewById<View>(R.id.costTV) as TextView
            createdTV = v.findViewById<View>(R.id.createdTV) as TextView





        }
    }
}
