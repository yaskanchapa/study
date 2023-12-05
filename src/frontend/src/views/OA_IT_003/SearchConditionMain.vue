<template>
    <va-card>
        <div class="row justify-start items-center px-3 pt-2 pb-[1px]">
            <div class="flex items-center xs4">
                <span class="first-column-label">申告日</span>
                <Datepicker
                    v-model="schCondition.declareDate1"
                    :auto-apply="true"
                    @closed="declareDate1PickerClosedChange"
                    :format="DatePickerFormat"
                    :enable-time-picker="false"
                    style="min-width: 12%"
                    placeholder="申告日(FROM)"
                />
                <span style="padding-right: 0.5rem; padding-left: 0.2rem; min-width: 3%; text-align: center; font-size: 20px">～</span>
                <Datepicker
                    v-model="schCondition.declareDate2"
                    :auto-apply="true"
                    @closed="declareDate2PickerClosedChange"
                    :format="DatePickerFormat"
                    :enable-time-picker="false"
                    style="min-width: 7%; size"
                    placeholder="申告日(TO)"
                />
            </div>

            <div class="flex items-center xs4">
                <span class="second-column-label">許可日</span>
                <Datepicker
                    v-model="schCondition.permitDate1"
                    :auto-apply="true"
                    @closed="permitDate1PickerClosedChange"
                    :format="DatePickerFormat"
                    :enable-time-picker="false"
                    style="min-width: 11%"
                    placeholder="許可日(FROM)"
                />
                <span style="padding-right: 0.5rem; padding-left: 0.2rem; min-width: 3%; text-align: center; font-size: 20px">～</span>
                <Datepicker
                    v-model="schCondition.permitDate2"
                    :auto-apply="true"
                    @closed="permitDate2PickerClosedChange"
                    :format="DatePickerFormat"
                    :enable-time-picker="false"
                    style="min-width: 12%"
                    placeholder="許可日(TO)"
                />
            </div>

            <div class="flex items-center xs4">
                <span class="third-column-label">通関コード</span>
                <va-input v-model="schCondition.customsCode" style="min-width: 9%" placeholder="通関コード" />
                <span class="second-column-label pl-3">上屋コード</span>
                <va-input v-model="schCondition.clearPlaceCode" style="min-width: 9%" placeholder="上屋コード" />
            </div>
        </div>
        <div class="row justify-start items-center px-3 pt-2 pb-[1px]">
            <div class="flex items-center xs4">
                <span class="first-column-label">MAWBNo</span>
                <va-input v-model="schCondition.mawbNo" style="min-width: 25%" placeholder="MAWBNo" />
            </div>
            <div class="flex items-center xs4">
                <span class="second-column-label">HAWBNo</span>
                <va-input v-model="schCondition.hawbNo" style="" placeholder="HAWBNo" />
            </div>

            <div class="flex items-center xs4">
                <span class="third-column-label">FLT</span>
                <va-input v-model="schCondition.flt1" style="min-width: 11%" placeholder="FLT1" />
                <span class="px-2">/</span>
                <va-input v-model="schCondition.flt2" style="min-width: 10%" placeholder="FLT2" />
            </div>
        </div>
        <div class="row justify-start items-center px-3 pt-2 pb-[1px]">
            <div class="flex items-center xs4">
                <span class="first-column-label">積出地</span>
                <va-input v-model="schCondition.unloadPlace" style="min-width: 25%" placeholder="積出地" />
            </div>

            <div class="flex items-center xs4">
                <span class="second-column-label">社内整理番号</span>
                <va-input v-model="schCondition.inHouseNo" style="min-width: 10%" placeholder="社内整理番号" />
                <va-select
                    style="min-width: 10%; font-size: 15px; margin-left: 5px"
                    v-model="schCondition.inHouseCd"
                    :options="this.inHouseListData"
                    :text-by="(option) => option.namecd"
                    :track-by="(option) => option.namecd + ' : ' + option.name"
                    :value-by="(option) => option.namecd"
                    placeholder="社内整理番号"
                />
            </div>

            <div class="flex items-center xs4">
                <span class="third-column-label">輸出入者コード</span>
                <va-input v-model="schCondition.clearPlaceName" style="min-width: 27%" placeholder="輸出入者コード" />
            </div>
        </div>
        <div class="row justify-start items-center px-3 pt-2 pb-[1px]">
            <div class="flex items-center xs4">
                <span class="first-column-label">輸入者名</span>
                <va-input v-model="schCondition.importCustomerName" style="min-width: 25%" placeholder="輸入者名" />
            </div>
            <div class="flex items-center xs4">
                <span class="second-column-label">輸入者住所</span>
                <va-input v-model="schCondition.importCustomerAdd" style="min-width: 59%" placeholder="輸入者住所" />
            </div>
            <div class="flex items-center xs4">
                <span class="third-column-label">記事1</span>
                <va-input v-model="schCondition.news1" style="min-width: 25%" placeholder="記事1" />
            </div>
        </div>
        <div class="row justify-start items-center px-3 pt-2 pb-[1px]">
            <div class="flex items-center xs4">
                <span class="first-column-label">輸出者名</span>
                <va-input v-model="schCondition.companyName" style="min-width: 25%" placeholder="輸出者名" />
            </div>
            <div class="flex items-center xs4">
                <span class="second-column-label">輸出者住所</span>
                <va-input v-model="schCondition.exportCustomerAdd" style="min-width: 59%" placeholder="輸出者住所" />
            </div>
            <div class="flex items-center xs4">
                <span class="third-column-label">記事2</span>
                <va-input v-model="schCondition.news2" style="min-width: 59%" placeholder="記事2" />
            </div>
        </div>
        <div class="row justify-start items-center px-3 pt-2 pb-[1px]">
            <div class="flex items-center xs4">
                <span class="first-column-label">品名</span>
                <va-input v-model="schCondition.itemName" style="min-width: 25%" placeholder="品名" />
            </div>
            <div class="flex items-center xs4">
                <span class="second-column-label">品目番号</span>
                <va-input v-model="schCondition.itemNo" style="min-width: 15%" placeholder="品目番号" />
                <va-input v-model="schCondition.naccsCode" style="min-width: 10%; margin-left: 5px" placeholder="NACCSコード" />
            </div>
            <div class="flex items-center xs4">
                <span class="third-column-label">原産地</span>
                <va-input v-model="schCondition.consignorCountryName" style="min-width: 9%" placeholder="原産地" />
                <span style="padding: 0.3rem; min-width: 6%; text-align: center; font-size: 20px">-</span>
                <va-input v-model="schCondition.consignCertifiDisc" style="min-width: 11%" placeholder="原産証明" />
            </div>
        </div>
        <div class="row justify-start items-center px-3 pt-2 pb-2">
            <div class="flex items-end xs11"></div>
            <div class="flex items-end xs1">
                <va-button style="min-width: 10%" color="#4278EB" size="medium" @click="searchImpData(schCondition)">検索</va-button>
            </div>
        </div>
    </va-card>
