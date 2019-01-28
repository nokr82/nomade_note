package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.adapter.VisitNationAdapter
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import kotlinx.android.synthetic.main.activity_visit_nation.*

class VisitNationActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    lateinit var VisitNationAdapter: VisitNationAdapter
    var data = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_nation)
        this.context = this
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
//        progressDialog = ProgressDialog(context)


        titleBackLL.setOnClickListener {
            finish()
        }

        VisitNationAdapter = VisitNationAdapter(context, R.layout.item_nation, 20)
        visitLV.adapter = VisitNationAdapter
    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

    override fun onBackPressed() {
        finish()
        Utils.hideKeyboard(context)
    }
}
