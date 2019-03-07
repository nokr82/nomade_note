package com.devstories.nomadnote_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import com.devstories.nomadnote_android.R
import com.devstories.nomadnote_android.actions.MemberAction
import com.devstories.nomadnote_android.base.PrefUtils
import com.devstories.nomadnote_android.base.Utils
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fra_friend_phoone.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject



class Friend_phone_Fragment : Fragment()  {
    lateinit var myContext: Context
    private var progressDialog: ProgressDialog? = null


     val m_Codes = arrayOf( "+82","+376", "+971", "+93", "+355", "+374", "+599", "+244", "+672", "+54", "+43", "+61", "+297", "+994", "+387", "+880", "+32", "+226", "+359", "+973", "+257", "+229", "+590", "+673", "+591", "+55", "+975", "+267", "+375", "+501", "+1", "+61", "+243", "+236", "+242", "+41", "+225", "+682", "+56", "+237", "+86", "+57", "+506", "+53", "+238", "+61", "+357", "+420", "+49", "+253", "+45", "+213", "+593", "+372", "+20", "+291", "+34", "+251", "+358", "+679", "+500", "+691", "+298", "+33", "+241", "+44", "+995", "+233", "+350", "+299", "+220", "+224", "+240", "+30", "+502", "+245", "+592", "+852", "+504", "+385", "+509", "+36", "+62", "+353", "+972", "+44", "+91", "+964", "+98", "+39", "+962", "+81", "+254", "+996", "+855", "+686", "+269", "+850", "+965", "+7", "+856", "+961", "+423", "+94", "+231", "+266", "+370", "+352", "+371", "+218", "+212", "+377", "+373", "+382", "+261", "+692", "+389", "+223", "+95", "+976", "+853", "+222", "+356", "+230", "+960", "+265", "+52", "+60", "+258", "+264", "+687", "+227", "+234", "+505", "+31", "+47", "+977", "+674", "+683", "+64", "+968", "+507", "+51", "+689", "+675", "+63", "+92", "+48", "+508", "+870", "+1", "+351", "+680", "+595", "+974", "+40", "+381", "+7", "+250", "+966", "+677", "+248", "+249", "+46", "+65", "+290", "+386", "+421", "+232", "+378", "+221", "+252", "+597", "+239", "+503", "+963", "+268", "+235", "+228", "+66", "+992", "+690", "+670", "+993", "+216", "+676", "+90", "+688", "+886", "+255", "+380", "+256", "+1", "+598", "+998", "+39", "+58", "+84", "+678", "+681", "+685", "+967", "+262", "+27", "+260", "+263")
    var option_phone = arrayOf("+82")
    lateinit var adapter: ArrayAdapter<String>
    var searchtype = "phone"
    var phone = ""
    var name = ""
    var pem_id = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
//        progressDialog = ProgressDialog(myContext)
        progressDialog = ProgressDialog(myContext, com.devstories.nomadnote_android.R.style.CustomProgressBar)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        return inflater.inflate(com.devstories.nomadnote_android.R.layout.fra_friend_phoone, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        searchIV.setOnClickListener {
            loadInfo()
        }

       /* search2IV.setOnClickListener {
            loadInfo()
        }*/



        adapter = ArrayAdapter(myContext, com.devstories.nomadnote_android.R.layout.spiner_item, m_Codes)
        phoneSP.adapter = adapter


        addTV.setOnClickListener {
            add_friend()
        }
        nameET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadInfo()
                true
            } else {
                false
            }
        }
        phoneET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadInfo()
                true
            } else {
                false
            }
        }

    }
    //친구찾기
    fun loadInfo() {
        name = Utils.getString(nameET)
        phone = Utils.getString(phoneET)

        Utils.hideKeyboard(myContext)
        if (name.equals("")){
            Toast.makeText(myContext,"이름을 입력해주세요.",Toast.LENGTH_SHORT).show()
            return
        }
        if (phone.equals("")){
            Toast.makeText(myContext,"연락처를 입력해주세요.",Toast.LENGTH_SHORT).show()
            return
        }


        val params = RequestParams()
        params.put("search_type",searchtype)
        params.put("phone",phone)
        params.put("name",name)
        params.put("member_id",PrefUtils.getIntPreference(myContext, "member_id"))

        MemberAction.search_member(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                if(activity == null || !isAdded) {
                    return
                }

                try {
                    val result = response!!.getString("result")

                    println("response:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::$response")


                    if ("ok" == result) {

                        if (Utils.getString(response, "friend_yn") == "Y") {
                            addTV.visibility = View.GONE
                        } else {
                            addTV.visibility = View.VISIBLE
                        }

//                        addTV.visibility = View.VISIBLE
                        startLL.visibility = View.GONE
                        friendLL.visibility = View.VISIBLE
                        var member = response.getJSONObject("member")
                        var name =  Utils.getString(member, "name")
                        var  age =  Utils.getInt(member, "age")
                        var gender =  Utils.getString(member, "gender")
                        pem_id = Utils.getString(member,"id")

                        if (gender == "M"){
                            genderIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.man)
                        }else if (gender =="F"){
                            genderIV.setImageResource(com.devstories.nomadnote_android.R.mipmap.famal)
                        }
                        nameTV.setText(name)
                        statTV.setText(age.toString()+"세")


                    } else {
                        Toast.makeText(myContext, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(myContext, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }


            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    //친구추가

    fun add_friend() {
        val params = RequestParams()
        params.put("pem_id", pem_id)
        params.put("member_id", PrefUtils.getIntPreference(myContext, "member_id") )

        MemberAction.add_friend(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        addTV.visibility = View.GONE
                        Toast.makeText(myContext, "추가하였습니다.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(myContext, "이미 친구입니다.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(myContext, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    throwable: Throwable,
                    errorResponse: JSONObject?
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    throwable: Throwable,
                    errorResponse: JSONArray?
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
