<!-- eslint-disable vue/no-mutating-props -->
<template>
    <div class="row justify-start" align="right" style="width: 100%;">
        <div class="item flex xs6">
            <span style="padding: 0.3rem;">輸入統計品目番号</span>
            <va-input maxlength="9" v-model="edaSub.expStatItemNo"  style="width: 5px;padding: 0.1rem;" />
            <va-input maxlength="1" input-class="text-center" v-model="edaSub.naccsCd"  style="width: 5px;padding: 0.1rem;" />
            <!-- <va-button color="info" size="small"> 検索 </va-button> -->
        </div>
        <div class="item flex xs6">
            <span style="padding: 0.3rem;">品名</span>
            <va-input maxlength="40" v-model="edaSub.item" style="width: 600px;padding: 0.1rem;" />
        </div>
    </div>
    
    <div class="row justify-start" style="width: 100%;" align="right">
        <div class="item flex xs6">
            <span style="padding: 0.3rem;">数量(1)</span>
            <va-input maxlength="11" type="number" step="0.00" input-class="text-right" v-model="edaSub.amo1"  style="width: 300px;padding: 0.1rem;" />
            <va-input maxlength="4" v-model="edaSub.amoUnit1"  style="width: 5px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs6">
            <span style="padding: 0.3rem;">数量(2)</span>
            <va-input maxlength="11" type="number" step="0.00" input-class="text-right" v-model="edaSub.amo2"  style="width: 300px;padding: 0.1rem;" />
            <va-input maxlength="4" v-model="edaSub.amoUnit2"  style="width: 5px;padding: 0.1rem;" />
        </div>
    </div>
    
    <div class="row justify-start" style="width: 100%;" align="right">
        <div class="item flex xs4">
            <span style="padding: 0.3rem;">BPR按分係数</span>
            <va-input maxlength="17" type="number" step="0.00" input-class="text-right" v-model="edaSub.basicPriceDivCoef"  style="width: 5px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs4">
            <span style="padding: 0.3rem;">BPR通貨コード</span>
            <va-input maxlength="3" v-model="edaSub.basicPriceCurCd"  style="width: 5px;padding: 0.1rem;" />
            <va-input maxlength="18" type="number" step="0.00" input-class="text-right" v-model="edaSub.basicPriceAmo" style="width: 5px;padding: 0.1rem;" />
        </div>
    </div>
    
    <div class="row justify-start" style="width: 100%;" align="right">
        <div class="item flex xs8">
            <span style="padding: 0.3rem;">他法令コード1</span>
            <va-input maxlength="2" input-class="text-center" v-model="edaSub.otherLawCd1"  style="width: 5px;padding: 0.1rem;" />
            <va-input maxlength="2" input-class="text-center" v-model="edaSub.otherLawCd2" style="width: 5px;padding: 0.1rem;" />
            <va-input maxlength="2" input-class="text-center" v-model="edaSub.otherLawCd3" style="width: 5px;padding: 0.1rem;" />
            <va-input maxlength="2" input-class="text-center" v-model="edaSub.otherLawCd4" style="width: 5px;padding: 0.1rem;" />
            <va-input maxlength="2" input-class="text-center" v-model="edaSub.otherLawCd5" style="width: 5px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs4">
            <span style="padding: 0.3rem;">輸出貿易管理令別表コード</span>
            <va-input maxlength="5" v-model="edaSub.expTradeContCd" style="width: 5px;padding: 0.1rem;" />
        </div>
    </div>
    
    <div class="row justify-start" align="right" style="width: 100%;">
        <div class="item flex xs3">
            <span style="padding: 0.3rem;">外為法第48条コード</span>
            <va-input maxlength="1" v-model="edaSub.forexchLow48" style="width: 5px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs3">
            <span style="padding: 0.3rem;">関税減免戻税コード</span>
            <va-input maxlength="5" v-model="edaSub.customsExemptCd" style="width: 5px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs3">
            <span style="padding: 0.3rem;">内国消費税免税コード</span>
            <va-input maxlength="1" v-model="edaSub.inconsTaxExempCd" style="width: 5px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs3">
            <span style="padding: 0.3rem;">内国消費税免税識別</span>
            <va-input maxlength="1" v-model="edaSub.inconsTaxExempDisc" style="width: 5px;padding: 0.1rem;" />
        </div>
    </div>
    
    <div class="row justify-start" style="width: 100%;">
       
    </div>
    
</template>
<script>
// eslint-disable-next-line no-unused-vars
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line

export default {
    props: {
        edaSub: Object
    },
    data: function () {
        return {
            micData: this.detailData,
           
        };
    }
};
</script>
