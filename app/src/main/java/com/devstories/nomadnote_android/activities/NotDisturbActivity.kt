package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import kotlinx.android.synthetic.main.activity_not_disturb.*

class NotDisturbActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var allblock = false
    var slot = false

    var spinnerItem:ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_disturb)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        spinnerItem.add("01:00")
        spinnerItem.add("02:00")
        spinnerItem.add("03:00")
        spinnerItem.add("04:00")
        spinnerItem.add("05:00")
        spinnerItem.add("06:00")
        spinnerItem.add("07:00")
        spinnerItem.add("08:00")
        spinnerItem.add("09:00")
        spinnerItem.add("10:00")
        spinnerItem.add("11:00")
        spinnerItem.add("12:00")

        var startAdpater = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, spinnerItem)
        startS.adapter = startAdpater

        var endAdpater = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, spinnerItem)
        endS.adapter = endAdpater

        startS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                val time = spinnerItem.get(pos)
                startTV.setText(time)
            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }

        }

        endS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                val time = spinnerItem.get(pos)
                endTV.setText(time)
            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }

        }

        titleBackLL.setOnClickListener {
            finish()
            Utils.hideKeyboard(this)
        }

        allblockLL.setOnClickListener {
            allblock = !allblock
            if (allblock) {
                allblockIV.setImageResource(R.mipmap.check_off)
            } else {
                allblockIV.setImageResource(R.mipmap.check_on)
            }
        }

        slotLL.setOnClickListener {
            slot = !slot
            if (slot) {
                slotIV.setImageResource(R.mipmap.check_off)
            } else {
                slotIV.setImageResource(R.mipmap.check_on)
            }



            startS.setOnClickListener {

            }

            endS.setOnClickListener {

            }


        }
    }
}
