package com.devstories.nomadnote_android.sms;

import com.devstories.nomadnote_android.base.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hooni on 2017. 9. 1..
 */

public final class SmsParserUtils {

    public static Map<String, String> getBody(String str){
        Map<String, String> body = new HashMap<String, String>();

        List<String> contents = new ArrayList<String>(Arrays.asList(str.split("\\n")));

        if(contents.size() > 0 && contents.get(0).toLowerCase().contains("web발신")){
            contents.remove(0);
        }


        //KDB산업은행 1
        if(str.contains("(KDB)") && str.contains("입금")){
            body = bankSmsParser(contents, 4, 3, 5, 1);
            body.put("bank", "KDB산업은행");
        }

        // KB국민은행 2
        else if(str.contains("[KB]") && str.contains("입금")){
            body = bankSmsParser(contents, 3, 5, 1, 2);
            body.put("bank", "KB국민은행");
        }

        // KEB하나 3
        else if(str.contains("KEB하나") && str.contains("입금")){
            body = bankSmsParser(contents, 4, 3, 1, 3);
            body.put("bank", "KEB하나은행");
        }

        // 하나 4
        else if(str.contains("하나") && str.contains("입금")){
            body = bankSmsParser(contents, 4, 3, 1, 4);
            body.put("bank", "KEB하나은행");
        }

        // 농협 5
        else if(str.contains("농협") && str.contains("입금")){
            body = bankSmsParser(contents, 3, 1, 2, 5);
            body.put("bank", "농협");
        }

        // 신한 6
        else if(str.contains("신한") && str.contains("입금")){
            body = bankSmsParser(contents, 5, 3, 1, 6);
            body.put("bank", "신한은행");
        }

        // 우리 7
        else if(str.contains("우리") && str.contains("입금")){
            body = bankSmsParser(contents, 4, 3, 1, 7);
            body.put("bank", "우리은행");
        }

        // 새마을금고 8
        else if(str.contains("<새마을금고>") && str.contains("입금")){
            body = bankSmsParser(contents, 2, 3, 4, 8);
            body.put("bank", "새마을금고");
        }

        // 신협 9
        else if(str.contains("신협") && str.contains("입금")){
            body = bankSmsParser(contents, 2, 2, 1, 9);
            body.put("bank", "신협");
        }

        // 시티 10
        else if((str.toLowerCase().contains("citi") || str.contains("시티")) && str.contains("입금")){
            body = bankSmsParser(contents, 5, 4, 2, 10);
            body.put("bank", "씨티은행");
        }



        // IBK기업은행 11
        else if(str.contains("IBK기업은행") && str.contains("입금")){

        }

        // SC제일 12
        else if(str.contains("SC제일") && str.contains("입금")){

        }

        // 대구 13
        else if(str.contains("대구") && str.contains("입금")){

        }

        // 부산 14
        else if(str.contains("부산") && str.contains("입금")){

        }

        // 광주 15
        else if(str.contains("광주") && str.contains("입금")){

        }

        // 제주 16
        else if(str.contains("제주") && str.contains("입금")){

        }

        // 전북 17
        else if(str.contains("전북") && str.contains("입금")){

        }

        // 경남 18
        else if(str.contains("경남") && str.contains("입금")){

        }

        body.put("message", str);

        return body;
    }

    public static Map<String, String> getMMSBody(String str){
        Map<String, String> body = new HashMap<String, String>();

        List<String> contents = new ArrayList<String>(Arrays.asList(str.split("\\n")));

        if(contents.size() > 0 && contents.get(0).toLowerCase().contains("web발신")){
            contents.remove(0);
        }

        // 전기요금
        if(str.contains("전기요금") || str.contains("전기 요금")){
            body = electricParser(contents);
        }

        // 가스요금
        else if(str.contains("가스요금") || str.contains("가스 요금")){
            body = gasParser(contents);
        }

        body.put("message", str);

        return body;
    }



    /**
     * @param contents sms 내용
     * @param idx1 보낸사람 이름 idx
     * @param idx2 금액 idx
     * @param division 은행 구분
     */
    private static Map<String, String> bankSmsParser(List<String> contents, int idx1, int idx2, int idx3, int division){
        Map<String, String> body = new HashMap<String, String>();

        String name = contents.get(idx1-1).trim();
        String money = contents.get(idx2-1).trim();
        String deposit = contents.get(idx3-1).trim();

        switch (division) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 6:
            case 7:
                money = money.replaceAll("[^0-9]", "").trim();
                break;
            case 5:
                name = name.replaceAll("[^\uAC00-\uD7AF\u1100-\u11FF\u3130-\u318F]", "").trim();
                money = money.replaceAll("[^0-9]", "").trim();
                break;
            case 8:
                String regex = money.substring(money.indexOf("잔액"), money.lastIndexOf("원"));
                money = money.replaceAll(regex, "");
                money = money.replaceAll("[^0-9]", "").trim();
                break;
            case 9:
                name = money.substring(money.indexOf("원")+1, money.lastIndexOf(" ")).trim();
                money = money.substring(money.indexOf("입금")+2, money.indexOf("원")).trim();
                money = money.replaceAll("[^0-9]", "").trim();
                break;
            case 10:
                name = name.replaceAll("적요", "").trim();
                money = money.replaceAll("입금", "").trim();
                break;
        }

