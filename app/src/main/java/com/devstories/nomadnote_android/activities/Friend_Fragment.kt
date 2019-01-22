package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.devstories.nomadnote_android.R
import kotlinx.android.synthetic.main.fra_friend_add.*
import kotlinx.android.synthetic.main.fra_friend_add.view.*

class Friend_Fragment : Fragment()  {
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    val Friend_add_Fragment : Friend_add_Fragment = Friend_add_Fragment()
    val Friend_id_Fragment : Friend_id_Fragment = Friend_id_Fragment()
    val Friend_phone_Fragment : Friend_phone_Fragment = Friend_phone_Fragment()

    var f_type = -1
    var type = -1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext)
        return inflater.inflate(R.layout.fra_friend_add, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setmenu()






        click()
        if (getArguments() != null) {

            f_type = getArguments()!!.getInt("type")

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
        phoneV.visibility =View.GONE
        idTV.setTextColor(Color.parseColor("#878787"))
        idV.visibility =View.GONE
        addTV.setTextColor(Color.parseColor("#878787"))
        addV.visibility =View.GONE
    }



    fun click(){
        titleBackLL.setOnClickListener {
            var intent = Intent()
            intent.action = "FRIEND_BACK"
            myContext.sendBroadcast(intent)

        }


        phoneLL.setOnClickListener {
            setmenu()
            childFragmentManager.beginTransaction().replace(R.id.friendFL,Friend_phone_Fragment).commit()
            phoneV.visibility =View.VISIBLE
            phoneTV.setTextColor(Color.parseColor("#000000"))
            phoneV.setBackgroundColor(Color.parseColor("#000000"))

        }
        idLL.setOnClickListener {
            setmenu()
            childFragmentManager.beginTransaction().replace(R.id.friendFL, Friend_id_Fragment).commit()
            idV.visibility =View.VISIBLE
            idTV.setTextColor(Color.parseColor("#000000"))
            idV.setBackgroundColor(Color.parseColor("#000000"))
        }
        addLL.setOnClickListener {
            setmenu()
            childFragmentManager.beginTransaction().replace(R.id.friendFL, Friend_add_Fragment).commit()
            addV.visibility =View.VISIBLE
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
