package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.adapter.ScrapAdapter
import com.devstories.nomadnote_android.base.RootActivity
import kotlinx.android.synthetic.main.activity_mapsearch.*

class MapSearchActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    lateinit var ScrapAdapter: ScrapAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapsearch)
        this.context = this
        progressDialog = ProgressDialog(context)
        titleBackLL.setOnClickListener {
            finish()
        }
        ScrapAdapter = ScrapAdapter(context, R.layout.item_scrap, 10)
        scrapLV.adapter = ScrapAdapter


    }
    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }


}
