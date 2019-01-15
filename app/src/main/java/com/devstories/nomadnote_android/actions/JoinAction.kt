package com.devstories.nomadnote_android.actions

import com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams


object JoinAction {

    // 핸드폰 인증
    fun send_sms(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/join/sms_code", params, handler)
    }

    // 회원가입
    fun join(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/join", params, handler)
    }

    // 닉네임 체크
    fun check_email(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/join/check_email", params, handler)
    }

}