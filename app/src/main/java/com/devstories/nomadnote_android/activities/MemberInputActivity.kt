package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.R.id.backIV
import com.devstories.nomadnote_android.R.id.startTV
import com.devstories.nomadnote_android.base.RootActivity
import kotlinx.android.synthetic.main.activity_member_infoinput.*

class MemberInputActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_infoinput)
        this.context = this
        progressDialog = ProgressDialog(context)

        backIV.setOnClickListener {
            finish()
        }


        startTV.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }



    }



}
