package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import kotlinx.android.synthetic.main.activity_write.*

class WriteActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var menu_position = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        this.context = this
        progressDialog = ProgressDialog(context)

        click()

        val time = Utils.timeStr()
        var timesplit = time.split(":")
        timeET.setText(timesplit.get(0))
        minuteET.setText(timesplit.get(1))







    }
    fun click(){
        titleBackLL.setOnClickListener {
            finish()
        }

        //힐링
        healingTV.setOnClickListener {
            menuSetImage()
            healingTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            healingTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 1
        }

        //핫플레이스
        hotplaceTV.setOnClickListener {
            menuSetImage()
            hotplaceTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            hotplaceTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 2
        }

        //문학 스타일
        literatureTV.setOnClickListener {
            menuSetImage()
            literatureTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            literatureTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 3
        }

        //역사 스타일
        historyTV.setOnClickListener {
            menuSetImage()
            historyTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            historyTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 4
        }

        //박물관 스타일
        museumTV.setOnClickListener {
            menuSetImage()
            museumTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            museumTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 5
        }

        addcontentLL.setOnClickListener {
            addContent()
        }


    }

    fun addContent(){
        val location = locationET.text.toString()
        if (location == "" || location == null){
            Toast.makeText(context, "지역은 필수입력 입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val time = timeET.text.toString()
        val minute = minuteET.text.toString()
        if (time == "" || time == null || minute == "" || minute == null){
            Toast.makeText(context, "시간은 필수입력 입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val content = contentET.text.toString()
        if (content == "" || content == null){
            Toast.makeText(context, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }






    }

    fun menuSetImage(){
        healingTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        healingTV.setTextColor(Color.parseColor("#878787"))
        hotplaceTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        hotplaceTV.setTextColor(Color.parseColor("#878787"))
        literatureTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        literatureTV.setTextColor(Color.parseColor("#878787"))
        historyTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        historyTV.setTextColor(Color.parseColor("#878787"))
        museumTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        museumTV.setTextColor(Color.parseColor("#878787"))
    }



}
