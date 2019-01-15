package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import kotlinx.android.synthetic.main.activity_email_join.*

class EmailJoinActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_join)
        this.context = this
        progressDialog = ProgressDialog(context)
        backIV.setOnClickListener {
            finish()
        }


        joinTV.setOnClickListener {
            val intent = Intent(context, MemberInputActivity::class.java)
            startActivity(intent)
        }



    }



}
