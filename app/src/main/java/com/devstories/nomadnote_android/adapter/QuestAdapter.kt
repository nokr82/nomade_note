package com.devstories.nomadnote_android.activities

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.Config
import com.devstories.nomadnote_android.base.Utils
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


open class QuestAdapter(context: Context, view: Int, data: ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context,view, data){

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
        val question = Utils.getString(json,"question")
        val created_at = Utils.getString(json,"created_at")
        var answer = Utils.getString(json,"answer")

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yy-MM-dd HH:mm:ss")
        val getTime = sdf.format(date)

        var d1 = sdf.parse(created_at);
        var d2 = sdf.parse(getTime);

        val diff = d2.time - d1.time
        val days = TimeUnit.MILLISECONDS.toDays(diff);
        val remainingHoursInMillis = diff - TimeUnit.DAYS.toMillis(days);
        val hours = TimeUnit.MILLISECONDS.toHours(remainingHoursInMillis);
        val remainingMinutesInMillis = remainingHoursInMillis - TimeUnit.HOURS.toMillis(hours);
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMinutesInMillis);

        item.timeTV.setText(hours.toString() + "H")


        if (answer != null && answer != ""){
            item.answerTV.setText("답변보기")
        }

        item.contentTV.setText(question)


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

        var timeTV: TextView
        var contentTV: TextView
        var answerLL: LinearLayout
        var answerTV: TextView

        init {

            timeTV = v.findViewById<View>(R.id.timeTV) as TextView
            contentTV = v.findViewById<View>(R.id.contentTV) as TextView
            answerLL = v.findViewById<View>(R.id.answerLL) as LinearLayout
            answerTV = v.findViewById<View>(R.id.answerTV) as TextView

        }
    }


}
