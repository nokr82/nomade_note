package com.devstories.nomadnote_android.actions

import com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams


object QnasAction {

    // 질문 불러오기
    fun get_qnas(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/qnas/get_qnas", params, handler)
    }

    // 질문 내용
    fun detail_qnas(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/qnas/detail_qnas", params, handler)
    }

    // 답변하기
    fun anwer(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/qnas/anwer", params, handler)
    }


}