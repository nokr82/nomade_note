package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devstories.nomadnote_android.R
import kotlinx.android.synthetic.main.fra_solo_time.*

class Solo_time_Fragment : Fragment()  {
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext)
        return inflater.inflate(R.layout.fra_solo_time, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        click()
    }
    fun click(){
        soloRL.setOnClickListener {
            val intent = Intent(myContext, Solo_detail_Activity::class.java)
            startActivity(intent)
        }
        writeRL.setOnClickListener {
            val intent = Intent(myContext, WriteActivity::class.java)
            startActivity(intent)
        }
    }



}

