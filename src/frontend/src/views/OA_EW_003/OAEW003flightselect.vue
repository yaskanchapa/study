<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
    <div>
      <div class="bar-color1-green text-white" style="width:100%;height:40px;font-size: 24px">
      <tbody>
        <tr>
      <td style="width:85%">
        輸出保税業務
        </td>  
      <td style="width:15%;text-align: right">
      <va-button  @click="goBack">戻る</va-button>
     </td>
     </tr>
        </tbody>
      </div>  
      </div>  
      <div class="ripple py-2 px-5" align="left"
           style="width:100%;height:40px;font-size: 25px">
        出発便選択
      </div>
    <div style="text-align: center">
      <div  style="margin: 0 auto 2rem;text-align: left;font-size: 50px">
            <va-button v-for="findflights in findflights" :key="findflights" style="width:100%;height:70px;font-size: 20px" 
            v-model="flight.flightname" @click="selectbutton(findflights)"> 
            <div style="text-align: center;font-size:26px">
            {{ findflights.linkTruckNo }}便
            </div>
            </va-button>
      </div>
       <div v-if="resultarea">
    <va-card :color="cardClass" style="margin-left: 10px; margin-right: 10px; margin-top:20px;">
      <div style = "font-size: 45px">
        <strong>{{ resultDisplay }}</strong>
      </div>
    </va-card>
    </div>
    </div>
  </div>
