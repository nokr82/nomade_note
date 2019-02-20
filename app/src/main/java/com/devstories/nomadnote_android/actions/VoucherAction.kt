package com.devstories.nomadnote_android.actions

import com.devstories.nomadnote_android.base.HttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

object VoucherAction {

    fun use_voucher(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/api/voucher/use_voucher", params, handler)
    }

}