package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.*
import kotlinx.android.synthetic.main.activity_notice.*

class NoticeActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var type = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        GoogleAnalytics.sendEventGoogleAnalytics(application as GlobalApplication, "android", "이용약관")

        var intent = getIntent()
        type = intent.getIntExtra("type", -1)


        if (type == 2) {
            val url = Config.url + "/admin/agree2view"
            noticeWV.loadUrl(url)
            logoTV.text = getString(R.string.private_infomation)
        } else if (type == 3) {
            val url = Config.url + "/admin/agree1view"
            noticeWV.loadUrl(url)
            logoTV.text = getString(R.string.terms_and_conditions)
        }

        titleBackLL.setOnClickListener {
            finish()
            Utils.hideKeyboard(this)
        }
    }
}
