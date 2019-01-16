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
import com.devstories.nomadnote_android.R
import kotlinx.android.synthetic.main.fra_setting.*
import kotlinx.android.synthetic.main.fra_setting.view.*

class Seting_Fragment : Fragment()  {
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext)
        return inflater.inflate(R.layout.fra_setting, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        click()
        op_click()

    }

    fun op_click(){

        //친구추가
        op_idLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                setmenu()
                op_idIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_idIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_addLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                setmenu()
                op_addIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_addIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_telLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                setmenu()
                op_telIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_telIV.setImageResource(R.drawable.circle_background3)
            }
        }

        //결제시스템
        op_1gbLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                setmenu2()
                op_1gbIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_1gbIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_600mbLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                setmenu2()
                op_600mbIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_600mbIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_20kbLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                setmenu2()
                op_20kbIV.setImageResource(R.mipmap.icon_check)
            } else {
                op_20kbIV.setImageResource(R.drawable.circle_background3)
            }
        }



        //여행스타일
        healTV.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                healTV.setBackgroundResource(R.drawable.background_border_radius10)
                healTV.setTextColor(Color.parseColor("#ffffff"))
            } else {
                healTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
                healTV.setTextColor(Color.parseColor("#878787"))
            }
        }
        hotplaceTV.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                hotplaceTV.setBackgroundResource(R.drawable.background_border_radius10)
                hotplaceTV.setTextColor(Color.parseColor("#ffffff"))
            } else {
                hotplaceTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
                hotplaceTV.setTextColor(Color.parseColor("#878787"))
            }
        }
        cultureTV.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                cultureTV.setBackgroundResource(R.drawable.background_border_radius10)
                cultureTV.setTextColor(Color.parseColor("#ffffff"))
            } else {
                cultureTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
                cultureTV.setTextColor(Color.parseColor("#878787"))
            }
        }
        sidmierTV.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                sidmierTV.setBackgroundResource(R.drawable.background_border_radius10)
                sidmierTV.setTextColor(Color.parseColor("#ffffff"))
            } else {
                sidmierTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
                sidmierTV.setTextColor(Color.parseColor("#878787"))
            }
        }
        artTV.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                artTV.setBackgroundResource(R.drawable.background_border_radius10)
                artTV.setTextColor(Color.parseColor("#ffffff"))
            } else {
                artTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
                artTV.setTextColor(Color.parseColor("#878787"))
            }
        }
        museumTV.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                museumTV.setBackgroundResource(R.drawable.background_border_radius10)
                museumTV.setTextColor(Color.parseColor("#ffffff"))
            } else {
                museumTV.setBackgroundResource(R.drawable.background_border_radius9_000000)
                museumTV.setTextColor(Color.parseColor("#878787"))
            }
        }
    }

    fun setmenu2(){
        op_1gbIV.setImageResource(R.drawable.circle_background3)
        op_600mbIV.setImageResource(R.drawable.circle_background3)
        op_20kbIV.setImageResource(R.drawable.circle_background3)
    }
    fun setmenu(){
        op_idIV.setImageResource(R.drawable.circle_background3)
        op_addIV.setImageResource(R.drawable.circle_background3)
        op_telIV.setImageResource(R.drawable.circle_background3)
    }

    fun click(){

        nationLL.setOnClickListener {
            val intent = Intent(myContext, VisitNationActivity::class.java)
            startActivity(intent)
        }


        logoutLL.setOnClickListener {
            val intent = Intent(myContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        myinfochangeLL.setOnClickListener {
            val intent = Intent(myContext, MyinfoChangeActivity::class.java)
            startActivity(intent)
        }


        travelLL.setOnClickListener {
            if ( op_travelLL.visibility==View.GONE){
                op_travelLL.visibility = View.VISIBLE
                styleIV.rotation = 90f
            }else{
                op_travelLL.visibility = View.GONE
                styleIV.rotation = 0f
            }

        }

        memoryLL.setOnClickListener {
            if ( op_memoryLL.visibility==View.GONE){
                op_memoryLL.visibility = View.VISIBLE
                memoryIV.rotation = 90f
            }else{
                op_memoryLL.visibility = View.GONE
                memoryIV.rotation = 0f
            }

        }

        friendaddLL.setOnClickListener {
            if ( op_friendaddLL.visibility==View.GONE){
                op_friendaddLL.visibility = View.VISIBLE
                friendaddIV.rotation = 90f
            }else{
                op_friendaddLL.visibility = View.GONE
                friendaddIV.rotation = 0f
            }

        }

        payLL.setOnClickListener {
            if ( op_payLL.visibility==View.GONE){
                op_payLL.visibility = View.VISIBLE
                payIV.rotation = 90f
            }else{
                op_payLL.visibility = View.GONE
                payIV.rotation = 0f
            }
        }

        snsLL.setOnClickListener {
            if ( op_snsLL.visibility==View.GONE){
                op_snsLL.visibility = View.VISIBLE
                snsIV.rotation = 90f
            }else{
                op_snsLL.visibility = View.GONE
                snsIV.rotation = 0f
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
