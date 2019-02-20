package com.devstories.nomadnote_android.sms;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by theclub on 11/30/14.
 */
public class MmsBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";

    Handler handler = new Handler();

    @Override
    public void onReceive(final Context context, Intent intent) {

        System.out.println(intent);

        String action = intent.getAction();
        String type = intent.getType();

        System.out.println("action : " + action + ", type : " + type);

        if(action.equals(ACTION_MMS_RECEIVED) && type.equals(MMS_DATA_TYPE)) {

            final Bundle bundle = intent.getExtras();

            System.out.println("bundle : " + bundle);

            if (bundle != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parseMMS(context, bundle);
                    }
                }, 6000);
            }

        }
    }

    private void parseMMS(Context context, Bundle bundle) {
        ContentResolver contentResolver = context.getContentResolver();
        final String[] projection = new String[]{"_id"};
        //Uri uri = Uri.parse("content://mms");
        Uri uri = Uri.parse("content://mms");
        Cursor cursor = contentResolver.query(uri, projection, null, null, "_id desc limit 1");

        if (cursor.getCount() == 0) {
            cursor.close();
            return;
        }

        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndex("_id"));
        cursor.close();

        // String number = parseNumber(id);
        String messageContents = parseMessage(context, id);

        new SmsBroadcastReceiver.SmsRegisterOperation(context, messageContents).execute("");

    }

    private String parseMessage(Context context, String $id) {
        String result = null;

        // 조회에 조건을 넣게되면 가장 마지막 한두개의 mms를 가져오지 않는다.
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://mms/part"), new String[]{"mid", "_id", "ct", "_data", "text"}, null, null, null);

//        // System.out.println.println("|mms 메시지 갯수 : " + cursor.getCount() + "|");
        if (cursor.getCount() == 0) {
            result = "";
        } else {
            if(cursor.moveToFirst()){
                do{
                    String mid = cursor.getString(cursor.getColumnIndex("mid"));
                    if ($id.equals(mid)) {
                        String partId = cursor.getString(cursor.getColumnIndex("_id"));
                        String type = cursor.getString(cursor.getColumnIndex("ct"));
                        if ("text/plain".equals(type)) {
                            String data = cursor.getString(cursor.getColumnIndex("_data"));

                            if (TextUtils.isEmpty(data))
                                result = cursor.getString(cursor.getColumnIndex("text"));
                            else
                                result = parseMessageWithPartId(context, partId);
                        }
                    }
                } while(cursor.moveToNext());
            }
        }

        cursor.close();

        return result;
    }


    private String parseMessageWithPartId(Context context, String $id) {
        Uri partURI = Uri.parse("content://mms/part/" + $id);
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = context.getContentResolver().openInputStream(partURI);
            if (is != null) {
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                String temp = reader.readLine();
                while (!TextUtils.isEmpty(temp)) {
                    sb.append(temp);
                    temp = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

}
