<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
    <div>
      <div class="bar-color1-green text-white" style="width:100%;height:50px;font-size: 30px">
        <tbody>
          <tr>
            <td style="width:85%">
              WMS - 輸入保税業務
            </td>
            <td style="width:15%;text-align: right">
              <router-link to="/airImportHT/oaiw004Main">
                <va-button>戻る</va-button>
              </router-link>
            </td>
          </tr>
        </tbody>
      </div>
    </div>
    <va-card style="margin-left: 10px; margin-right: 10px">
      <div style="margin-top:5px; margin-left: 10px; margin-right: 10px; margin-bottom: 0px; font-size: 30px">
        <tbody>
          <tr>
            <td>
              <strong>搬入チェック</strong>
            </td>
            <td style="width: 15px;" />
            <td style = "font-size: 20px;">
              {{ this.warehouseName }}
              {{ this.warehouseCode }}
              {{ this.warehouseID }}
            </td>
          </tr>
        </tbody>
      </div>
      <div style="margin-bottom: 5px;text-align: right;font-size: 25px;margin-right: 10px">
        端末ID : {{ schCondition.termNo }}
        <va-input style = "width:10px" v-if="!termNoFileExists" v-model="schCondition.termNo" />
        <!--
        ユーザーID :
        <va-input style = "width:50px" v-model="schCondition.userNo" />
        -->
      </div>
      <div style="margin-left: 10px; margin-right: 10px;font-size: 25px">
        マスター番号
      </div>
      <div style="text-align: left;margin-left: 10px; margin-right: 10px">
        <va-select  style="width: 80% ; margin-bottom: 10px;font-size: 20px" v-model="schCondition.mawbNo"
          :options="this.mawbNoList"
          :text-by="(option) => option.awbNoDisplay"
          :track-by="(option) => option.awbNoDisplay"
          :value-by="(option) => option.awbNo"
          placeholder="マスター番号選択" />
        <va-button style="width: 10% ; margin-left: 10px;font-size: 30px" @click = "bondedInMawbCheck(schCondition)">更新</va-button>
      </div>
      <div style="margin-left: 10px; margin-right: 10px; font-size: 25px">
        <tbody>
          <tr>
            <td style="width: 150px">
            ハウス番号
            </td>
            <td v-if="resultGreen" style="font-weight: bold;">
              {{ cargoCount }} / {{ totalCount }}
            </td>
            <td v-if="resultRed" style="font-weight: bold;">
              {{ cargoCount }} / {{ totalCount }}
            </td>
            <td v-if="resultBlack" style="font-weight: bold;">
              {{ cargoCount }} / {{ totalCount }}
            </td>
          </tr>
        </tbody>
      </div>
      <div style="text-align: center;margin-left: 10px; margin-right: 10px">
        <va-input style="width: 100%; margin-bottom: 10px;font-size: 30px" ref="hawbNoInput" v-model="schCondition.hawbNo" placeholder="ハウス番号" v-on:keydown.enter="checkDataPicec(schCondition)" />
      </div>
      <!--複数個口の貨物処理-->
      <div v-if="multipleDataPicec">
        <div style="margin-left: 10px; margin-right: 10px; font-size: 25px">複数個口の貨物です。({{this.hawbNoInput}}, Total:{{cargoPiece}})</div>
        <div style="text-align: left;margin-left: 10px; margin-right: 10px">
          <va-input ref="sPicec" style="width: 80%; margin-bottom: 10px;font-size: 30px"  v-model="schCondition.inputScanPicec" mask="numeral" placeholder = "複数個口の貨物数入力" v-on:keydown.enter="bondedInCheck(schCondition)"/>
          <va-button style="width: 10% ; margin-left: 10px;font-size: 30px" @click = "bondedInvCheck(schCondition)">入力</va-button>
        </div>
      </div>

    </va-card>
  </div>

  <div style = "text-align: center;" v-if="resultGreen">
    <va-card style="margin-left: 10px; margin-right: 10px; margin-top:20px;" color="#2d8524">
      <div style="margin-left: 10px; margin-right: 10px;margin-top: 10px">
        マスター番号 : {{ mawbNoInput }}
      </div>
      <div style="margin-left: 10px; margin-right: 10px;margin-top: 10px">
        ハウス番号 : {{ hawbNoInput }}
      </div>
      <div style = "text-align : right">
        {{ scanPiece }} / {{ cargoPiece }}
      </div>
      <div style = "font-size: 70px">
        <strong>{{ resultDisplay }}</strong>
      </div>
      <div style = "font-size: 30px" v-if="isTimeout">
        <strong>{{ timeoutDisplay }}</strong>
      </div>
    </va-card>
  </div>

  <div style = "text-align: center;" v-if="resultRed">
    <va-card style="margin-left: 10px; margin-right: 10px; margin-top:20px;" color="#b51818">
      <div style="margin-left: 10px; margin-right: 10px;margin-top: 10px">
        マスター番号 : {{ mawbNoInput }}
      </div>
      <div style="margin-left: 10px; margin-right: 10px;margin-top: 10px">
        ハウス番号 : {{ hawbNoInput }}
      </div>
      <div style = "text-align : right">
        {{ scanPiece }} / {{ cargoPiece }}
      </div>
      <div style = "font-size: 70px">
        <strong>{{ resultDisplay }}</strong>
      </div>
      <div style = "font-size: 30px" v-if="isTimeout">
        <strong>{{ timeoutDisplay }}</strong>
      </div>
    </va-card>
  </div>

  <div style = "text-align: center;" v-if="resultBlack">
    <va-card style="margin-left: 10px; margin-right: 10px; margin-top:20px;" color="#000000">
      <div style="margin-left: 10px; margin-right: 10px;margin-top: 10px">
        マスター番号 : {{ mawbNoInput }}
      </div>
      <div style="margin-left: 10px; margin-right: 10px;margin-top: 10px">
        ハウス番号 : {{ hawbNoInput }}
      </div>
      <div style = "text-align : right">
        {{ scanPiece }} / {{ cargoPiece }}
      </div>
      <div style = "font-size: 70px">
        <strong>{{ resultDisplay }}</strong>
      </div>
      <div style = "font-size: 30px" v-if="isTimeout">
        <strong>{{ timeoutDisplay }}</strong>
      </div>
    </va-card>
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
import {notificationError} from "@/components/Notification/NotificationApi";

