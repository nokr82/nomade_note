package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devstories.nomadnote_android.R
import kotlinx.android.synthetic.main.fra_friend_id_phoone.*

class Friend_id_phone_Fragment : Fragment()  {
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null




    var f_type = -1

    var searchtype = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext)
        return inflater.inflate(R.layout.fra_friend_id_phoone, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (getArguments() != null) {
            f_type = getArguments()!!.getInt("type")

            Log.d("타입",f_type.toString())
            if (f_type==2){
                searchtype="id"
                titleTV.setText("ID로 친구를 추가할 수 있습니다.")
                contentTV.setText("추가하고 싶은 친구의 ID를 입력해 주세요.")
            }else{
                titleTV.setText("연락처로 친구를 추가할 수 있습니다.")
                contentTV.setText("추가하고 싶은 친구의 이름과\n휴대전화 번호를 입력해 주세요.")
                searchtype="phone"
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
