package com.kse.wmsv2.oc_cs_006.util;

import net.arnx.jsonic.JSON;

import java.io.*;
import java.util.Map;

public class FileUtil {
    public static boolean folderCheck(String folderName){
        boolean result = true;
        File folder = new File(folderName);
        if(!folder.exists()){
            try {
                folder.mkdir();
            } catch (Exception e){
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }
}