export default {
  components: {
    VaCard,
  },
  data: () => ({
    currentView: 'searchCondition',
    isLoading: false,
    isTimeout: false,
    schCondition: {
      mawbNo : '',
      hawbNo : '',
      termNo : '',
      userNo : '',
      inputScanPicec : '',
    },
    resultDisplay : '',
    termNoDisplay : '',
    timeoutDisplay : '',
    mawbNoInput : '',
    hawbNoInput : '',
    scanPiece : '',
    cargoPiece : '',
    cargoCount : '',
    totalCount : '',
    termNoFileExists : false,
    termNoAuthorized : false,
    resultGreen : false,
    resultRed : false,
    resultBlack : false,
    multipleDataPicec : false,
    errorFlg : false,
    resultAudioPath : "",
    termNoFileName : "termno.txt",
    mawbNoList: [],
    warehouseName : '',
    warehouseCode : '',
    warehouseID : '',
    htBarcodeType : '',
    errorMessage : '',
  }),
  mounted() {

    this.termNoFileCheck();
    if (this.termNoFileExists === true) {
      this.searchWarehouse();
      this.bondedInMawbCheck(this.schCondition);
    }
  },
  methods: {
    checkDataPicec(schCondition) {
      this.resultGreen = false;
      this.resultRed = false;
      this.resultBlack = false;
      this.hawbNoInput = '';
      if (schCondition.termNo == null || schCondition.termNo === '') {
        console.log("no Terminal ID")
        this.bondedInCheck(schCondition);
      } else if (schCondition.mawbNo == null || schCondition.mawbNo === '') {
        console.log("no mawbNo");
        this.bondedInCheck(schCondition);
      } else if (schCondition.hawbNo == null || schCondition.hawbNo === '') {
        console.log("no hawbNo");
        this.bondedInCheck(schCondition);
      } else {
        this.multipleDataPicec = false;
        this.axios({
          url: '/api/oaiw004/checkDataPicecIn',
          method: 'get',
          params: schCondition,
          enctype: 'multipart/form-data'
        })
          .then(res => {
            if(res.data.errorflg === "1") {
              console.log("error");
              notificationError("管理者にお問い合わせください「Barcode Error」");
              return
            }
            console.log(res.data);
            console.log(this.schCondition.mawbNo);
            console.log(Number(res.data.datapicec));
            if (res.data.awbno == null) {
              console.log("over");
              this.bondedInCheck(schCondition);
            } else if (res.data.awbno !== this.schCondition.mawbNo.toString()) {
              console.log("diffMaster");
              this.bondedInCheck(schCondition);
            } else if (Number(res.data.datapicec) > 1 && res.data.overflg === "0") {
              console.log("more than one datapicec");
              this.multipleDataPicec = true;
              this.cargoPiece = res.data.datapicec;
              this.hawbNoInput = this.schCondition.hawbNo;

              document.activeElement.blur();
              this.$nextTick(() => this.$refs.sPicec.focus());

            } else {
              console.log("just one datapicec");
              this.bondedInCheck(schCondition);
            }
          }).catch(error => {
          console.log('error' + error);
        }).finally(() => {
          console.log('finally');
        })
      }


    },
    bondedInCheck(schCondition) {

      document.activeElement.blur();
      console.log('schCondition', schCondition);
      if (schCondition.userNo === '') {
        console.log("No User ID");
      }

      this.axios({
        url: '/api/oaiw004/searchIn',
        method: 'get',
        params: schCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {

          this.resultDisplay = '';
          this.mawbNoInput = '';
          this.hawbNoInput = '';
          this.cargoPiece = '';
          this.scanPiece = '';
          this.timeoutDisplay = '';
          this.htBarcodeType = '';
          this.errorMessage = '';
          this.resultGreen = false;
          this.resultRed = false;
          this.resultBlack = false;
          this.errorFlg = false;

          this.mawbNoInput = schCondition.mawbNo;
          this.hawbNoInput = schCondition.hawbNo;

          console.log(res.data);
          this.scanPiece = res.data.scanpicec;
          this.cargoPiece = res.data.datapicec;
          // const audioBeep = new Audio(require("./Audio/beep.mp3"));

          this.resultDisplay = res.data.scanresult;
          this.resultAudioPath = res.data.audiopath;
          const resultAudio = new Audio(require("." + this.resultAudioPath));
          resultAudio.play();
          if (res.data.audiopath === "/Audio/Error.mp3") {
            this.errorFlg = true;
          }

          this.countCargo();
          this.$refs.hawbNoInput.focus();

        }).catch(error => {
        console.log('error' + error);
        // this.errorMessage = error.toString();
        const audioError = new Audio(require("./Audio/Error.mp3"));
        audioError.play();
        this.resultDisplay = "エラー";
      }).finally(() => {
        console.log('finally')
        this.multipleDataPicec = false;
        this.mawbNoInput = schCondition.mawbNo;
        this.hawbNoInput = schCondition.hawbNo;
        this.schCondition.hawbNo = '';
        if (this.errorFlg === true) {
          this.resultGreen = false;
          this.resultRed = false;
          this.resultBlack = true;
          document.activeElement.blur();
          if (this.resultDisplay === "エラー") {
            notificationError("Error 管理者にお問い合わせください : " + this.errorMessage);
          }
        } else if (this.resultDisplay === "オーバー" || this.resultDisplay === "フライト違い") {
          this.resultGreen = false;
          this.resultRed = true;
          this.resultBlack = false;
          document.activeElement.blur();
        } else {
          this.resultGreen = true;
          this.resultRed = false;
          this.resultBlack = false;
        }
      })
    },
    termNoFileCheck(){
      /* eslint-disable */
      var KJS=KJS||{Scanner:{},Notification:{},File:{},FTP:{},DeviceInfo:{},LicenceInfo:{}};KJS.FTP=(function(){setOptionSettings=function(b){var a=JSON.stringify(b);_ftpClient.setOptionSettings(a)};getOptionSettings=function(){var a=_ftpClient.getOptionSettings();return JSON.parse(a)};connect=function(c,b,a,d){return _ftpClient.connect(c,b,a,d)};quit=function(){_ftpClient.quit()};putFile=function(b,c,a){return _ftpClient.putFile(b,c,a)};append=function(b,c,a){return _ftpClient.append(b,c,a)};getFile=function(a,b,c){return _ftpClient.getFile(a,b,c)};dir=function(b){var a=_ftpClient.dir(b);return JSON.parse(a)};deleteFile=function(a){return _ftpClient.deleteFile(a)};rename=function(b,a){return _ftpClient.rename(b,a)};createDirectory=function(a){return _ftpClient.createDirectory(a)};deleteDirectory=function(a){return _ftpClient.deleteDirectory(a)};return{setOptionSettings:setOptionSettings,getOptionSettings:getOptionSettings,connect:connect,quit:quit,putFile:putFile,append:append,getFile:getFile,dir:dir,rename:rename,deleteFile:deleteFile,createDirectory:createDirectory,deleteDirectory:deleteDirectory}}());KJS.File=(function(){exists=function(a,b){return _fileUtil.exists(a,b)};getDirectoryInfo=function(c,b){var a=_fileUtil.list(c,b);return JSON.parse(a)};open=function(a,b){return _fileUtil.open(a,b)};close=function(a){return _fileUtil.close(a)};closeAll=function(){_fileUtil.closeAll()};seek=function(a,b){return _fileUtil.seek(a,b)};getLength=function(a){return _fileUtil.getLength(a)};getFilePointer=function(a){return _fileUtil.getFilePointer(a)};readBytes=function(b,c){var a=_fileUtil.readBytes(b,c);return JSON.parse(a)};readLine=function(a){return _fileUtil.readLine(a)};writeBytes=function(b,c){var a=JSON.stringify(c);return _fileUtil.writeBytes(b,a)};write=function(a,b){return _fileUtil.write(a,b)};createDirectory=function(b,a){return _fileUtil.createDirectory(b,a)};deleteDirectory=function(b,a){return _fileUtil.deleteDirectory(b,a)};copyDirectory=function(d,a,b,c){return _fileUtil.copyDirectory(d,a,b,c)};moveDirectory=function(c,d,b,a){return _fileUtil.moveDirectory(c,d,b,a)};createFile=function(b,a){return _fileUtil.createFile(b,a)};deleteFile=function(b,a){return _fileUtil.deleteFile(b,a)};copyFile=function(d,c,b,a){return _fileUtil.copyFile(d,c,b,a)};moveFile=function(d,c,b,a){return _fileUtil.moveFile(d,c,b,a)};rename=function(b,c,a){return _fileUtil.rename(b,c,a)};return{exists:exists,getDirectoryInfo:getDirectoryInfo,open:open,close:close,closeAll:closeAll,seek:seek,getLength:getLength,getFilePointer:getFilePointer,readBytes:readBytes,readLine:readLine,writeBytes:writeBytes,write:write,createDirectory:createDirectory,deleteDirectory:deleteDirectory,copyDirectory:copyDirectory,moveDirectory:moveDirectory,createFile:createFile,deleteFile:deleteFile,copyFile:copyFile,moveFile:moveFile,rename:rename}}());KJS.Notification=(function(){startVibrator=function(a,c,b){return _notificationUtil.startVibrator(a,c,b)};stopVibrator=function(){_notificationUtil.stopVibrator()};startLed=function(c,a,d,b){return _notificationUtil.startLed(c,a,d,b)};stopLed=function(){_notificationUtil.stopLed()};startBuzzer=function(d,a,c,b){return _notificationUtil.startBuzzer(d,a,c,b)};stopBuzzer=function(){_notificationUtil.stopBuzzer()};return{startVibrator:startVibrator,stopVibrator:stopVibrator,startLed:startLed,stopLed:stopLed,startBuzzer:startBuzzer,stopBuzzer:stopBuzzer}}());KJS.Scanner=(function(){const b=2;const a=Object.freeze({ScanParams:0,CodeType:1,DataFormat:2,DataOutput:3,UserFeedback:4,TuningParams:5,UPCEAN:100,JAN:100,C128:101,C39:102,ITF:103,GS1:104,DM:105,QR:106,PDF:107,TOF:108,CODABAR:109,NW7:109,COOP:110,C93:111,COMPOSITE:112,POSTAL:113,OCR:114});setReadCallback=function(c){return _scanManager.setReadCallback(c)};clearReadCallback=function(){_scanManager.clearReadCallback()};getConfig=function(d){var c="";switch(d){case a.ScanParams:c=_scanManager.getScanParams();break;case a.CodeType:c=_scanManager.getCodeType();break;case a.DataFormat:c=_scanManager.getDataFormat();break;case a.DataOutput:c=_scanManager.getDataOutput();break;case a.UserFeedback:c=_scanManager.getUserFeedback();break;case a.TuningParams:c=_scanManager.getTuningParams();break;case a.UPCEAN:c=_scanManager.getCodeParamsUpcEanJan();break;case a.C128:c=_scanManager.getCodeParamsCode128();break;case a.C39:c=_scanManager.getCodeParamsCode39();break;case a.ITF:c=_scanManager.getCodeParamsItf();break;case a.GS1:c=_scanManager.getCodeParamsGs1Databar();break;case a.DM:c=_scanManager.getCodeParamsDatamatrix();break;case a.QR:c=_scanManager.getCodeParamsQrCode();break;case a.PDF:c=_scanManager.getCodeParamsPdf417();break;case a.TOF:c=_scanManager.getCodeParamsIndustrial2Of5();break;case a.CODABAR:c=_scanManager.getCodeParamsCodabarNw7();break;case a.COOP:c=_scanManager.getCodeParamsCoop2Of5();break;case a.C93:c=_scanManager.getCodeParamsCode93();break;case a.COMPOSITE:c=_scanManager.getCodeParamsCompositeGs1_128();break;case a.POSTAL:c=_scanManager.getCodeParamsPostal();break;case a.OCR:c=_scanManager.getCodeParamsOcr();break}return JSON.parse(c)};setConfig=function(e,d){var c=JSON.stringify(d);switch(e){case a.ScanParams:return _scanManager.setScanParams(c);case a.CodeType:return _scanManager.setCodeType(c);case a.DataFormat:return _scanManager.setDataFormat(c);case a.DataOutput:return _scanManager.setDataOutput(c);case a.UserFeedback:return _scanManager.setUserFeedback(c);case a.TuningParams:return _scanManager.setTuningParams(c);case a.UPCEAN:return _scanManager.setCodeParamsUpcEanJan(c);case a.C128:return _scanManager.setCodeParamsCode128(c);case a.C39:return _scanManager.setCodeParamsCode39(c);case a.ITF:return _scanManager.setCodeParamsItf(c);case a.GS1:return _scanManager.setCodeParamsGs1Databar(c);case a.DM:return _scanManager.setCodeParamsDatamatrix(c);case a.QR:return _scanManager.setCodeParamsQrCode(c);case a.PDF:return _scanManager.setCodeParamsPdf417(c);case a.TOF:return _scanManager.setCodeParamsIndustrial2Of5(c);case a.CODABAR:return _scanManager.setCodeParamsCodabarNw7(c);case a.COOP:return _scanManager.setCodeParamsCoop2Of5(c);case a.C93:return _scanManager.setCodeParamsCode93(c);case a.COMPOSITE:return _scanManager.setCodeParamsCompositeGs1_128(c);case a.POSTAL:return _scanManager.setCodeParamsPostal(c);case a.OCR:return _scanManager.setCodeParamsOcr(c)}return b};getCodeTypeAllEnabled=function(){var c=_scanManager.getCodeTypeAllEnabled();return JSON.parse(c)};getCodeTypeAllDisabled=function(){var c=_scanManager.getCodeTypeAllDisabled();return JSON.parse(c)};startRead=function(){return _scanManager.startRead()};stopRead=function(){_scanManager.stopRead()};isReading=function(){return _scanManager.isReading()};lockScanner=function(){return _scanManager.lockScanner()};unlockScanner=function(){return _scanManager.unlockScanner()};getConfigGroupId=function(){return _scanManager.getConfigGroupId()};changeConfigGroupId=function(c){return _scanManager.changeConfigGroupId(c)};return{ConfigId:a,setReadCallback:setReadCallback,clearReadCallback:clearReadCallback,getConfig:getConfig,setConfig:setConfig,getCodeTypeAllEnabled:getCodeTypeAllEnabled,getCodeTypeAllDisabled:getCodeTypeAllDisabled,startRead:startRead,stopRead:stopRead,isReading:isReading,lockScanner:lockScanner,unlockScanner:unlockScanner,getConfigGroupId:getConfigGroupId,changeConfigGroupId:changeConfigGroupId}}());KJS.DeviceInfo=(function(){getLicenceInfo=function(c){var a="";var b="";switch(c){case KJS.LicenceInfo.FunctionType.OCR:a=_deviceInfo.getLicenceInfo("OCR");b=JSON.parse(a);break}return b};return{getLicenceInfo:getLicenceInfo}}());KJS.LicenceInfo=(function(){const a=Object.freeze({OCR:0});return{FunctionType:a}}());
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
    bondedInMawbCheck(schCondition){
      this.resultGreen = false;
      this.resultRed = false;
      this.resultBlack = false;
      this.mawbNoList = [];
      console.log('schCondition', schCondition);
      this.axios({
        url: '/api/oaiw004/searchInMawb',
        method: 'get',
        params : schCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          console.log(res.data);
          for (let i = 0; i < res.data.length; i++) {
            this.mawbNoList.push(res.data[i]);
          }
        }).catch(error => {
          console.log(error);
          console.log('error' + error)
          const audioError = new Audio(require("./Audio/Error.mp3"));
          audioError.play();
          this.resultDisplay = "エラー";
        this.resultGreen = false;
        this.resultRed = false;
        this.resultBlack = true;
      }).finally(() => {
        console.log('finally')
        console.log(this.mawbNoList);
      })
    },
    countCargo(){
      this.cargoCount = '';
      this.totalCount = '';
      this.axios({
        url: '/api/oaiw004/countIn',
        method: 'get',
        params: this.schCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          console.log(res.data);
          this.cargoCount = res.data.scancnt;
          this.totalCount = res.data.totalcnt;
        }).catch(error => {
        console.log('error' + error)
      }).finally(() => {
        console.log('finally');
      })
    },
    searchWarehouse(){

      // this.schCondition.termNo = 'A72';
      // console.log("端末ID(任意) : " + this.schCondition.termNo);
      this.warehouseName = '';
      this.warehouseID = '';
      this.warehouseCode = ''
      this.axios({
        url: '/api/oaiw004/searchWarehouse',
        method: 'get',
        params: this.schCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          console.log(res.data);
          this.warehouseName = res.data[0].bondedwarehousename;
          this.warehouseCode = "/" + res.data[0].bondedwarehousecd;
          if (res.data[0].regusercd != null) {
            this.warehouseID = "(" + res.data[0].regusercd + ")";
          }
          console.log(this.warehouseName);
          console.log(this.warehouseCode);
          console.log(this.warehouseID);
          this.termNoAuthorized = true;
        }).catch(error => {
        console.log('error' + error);
        this.termNoAuthorized = false;
      }).finally(() => {
        console.log('finally');
      })
    }
  }
}

</script>
