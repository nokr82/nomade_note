package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.R.id.*
import com.kakao.s2.StringSet.count
import kotlinx.android.synthetic.main.fra_friend_add.*

class Friend_Fragment : Fragment()  {
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    val Friend_add_Fragment : Friend_add_Fragment = Friend_add_Fragment()
    val Friend_id_phone_Fragment : Friend_id_phone_Fragment = Friend_id_phone_Fragment()


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
        phoneV.setBackgroundColor(Color.parseColor("#878787"))
        idTV.setTextColor(Color.parseColor("#878787"))
        idV.setBackgroundColor(Color.parseColor("#878787"))
        addTV.setTextColor(Color.parseColor("#878787"))
        addV.setBackgroundColor(Color.parseColor("#878787"))
    }



    fun click(){
        phoneLL.setOnClickListener {
            setmenu()
            var args: Bundle = Bundle()
            type=1
            args.putInt("type", type)
            Friend_id_phone_Fragment.setArguments(args)
            childFragmentManager.beginTransaction().replace(R.id.friendFL, Friend_id_phone_Fragment).commit()
            phoneTV.setTextColor(Color.parseColor("#000000"))
            phoneV.setBackgroundColor(Color.parseColor("#000000"))

        }
        idLL.setOnClickListener {
            setmenu()
            var args: Bundle = Bundle()
            type=2
            args.putInt("type", type)
            Friend_id_phone_Fragment.setArguments(args)
            childFragmentManager.beginTransaction().replace(R.id.friendFL, Friend_id_phone_Fragment).commit()
            idTV.setTextColor(Color.parseColor("#000000"))
            idV.setBackgroundColor(Color.parseColor("#000000"))
        }
        addLL.setOnClickListener {
            setmenu()
            childFragmentManager.beginTransaction().replace(R.id.friendFL, Friend_add_Fragment).commit()
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
