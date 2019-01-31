package com.devstories.nomadnote_android.actions

import com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams


object LoginAction {

    // 핸드폰 인증
    fun login(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/login/login", params, handler)
    }

    fun validUser(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/login/valid_user", params, handler)
    }


}