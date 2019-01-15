package com.devstories.nomadnote_android.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import java.util.ArrayList


open class OthertimeAdapter (context:Context, view:Int, data:Int) : ArrayAdapter<Int>(context, view, data) {

    private lateinit var item: ViewHolder
    var view:Int = view
    var data: Int = data


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




        return retView
    }

    override fun getItem(position: Int): Int {
        return data
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data
    }
    class ViewHolder(v: View) {



        init {


        }
    }
}
