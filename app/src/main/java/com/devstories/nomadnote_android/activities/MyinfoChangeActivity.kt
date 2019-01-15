package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import kotlinx.android.synthetic.main.activity_info_change.*

class MyinfoChangeActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_change)
        this.context = this
        progressDialog = ProgressDialog(context)

        titleBackLL.setOnClickListener {
            finish()
        }


    }



}
