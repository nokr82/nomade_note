package com.devstories.nomadnote_android.actions

import com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams


object MemberAction {

    // 닉네임 체크
    fun update_info(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/update_info", params, handler)
    }
    fun my_info(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/my_info", params, handler)
    }
    fun search_member(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/search_member", params, handler)
    }

    fun regist_token(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/regist_token", params, handler)
    }
}