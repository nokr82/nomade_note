package donggolf.android.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.activities.Solo_detail_Activity
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.Utils
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONArray
import org.json.JSONObject


open class SoloTimeAdapter(context: Context, view:Int, data:ArrayList<JSONArray>) : ArrayAdapter<JSONArray>(context,view, data){

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONArray> = data

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

        val length = json.length()
        if(length == 1) {
            item.item2RL.visibility = View.GONE
            item.item3RL.visibility = View.GONE

            item.paddingLeft2V.visibility = View.GONE
            item.paddingLeft3V.visibility = View.GONE

            // 1
            val chunk = json.getJSONObject(0)
            setFirst(chunk)


        } else if(length == 2) {
            item.item2RL.visibility = View.VISIBLE
            item.item3RL.visibility = View.GONE

            item.paddingLeft2V.visibility = View.VISIBLE
            item.paddingLeft3V.visibility = View.GONE

            // 1
            val chunk1 = json.getJSONObject(0)
            setFirst(chunk1)

            // 2
            val chunk2 = json.getJSONObject(1)
            setSecond(chunk2)

        } else if(length == 3) {
            item.item2RL.visibility = View.VISIBLE
            item.item3RL.visibility = View.VISIBLE

            item.paddingLeft2V.visibility = View.VISIBLE
            item.paddingLeft3V.visibility = View.VISIBLE

            // 1
            val chunk1 = json.getJSONObject(0)
            setFirst(chunk1)

            // 2
            val chunk2 = json.getJSONObject(1)
            setSecond(chunk2)

            // 3
            val chunk3 = json.getJSONObject(2)
            setThird(chunk3)

        }

