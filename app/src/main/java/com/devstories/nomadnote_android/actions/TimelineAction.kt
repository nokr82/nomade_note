package com.devstories.nomadnote_android.actions

import com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams


object TimelineAction {

    // 타임라인 작성
    fun addtimeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/addtimeline", params, handler)
    }

    // 타임라인 가져오기
    fun my_timeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/my_timeline", params, handler)
    }

    // 타임라인 상세
    fun detail_timeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/detail_timeline", params, handler)
    }


}