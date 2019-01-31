package com.devstories.nomadnote_android.actions

import com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams


object TimelineAction {

    // 타임라인 작성
    fun addtimeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/addtimeline", params, handler)
    }

    fun testaddtimeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/testaddtimeline", params, handler)
    }

    // 타임라인 가져오기
    fun my_timeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/my_timeline", params, handler)
    }
    //장소타임라인
    fun place_timeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/place_timeline", params, handler)
    }

    //나라별 타임라인
    fun country_timeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/country_timeline", params, handler)
    }

    // 타임라인 상세
    fun detail_timeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/detail_timeline", params, handler)
    }

    //스크랩
    fun set_scrap(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/set_scrap", params, handler)
    }

    //키워드,스크랩 검색
    fun search_keword(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/search_keword", params, handler)
    }

    //업데이트
    fun update_timeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/update_timeline", params, handler)
    }

    //삭제
    fun delete_timeline(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/delete_timeline", params, handler)
    }

    //잠금,잠금해제
    fun change_block(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/timeline/change_block", params, handler)
    }

}