        switch (division) {
            case 1:
                deposit = DateUtils.getToday("yyyy-MM-dd") + " " + deposit.substring(deposit.length()-8, deposit.length()).trim();
                break;
            case 2:
                deposit = deposit.replaceAll("\\[KB\\]", "").trim();
                deposit = DateUtils.getToday("yyyy") + "-" + DateUtils.stringTostring(deposit, "MM/dd HH:mm", "MM-dd HH:mm");
                break;
            case 3:
                deposit = deposit.replaceAll("KEB하나,", "").trim();
                deposit = DateUtils.getToday("yyyy") + "-" + DateUtils.stringTostring(deposit, "MM/dd,HH:mm", "MM-dd HH:mm");
                break;
            case 4:
                deposit = deposit.replaceAll("하나,", "").trim();
                deposit = DateUtils.getToday("yyyy") + "-" + DateUtils.stringTostring(deposit, "MM/dd,HH:mm", "MM-dd HH:mm");
                break;
            case 5:
                deposit = DateUtils.getToday("yyyy") + "-" + DateUtils.stringTostring(deposit, "MM/dd HH:mm", "MM-dd HH:mm");
                break;
            case 6:
                deposit = deposit.replaceAll("신한", "").trim();
                deposit = DateUtils.getToday("yyyy") + "-" + DateUtils.stringTostring(deposit, "MM/dd HH:mm", "MM-dd HH:mm");
                break;
            case 7:
                deposit = deposit.replaceAll("우리", "").trim();
                deposit = DateUtils.getToday("yyyy") + "-" + DateUtils.stringTostring(deposit, "MM/dd HH:mm", "MM-dd HH:mm");
                break;
            case 8:
                deposit = DateUtils.getToday("yyyy") + "-" + DateUtils.stringTostring(deposit, "MM/dd HH:mm", "MM-dd HH:mm");
                break;
            case 9:
                deposit += " " + contents.get(idx3).trim();
                deposit = deposit.replaceAll("신협", "").trim();
                deposit = deposit.substring(0, deposit.indexOf("입금")).trim();
                deposit = deposit.substring(deposit.length()-15, deposit.length()).trim();
                deposit = DateUtils.getToday("yyyy") + "-" + DateUtils.stringTostring(deposit, "MM/dd HH:mm", "MM-dd HH:mm");
                break;
            case 10:
                deposit = DateUtils.getToday("yyyy") + "-" + DateUtils.stringTostring(deposit, "MM/dd HH:mm", "MM-dd HH:mm");
                break;
        }

        body.put("name", name);
        body.put("money", money);
        body.put("deposit", deposit);
        body.put("type", "monthly_rent");

        return body;
    }

    private static Map<String, String> electricParser(List<String> contents){
        Map<String, String> body = new HashMap<String, String>();
        String applicable_dt = null;

        for(String s : contents){
            if(s.contains("사용기간")){
                String txt = s.replaceAll("사용기간", "");
                String[] dt = txt.split("~");
                if(dt.length > 1){
                    applicable_dt = dt[1].trim();
                }
                break;
            }
        }
        if(applicable_dt != null) {
            // body.put("applicable_y", DateUtils.stringTostring(applicable_dt, "yy.MM.dd", "yyyy"));
            // body.put("applicable_m", DateUtils.stringTostring(applicable_dt, "yy.MM.dd", "MM"));
            body.put("applicable_y", DateUtils.getToday("yyyy"));
            body.put("applicable_m", DateUtils.stringTostring(applicable_dt, "MM.dd", "MM"));
            body.put("electric_yn", "Y");
            body.put("isRegist", "Y");
        } else {
            body.put("applicable_y", "");
            body.put("applicable_m", "");
            body.put("electric_yn", "Y");
            body.put("isRegist", "N");
        }
        body.put("type", "electric");

        return body;
    }

    private static Map<String, String> gasParser(List<String> contents){
        Map<String, String> body = new HashMap<String, String>();
        String applicable_dt = null;

        for(String s : contents){
            if(s.contains("사용기간")){
                String txt = s.replaceAll("사용기간", "");
                String[] dt = txt.split("~");
                if(dt.length > 1){
                    applicable_dt = dt[1].trim();
                }
                break;
            }
        }
        if(applicable_dt != null) {
            body.put("applicable_y", DateUtils.stringTostring(applicable_dt, "yyyy.MM.dd", "yyyy"));
            body.put("applicable_m", DateUtils.stringTostring(applicable_dt, "yyyy.MM.dd", "MM"));
            body.put("isRegist", "Y");
        } else {
            body.put("applicable_y", "");
            body.put("applicable_m", "");
            body.put("isRegist", "N");
        }
        body.put("type", "gas");

        return body;
    }
}
