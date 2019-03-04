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
    fun add_friend(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/add_friend", params, handler)
    }
    fun my_friend(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/my_friend", params, handler)
    }
    fun friend_del(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/del_friend", params, handler)
    }
    fun search_member(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/search_member", params, handler)
    }

    fun regist_token(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/regist_token", params, handler)
    }

    fun check_phone(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/check_phone", params, handler)
    }

    fun request_friends(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/request_friends", params, handler)
    }

    fun confirm_friend(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/member/confirm_friend", params, handler)
    }
}