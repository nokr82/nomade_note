package com.devstories.nomadnote_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.base.RootActivity
import com.devstories.nomadnote_android.base.Utils
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.loopj.android.http.RequestParams
import kotlinx.android.synthetic.main.activity_write.*
import java.util.ArrayList

class WriteActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var menu_position = 1

    val SELECT_PICTURE = 1000

    var images_path: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        this.context = this
        progressDialog = ProgressDialog(context)

        click()

        val time = Utils.timeStr()
        var timesplit = time.split(":")
        timeET.setText(timesplit.get(0))
        minuteET.setText(timesplit.get(1))
        var edittime = ""
        if (timesplit.get(0).toInt() >= 12){
            edittime += " PM " + (timesplit.get(0).toInt() - 12).toString() + ":" + timesplit.get(1)
        } else {
            edittime += " AM " + timesplit.get(0)+ ":" + timesplit.get(1)
        }
        pulldateTV.setText(Utils.todayStr() + "" + edittime)

    }
    fun click(){
        titleBackLL.setOnClickListener {
            finish()
        }

        //힐링
        healingTV.setOnClickListener {
            menuSetImage()
            healingTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            healingTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 1
        }

        //핫플레이스
        hotplaceTV.setOnClickListener {
            menuSetImage()
            hotplaceTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            hotplaceTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 2
        }

        //문학 스타일
        literatureTV.setOnClickListener {
            menuSetImage()
            literatureTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            literatureTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 3
        }

        //역사 스타일
        historyTV.setOnClickListener {
            menuSetImage()
            historyTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            historyTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 4
        }

        //박물관 스타일
        museumTV.setOnClickListener {
            menuSetImage()
            museumTV.setBackgroundResource(R.drawable.background_border_radius7_000000)
            museumTV.setTextColor(Color.parseColor("#ffffff"))
            menu_position = 5
        }

        addcontentLL.setOnClickListener {
            addContent()
        }

        //이미지추가
        addpictureLL.setOnClickListener {
            permission()
        }


    }

    fun addContent(){
        val location = locationET.text.toString()
        if (location == "" || location == null){
            Toast.makeText(context, "지역은 필수입력 입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val time = timeET.text.toString()
        val minute = minuteET.text.toString()
        if (time == "" || time == null || minute == "" || minute == null){
            Toast.makeText(context, "시간은 필수입력 입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val content = contentET.text.toString()
        if (content == "" || content == null){
            Toast.makeText(context, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val params = RequestParams()
//        params.put("member_id",)
//        params.put("place_name",)
//        params.put("duration",)
//        params.put("cost",)
//        params.put("place_id",)
//        params.put("country_id",)






    }

    fun menuSetImage(){
        healingTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        healingTV.setTextColor(Color.parseColor("#878787"))
        hotplaceTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        hotplaceTV.setTextColor(Color.parseColor("#878787"))
        literatureTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        literatureTV.setTextColor(Color.parseColor("#878787"))
        historyTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        historyTV.setTextColor(Color.parseColor("#878787"))
        museumTV.setBackgroundResource(R.drawable.background_border_radius8_000000)
        museumTV.setTextColor(Color.parseColor("#878787"))
    }

    private fun permission() {

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {

                var intent = Intent(context, FindPictureGridActivity::class.java)
                startActivityForResult(intent, SELECT_PICTURE)

            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {

            }

        }

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                SELECT_PICTURE -> {

                    var item = data?.getStringArrayExtra("images")//photoPath
                    //var name = data?.getStringArrayExtra("displayname")

                    for (i in 0..(item!!.size - 1)) {
                        val str = item[i]

                        images_path!!.add(str)

                        var add_file = Utils.getImage(context.contentResolver, str)

                        var imageView = View.inflate(context, R.layout.item_addgoods, null)
                        val imageIV: ImageView = imageView.findViewById(R.id.addedImgIV)
                        val delIV: ImageView = imageView.findViewById(R.id.delIV)
                        imageIV.setImageBitmap(add_file)
                        addPicturesLL?.addView(imageView)

                        delIV.setOnClickListener {
                            if (addPicturesLL != null) {
                                addPicturesLL!!.removeView(imageView)
                            }
                        }

                    }
                }
            }
        }


    }

}
