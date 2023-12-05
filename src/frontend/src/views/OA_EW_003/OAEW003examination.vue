<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-2">
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
      <va-card style="margin-left: 10px; margin-right: 10px">
        <va-inner-loading :loading="isLoading" :size="60">
      <div class="ripple py-2 px-5" align="left"
           style="width:100%;height:50px;font-size: 25px">
        対査確認
      <div style="margin-bottom: 5px;text-align: right;font-size: 25px;margin-right: 10px">
        端末ID : {{ schCondition.termNo }}
        <va-input style = "width:10px" v-if="!termNoFileExists" v-model="schCondition.termNo" :disabled="!isEditable2" v-on:keydown.enter="update()" />
      <va-button style="width: 10% ; margin-left: 10px;font-size: 30px" @click = "update">更新</va-button>
      </div>
      </div>
      <div class="ripple py-2 px-5" align="left"
           style="width:100%;height:30px;font-size: 18px">
        積付港
      </div>       
            <div class="ripple py-2 px-5" align="left"
           style="width:100%;height:40px;font-size: 25px">
     {{schCondition.portName}} 
    </div>
      <div class="ripple py-2 px-5" align="left"
           style="width:100%;height:35px;font-size: 20px">
        HAWB
      </div>
      <div style="text-align: center;margin-left: 10px; margin-right: 10px">
      <va-input ref="numberBox" style="width: 100% ; margin-bottom: 10px;font-size: 50px" v-model="schCondition.cwbNo" :disabled="!isEditable" v-on:keydown.enter="cargoCheck(schCondition)" />            
      <div v-if="isScanned">
      <div class="ripple py-2 px-5" align="left"
           style="width:100%;height:50px;font-size: 18px">
        個数
      </div><div style="text-align: left;margin-left: 10px; margin-right: 10px;font-size: 35px">
      <va-input type="number" ref="pieceBox" style="width: 30% ; margin-bottom: 10px;font-size: 30px" v-model="schCondition.piece" v-on:keydown.enter="cargoRegist(schCondition)" /> /{{ linkPiece }}     
    </div>
    </div>
    </div>
    </va-inner-loading>
    </va-card>
        <div v-if="resultarea">
    <va-card :color="cardClass" style="margin-left: 10px; margin-right: 10px;">
      <div style = "font-size: 45px">
        <strong>{{ resultDisplay }}</strong>
      </div> 
      <div style= "font-size: 39px; margin-left: 5px; margin-right: 10px;">
        ハウス番号 : {{ hawbNoInput }}
      </div>    
    </va-card> 
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
import {VaCard} from "vuestic-ui";
import "./lib/kjs-modules.js";
export default {
    props: {        
    VaCard,
  },
  data: () => ({
    currentView: 'searchCondition',
    isLoading: false,
    isShow: false,
    cardClass: '',
    schCondition: {
      cwbNo : '',
      portName : '',
      currentCargoStatusCd : 'EB00200',
      termNo : '',
      awbNo : '',
      piece : '',
      linkPiece: '',
      regUserCd: '',
      bondedWarehouseCd: '',
    },
      termNoFileName : "termno.txt",
      resultDisplay : '',
      isScanned : false,
      resultarea : false,
      isEditable :true,
      termNoFileExists : false,
      isEditable2 :true,
  }),
  mounted: function (){
     this.termNoFileCheck();
      if (this.termNoFileExists === true) {
      this.searchWarehouse();    // DBからcustomsPlaceCd、USERCDの取得を行う
    }
    console.log(this.$route.params)
  this.schCondition.portName = this.$route.params.portName
  this.$refs.numberBox.focus(); 
  },
  methods: {
    // 貨物情報の確認
    cargoCheck(schCondition) {
      console.log('schCondition', schCondition);
      this.changeLoading();
      this.resultarea = false;
      const usercd = schCondition.regUserCd;
      const bondedWarehouseCd = schCondition.bondedWarehouseCd;
      if ((usercd === '')||(usercd === null)||(bondedWarehouseCd === '')||(bondedWarehouseCd === null)) {
        console.log('null or empty')
        this.resultarea = true;
        this.hawbNoInput = schCondition.cwbNo;
        this.cardClass = "#b51818";
        this.resultDisplay = "端末IDを入力してください";
        this.changeLoading();
        this.schCondition.cwbNo = '';
        return false;
        }
      this.axios({
        url: '/api/oaew003/searchExamination',
        method: 'get',
        params: schCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          console.log(res.data);
          this.hawbNoInput = '';
          this.isScanned = false;        
          this.hawbNoInput = schCondition.cwbNo;
          if(res.data.result){
            this.findflights = res.data.resultData
            this.schCondition.awbNo = res.data.awbNo ;
            this.linkPiece =  res.data.linkPiece ;
            this.schCondition.linkPiece =  res.data.linkPiece ;
            this.show = !this.show;
            this.isScanned = true;
            this.$nextTick(() => {
            this.$refs.pieceBox.focus();
            this.schCondition.piece = res.data.defaultPiece;
            this.hawbNoInput = schCondition.cwbNo;
            this.isEditable = false;
        });
          } else {
            this.resultarea = true;
            this.hawbNoInput = schCondition.cwbNo;
            this.resultDisplay = res.data.message;
            this.schCondition.cwbNo = '';
            if(this.resultDisplay === "対査対象貨物ではありません"){
             this.cardClass = "#2d8524";
            }else if(this.resultDisplay === "対査済み"){
             this.cardClass = "#DDEE44";
             this.$nextTick(() => {
             this.$refs.numberBox.focus();})
            }else if(this.resultDisplay === "積込港間違い"){
             this.cardClass="#b51818";
             const audioError = new Audio(require("./Audio/Error.mp3"));
             audioError.play();           
             document.activeElement.blur();
            }else if(this.resultDisplay === "貨物情報無し"){
             this.cardClass="#b51818";
             const audioError = new Audio(require("./Audio/Error.mp3"));
             audioError.play();
            this.$nextTick(() => {
            this.$refs.numberBox.focus();})
             }            
        }
        }).catch(error => {
           this.resultarea = true;
          this.cardClass = "#000000"
          this.hawbNoInput = schCondition.cwbNo;
          this.resultDisplay = "処理エラー";
         // document.activeElement.blur();
           this.schCondition.cwbNo = '';
           console.log('error' + error);
           this.$nextTick(() => {
            this.$refs.numberBox.focus();})
      }).finally(() => {
        console.log('finally')
        this.changeLoading();
      })
    },
    searchWarehouse(){
      this.axios({
        url: '/api/oaew003/searchWarehouse',
        method: 'get',
        params: this.schCondition,
        enctype: 'multipart/form-data'
      }).then(res => {
          console.log(res.data);
          if(res.data.result){
            this.schCondition.regUserCd = res.data.resultData[0].regUserCd;
            this.schCondition.bondedWarehouseCd = res.data.resultData[0].bondedWarehouseCd;
            if(this.resultDisplay === "端末確認OK"){
          this.cardClass = "#2d8524";
          this.isEditable2 = false;
            }
          } else {
        this.hawbNoInput = '';
        this.resultarea = true;
        this.resultDisplay = res.data.message;
        if(this.resultDisplay === "未許可の端末です"||this.resultDisplay ==="ユーザコード確認中にエラーが発生しました。"){
          this.cardClass = "#b51818";
          this.hawbNoInput = '';
          this.schCondition.usercd ='';
          this.schCondition.bondedWarehouseCd='';           
          } 
        this.schCondition.usercd ='';
        this.schCondition.bondedWarehouseCd='';
        }
        }).catch(error => {
        this.resultarea = true;
        this.cardClass="#b51818";
        this.resultDisplay = "ユーザーコード確認でエラーが発生しました。";
        console.log("ユーザーコード確認でエラーが発生しました。", error.message)
      }).finally(() => {
        console.log('finally')
        
      })
    },
        cargoRegist(schCondition) {
      console.log('schCondition', schCondition);
      this.changeLoading();
      const piece = schCondition.piece;
      if ((piece === '')||(piece === null)) {
        console.log('null or empty')
        this.resultarea = true;
        this.cardClass = "#b51818";
        this.hawbNoInput = schCondition.cwbNo;
        this.resultDisplay = "個数を入力してください";
        return false;
        }
      this.axios({
        url: '/api/oaew003/updateExamination',
        method: 'get',
        params: schCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          console.log(res.data);
          this.resultarea = false;
          if(res.data.result){
            this.hawbNoInput = schCondition.cwbNo;
            this.resultarea = true;
            this.cardClass = "#2d8524";
            this.resultDisplay = "対査完了";
            const audioError = new Audio(require("./Audio/Ok.mp3"));
            audioError.play();   
          //  this.isScanned = true;
            this.show = !this.show;
            this.isScanned = false;
            this.schCondition.cwbNo = '';
            this.schCondition.piece = '';
            this.isEditable = true;
            this.$nextTick(() => {
            this.$refs.numberBox.focus()});           
          } else {
            this.resultarea = true;
            this.hawbNoInput = schCondition.cwbNo;
            this.resultDisplay = res.data.message;
            if(this.resultDisplay === "個数間違い"){
            this.cardClass = "#b51818";
            const audioError = new Audio(require("./Audio/Error.mp3"));
            audioError.play();            
            }
        }
        }).catch(error => {
           this.resultarea = true;
           this.hawbNoInput = schCondition.cwbNo;
           this.cardClass = "#000000"
           this.resultDisplay = "処理エラー";
           console.log('error' + error);
      }).finally(() => {
        console.log('finally')  
         this.changeLoading();
       // this.resultarea = true;
      })
    },
        termNoFileCheck(){
      /* eslint-disable */
      var KJS=KJS||{Scanner:{},Notification:{},File:{},FTP:{},DeviceInfo:{},LicenceInfo:{}};KJS.FTP=(function(){setOptionSettings=function(b){var a=JSON.stringify(b);_ftpClient.setOptionSettings(a)};getOptionSettings=function(){var a=_ftpClient.getOptionSettings();return JSON.parse(a)};connect=function(c,b,a,d){return _ftpClient.connect(c,b,a,d)};quit=function(){_ftpClient.quit()};putFile=function(b,c,a){return _ftpClient.putFile(b,c,a)};append=function(b,c,a){return _ftpClient.append(b,c,a)};getFile=function(a,b,c){return _ftpClient.getFile(a,b,c)};dir=function(b){var a=_ftpClient.dir(b);return JSON.parse(a)};deleteFile=function(a){return _ftpClient.deleteFile(a)};rename=function(b,a){return _ftpClient.rename(b,a)};createDirectory=function(a){return _ftpClient.createDirectory(a)};deleteDirectory=function(a){return _ftpClient.deleteDirectory(a)};return{setOptionSettings:setOptionSettings,getOptionSettings:getOptionSettings,connect:connect,quit:quit,putFile:putFile,append:append,getFile:getFile,dir:dir,rename:rename,deleteFile:deleteFile,createDirectory:createDirectory,deleteDirectory:deleteDirectory}}());KJS.File=(function(){exists=function(a,b){return _fileUtil.exists(a,b)};getDirectoryInfo=function(c,b){var a=_fileUtil.list(c,b);return JSON.parse(a)};open=function(a,b){return _fileUtil.open(a,b)};close=function(a){return _fileUtil.close(a)};closeAll=function(){_fileUtil.closeAll()};seek=function(a,b){return _fileUtil.seek(a,b)};getLength=function(a){return _fileUtil.getLength(a)};getFilePointer=function(a){return _fileUtil.getFilePointer(a)};readBytes=function(b,c){var a=_fileUtil.readBytes(b,c);return JSON.parse(a)};readLine=function(a){return _fileUtil.readLine(a)};writeBytes=function(b,c){var a=JSON.stringify(c);return _fileUtil.writeBytes(b,a)};write=function(a,b){return _fileUtil.write(a,b)};createDirectory=function(b,a){return _fileUtil.createDirectory(b,a)};deleteDirectory=function(b,a){return _fileUtil.deleteDirectory(b,a)};copyDirectory=function(d,a,b,c){return _fileUtil.copyDirectory(d,a,b,c)};moveDirectory=function(c,d,b,a){return _fileUtil.moveDirectory(c,d,b,a)};createFile=function(b,a){return _fileUtil.createFile(b,a)};deleteFile=function(b,a){return _fileUtil.deleteFile(b,a)};copyFile=function(d,c,b,a){return _fileUtil.copyFile(d,c,b,a)};moveFile=function(d,c,b,a){return _fileUtil.moveFile(d,c,b,a)};rename=function(b,c,a){return _fileUtil.rename(b,c,a)};return{exists:exists,getDirectoryInfo:getDirectoryInfo,open:open,close:close,closeAll:closeAll,seek:seek,getLength:getLength,getFilePointer:getFilePointer,readBytes:readBytes,readLine:readLine,writeBytes:writeBytes,write:write,createDirectory:createDirectory,deleteDirectory:deleteDirectory,copyDirectory:copyDirectory,moveDirectory:moveDirectory,createFile:createFile,deleteFile:deleteFile,copyFile:copyFile,moveFile:moveFile,rename:rename}}());KJS.Notification=(function(){startVibrator=function(a,c,b){return _notificationUtil.startVibrator(a,c,b)};stopVibrator=function(){_notificationUtil.stopVibrator()};startLed=function(c,a,d,b){return _notificationUtil.startLed(c,a,d,b)};stopLed=function(){_notificationUtil.stopLed()};startBuzzer=function(d,a,c,b){return _notificationUtil.startBuzzer(d,a,c,b)};stopBuzzer=function(){_notificationUtil.stopBuzzer()};return{startVibrator:startVibrator,stopVibrator:stopVibrator,startLed:startLed,stopLed:stopLed,startBuzzer:startBuzzer,stopBuzzer:stopBuzzer}}());KJS.Scanner=(function(){const b=2;const a=Object.freeze({ScanParams:0,CodeType:1,DataFormat:2,DataOutput:3,UserFeedback:4,TuningParams:5,UPCEAN:100,JAN:100,C128:101,C39:102,ITF:103,GS1:104,DM:105,QR:106,PDF:107,TOF:108,CODABAR:109,NW7:109,COOP:110,C93:111,COMPOSITE:112,POSTAL:113,OCR:114});setReadCallback=function(c){return _scanManager.setReadCallback(c)};clearReadCallback=function(){_scanManager.clearReadCallback()};getConfig=function(d){var c="";switch(d){case a.ScanParams:c=_scanManager.getScanParams();break;case a.CodeType:c=_scanManager.getCodeType();break;case a.DataFormat:c=_scanManager.getDataFormat();break;case a.DataOutput:c=_scanManager.getDataOutput();break;case a.UserFeedback:c=_scanManager.getUserFeedback();break;case a.TuningParams:c=_scanManager.getTuningParams();break;case a.UPCEAN:c=_scanManager.getCodeParamsUpcEanJan();break;case a.C128:c=_scanManager.getCodeParamsCode128();break;case a.C39:c=_scanManager.getCodeParamsCode39();break;case a.ITF:c=_scanManager.getCodeParamsItf();break;case a.GS1:c=_scanManager.getCodeParamsGs1Databar();break;case a.DM:c=_scanManager.getCodeParamsDatamatrix();break;case a.QR:c=_scanManager.getCodeParamsQrCode();break;case a.PDF:c=_scanManager.getCodeParamsPdf417();break;case a.TOF:c=_scanManager.getCodeParamsIndustrial2Of5();break;case a.CODABAR:c=_scanManager.getCodeParamsCodabarNw7();break;case a.COOP:c=_scanManager.getCodeParamsCoop2Of5();break;case a.C93:c=_scanManager.getCodeParamsCode93();break;case a.COMPOSITE:c=_scanManager.getCodeParamsCompositeGs1_128();break;case a.POSTAL:c=_scanManager.getCodeParamsPostal();break;case a.OCR:c=_scanManager.getCodeParamsOcr();break}return JSON.parse(c)};setConfig=function(e,d){var c=JSON.stringify(d);switch(e){case a.ScanParams:return _scanManager.setScanParams(c);case a.CodeType:return _scanManager.setCodeType(c);case a.DataFormat:return _scanManager.setDataFormat(c);case a.DataOutput:return _scanManager.setDataOutput(c);case a.UserFeedback:return _scanManager.setUserFeedback(c);case a.TuningParams:return _scanManager.setTuningParams(c);case a.UPCEAN:return _scanManager.setCodeParamsUpcEanJan(c);case a.C128:return _scanManager.setCodeParamsCode128(c);case a.C39:return _scanManager.setCodeParamsCode39(c);case a.ITF:return _scanManager.setCodeParamsItf(c);case a.GS1:return _scanManager.setCodeParamsGs1Databar(c);case a.DM:return _scanManager.setCodeParamsDatamatrix(c);case a.QR:return _scanManager.setCodeParamsQrCode(c);case a.PDF:return _scanManager.setCodeParamsPdf417(c);case a.TOF:return _scanManager.setCodeParamsIndustrial2Of5(c);case a.CODABAR:return _scanManager.setCodeParamsCodabarNw7(c);case a.COOP:return _scanManager.setCodeParamsCoop2Of5(c);case a.C93:return _scanManager.setCodeParamsCode93(c);case a.COMPOSITE:return _scanManager.setCodeParamsCompositeGs1_128(c);case a.POSTAL:return _scanManager.setCodeParamsPostal(c);case a.OCR:return _scanManager.setCodeParamsOcr(c)}return b};getCodeTypeAllEnabled=function(){var c=_scanManager.getCodeTypeAllEnabled();return JSON.parse(c)};getCodeTypeAllDisabled=function(){var c=_scanManager.getCodeTypeAllDisabled();return JSON.parse(c)};startRead=function(){return _scanManager.startRead()};stopRead=function(){_scanManager.stopRead()};isReading=function(){return _scanManager.isReading()};lockScanner=function(){return _scanManager.lockScanner()};unlockScanner=function(){return _scanManager.unlockScanner()};getConfigGroupId=function(){return _scanManager.getConfigGroupId()};changeConfigGroupId=function(c){return _scanManager.changeConfigGroupId(c)};return{ConfigId:a,setReadCallback:setReadCallback,clearReadCallback:clearReadCallback,getConfig:getConfig,setConfig:setConfig,getCodeTypeAllEnabled:getCodeTypeAllEnabled,getCodeTypeAllDisabled:getCodeTypeAllDisabled,startRead:startRead,stopRead:stopRead,isReading:isReading,lockScanner:lockScanner,unlockScanner:unlockScanner,getConfigGroupId:getConfigGroupId,changeConfigGroupId:changeConfigGroupId}}());KJS.DeviceInfo=(function(){getLicenceInfo=function(c){var a="";var b="";switch(c){case KJS.LicenceInfo.FunctionType.OCR:a=_deviceInfo.getLicenceInfo("OCR");b=JSON.parse(a);break}return b};return{getLicenceInfo:getLicenceInfo}}());KJS.LicenceInfo=(function(){const a=Object.freeze({OCR:0});return{FunctionType:a}}());
      console.log(KJS);
      try {
        const fp = KJS.File.open(0, this.termNoFileName);
        this.schCondition.termNo = KJS.File.readLine(fp);
        console.log(this.schCondition.termNo);
        KJS.File.close(fp);
        this.termNoFileExists = true;
      } catch (e) {
        console.log("HTから端末ID取得失敗");
        this.termNoFileExists = false;
      }
      /* eslint-enable */
    },
    // 戻るボタン
    goBack() {
      this.$router.push({ name: 'OAEW003portselect'})
    },
        changeLoading() {
      this.isLoading = !this.isLoading;
    },
    // 更新ボタン押下の処理
    update(){      
      this.searchWarehouse();
      this.hawbNoInput = '';
      this.resultarea = true;
      this.resultDisplay = "端末確認OK";
    },
  },
}
</script>