</template>
<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import { notificationError } from '@/components/Notification/NotificationApi'

export default {
    components: {
        Datepicker, // eslint-disable-line
    },
    props: {
        searchImpData: Function,
    },
    data() {
        return {
            schCondition: {
                declareDate1: null,
                declareDate2: null,
                permitDate1: null,
                permitDate2: null,
                customsCode: '',
                clearPlaceCode: '',
                mawbNo: '',
                hawbNo: '',
                flt1: '',
                flt2: '',
                companyName: '',
                importCustomerName: '',
                consignorName: '',
                itemName: '',
                itemCode: '',
                naccsCode: '',
                consignorCountryName: '',
                consignCertifiDisc: '',
                inHouseNo: '',
                inHouseCd: '',
                unloadPlace: '',
                importCustomerAdd: '',
                exportCustomerAdd: '',
                consignorAdd: '',
                itemNo: '',
                clearPlaceName: '',
                news1: '',
                news2: '',
            },
            mawbNoOption: ['one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine'],
            inHouseListData: [],
            declareDate1: {
                arrPortDate: '',
                awbno: '',
                isBatchTarget: true,
                isBatchNotTarget: true,
                isUnMatchHawbCnt: true,
                isUnMatchPieceCnt: true,
                isMatch: true,
            },
            declareDate2: {
                arrPortDate1: '',
                awbno: '',
                isBatchTarget: true,
                isBatchNotTarget: true,
                isUnMatchHawbCnt: true,
                isUnMatchPieceCnt: true,
                isMatch: true,
            },
            DatePickerFormat: 'yyyy-MM-dd',
        }
    },
    mounted() {
        this.inHouseList()
    },
    methods: {
        declareDate1PickerClosedChange() {
            if (this.schCondition.declareDate1) {
                this.schCondition.declareDate1 = moment(this.schCondition.declareDate1).format('YYYY-MM-DD')
            }
        },
        declareDate2PickerClosedChange() {
            if (this.schCondition.declareDate2) {
                this.schCondition.declareDate2 = moment(this.schCondition.declareDate2).format('YYYY-MM-DD')
            }
        },
        permitDate1PickerClosedChange() {
            if (this.schCondition.permitDate1) {
                this.schCondition.permitDate1 = moment(this.schCondition.permitDate1).format('YYYY-MM-DD')
            }
        },
        permitDate2PickerClosedChange() {
            if (this.schCondition.permitDate2) {
                this.schCondition.permitDate2 = moment(this.schCondition.permitDate2).format('YYYY-MM-DD')
            }
        },
        inHouseList() {
            this.inHouseListData = []
            this.inHouseListData.push({ namecd: '', name: '無し' })
            console.log(this.inHouseListData)
            this.axios({
                url: '/api/oait003/inHouseList',
                method: 'get',
                enctype: 'multipart/form-data',
            })
                .then((res) => {
                    for (let i = 0; i < res.data.length; i++) {
                        this.inHouseListData.push(res.data[i])
                    }
                    console.log(this.inHouseListData)
                })
                .catch((error) => {
                    console.log('error' + error)
                    notificationError('Error 管理者にお問い合わせください : ' + error)
                })
                .finally(() => {
                    console.log('finally')
                })
        },
    },
}
</script>
<style scoped>
.first-column-label {
    min-width: 15%;
    max-width: 15%;
    padding: 0.3rem;
}
.second-column-label {
    min-width: 20%;
    max-width: 20%;
    padding: 0.3rem;
}
.third-column-label {
    min-width: 23%;
    max-width: 23%;
    padding: 0.3rem;
}
</style>
