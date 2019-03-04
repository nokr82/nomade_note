package com.devstories.nomadnote_android.actions

import  com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

object AdvertiseAction {

    fun adver_list(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/advertise/adver_list", params, handler)
    }

}