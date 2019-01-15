package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.adapter.ScrapAdapter
import com.devstories.nomadnote_android.adapter.VisitNationAdapter
import com.devstories.nomadnote_android.base.RootActivity
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
        progressDialog = ProgressDialog(context)


        titleBackLL.setOnClickListener {
            finish()
        }

        VisitNationAdapter = VisitNationAdapter(context, R.layout.item_nation, 20)
        visitLV.adapter = VisitNationAdapter
    }



}
