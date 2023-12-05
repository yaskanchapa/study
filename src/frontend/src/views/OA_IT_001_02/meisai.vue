<!-- eslint-disable vue/no-mutating-props -->
<template>

    <div class="row justify-start" align="right" style="width: 100%;">

        <div class="item flex xs4">
            <span style="padding: 0.3rem;">輸入統計品目番号</span>
            <va-input maxlength="9" v-model="idaSub.itemCD"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
            <va-input maxlength="1" v-model="idaSub.naccsCD"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
            <!-- <va-button color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg4 == 1"> 検索 </va-button> -->
        </div>

        <div class="item flex xs4">
            <span style="padding: 0.3rem;">品名</span>
            <va-input maxlength="40" v-model="idaSub.item" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs4" align="center">
            <span style="padding: 0.3rem;">原産地</span>
            <va-input maxlength="2" v-model="idaSub.countryOfOriginCD"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
            <span style="padding: 0.3rem;">-</span>
            <va-input maxlength="4" v-model="idaSub.originCertifiDisc"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

    </div>
    
    <div class="row justify-start" style="width: 100%;" align="right">

        <div class="item flex xs4">
            <span style="padding: 0.3rem;">数量(1)</span>
            <va-input maxlength="14" mask="numeral" input-class="text-right" v-model="idaSub.amo1"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
            <va-input maxlength="4" v-model="idaSub.amoUnit1"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs4">
            <span style="padding: 0.3rem;">数量(2)</span>
            <va-input maxlength="16" mask="numeral" input-class="text-right" v-model="idaSub.amo2"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
            <va-input maxlength="4" v-model="idaSub.amoUnit2"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs4">
            <span style="padding: 0.3rem;">輸入令別表</span>
            <va-input maxlength="4" v-model="idaSub.impTradeContCD"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

    </div>
    
    <div class="row justify-start" style="width: 100%;" align="right">

        <div class="item flex xs4">
            <span style="padding: 0.3rem;">BPR係数</span>
            <va-input maxlength="24" mask="numeral" input-class="text-right" v-model="idaSub.taxablesAmoPdivTo"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs4">
            <span style="padding: 0.3rem;">運賃按分</span>
            <va-input maxlength="1" v-model="idaSub.fareDivDisc"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs4">
            <span style="padding: 0.3rem;">課税価格</span>
            <va-input maxlength="3" v-model="idaSub.fobCurrencyCD"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
            <va-input maxlength="24" mask="numeral" input-class="text-right" v-model="idaSub.taxablesAmo"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

    </div>
    
    <div class="row justify-start" style="width: 100%;" align="right">

        <div class="item flex xs3">
            <span style="padding: 0.3rem;">関税減免税コード</span>
            <va-input maxlength="5" v-model="idaSub.customsExemptCD"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs3">
            <span style="padding: 0.3rem;">関税減税額</span>
            <va-input maxlength="14" mask="numeral" input-class="text-right" v-model="idaSub.customsExemptAmo"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs3">
            <span style="padding: 0.3rem;">事前教示（分類）</span>
            <va-input maxlength="9" v-model="idaSub.advanceRulingClassification"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs3">
            <span style="padding: 0.3rem;">事前教示（原産地）</span>
            <va-input maxlength="7" v-model="idaSub.advanceRulingOrigin"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

    </div>
    
    <div class="row justify-start" align="right" style="width: 100%;">

        <div class="item flex xs2">
            <span style="padding: 0.3rem;max-width: initial;">内消税等識別</span>
        </div>

        <div class="item flex xs2">
            <span style="padding: 0.3rem;max-width: initial;">減免税コード</span>
        </div>

        <div class="item flex xs2">
            <span style="padding: 0.3rem;max-width: initial;">内消税減税額</span>
        </div>

        <div class="item flex xs2">
            <span style="padding: 0.3rem;max-width: initial;">内消税等識別</span>
        </div>

        <div class="item flex xs2">
            <span style="padding: 0.3rem;max-width: initial;">減免税コード</span>
        </div>

        <div class="item flex xs2">
            <span style="padding: 0.3rem;max-width: initial;">内消税減税額</span>
        </div>

    </div>
    
    <div class="row justify-start" style="width: 100%;">

        <div class="item flex xs1" align="left">
            <span style="padding: 0.3rem;">1</span>
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="10" v-model="idaSub.inConsTaxKindCD1"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="3" v-model="idaSub.inConsTaxExemCD1"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="14" mask="numeral" v-model="idaSub.inConsTaxExemAmo1"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="left">
            <span style="padding: 0.3rem;">2</span>
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="10" v-model="idaSub.inConsTaxKindCD2"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="3" v-model="idaSub.inConsTaxExemCD2"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="14" mask="numeral" v-model="idaSub.inConsTaxExemAmo2"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

    </div>
    
    <div class="row justify-start" style="width: 100%;">

        <div class="item flex xs1" align="left">
            <span style="padding: 0.3rem;">3</span>
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="10" v-model="idaSub.inConsTaxKindCD3"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="3" v-model="idaSub.inConsTaxExemCD3"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="14" mask="numeral" v-model="idaSub.inConsTaxExemAmo3"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="left">
            <span style="padding: 0.3rem;">4</span>
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="10" v-model="idaSub.inConsTaxKindCD4"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="3" v-model="idaSub.inConsTaxExemCD4"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="14" mask="numeral" v-model="idaSub.inConsTaxExemAmo4"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

    </div>
    
    <div class="row justify-start" style="width: 100%;">

        <div class="item flex xs1" align="left">
            <span style="padding: 0.3rem;">5</span>
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="10" v-model="idaSub.inConsTaxKindCD5"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="3" v-model="idaSub.inConsTaxExemCD5"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="14" mask="numeral" v-model="idaSub.inConsTaxExemAmo5"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="left">
            <span style="padding: 0.3rem;">6</span>
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="10" v-model="idaSub.inConsTaxKindCD6"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="3" v-model="idaSub.inConsTaxExemCD6"  style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs1" align="right">
        </div>

        <div class="item flex xs1" align="right">
            <va-input maxlength="14" mask="numeral" v-model="idaSub.inConsTaxExemAmo6"  style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>
        
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
        idaSub: Object,
        disabledFlg: String,
        disabledFlg4: Number
    },
    data: function () {
        return {
            micData: this.detailData,
        };
    }
};
</script>
<style>
span {
    max-width: fit-content;
    line-height: 2;
}
</style>
