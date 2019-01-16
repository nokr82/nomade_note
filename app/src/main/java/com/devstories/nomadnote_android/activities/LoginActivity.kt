package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null


    var jointype = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.context = this
        progressDialog = ProgressDialog(context)


        try {
            val info = context.packageManager.getPackageInfo("com.devstories.nomadnote_android", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }

        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }


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
            jointype = 1
            intent.putExtra("jointype",jointype)
            startActivity(intent)
        }
        naverLL.setOnClickListener {
            val intent = Intent(context, Login2Activity::class.java)
            jointype = 2
            intent.putExtra("jointype",jointype)
            startActivity(intent)
        }
        googleLL.setOnClickListener {
            val intent = Intent(context, Login2Activity::class.java)
            jointype = 3
            intent.putExtra("jointype",jointype)
            startActivity(intent)
        }

        facebookLL.setOnClickListener {
            val intent = Intent(context, Login2Activity::class.java)
            jointype = 4
            intent.putExtra("jointype",jointype)
            startActivity(intent)
        }

    }



}
