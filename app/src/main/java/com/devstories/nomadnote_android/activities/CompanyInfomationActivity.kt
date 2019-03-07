package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.GlobalApplication
import com.devstories.nomadnote_android.base.GoogleAnalytics
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.kakao.usermgmt.StringSet.email
import donggolf.android.adapters.PlaceAdapter
import kotlinx.android.synthetic.main.activity_company_infomation.*

class CompanyInfomationActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null


    var type = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_infomation)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        GoogleAnalytics.sendEventGoogleAnalytics(application as GlobalApplication, "android", "사업자정보")


        privacyTV.setOnClickListener {
            type=2
            val intent = Intent(context, NoticeActivity::class.java)
            intent.putExtra("type",type)
            startActivity(intent)
        }
        serviceTV.setOnClickListener {
            type=3
            val intent = Intent(context, NoticeActivity::class.java)
            intent.putExtra("type",type)
            startActivity(intent)
        }



        titleBackLL.setOnClickListener {
            finish()
            Utils.hideKeyboard(this)
        }
    }
}
