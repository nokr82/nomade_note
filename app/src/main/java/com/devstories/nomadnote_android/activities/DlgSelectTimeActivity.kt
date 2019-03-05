package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import kotlinx.android.synthetic.main.activity_dlg_select_time.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DlgSelectTimeActivity : RootActivity() {

    private var context: Context? = null
    private var progressDialog: ProgressDialog? = null

    private val hour_list = ArrayList<String>()
    private val minute_list = ArrayList<String>()
    private val ampm = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dlg_select_time)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        var intent = getIntent()
        var intent_ampm = intent.getStringExtra("ampm")
        var intent_time = intent.getStringExtra("time")
        var ampm_value = 0
        if (intent_ampm == "PM"){
            ampm_value = 1
        }

        var time_split = intent_time.split(":")
        var hour = time_split.get(0)
        var minute = time_split.get(1)

        ampm.add("AM")
        ampm.add("PM")

        ampmNP.minValue = 0
        ampmNP.maxValue = ampm.size - 1
        ampmNP.value = ampm_value
        ampmNP.setFormatter { i -> ampm[i] }

        for (i in 1 until 13) {
            if (i < 10) {
                val h = "0" + i
                hour_list.add(h)
            } else {
                val h = "" + i
                hour_list.add(h)
            }

            if (i == 24) {

            }
        }

        for (i in 0..59) {
            if (i < 10) {
                val m = "0" + i
                minute_list.add(m)
            } else {
                val m = "" + i
                minute_list.add(m)
            }

            if (i == 59) {

            }
        }

        hourNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS)
        hourNP.minValue = 0
        hourNP.maxValue = hour_list.size - 1
        hourNP.value = hour.toInt() - 1
        hourNP.setFormatter { i -> hour_list[i] }

        minuteNP.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        minuteNP.minValue = 0
        minuteNP.maxValue = minute_list.size - 1
        minuteNP.value = minute.toInt()
        minuteNP.setFormatter { i -> minute_list[i] }

        okTV.setOnClickListener(View.OnClickListener {
            try {
                val intent = Intent()
                intent.putExtra("hourNP", hour_list[hourNP.value])
                intent.putExtra("minuteNP", minute_list[minuteNP.value])
                intent.putExtra("ampm",ampm[ampmNP.value])
                setResult(RESULT_OK, intent)
                finish()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        })

        closeLL.setOnClickListener {
            finish()
        }


    }
}