        return retView

    }

    private fun setFirst(chunk: JSONObject) {
        var place_name = Utils.getString(chunk, "place_name")
        var duration = Utils.getString(chunk, "duration")
        var cost = Utils.getString(chunk, "cost")
        var contents = Utils.getString(chunk, "contents")
        var created = Utils.getString(chunk, "created_at")

        //이거슨 그친구에 이미지를 연결해야함


        if (chunk.has("images")) {
            var image = chunk.getJSONArray("images")

            if (image.length() > 0) {
                val image_item = image.get(image.length() - 1) as JSONObject
                val image_uri = Utils.getString(image_item, "image_uri")
                var uri = Config.url + "/" + image_uri
                ImageLoader.getInstance().displayImage(uri, item.backgroundIV, Utils.UILoptionsUserProfile)
            } else {
                item.backgroundIV.setImageResource(R.mipmap.time_bg)
            }
        }


        var createdsplit = created.split(" ")
        //이거슨 이유모를오류
        var timesplit = createdsplit.get(1).split(":")


        item.placeTV.setText(place_name)
        item.durationTV.setText(duration)
        item.costTV.setText(cost + "$ ")
        item.contentTV.setText(contents)
        if (timesplit.get(0).toInt() >= 12) {
            item.createdTV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":" + timesplit.get(1))
        } else {
            item.createdTV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":" + timesplit.get(1))
        }

        item.item1RL.setOnClickListener {

            val timeline_id = Utils.getString(chunk, "id")

            val intent = Intent(context, Solo_detail_Activity::class.java)
            intent.putExtra("timeline_id", timeline_id)
            context.startActivity(intent)
        }


    }

    private fun setSecond(chunk: JSONObject) {
        var place_name = Utils.getString(chunk, "place_name")
        var duration = Utils.getString(chunk, "duration")
        var cost = Utils.getString(chunk, "cost")
        var contents = Utils.getString(chunk, "contents")
        var created = Utils.getString(chunk, "created_at")

        //이거슨 그친구에 이미지를 연결해야함


        if (chunk.has("images")) {
            var image = chunk.getJSONArray("images")

            if (image.length() > 0) {
                val image_item = image.get(image.length() - 1) as JSONObject
                val image_uri = Utils.getString(image_item, "image_uri")
                var uri = Config.url + "/" + image_uri
                ImageLoader.getInstance().displayImage(uri, item.background2IV, Utils.UILoptionsUserProfile)
            } else {
                item.background2IV.setImageResource(R.mipmap.time_bg)
            }
        }


        var createdsplit = created.split(" ")
        //이거슨 이유모를오류
        var timesplit = createdsplit.get(1).split(":")


        item.place2TV.setText(place_name)
        item.duration2TV.setText(duration)
        item.cost2TV.setText(cost + "$ ")
        item.content2TV.setText(contents)
        if (timesplit.get(0).toInt() >= 12) {
            item.created2TV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":" + timesplit.get(1))
        } else {
            item.created2TV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":" + timesplit.get(1))
        }

        item.item2RL.setOnClickListener {

            val timeline_id = Utils.getString(chunk, "id")

            val intent = Intent(context, Solo_detail_Activity::class.java)
            intent.putExtra("timeline_id", timeline_id)
            context.startActivity(intent)
        }


    }

    private fun setThird(chunk: JSONObject) {
        var place_name = Utils.getString(chunk, "place_name")
        var duration = Utils.getString(chunk, "duration")
        var cost = Utils.getString(chunk, "cost")
        var contents = Utils.getString(chunk, "contents")
        var created = Utils.getString(chunk, "created_at")

        //이거슨 그친구에 이미지를 연결해야함


        if (chunk.has("images")) {
            var image = chunk.getJSONArray("images")

            if (image.length() > 0) {
                val image_item = image.get(image.length() - 1) as JSONObject
                val image_uri = Utils.getString(image_item, "image_uri")
                var uri = Config.url + "/" + image_uri
                ImageLoader.getInstance().displayImage(uri, item.background3IV, Utils.UILoptionsUserProfile)
            } else {
                item.background3IV.setImageResource(R.mipmap.time_bg)
            }
        }


        var createdsplit = created.split(" ")
        //이거슨 이유모를오류
        var timesplit = createdsplit.get(1).split(":")


        item.place3TV.setText(place_name)
        item.duration3TV.setText(duration)
        item.cost3TV.setText(cost + "$ ")
        item.content3TV.setText(contents)
        if (timesplit.get(0).toInt() >= 12) {
            item.created3TV.setText(createdsplit.get(0) + " PM" + timesplit.get(0) + ":" + timesplit.get(1))
        } else {
            item.created3TV.setText(createdsplit.get(0) + " AM" + timesplit.get(0) + ":" + timesplit.get(1))
        }

        item.item3RL.setOnClickListener {

            val timeline_id = Utils.getString(chunk, "id")

            val intent = Intent(context, Solo_detail_Activity::class.java)
            intent.putExtra("timeline_id", timeline_id)
            context.startActivity(intent)
        }
    }

    override fun getItem(position: Int): JSONArray {

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

        var background2IV : ImageView
        var content2TV : TextView
        var place2TV: TextView
        var duration2TV: TextView
        var cost2TV: TextView
        var created2TV:TextView

        var background3IV : ImageView
        var content3TV : TextView
        var place3TV: TextView
        var duration3TV: TextView
        var cost3TV: TextView
        var created3TV:TextView

        var paddingLeft2V:View
        var paddingLeft3V:View

        var item1RL:RelativeLayout
        var item2RL:RelativeLayout
        var item3RL:RelativeLayout

        init {
            backgroundIV = v.findViewById<View>(R.id.backgroundIV) as ImageView
            contentTV = v.findViewById<View>(R.id.contentTV) as TextView
            placeTV = v.findViewById<View>(R.id.placeTV) as TextView
            durationTV = v.findViewById<View>(R.id.durationTV) as TextView
            costTV = v.findViewById<View>(R.id.costTV) as TextView
            createdTV = v.findViewById<View>(R.id.createdTV) as TextView

            background2IV = v.findViewById<View>(R.id.background2IV) as ImageView
            content2TV = v.findViewById<View>(R.id.content2TV) as TextView
            place2TV = v.findViewById<View>(R.id.place2TV) as TextView
            duration2TV = v.findViewById<View>(R.id.duration2TV) as TextView
            cost2TV = v.findViewById<View>(R.id.cost2TV) as TextView
            created2TV = v.findViewById<View>(R.id.created2TV) as TextView

            background3IV = v.findViewById<View>(R.id.background3IV) as ImageView
            content3TV = v.findViewById<View>(R.id.content3TV) as TextView
            place3TV = v.findViewById<View>(R.id.place3TV) as TextView
            duration3TV = v.findViewById<View>(R.id.duration3TV) as TextView
            cost3TV = v.findViewById<View>(R.id.cost3TV) as TextView
            created3TV = v.findViewById<View>(R.id.created3TV) as TextView

            item1RL = v.findViewById<View>(R.id.item1RL) as RelativeLayout
            item2RL = v.findViewById<View>(R.id.item2RL) as RelativeLayout
            item3RL = v.findViewById<View>(R.id.item3RL) as RelativeLayout

            paddingLeft2V = v.findViewById<View>(R.id.paddingLeft2V) as View
            paddingLeft3V = v.findViewById<View>(R.id.paddingLeft3V) as View




        }
    }
}
