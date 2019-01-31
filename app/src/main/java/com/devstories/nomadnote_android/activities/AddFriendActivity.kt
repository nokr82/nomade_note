package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.Utils
import kotlinx.android.synthetic.main.fra_friend_add.*

class AddFriendActivity : FragmentActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    val Friend_add_Fragment : Friend_add_Fragment = Friend_add_Fragment()
    val Friend_id_Fragment : Friend_id_Fragment = Friend_id_Fragment()
    val Friend_phone_Fragment : Friend_phone_Fragment = Friend_phone_Fragment()

    var f_type = -1
    var type = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        this.context = this
//        progressDialog = ProgressDialog(myContext)
        progressDialog = ProgressDialog(context, R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)

        setmenu()

        click()

        var intent = getIntent()

        if (intent.getIntExtra("type",0) != null){
            f_type = intent.getIntExtra("type",0)

            if (f_type==1){
                phoneLL.callOnClick()
            }else if (f_type==2){
                idLL.callOnClick()
            }else{
                addLL.callOnClick()
            }
        }

    }

    fun setmenu(){
        phoneTV.setTextColor(Color.parseColor("#878787"))
        phoneV.visibility = View.GONE
        idTV.setTextColor(Color.parseColor("#878787"))
        idV.visibility = View.GONE
        addTV.setTextColor(Color.parseColor("#878787"))
        addV.visibility = View.GONE
    }



    fun click(){
        titleBackLL.setOnClickListener {
            finish()
            Utils.hideKeyboard(context)

        }


        phoneLL.setOnClickListener {
            setmenu()
            supportFragmentManager.beginTransaction().replace(R.id.friendFL,Friend_phone_Fragment).commit()
            phoneV.visibility = View.VISIBLE
            phoneTV.setTextColor(Color.parseColor("#000000"))
            phoneV.setBackgroundColor(Color.parseColor("#000000"))

        }
        idLL.setOnClickListener {
            setmenu()
            supportFragmentManager.beginTransaction().replace(R.id.friendFL, Friend_id_Fragment).commit()
            idV.visibility = View.VISIBLE
            idTV.setTextColor(Color.parseColor("#000000"))
            idV.setBackgroundColor(Color.parseColor("#000000"))
        }
        addLL.setOnClickListener {
            setmenu()
            supportFragmentManager.beginTransaction().replace(R.id.friendFL, Friend_add_Fragment).commit()
            addV.visibility = View.VISIBLE
            addTV.setTextColor(Color.parseColor("#000000"))
            addV.setBackgroundColor(Color.parseColor("#000000"))
        }




    }


    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }
}
