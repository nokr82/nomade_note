package com.devstories.nomadnote_android.actions

import com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams


object CertificationController {

    // 인증하기
    fun add_certification(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/certification/add_certification", params, handler)
    }


}