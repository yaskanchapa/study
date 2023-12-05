package com.kse.wmsv2.common.util;

import com.kse.wmsv2.oa_it_001.common.OAIT001MICConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StringUtil {

    static String replaceString = "[`$\\[\\]^_\\\\ｱ-ﾛﾜ-ﾝｧ-ｮｦｯ*#]";


    public static boolean isStringEmpty(String str){
        return str == null || str.isBlank();
    }

    public static String isStringNull(String str){
        return str == null ? "" : str;
    }

    public static int stringParseInt(String str){
        return str == null ? 0 : Integer.parseInt(str);
    }

    public static String fillSpace(String inputString, int length,boolean flgLR) {
        StringBuilder sb = new StringBuilder();
        inputString = isStringNull(inputString);
        inputString = inputString.replaceAll(replaceString," ");
        inputString = inputString.toUpperCase();
        if(inputString == null){
            while (sb.length() < length){
                sb.append(" ");
            }
            return sb.toString();
        } else if (inputString.length() == length) {
            return inputString;
        } else if(inputString.length() > length){
            return inputString.substring(0,length);
        }
        if(flgLR){
            sb.append(inputString);
            while (sb.length() < length) {
                sb.append(' ');
            }
        } else {
            while (sb.length() < length - inputString.length()) {
                sb.append(' ');
            }
            sb.append(inputString);
        }
//        String tmpStr = sb.toString().replaceAll(replaceString," ").replaceAll("\\*", " ");
        return sb.toString();
    }

    public static String changeReportData(String date){
        if(isStringEmpty(date)){
            return "";
        } else if (date.replaceAll("-","").length() >=8){
            String tmpStr = date.replaceAll("-","").substring(0,8);
            return tmpStr;
        } else {
            return "";
        }
    }

    public static String subString(String target, int leng){
        if(isStringEmpty(target)){
            return "";
        }
        leng = isStringNull(target).length() > leng ? leng : target.length();
        return isStringNull(target).substring(0,leng);
    }

}
