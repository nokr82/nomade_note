package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.R.id.backIV
import com.devstories.nomadnote_android.R.id.loginTV
import com.devstories.nomadnote_android.base.RootActivity
import kotlinx.android.synthetic.main.activity_login2.*

class Login2Activity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        this.context = this
        progressDialog = ProgressDialog(context)

        loginTV.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        backIV.setOnClickListener {
            finish()
        }


    }



}
