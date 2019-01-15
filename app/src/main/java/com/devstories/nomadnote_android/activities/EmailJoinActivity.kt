package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import kotlinx.android.synthetic.main.activity_email_join.*

class EmailJoinActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null


    var pw = ""
    var pw2 = ""
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_join)
        this.context = this
        progressDialog = ProgressDialog(context)
        backIV.setOnClickListener {
            finish()
        }




        joinTV.setOnClickListener {
            email = Utils.getString(emailET)
            pw = Utils.getString(pwET)
            pw2 = Utils.getString(pw2ET)
            if (email.equals("")){
                Toast.makeText(context,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pw.equals("")){
                Toast.makeText(context,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pw !=pw2){
                Toast.makeText(context,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(context, MemberInputActivity::class.java)
            intent.putExtra("email",email)
            intent.putExtra("pw",pw)
            startActivity(intent)
        }



    }



}