</template>
<style>
.bar-color1-green {
  --tw-bg-opacity: 1;
  background-color: #15803BFF;
}
.button-large {
  --tw-bg-opacity: 1;
  width : 98%;
  height : 100px;
}
</style>
<script>
import { notificationError } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
import "./lib/kjs-modules.js";
export default {
  data() {
    return {
      getdate: [],
      findflights: [], 
      flight: {
      flightName : '', 
      termNo : '',
    },
    termNoFileName : "termno.txt",
    resultarea : false,
    }
  }  ,
// 画面表示時にDBから値を取得
mounted() {
 /* this.termNoFileCheck();
  if (this.termNoFileExists === true) {
      this.searchWarehouse();    // DBからcustomsPlaceCd、USERCDの取得を行う
    } */
      this.axios({
        url: '/api/oaew003/searchFlight',
        method: 'get',
      })    
         .then(res => {
          if(res.data.result){
          this.findflights = res.data.resultData
          } else {
          this.resultarea = true;
          this.cardClass = "#b51818";
          this.resultDisplay = res.data.errorMessage;
          }
          console.log(res.data )
          this.resultRowData = res.data.resultData          
        }).catch(error => {
          console.log(error)
          this.resultarea = true;
          this.cardClass = "#b51818";
          this.resultDisplay = error.Message;
        }).finally(() => {
          console.log('finally')
        })
},
methods: {
   selectbutton: function (findflights) {
      this.select = findflights;
 this.$router.push({ name: 'OAEW003containerregist', params: { flightName: findflights.linkTruckNo } })
  },
  termNoFileCheck(){
      /* eslint-disable */
      var KJS=KJS||{Scanner:{},Notification:{},File:{},FTP:{},DeviceInfo:{},LicenceInfo:{}};KJS.FTP=(function(){setOptionSettings=function(b){var a=JSON.stringify(b);_ftpClient.setOptionSettings(a)};getOptionSettings=function(){var a=_ftpClient.getOptionSettings();return JSON.parse(a)};connect=function(c,b,a,d){return _ftpClient.connect(c,b,a,d)};quit=function(){_ftpClient.quit()};putFile=function(b,c,a){return _ftpClient.putFile(b,c,a)};append=function(b,c,a){return _ftpClient.append(b,c,a)};getFile=function(a,b,c){return _ftpClient.getFile(a,b,c)};dir=function(b){var a=_ftpClient.dir(b);return JSON.parse(a)};deleteFile=function(a){return _ftpClient.deleteFile(a)};rename=function(b,a){return _ftpClient.rename(b,a)};createDirectory=function(a){return _ftpClient.createDirectory(a)};deleteDirectory=function(a){return _ftpClient.deleteDirectory(a)};return{setOptionSettings:setOptionSettings,getOptionSettings:getOptionSettings,connect:connect,quit:quit,putFile:putFile,append:append,getFile:getFile,dir:dir,rename:rename,deleteFile:deleteFile,createDirectory:createDirectory,deleteDirectory:deleteDirectory}}());KJS.File=(function(){exists=function(a,b){return _fileUtil.exists(a,b)};getDirectoryInfo=function(c,b){var a=_fileUtil.list(c,b);return JSON.parse(a)};open=function(a,b){return _fileUtil.open(a,b)};close=function(a){return _fileUtil.close(a)};closeAll=function(){_fileUtil.closeAll()};seek=function(a,b){return _fileUtil.seek(a,b)};getLength=function(a){return _fileUtil.getLength(a)};getFilePointer=function(a){return _fileUtil.getFilePointer(a)};readBytes=function(b,c){var a=_fileUtil.readBytes(b,c);return JSON.parse(a)};readLine=function(a){return _fileUtil.readLine(a)};writeBytes=function(b,c){var a=JSON.stringify(c);return _fileUtil.writeBytes(b,a)};write=function(a,b){return _fileUtil.write(a,b)};createDirectory=function(b,a){return _fileUtil.createDirectory(b,a)};deleteDirectory=function(b,a){return _fileUtil.deleteDirectory(b,a)};copyDirectory=function(d,a,b,c){return _fileUtil.copyDirectory(d,a,b,c)};moveDirectory=function(c,d,b,a){return _fileUtil.moveDirectory(c,d,b,a)};createFile=function(b,a){return _fileUtil.createFile(b,a)};deleteFile=function(b,a){return _fileUtil.deleteFile(b,a)};copyFile=function(d,c,b,a){return _fileUtil.copyFile(d,c,b,a)};moveFile=function(d,c,b,a){return _fileUtil.moveFile(d,c,b,a)};rename=function(b,c,a){return _fileUtil.rename(b,c,a)};return{exists:exists,getDirectoryInfo:getDirectoryInfo,open:open,close:close,closeAll:closeAll,seek:seek,getLength:getLength,getFilePointer:getFilePointer,readBytes:readBytes,readLine:readLine,writeBytes:writeBytes,write:write,createDirectory:createDirectory,deleteDirectory:deleteDirectory,copyDirectory:copyDirectory,moveDirectory:moveDirectory,createFile:createFile,deleteFile:deleteFile,copyFile:copyFile,moveFile:moveFile,rename:rename}}());KJS.Notification=(function(){startVibrator=function(a,c,b){return _notificationUtil.startVibrator(a,c,b)};stopVibrator=function(){_notificationUtil.stopVibrator()};startLed=function(c,a,d,b){return _notificationUtil.startLed(c,a,d,b)};stopLed=function(){_notificationUtil.stopLed()};startBuzzer=function(d,a,c,b){return _notificationUtil.startBuzzer(d,a,c,b)};stopBuzzer=function(){_notificationUtil.stopBuzzer()};return{startVibrator:startVibrator,stopVibrator:stopVibrator,startLed:startLed,stopLed:stopLed,startBuzzer:startBuzzer,stopBuzzer:stopBuzzer}}());KJS.Scanner=(function(){const b=2;const a=Object.freeze({ScanParams:0,CodeType:1,DataFormat:2,DataOutput:3,UserFeedback:4,TuningParams:5,UPCEAN:100,JAN:100,C128:101,C39:102,ITF:103,GS1:104,DM:105,QR:106,PDF:107,TOF:108,CODABAR:109,NW7:109,COOP:110,C93:111,COMPOSITE:112,POSTAL:113,OCR:114});setReadCallback=function(c){return _scanManager.setReadCallback(c)};clearReadCallback=function(){_scanManager.clearReadCallback()};getConfig=function(d){var c="";switch(d){case a.ScanParams:c=_scanManager.getScanParams();break;case a.CodeType:c=_scanManager.getCodeType();break;case a.DataFormat:c=_scanManager.getDataFormat();break;case a.DataOutput:c=_scanManager.getDataOutput();break;case a.UserFeedback:c=_scanManager.getUserFeedback();break;case a.TuningParams:c=_scanManager.getTuningParams();break;case a.UPCEAN:c=_scanManager.getCodeParamsUpcEanJan();break;case a.C128:c=_scanManager.getCodeParamsCode128();break;case a.C39:c=_scanManager.getCodeParamsCode39();break;case a.ITF:c=_scanManager.getCodeParamsItf();break;case a.GS1:c=_scanManager.getCodeParamsGs1Databar();break;case a.DM:c=_scanManager.getCodeParamsDatamatrix();break;case a.QR:c=_scanManager.getCodeParamsQrCode();break;case a.PDF:c=_scanManager.getCodeParamsPdf417();break;case a.TOF:c=_scanManager.getCodeParamsIndustrial2Of5();break;case a.CODABAR:c=_scanManager.getCodeParamsCodabarNw7();break;case a.COOP:c=_scanManager.getCodeParamsCoop2Of5();break;case a.C93:c=_scanManager.getCodeParamsCode93();break;case a.COMPOSITE:c=_scanManager.getCodeParamsCompositeGs1_128();break;case a.POSTAL:c=_scanManager.getCodeParamsPostal();break;case a.OCR:c=_scanManager.getCodeParamsOcr();break}return JSON.parse(c)};setConfig=function(e,d){var c=JSON.stringify(d);switch(e){case a.ScanParams:return _scanManager.setScanParams(c);case a.CodeType:return _scanManager.setCodeType(c);case a.DataFormat:return _scanManager.setDataFormat(c);case a.DataOutput:return _scanManager.setDataOutput(c);case a.UserFeedback:return _scanManager.setUserFeedback(c);case a.TuningParams:return _scanManager.setTuningParams(c);case a.UPCEAN:return _scanManager.setCodeParamsUpcEanJan(c);case a.C128:return _scanManager.setCodeParamsCode128(c);case a.C39:return _scanManager.setCodeParamsCode39(c);case a.ITF:return _scanManager.setCodeParamsItf(c);case a.GS1:return _scanManager.setCodeParamsGs1Databar(c);case a.DM:return _scanManager.setCodeParamsDatamatrix(c);case a.QR:return _scanManager.setCodeParamsQrCode(c);case a.PDF:return _scanManager.setCodeParamsPdf417(c);case a.TOF:return _scanManager.setCodeParamsIndustrial2Of5(c);case a.CODABAR:return _scanManager.setCodeParamsCodabarNw7(c);case a.COOP:return _scanManager.setCodeParamsCoop2Of5(c);case a.C93:return _scanManager.setCodeParamsCode93(c);case a.COMPOSITE:return _scanManager.setCodeParamsCompositeGs1_128(c);case a.POSTAL:return _scanManager.setCodeParamsPostal(c);case a.OCR:return _scanManager.setCodeParamsOcr(c)}return b};getCodeTypeAllEnabled=function(){var c=_scanManager.getCodeTypeAllEnabled();return JSON.parse(c)};getCodeTypeAllDisabled=function(){var c=_scanManager.getCodeTypeAllDisabled();return JSON.parse(c)};startRead=function(){return _scanManager.startRead()};stopRead=function(){_scanManager.stopRead()};isReading=function(){return _scanManager.isReading()};lockScanner=function(){return _scanManager.lockScanner()};unlockScanner=function(){return _scanManager.unlockScanner()};getConfigGroupId=function(){return _scanManager.getConfigGroupId()};changeConfigGroupId=function(c){return _scanManager.changeConfigGroupId(c)};return{ConfigId:a,setReadCallback:setReadCallback,clearReadCallback:clearReadCallback,getConfig:getConfig,setConfig:setConfig,getCodeTypeAllEnabled:getCodeTypeAllEnabled,getCodeTypeAllDisabled:getCodeTypeAllDisabled,startRead:startRead,stopRead:stopRead,isReading:isReading,lockScanner:lockScanner,unlockScanner:unlockScanner,getConfigGroupId:getConfigGroupId,changeConfigGroupId:changeConfigGroupId}}());KJS.DeviceInfo=(function(){getLicenceInfo=function(c){var a="";var b="";switch(c){case KJS.LicenceInfo.FunctionType.OCR:a=_deviceInfo.getLicenceInfo("OCR");b=JSON.parse(a);break}return b};return{getLicenceInfo:getLicenceInfo}}());KJS.LicenceInfo=(function(){const a=Object.freeze({OCR:0});return{FunctionType:a}}());
      console.log(KJS);
      try {
        const fp = KJS.File.open(0, this.termNoFileName);
        this.flight.termNo = KJS.File.readLine(fp);
        console.log(this.flight.termNo);
        KJS.File.close(fp);
        this.termNoFileExists = true;
      } catch (e) {
        console.log("HTから端末ID取得失敗");
        notificationError("端末IDを確認できません、端末設定を確認してください")
        this.termNoFileExists = false;
      }
      /* eslint-enable */
    },
    goBack() {
      this.$router.push({ name: 'OAEW003Main'})
    }
 }
}
</script>