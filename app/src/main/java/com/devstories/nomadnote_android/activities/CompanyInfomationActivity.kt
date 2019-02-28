package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import donggolf.android.adapters.PlaceAdapter
import kotlinx.android.synthetic.main.activity_company_infomation.*

class CompanyInfomationActivity : RootActivity() {


    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_infomation)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        titleBackLL.setOnClickListener {
            finish()
            Utils.hideKeyboard(this)
        }
    }
}
