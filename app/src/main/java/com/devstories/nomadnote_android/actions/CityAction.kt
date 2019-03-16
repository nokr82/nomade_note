package com.devstories.nomadnote_android.actions

import  com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

object CityAction {

    fun findCity(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/city/find_city", params, handler)
    }
}