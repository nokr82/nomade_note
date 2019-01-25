package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Base64
import android.util.Log
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.RootActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class IntroActivity : RootActivity() {

    protected var _splashTime = 2000 // time to display the splash screen in ms
    private val _active = true
    private var splashThread: Thread? = null

    private var progressDialog: ProgressDialog? = null

    private var context: Context? = null

    private var is_push:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.devstories.nomadnote_android.R.layout.activity_intro)
        this.context = this
        progressDialog = ProgressDialog(context)

        printHashKey()

        splashThread = object : Thread() {
            override fun run() {
                try {
                    var waited = 0
                    while (waited < _splashTime && _active) {
                        Thread.sleep(100)
                        waited += 100
                    }
                } catch (e: InterruptedException) {

                } finally {
                    stopIntro()
                }
            }
        }
        (splashThread as Thread).start()


    }

    private fun stopIntro() {

        val autoLogin = PrefUtils.getBooleanPreference(context, "autoLogin")
        //val autoLogin = false

        if (!autoLogin) {
            PrefUtils.clear(context)
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        } else {
            handler.sendEmptyMessage(0)
        }

    }
    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            //versionInfo();
            //PrefUtils.setPreference(context, "company_id", 1)
        }
    }
    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

    private fun printHashKey() {

        try {
            val info = packageManager.getPackageInfo(
                    "-----------PUT YOUR PACKAGE NAME ------------",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

    }
}
