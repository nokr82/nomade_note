package com.devstories.nomadnote_android.sms;

import com.devstories.nomadnote_android.base.Config;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

public class SMSAction {

    // 임대인 월세 입금 문자 등록
    public static void registSms(int member_id, String messageContents, JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();

        params.put("member_id", member_id);
        params.put("messageContents", messageContents);

        SyncHttpClient client = new SyncHttpClient();

        String url = "/api/sms/regist_sms";

        System.out.println(Config.url + url + "?" + params);

        client.post(Config.url + url, params, handler);
    }

}
