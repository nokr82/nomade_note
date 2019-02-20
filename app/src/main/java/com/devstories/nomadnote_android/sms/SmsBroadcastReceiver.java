package com.devstories.nomadnote_android.sms;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsMessage;

import com.devstories.nomadnote_android.base.PrefUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by theclub on 11/30/14.
 */
public class SmsBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    Handler handler = new Handler();

    @Override
    public void onReceive(final Context context, Intent intent) {

        System.out.println("intent : " + intent);

        if(intent.getAction().equals(ACTION)) {

            final Bundle bundle = intent.getExtras();

            System.out.println("bundle : " + bundle);

            if (bundle != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parseSMS(context, bundle);
                    }
                }, 6000);
            }

        }
    }

    private void parseSMS(Context context, Bundle bundle) {

        String messageSender = "";
        String messageContents = "";
        String accessNumber = "";

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            // msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // messageSender = msgs[i].getOriginatingAddress();
                messageContents = msg.getMessageBody().toString();
                // accessNumber = getAccessNumber(messageContents);

                Map<String, String> sms = SmsParserUtils.getBody(messageContents);

                new SmsRegisterOperation(context, messageContents).execute("");

            }
        }
    }

    private String getAccessNumber(String messageContents) {
        Pattern accessNumPattern = Pattern.compile("[0-9]{6}");
        Matcher macher = accessNumPattern.matcher(messageContents);
        String accessNumber = "";
        if (macher.find()) {
            accessNumber = macher.group(0);
        }

        return accessNumber;
    }


    public static class SmsRegisterOperation extends AsyncTask<String, Void, String> {

        private WeakReference contextReference = null;
        private WeakReference contentsReference = null;

        public SmsRegisterOperation(Context context, String messageContents) {
            this.contextReference = new WeakReference(context);
            this.contentsReference = new WeakReference(messageContents);
        }

        @Override
        protected String doInBackground(String... params) {
            String msg = "";

            registerSms();

            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {

        }

        private void registerSms() {
            int member_id = PrefUtils.getIntPreference((Context) contextReference.get() , "member_id");

            SMSAction.registSms(member_id, (String) contentsReference.get(), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    System.out.println(errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);

                    System.out.println(responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }

    }

}
