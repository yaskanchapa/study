package com.kse.wmsv2.oa_it_001.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.dao.OAIT001DetailDataDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDAReportDao;
import com.kse.wmsv2.oa_it_001.dto.OAIT001SearchDto;
import com.kse.wmsv2.oa_it_001.service.OAIT001IDAService;
import org.apache.commons.compress.archivers.zip.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.javassist.compiler.SymbolTable;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.DateFormatter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestMain {





    public static void main(String[] args) throws Exception {
        String path = "C:\\Users\\m-ma\\Documents\\2022\\filechanger\\";

        File dir = new File(path);

        File[] files = dir.listFiles();

        for(File file:files){
            String fileName = file.getName().substring(0,file.getName().indexOf("."));
            if(fileName.length() >20)  fileName = fileName.substring(0,20);
            File newFile = new File(path + fileName);
            file.renameTo(newFile);
        }


//        String testList = "12345678901234567890123456789012345678901234567890";
//        System.out.println(testList.substring(0,40));
//        System.out.println(testList[7]);



//        String replaceString = "[,`$\\[\\]^_\\\\ｱ-ﾛﾜ-ﾝｧ-ｮｦｯ*#]";
//        String target = "1 : `,2 : $,3 : [,4 : ],5 : ^,6 : _,7 : \\\\,8 : ｱ,9 : ｲ,10 : ｳ,11 : ｴ,12 : ｵ,13 : ｶ,14 : ｷ,15 : ｸ,16 : ｹ,17 : ｺ,18 : ｻ,19 : ｼ,20 : ｽ,21 : ｾ,22 : ｿ,23 : ﾀ,24 : ﾁ,25 : ﾂ,26 : ﾃ,27 : ﾄ,28 : ﾅ,29 : ﾆ,30 : ﾇ,31 : ﾈ,32 : ﾉ,33 : ﾊ,34 : ﾋ,35 : ﾌ,36 : ﾍ,37 : ﾎ,38 : ﾏ,39 : ﾐ,40 : ﾑ,41 : ﾒ,42 : ﾓ,43 : ﾔ,44 : ﾕ,45 : ﾖ,46 : ﾗ,47 : ﾘ,48 : ﾙ,49 : ﾚ,50 : ﾛ,51 : ﾜ,52 : ｦ,53 : ﾝ,54 : ｧ,55 : ｨ,56 : ｩ,57 : ｪ,58 : ｫ,59 : ｬ,60 : ｭ,61 : ｮ,62 : ｯ,63 : *,64 : #";
//        String target2 = "A`B$C[D]E^F_G\\\\HｱIｲJｳKｴLｵMｶNｷOｸPｹQｺRｻSｼTｽUｾVｿWﾀHﾁYﾂZﾃAﾄBﾅCﾆDﾇEﾈFﾉGﾊHﾋIﾌJﾍKﾎLﾏMﾐNﾑOﾒPﾓQﾔRﾕSﾖTﾗUﾘVﾙWﾚXﾛYﾜZｦAﾝBｧCｨDｩEｪFｫGｬHｭIｮJｯK*L#M";
//        System.out.println(target.replaceAll(replaceString," "));
//        System.out.println(target2.replaceAll(replaceString," "));
//        List<String> tmpList = new ArrayList<>();
//        tmpList.add("KNIT SHIRT(JPY400*30P");
//        tmpList.add("KNIT SHIRT(JPY400*12P");
//        tmpList.add("KNIT SHIRT(JPY400*3PC");
//        tmpList.add("KNIT SOCKS(JPY451*1PC");
//        tmpList.add("KNIT SOCKS(JPY1,356*1");
//        tmpList.add("KNIT SHIRTS(JPY747*1P");
//        tmpList.add("KNIT SHIRTS(JPY1,082*");
//
//        for(int i = 0; i < tmpList.size(); i++){
//            System.out.println("========================================");
//            System.out.println(tmpList.get(i).replaceAll("[*]"," "));
//            System.out.println("========================================");
//        }



//        String dateNow = com.kse.wmsv2.common.util.DateUtil.getNow("yyyy-MM-dd 00:00:00.000");
//        System.out.println(dateNow);
//        String defaultStr = "[[\"001\",\"\"],[\"002\",\"\"],[\"003\",\"00096619604528\"],[\"004\",\"\"],[\"005\",\"\"],[\"006\",\"Z\"],[\"007\",\"\"],[\"008\",\"2\"],[\"009\",\"1F\"],[\"010\",\"S\"],[\"011\",\"C\"],[\"012\",\"S\"],[\"013\",\"\"],[\"014\",\"B\"],[\"015\",\"CIF\"],[\"016\",\"\"],[\"017\",\"1F\"],[\"018\",\"1FWM5\"],[\"019\",\"HND\"],[\"020\",\"D\"],[\"021\",\"\"],[\"022\",\"\"],[\"023\",\"\"],[\"024\",\"4BKKJ\"],[\"025\",\"KGM\"],[\"026\",\"\"],[\"027\",\"0\"],[\"028\",\"\"],[\"029\",\"\"],[\"030\",\"R\"],[\"031\",\"R\"],[\"032\",\"\"],[\"033\",\"F4\"],[\"034\",\"\"],[\"035\",\"\"],[\"036\",\"\"],[\"037\",\"\"],[\"038\",\"1\"],[\"039\",\"HND\"],[\"040\",\"KKJ\"],[\"041\",\"4BKKJ\"],[\"042\",\"4BKKJ\"],[\"043\",\"HND\"],[\"044\",\"1FWM5\"],[\"045\",\"SPT,\"]]";
//        String[] tmpStr = defaultStr.replaceAll(",\\[","").replaceAll("\\[","").replaceAll("\"","").split("]");
//
//        Map<String,String> valueMap = new HashMap<>();
//        for(int i = 0; i < tmpStr.length; i++){
//            System.out.println(tmpStr[i].substring(0,3));
//            System.out.println(tmpStr[i].substring(4));
//            String[] tmp = tmpStr[i].split(",");
//            if(tmp.length > 1){
//                valueMap.put(tmp[0],tmp[1]);
//            } else {
//                valueMap.put(tmp[0],"");
//            }
//        }
//        System.out.println(valueMap.toString());


//        String folderPath = "C:\\Users\\m-ma\\Documents\\2022\\TEST\\MEC\\MEC0228_132341_SEARCH004";
//        File zipFolder = new File(folderPath);
//        File[] fileList = zipFolder.listFiles();
//        List<File> zipFileList = new ArrayList<>();
//
//        for(int i = 0 ; i < fileList.length; i++){
//            zipFileList.add(fileList[i]);
//        }
//
//        Path tempDir = Files.createTempDirectory("tempDir");
//        Path zipFilePath = Paths.get(tempDir.toString(),"test2.zip");
//        File zipFile = new File(zipFilePath.toString());
//        byte[] buf = new byte[4096];
//
//        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));){
//            for(File file : zipFileList){
//                try(FileInputStream in = new FileInputStream(file);){
//                    ZipEntry ze = new ZipEntry(file.getName());
//                    out.putNextEntry(ze);
//                    int len;
//                    while((len = in.read(buf)) > 0 ){
//                        out.write(buf,0,len);
//                    }
//                    out.closeEntry();
//                }
//
//            }
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        byte[] zipFileContent = Files.readAllBytes(zipFilePath);
//        FileUtils.deleteDirectory(tempDir.toFile());
//        for(byte by : zipFileContent){
//            System.out.println(by);
//        }


//        String path = "C:\Users\m-ma\Documents\2022\TEST\MEC\MEC0228_132341_SEARCH003";
//
//        String[] list = path.split("\\\\");
//
//        System.out.println(list[list.length-1]);



//        String dateNow = com.kse.wmsv2.common.util.DateUtil.getNow("yyyy-MM-dd 00:00:00");
//
//        System.out.println(dateNow);

//        double test1 = 0.8;
//        double value = 20000;
//        double vlaue = Double.parseDouble("27000");
//        BigDecimal bd1 = BigDecimal.valueOf(test1);
//        BigDecimal bd2 = BigDecimal.valueOf(value);
//        BigDecimal bd3 = BigDecimal.valueOf(vlaue);
//
//
//        System.out.println(bd1.multiply(bd3));
//        System.out.println(bd1.multiply(bd3).compareTo(bd2));





//
//        String test = "012345678901234";
//
//        System.out.println(test.substring(0,9));




//        String testStr = "[{\"nameCd\":\"001\",\"name\":\"申告予定年月日\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"REPORTPLANINGDATA\"},{\"nameCd\":\"002\",\"name\":\"BP申請事由\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"BPAPPLICATIONCD\"},{\"nameCd\":\"003\",\"name\":\"口座番号\",\"characterItem\":\"00096619604528\",\"tableName\":\"DATA\",\"columnName\":\"ACCOUNTNO\"},{\"nameCd\":\"004\",\"name\":\"担保登録番号1\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"SECURITYREGNO_1\"},{\"nameCd\":\"005\",\"name\":\"担保登録番号2\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"SECURITYREGNO_2\"},{\"nameCd\":\"006\",\"name\":\"申告条件（MIC・IDC）\",\"characterItem\":\"Z\",\"tableName\":\"DATA\",\"columnName\":\"REPORTCONDITION\"},{\"nameCd\":\"007\",\"name\":\"申告先種別\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"REPORTKINDCD_2\"},{\"nameCd\":\"008\",\"name\":\"識別符号\",\"characterItem\":\"1\",\"tableName\":\"DATA\",\"columnName\":\"DISCERNMENTMARK\"},{\"nameCd\":\"009\",\"name\":\"あて先官署\",\"characterItem\":\"1F\",\"tableName\":\"DATA\",\"columnName\":\"REPORTDIVCD\"},{\"nameCd\":\"010\",\"name\":\"大額・小額識別\",\"characterItem\":\"S\",\"tableName\":\"DATA\",\"columnName\":\"BIGSMALLFLG\"},{\"nameCd\":\"011\",\"name\":\"申告等種別\",\"characterItem\":\"C\",\"tableName\":\"DATA\",\"columnName\":\"REPORTKINDCD_1\"},{\"nameCd\":\"012\",\"name\":\"申告貨物識別\",\"characterItem\":\"S\",\"tableName\":\"DATA\",\"columnName\":\"CARGODISC\"},{\"nameCd\":\"013\",\"name\":\"あて先部門(IDA)\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"REPORTDEPCD\"},{\"nameCd\":\"014\",\"name\":\"インボイス識別\",\"characterItem\":\"A\",\"tableName\":\"DATA\",\"columnName\":\"INVOICEDISCERNMENT\"},{\"nameCd\":\"015\",\"name\":\"インボイス価格条件\",\"characterItem\":\"FOB\",\"tableName\":\"DATA\",\"columnName\":\"INVOICEVALCONCD\"},{\"nameCd\":\"016\",\"name\":\"あて先部門(MIC)\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"REPORTDEPCD\"},{\"nameCd\":\"017\",\"name\":\"税関官署（HCH）\",\"characterItem\":\"1F\",\"tableName\":\"HEAD\",\"columnName\":\"CUSTOMSDIV\"},{\"nameCd\":\"018\",\"name\":\"通関予定蔵置場\",\"characterItem\":\"1FWW6\",\"tableName\":\"DATA\",\"columnName\":\"BONDEDWAREHOUSECD\"},{\"nameCd\":\"019\",\"name\":\"取卸港\",\"characterItem\":\"NRT\",\"tableName\":\"DATA\",\"columnName\":\"GETPORT\"},{\"nameCd\":\"020\",\"name\":\"インボイス価格区分\",\"characterItem\":\"A\",\"tableName\":\"DATA\",\"columnName\":\"INVOICEVALCLASSCD\"},{\"nameCd\":\"021\",\"name\":\"運賃区分\",\"characterItem\":\"A\",\"tableName\":\"DATA\",\"columnName\":\"FAREFLG\"},{\"nameCd\":\"022\",\"name\":\"保険区分\",\"characterItem\":\"D\",\"tableName\":\"DATA\",\"columnName\":\"INSURANCECLASSCD\"},{\"nameCd\":\"023\",\"name\":\"社内整理番号\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"INHOUSEREFNUMBER\"},{\"nameCd\":\"024\",\"name\":\"申告予定者\",\"characterItem\":\"1AKKJ\",\"tableName\":\"DATA\",\"columnName\":\"REPORTPERSONCD\"},{\"nameCd\":\"025\",\"name\":\"重量単位（IDA）\",\"characterItem\":\"KGM\",\"tableName\":\"DATA\",\"columnName\":\"WEIGHTUNITCD\"},{\"nameCd\":\"026\",\"name\":\"貿易形態別符号\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"TRADETYPEMARK\"},{\"nameCd\":\"027\",\"name\":\"評価区分\",\"characterItem\":\"0\",\"tableName\":\"DATA\",\"columnName\":\"ESTIMATIONFLGCD\"},{\"nameCd\":\"028\",\"name\":\"評価補正区分\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"INCREVISEFLGCD\"},{\"nameCd\":\"029\",\"name\":\"納期限延長\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"DELIVERYDATEEXTCD\"},{\"nameCd\":\"030\",\"name\":\"納付方法識別\",\"characterItem\":\"R\",\"tableName\":\"DATA\",\"columnName\":\"PAYMETHODDISC\"},{\"nameCd\":\"031\",\"name\":\"原産地証明書識別\",\"characterItem\":\"R\",\"tableName\":\"DETAIL\",\"columnName\":\"ORIGINCERTIFIDISC\"},{\"nameCd\":\"032\",\"name\":\"運賃按分識別\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"FAREDIVDISC\"},{\"nameCd\":\"033\",\"name\":\"内国消費税等種別\",\"characterItem\":\"F4\",\"tableName\":\"DETAIL\",\"columnName\":\"INCONSTAXKINDCD_1\"},{\"nameCd\":\"034\",\"name\":\"重量単位（HCH）\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"WEIGHTUNITCD\"},{\"nameCd\":\"035\",\"name\":\"管理資料計上表示\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"MANAGEDDATAFLG\"},{\"nameCd\":\"036\",\"name\":\"混載仕立業（HCH）\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"MIXEDFORWARDER\"},{\"nameCd\":\"037\",\"name\":\"搬入等先保税地域コード（IDA）\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"LONGKEEPBONDEDWAREHOUSE\"},{\"nameCd\":\"038\",\"name\":\"仕立\",\"characterItem\":\"1\",\"tableName\":\"DATA\",\"columnName\":\"MIXEDFORWARDER\"},{\"nameCd\":\"039\",\"name\":\"仕向地\",\"characterItem\":\"NRT\",\"tableName\":\"DATA\",\"columnName\":\"DESTINATION\"},{\"nameCd\":\"040\",\"name\":\"通関業者コード\",\"characterItem\":\"KKJ\",\"tableName\":\"DATA\",\"columnName\":\"CUSTOMSTRADERCD\"},{\"nameCd\":\"041\",\"name\":\"通関場所コード\",\"characterItem\":\"1AKKJ\",\"tableName\":\"DATA\",\"columnName\":\"CUSTOMSPLACECD\"},{\"nameCd\":\"042\",\"name\":\"申告予定者\",\"characterItem\":\"1AKKJ\",\"tableName\":\"DATA\",\"columnName\":\"REPORTPERSONCD\"},{\"nameCd\":\"043\",\"name\":\"到着空港\",\"characterItem\":\"NRT\",\"tableName\":\"DATA\",\"columnName\":\"ARRAIRPORTCD\"},{\"nameCd\":\"044\",\"name\":\"搬入保税蔵地場\",\"characterItem\":\"1FWW6\",\"tableName\":\"DATA\",\"columnName\":\"CARBONDEDWAREHOUSE\"},{\"nameCd\":\"045\",\"name\":\"ロケーション\",\"characterItem\":\"SPT,\",\"tableName\":\"DATA\",\"columnName\":\"LOCATIONCD\"},{\"nameCd\":\"046\",\"name\":\"税関事務管理人コード\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"CUSTOMSOFFICEJANITORCD\"},{\"nameCd\":\"047\",\"name\":\"税関事務管理人受理番号\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"CUSTOMSOFFICEJANITORRENO\"},{\"nameCd\":\"048\",\"name\":\"税関事務管理人名\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"CUSTOMSOFFICEJANITORNAME\"},{\"nameCd\":\"049\",\"name\":\"検査立会者\",\"characterItem\":\"\",\"tableName\":\"DATA\",\"columnName\":\"INSPECTIONWITNESS\"}]";
//        String[] strList = testStr.split("}");
//
//        List<Map<String,String>> list = new ArrayList<>();
//
//
//
//        for(int i = 0; i < strList.length; i++){
//            String tmp = strList[i].replaceAll("\"","").replaceAll("\\{","");
//            String[] tmpList = tmp.split(",");
//            for(int j = 0; j < tmpList.length; j++){
//                if(tmpList[j].contains(":")) {
//                   String[] aTmp = tmpList[j].split(":");
//                    Map<String,String> tmpMap = new HashMap<>();
//                   if(aTmp.length > 1){
//                       tmpMap.put(aTmp[0], StringUtil.isStringNull(aTmp[1]));
//                   } else {
//                       tmpMap.put(aTmp[0], "");
//                   }
//                    list.add(tmpMap);
//                }
//            }
//         }
//
//        System.out.println(list.toString());

//        Pattern pattern = Pattern.compile("[{](.*)[}]");
//        Matcher mat = pattern.matcher(testStr);
//
//        while(mat.find()){
//            System.out.println(mat.group(1));
//        }

//        String[] strList = new String[4];
//        strList[0] = "\\@";
//        strList[1] = "\\#";
//        strList[2] = "\\?";
//        strList[3] = "\\!";
//
//        int seq = 1;
//
//        System.out.println(seq % 20);

    }

    public static String padLeftSpace(String inputString, int length,boolean flgLR) {
        if (inputString.length() == length) {
            return inputString;
        } else if(inputString.length() > length){
            return inputString.substring(0,length);
        }
        StringBuilder sb = new StringBuilder();
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

        return sb.toString();
    }

    public static void createTest(){
        String path = "C:\\Users\\m-ma\\Documents\\2022\\TEST\\MIC\\20230209";
        File folder = new File(path);
        if(!folder.exists()){
            try {
                folder.mkdir();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    static List<String> prohibitionList = new ArrayList() {
        {
            add("\\`");
            add("\\$");
            add("\\[");
            add("\\]");
            add("\\^");
            add("\\_");
            add("\\\\");
            add("\\ｱ");
            add("\\ｲ");
            add("\\ｳ");
            add("\\ｴ");
            add("\\ｵ");
            add("\\ｶ");
            add("\\ｷ");
            add("\\ｸ");
            add("\\ｹ");
            add("\\ｺ");
            add("\\ｻ");
            add("\\ｼ");
            add("\\ｽ");
            add("\\ｾ");
            add("\\ｿ");
            add("\\ﾀ");
            add("\\ﾁ");
            add("\\ﾂ");
            add("\\ﾃ");
            add("\\ﾄ");
            add("\\ﾅ");
            add("\\ﾆ");
            add("\\ﾇ");
            add("\\ﾈ");
            add("\\ﾉ");
            add("\\ﾊ");
            add("\\ﾋ");
            add("\\ﾌ");
            add("\\ﾍ");
            add("\\ﾎ");
            add("\\ﾏ");
            add("\\ﾐ");
            add("\\ﾑ");
            add("\\ﾒ");
            add("\\ﾓ");
            add("\\ﾔ");
            add("\\ﾕ");
            add("\\ﾖ");
            add("\\ﾗ");
            add("\\ﾘ");
            add("\\ﾙ");
            add("\\ﾚ");
            add("\\ﾛ");
            add("\\ﾜ");
            add("\\ｦ");
            add("\\ﾝ");
            add("\\ｧ");
            add("\\ｨ");
            add("\\ｩ");
            add("\\ｪ");
            add("\\ｫ");
            add("\\ｬ");
            add("\\ｭ");
            add("\\ｮ");
            add("\\ｯ");
            add("[*#]");

        }};
}
