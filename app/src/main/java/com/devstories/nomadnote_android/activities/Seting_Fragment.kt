package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.Config
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.appevents.AppEventsLogger
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.FeedTemplate
import com.kakao.message.template.LinkObject
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.kakao.util.helper.log.Logger
import kotlinx.android.synthetic.main.fra_setting.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class Seting_Fragment : Fragment()  {
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null

    var REQUEST_EXTERNAL_STORAGE_CODE = 1
    var permissionCheck = false
    private lateinit var activity:MainActivity

    var f_type = -1





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

        activity = getActivity() as MainActivity
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(myContext)

        click()
        op_click()

    }

    fun op_click(){

        instaIV.setOnClickListener {
            shareInstagram()
        }
        facebookIV.setOnClickListener {
            shareFacebook()
        }
        naverIV.setOnClickListener {

        }
        kakaoIV.setOnClickListener {
            shareKakao()
        }





        //친구추가
        op_idLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                setmenu()
                op_idIV.setImageResource(R.mipmap.icon_check)
                f_type = 2
                var intent = Intent()
                intent.putExtra("type",f_type)
                intent.action = "FRIEND"
                myContext!!.sendBroadcast(intent)
            } else {
                op_idIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_addLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                setmenu()
                op_addIV.setImageResource(R.mipmap.icon_check)
                f_type = 3
                var intent = Intent()
                intent.putExtra("type",f_type)
                intent.action = "FRIEND"
                myContext!!.sendBroadcast(intent)
            } else {
                op_addIV.setImageResource(R.drawable.circle_background3)
            }
        }
        op_telLL.setOnClickListener {
            it.isSelected = !it.isSelected
            if(it.isSelected) {
                setmenu()
                op_telIV.setImageResource(R.mipmap.icon_check)
                f_type = 1
                var intent = Intent()
                intent.putExtra("type",f_type)
                intent.action = "FRIEND"
                myContext!!.sendBroadcast(intent)
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

    fun shareKakao() {
        val url = "market://details?id=donggolf.android"
        val imgBuilder = ContentObject.newBuilder("노마드 노트",
                Config.url + "/data/member/5c3cb2dc-c8a8-4351-9b29-16b9ac1f19c8",
                LinkObject.newBuilder().setWebUrl(url).setMobileWebUrl(url).build())
                .setDescrption("노마드 노트")
                .build()


        val builder = FeedTemplate.newBuilder(imgBuilder)
        builder.addButton(ButtonObject("노마드 노트", LinkObject.newBuilder()
                .setWebUrl(url)
                .setMobileWebUrl(url)
                .build()))

        val params = builder.build()

        KakaoLinkService.getInstance().sendDefault(myContext, params, object : ResponseCallback<KakaoLinkResponse>() {
            override fun onFailure(errorResult: ErrorResult) {
                Logger.e(errorResult.toString())
            }

            override fun onSuccess(result: KakaoLinkResponse) {

            }
        })


    }
    fun shareFacebook() {
        val content = ShareLinkContent.Builder()

                //링크의 콘텐츠 제목
                .setContentTitle("페이스북 공유 링크입니다.")

                //게시물에 표시될 썸네일 이미지의 URL
                .setImageUrl(Uri.parse("https://lh3.googleusercontent.com/hmVeH1KmKDy1ozUlrjtYMHpzSDrBv9NSbZ0DPLzR8HdBip9kx3wn_sXmHr3wepCHXA=rw"))

                //공유될 링크
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.devstories.nomadnote_android"))

                //게일반적으로 2~4개의 문장으로 구성된 콘텐츠 설명
                .setContentDescription("문장1, 문장2, 문장3, 문장4")
                .build()

        val shareDialog = ShareDialog(this)
        shareDialog.show(content, ShareDialog.Mode.FEED)   //AUTOMATIC, FEED, NATIVE, WEB 등이 있으며 이는 다이얼로그 형식을 말합니다.
    }
    fun shareInstagram() {
        //외부저장 권한 요청(안드로이드 6.0 이후 필수)
        onRequestPermission()

        if (permissionCheck) {
            val bm = BitmapFactory.decodeResource(resources, R.drawable.kakao_default_profile_image)
            val storage = Environment.getExternalStorageDirectory().absolutePath
            val fileName = ".png"

            val folderName = "/nomadnote/"
            val fullPath = storage + folderName
            val filePath: File

            try {
                filePath = File(fullPath)
                if (!filePath.isDirectory) {
                    filePath.mkdirs()
                }
                val fos = FileOutputStream(fullPath + fileName)
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }


            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            val uri = Uri.fromFile(File(fullPath, fileName))
            try {
//                share.putExtra(Intent.EXTRA_STREAM, uri)
                share.putExtra(Intent.EXTRA_TEXT, "텍스트는 지원하지 않음!")
                share.setPackage("com.instagram.android")
                startActivity(share)
            } catch (e: ActivityNotFoundException) {
                Log.d("에러",e.toString())
                Toast.makeText(myContext, "인스타그램이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Log.d("에러",e.toString())
                e.printStackTrace()
            }

        }
    }

    fun onRequestPermission() {
        val permissionReadStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionWriteStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionReadStorage == PackageManager.PERMISSION_DENIED || permissionWriteStorage == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE_CODE)
        } else {
            permissionCheck = true //이미 허용되어 있으므로 PASS
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE_CODE -> for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]
                if (permission == android.Manifest.permission.READ_EXTERNAL_STORAGE) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(myContext, "허용했으니 가능함", Toast.LENGTH_LONG).show()
                        permissionCheck = true
                    } else {
                        Toast.makeText(myContext, "허용하지 않으면 공유 못함 ㅋ", Toast.LENGTH_LONG).show()
                        permissionCheck = false
                    }
                }
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
