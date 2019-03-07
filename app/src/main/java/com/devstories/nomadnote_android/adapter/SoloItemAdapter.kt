package com.devstories.nomadnote_android.adapter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.Utils
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import java.util.*




class SoloItemAdapter(private val context:Context, private val activity:Activity, private var timelineDatas: ArrayList<JSONObject>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class SoloItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var backgroundIV = itemView.findViewById<View>(R.id.backgroundIV) as ImageView
        var contentTV = itemView.findViewById<View>(R.id.contentTV) as TextView
        var placeTV = itemView.findViewById<View>(R.id.placeTV) as TextView
        var durationTV = itemView.findViewById<View>(R.id.durationTV) as TextView
        var costTV = itemView.findViewById<View>(R.id.costTV) as TextView
        var createdTV = itemView.findViewById<View>(R.id.createdTV) as TextView
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.item_solo_grid, parent, false) as View

        itemView.layoutParams.height = Utils.dpToPx(86f).toInt()

        return SoloItemHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = timelineDatas.get(position)

        var place_name = Utils.getString(item, "place_name")
        var duration = Utils.getString(item, "duration")
        var cost = Utils.getString(item, "cost")
        var contents = Utils.getString(item, "contents")
        var created = Utils.getString(item, "created_at")
        var cost_str = Utils.getString(item, "cost_str")


        val holder = holder as SoloItemHolder

        if (item.has("images")) {
            var image = item.getJSONArray("images")

            if (image.length() > 0) {
                val image_item = image.get(0) as JSONObject
                val image_uri = Utils.getString(image_item, "image_uri")
                var uri = Config.url + "/" + image_uri
                ImageLoader.getInstance().displayImage(uri, holder.backgroundIV, Utils.UILoptionsUserProfile)

            } else {
                holder.backgroundIV.setImageResource(R.mipmap.time_bg)
            }
        } else {
            holder.backgroundIV.setImageResource(R.mipmap.time_bg)
        }

        var createdsplit = created.split(" ")
        //이거슨 이유모를오류
        var timesplit = createdsplit.get(1).split(":")

        holder.placeTV.text = place_name
        holder.durationTV.text = duration
//        holder.costTV.setText(cost + "$ ")
//        var language = Locale.getDefault().language
//        if(language == "en" || language == "ja") {
//            holder.costTV.text = activity.getString(R.string.unit) + cost
//        } else {
//            holder.costTV.text = cost + activity.getString(R.string.unit)
//        }
        var language = Locale.getDefault().language

        if (cost_str.count() > 0 && cost_str != "") {
            holder.costTV.setText(cost_str)
        } else {
            if (language == "en" || language == "ja") {
                holder.costTV.setText(context.getString(R.string.unit) + cost)
            } else {
                holder.costTV.setText(cost + context.getString(R.string.unit))
            }
        }

        holder.contentTV.text = contents
        if (timesplit.get(0).toInt() >= 12) {
            holder.createdTV.text = createdsplit.get(0) + " PM" + timesplit.get(0) + ":" + timesplit.get(1)
        } else {
            holder.createdTV.text = createdsplit.get(0) + " AM" + timesplit.get(0) + ":" + timesplit.get(1)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = timelineDatas.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                timelineDatas[i + 1] = timelineDatas[i]
                timelineDatas[i] = timelineDatas[i + 1]
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                timelineDatas[i - 1] = timelineDatas[i]
                timelineDatas[i] = timelineDatas[i + 1]
            }
        }

        notifyItemMoved(fromPosition, toPosition)

    }
}

