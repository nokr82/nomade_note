package com.devstories.nomadnote_android.actions

import com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams


object PlaceAction {

    // 핸드폰 인증
    fun load_place(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/place/load_place", params, handler)
    }


}