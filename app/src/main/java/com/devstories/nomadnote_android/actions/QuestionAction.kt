package com.devstories.nomadnote_android.actions

import com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

object QuestionAction {

    // question
    fun question(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/question/question", params, handler)
    }
}