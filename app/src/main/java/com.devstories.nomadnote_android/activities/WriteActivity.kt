package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import kotlinx.android.synthetic.main.activity_write.*

class WriteActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        this.context = this
        progressDialog = ProgressDialog(context)

        click()


    }
fun click(){
    titleBackLL.setOnClickListener {
        finish()
    }
}


}
