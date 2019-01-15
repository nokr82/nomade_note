package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.context = this
        progressDialog = ProgressDialog(context)

        loginTV.setOnClickListener {
            val intent = Intent(context, Login2Activity::class.java)
            startActivity(intent)
        }
        joinTV.setOnClickListener {
            val intent = Intent(context, EmailJoinActivity::class.java)
            startActivity(intent)
        }
        kakaoLL.setOnClickListener {
            val intent = Intent(context, Login2Activity::class.java)
            startActivity(intent)
        }



    }



}
