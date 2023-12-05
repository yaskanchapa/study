<!-- eslint-disable vue/no-parsing-error -->
<script setup>
import oaet001store from '@/store/OA_ET_001/OAET001Store'
import MEC from '../OA_ET_001_03/modal.mec.vue'
import EDA from '../OA_ET_001_02/modal.eda.vue'
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
import { getLogicalLock, getLogicalLockWithModal, releaseLogicalLock } from '../../components/HawbLogicalLock/HawbLogicalLock.vue' // eslint-disable-line
import loginStore from '@/store/OC_CS_003/loginUserInfoStore'
</script>

<template>
    <div style="align-items: center; justify-content: center;">
        <va-button style="width:100%;" class="mr-4 my-1" @click="checkLogicalLock"> {{ data }} </va-button>
    </div>
    <div v-show="false">
        <va-modal v-model="showModalWithFixedLayout" :z-index="1021" max-width="1500px" hide-default-actions
            without-transitions no-outside-dismiss fixed-layout :before-close="beforeClose">
            <template #header>
                <div class="bg-[#cce0ff]" v-if="detailData.editFlg == '0' || detailData.editFlg == ''">
                    <va-icon name='va-clear' style="float: right;" @click="beforeClose" />
                    <h2 style="text-align: left;font-size: x-large;"> {{ title }} [ 未登録 ]</h2>
                </div>
                <div class="bg-[#ffcccc]" v-if="detailData.editFlg == '1'">
                    <va-icon name='va-clear' style="float: right;" @click="beforeClose" />
                    <h2 style="text-align: left;font-size: x-large;"> {{ title }} [ 登録済 ]</h2>
                </div>
            </template>
            <va-inner-loading :loading="isLoading" :size="60">
                <va-card>
                    <div class="container" style="width:1400px">
                        <component :is="currentView" v-model:detailData="detailData" v-model:cwbList="cwbList"
                            v-model:edaSubList="edaSubList" v-bind:changeModalLayout="changeModalLayout"
                            v-model:insertModalData="insertModalData" v-model:changeDetailDataEDA="changeDetailDataEDA"
                            v-model:changeDetailDataMEC="changeDetailDataMEC" v-model:saveModalData="saveModalData"
                            v-model:prevFlg="prevFlg" v-model:nextFlg="nextFlg" v-model:disabledFlg="disabledFlg"
                            v-model:disabledFlg2="disabledFlg2" />
                    </div>
                </va-card>
            </va-inner-loading>
        </va-modal>
    </div>
</template>


<script>

export default {
    components: {
        mec: MEC, // eslint-disable-line
        eda: EDA  // eslint-disable-line
    },
    data: function () {
        return {
            showConfirm: false,
            currentView: '',
            detailData: {

            },
            edaSubList: [

            ],
            cwbList: [

            ],
            data: this.params.value,
            showModalWithFixedLayout: false,
            title: '',
            edaCheckFlag: 'true',
            prevFlg: true,
            nextFlg: true,
            category: '',
            isLoading: false,
            bgColor: 'rgb(50 60 30)',
            disabledFlg: false,
            disabledFlg2: false,
            saveInsertFlg: '',
        }
    },
    beforeMount() { },
    mounted() {

    },
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },
        checkLogicalLock() {
            const dpCd = loginStore.getters.getAuthPermission.departmentCd
            if (dpCd !== this.params.data.customsPlaceCd) { 
                notificationError("現在ログインしたユーザと別の通関場所で登録したデータです。")
                return
            }
        
            getLogicalLockWithModal(this.axios, this.$vaModal, {
                awbNo: this.params.data.awbNo,
                cwbNo: this.params.data.cwbNo,
                userCd: loginStore.getters.getAuthPermission.userCd,
                override: false
            }, this.showDetailModal, this.params)
        },
        showDetailModal() {
            if (this.params.data.eda === '1') {
                this.selectEdaData({ awbNo: this.params.data.awbNo, cwbNo: this.params.data.cwbNo })
                this.currentView = 'eda'
                this.category = 'EDA'
            } else {
                this.selectMecData({ awbNo: this.params.data.awbNo, cwbNo: this.params.data.cwbNo })
                this.currentView = 'mec'
                this.category = 'MEC'
            }
        },
        selectMecData(params) {
            this.axios.get('/api/oaet001/selectMECDetail', {
                params: {
                    awbNo: params.awbNo,
                    cwbNo: params.cwbNo
                }
            })
                .then(res => {
                    this.detailData = res.data
                    this.detailData.category = 'MEC'
                    const tmpCwbList = oaet001store.getters.getMecCwbList
                    this.cwbList = []
                    for (let i = 0; i < tmpCwbList.length; i++) {
                        this.cwbList.push(tmpCwbList[i].cwbNo)
                    }
                    if (this.cwbList[0] === this.detailData.cwbNo) {
                        this.prevFlg = true
                    } else {
                        this.prevFlg = false
                    }
                    if (this.cwbList[this.cwbList.length - 1] === this.detailData.cwbNo) {
                        this.nextFlg = true
                    } else {
                        this.nextFlg = false
                    }
                    if (!this.showModalWithFixedLayout) {
                        this.showModalWithFixedLayout = !this.showModalWithFixedLayout
                    }
                    this.title = 'MEC登録'
                })
                .catch(error => {
                    notificationError("検索処理でエラーが発生しました。", error.message)
                })

        },
        selectEdaData(params) {
            this.axios.get('/api/oaet001/selectEDADetail', {
                params: {
                    awbNo: params.awbNo,
                    cwbNo: params.cwbNo
                }
            }).then(res => {
                if (res.data.headerData === null) {
                    notificationWarn("他の通関場所で登録したデータです。")
                    releaseLogicalLock(this.axios, {
                        awbNo: params.awbNo,
                        cwbNo: params.cwbNo,
                        userCd: loginStore.getters.getAuthPermission.userCd,
                        override: false
                    })
                    return
                }
                this.detailData = res.data.headerData
                this.edaSubList = res.data.detailDataList
                this.detailData.category = 'EDA'
                const tmpCwbList = oaet001store.getters.getEdaCwbList
                this.cwbList = []
                for (let i = 0; i < tmpCwbList.length; i++) {
                    this.cwbList.push(tmpCwbList[i].cwbNo)
                }
                if (this.cwbList[0] === this.detailData.cwbNo) {
                    this.prevFlg = true
                } else {
                    this.prevFlg = false
                }
                if (this.cwbList[this.cwbList.length - 1] === this.detailData.cwbNo) {
                    this.nextFlg = true
                } else {
                    this.nextFlg = false
                }
                if (!this.showModalWithFixedLayout) {
                    this.showModalWithFixedLayout = !this.showModalWithFixedLayout
                }
                this.title = 'EDA登録'
            }).catch(error => {
                notificationError("検索処理でエラーが発生しました。", error.message)
            })
        },
        changeModalLayout() {
            if (this.params.data.eda === '1') {
                this.params.data.eda = '0'
            } else {
                this.params.data.eda = '1'
            }
            this.showModalWithFixedLayout = !this.showModalWithFixedLayout
            this.showDetailModal()
        },
        invokeParentMethod() {
            this.params.context.componentParent.methodFromParent(
                `Row: ${this.params.node.rowIndex}, Col: ${this.params.colDef.headerName}`
            );
        },
        getNextImagePath(val) {

        },
        getNextCwbNo() {

        },
        insertModalData() {
            this.saveInsertFlg = 'IN'
            if (this.params.data.eda === '1') {
                getLogicalLockWithModal(this.axios, this.$vaModal, {
                    awbNo: this.detailData.awbNo,
                    cwbNo: this.detailData.cwbNo,
                    userCd: loginStore.getters.getAuthPermission.userCd,
                    override: false
                }, this.checkEdaData, 'IN')
            } else {
                getLogicalLockWithModal(this.axios, this.$vaModal, {
                    awbNo: this.detailData.awbNo,
                    cwbNo: this.detailData.cwbNo,
                    userCd: loginStore.getters.getAuthPermission.userCd,
                    override: false
                }, this.checkMecData, 'IN')
            }
        },
        saveModalData() {
            this.saveInsertFlg = 'SV'
            if (this.params.data.eda === '1') {
                getLogicalLockWithModal(this.axios, this.$vaModal, {
                    awbNo: this.detailData.awbNo,
                    cwbNo: this.detailData.cwbNo,
                    userCd: loginStore.getters.getAuthPermission.userCd,
                    override: false
                }, this.checkEdaData, 'SV')
            } else {
                getLogicalLockWithModal(this.axios, this.$vaModal, {
                    awbNo: this.detailData.awbNo,
                    cwbNo: this.detailData.cwbNo,
                    userCd: loginStore.getters.getAuthPermission.userCd,
                    override: false
                }, this.checkMecData, 'SV')
            }
        },
        checkMecData(flg) {
            this.changeLoading()
            this.axios({
                url: '/api/oaet001/checkMecData',
                method: 'get',
                params: this.detailData,
                enctype: 'multipart/form-data'
            })
                .then(res => {
                    if (res.data.errFlg === 'error') {
                        notificationError(res.data.message)
                    } else if (res.data.errFlg === 'warning') {
                        notificationWarn(res.data.message)
                        if (this.saveInsertFlg === 'IN') {
                            this.updateMecData()
                        } else {
                            this.saveMecData()
                        }
                    } else if (res.data.errFlg === 'false') {
                        if (this.saveInsertFlg === 'IN') {
                            this.updateMecData()
                        } else {
                            this.saveMecData()
                        }
                    }
                }).catch(error => {
                    notificationError("検索処理でエラーが発生しました。", error.message)
                }).finally(() => {
                    this.changeLoading()
                })
        },
        updateMecData() {
            this.changeLoading()
            this.axios.post('/api/oaet001/insertMecData', this.detailData)
                .then(res => {
                    if (res.data.errFlg === 'error') {
                        notificationError(res.data.message)
                    } else {
                        notificationSuccess(res.data.message, "", 3)
                    }
                })
                .catch(error => {
                    notificationError(error)
                }).finally(() => {
                    this.changeLoading()
                })
        },
        checkEdaData(flg) {
            const edaData = {
                headerData: this.detailData,
                detailDataList: this.edaSubList,
                edaCheckFlag: this.edaCheckFlag
            }
            this.changeLoading()
            this.axios.post('/api/oaet001/checkEdaData', edaData)
                .then(res => {
                    if (res.data.errFlg === 'error') {
                        notificationError(res.data.message)
                    } else if (res.data.errFlg === 'warning') {
                        notificationWarn(res.data.message)
                        if (this.saveInsertFlg === 'IN') {
                            this.updateEdaData()
                        } else {
                            this.saveEdaData()
                        }
                    } else if (res.data.errFlg === 'false') {
                        if (this.saveInsertFlg === 'IN') {
                            this.updateEdaData()
                        } else {
                            this.saveEdaData()
                        }
                    }
                })
                .catch(error => {
                    notificationError(error)
                }).finally(() => {
                    this.changeLoading()
                })
        },
        updateEdaData() {
            const edaData = {
                headerData: this.detailData,
                detailDataList: this.edaSubList,
                edaCheckFlag: this.edaCheckFlag
            }
            this.changeLoading()
            this.axios.post('/api/oaet001/insertEdaData', edaData)
                .then(res => {
                    if (res.data.errFlg === 'error') {
                        notificationError(res.data.message)
                    } else {
                        notificationSuccess(res.data.message, "", 3)
                    }
                })
                .catch(error => {
                    notificationError(error)
                }).finally(() => {
                    this.changeLoading()
                })

        },
        changeDetailDataEDA(msg, originAwb, originCwb) {
            let nextAwb
            let nextCwb
            if (msg === 'S') {
                const tmpCwbList = oaet001store.getters.getEdaCwbList
                for (let i = 0; i < tmpCwbList.length; i++) {
                    if (tmpCwbList[i].cwbNo === this.detailData.cwbNo) {
                        nextAwb = tmpCwbList[i].awbNo
                        nextCwb = tmpCwbList[i].cwbNo
                    }
                }
            } else if (msg === 'N') {
                const tmpCwbList = oaet001store.getters.getEdaCwbList
                for (let i = 0; i < tmpCwbList.length; i++) {
                    if (tmpCwbList[i].cwbNo === this.detailData.cwbNo) {
                        if (i === tmpCwbList.length - 1) {
                            notificationError("最終のデータです。")
                            return
                        } else {
                            nextAwb = tmpCwbList[i + 1].awbNo
                            nextCwb = tmpCwbList[i + 1].cwbNo
                        }
                    }
                }
            } else if (msg === 'P') {
                const tmpCwbList = oaet001store.getters.getEdaCwbList
                for (let i = 0; i < tmpCwbList.length; i++) {
                    if (tmpCwbList[i].cwbNo === this.detailData.cwbNo) {
                        if (i === 0) {
                            this.prevFlg = false
                            notificationError("最初のデータです。")
                            return
                        } else {
                            this.prevFlg = true
                            nextAwb = tmpCwbList[i - 1].awbNo
                            nextCwb = tmpCwbList[i - 1].cwbNo
                        }
                    }
                }
            }
            releaseLogicalLock(this.axios, {
                awbNo: originAwb,
                cwbNo: originCwb,
                userCd: loginStore.getters.getAuthPermission.userCd,
                override: false
            })
            getLogicalLockWithModal(this.axios, this.$vaModal, {
                awbNo: nextAwb,
                cwbNo: nextCwb,
                userCd: loginStore.getters.getAuthPermission.userCd,
                override: false
            }, this.selectEdaData, { awbNo: nextAwb, cwbNo: nextCwb })

        },
        changeDetailDataMEC(msg, originAwb, originCwb) {
            let nextAwb
            let nextCwb

            if (msg === 'S') {
                const tmpCwbList = oaet001store.getters.getMecCwbList
                for (let i = 0; i < tmpCwbList.length; i++) {
                    if (tmpCwbList[i].cwbNo === this.detailData.cwbNo) {
                        nextAwb = tmpCwbList[i].awbNo
                        nextCwb = tmpCwbList[i].cwbNo
                    }
                }
            } else if (msg === 'N') {
                const tmpCwbList = oaet001store.getters.getMecCwbList
                for (let i = 0; i < tmpCwbList.length; i++) {
                    if (tmpCwbList[i].cwbNo === this.detailData.cwbNo) {
                        if (i === tmpCwbList.length - 1) {
                            notificationError("最終のデータです。")
                            return
                        } else {
                            nextAwb = tmpCwbList[i + 1].awbNo
                            nextCwb = tmpCwbList[i + 1].cwbNo
                        }
                    }
                }
            } else if (msg === 'P') {
                const tmpCwbList = oaet001store.getters.getMecCwbList
                for (let i = 0; i < tmpCwbList.length; i++) {
                    if (tmpCwbList[i].cwbNo === this.detailData.cwbNo) {
                        if (i === 0) {
                            notificationError("最初のデータです。")
                            return
                        } else {
                            nextAwb = tmpCwbList[i - 1].awbNo
                            nextCwb = tmpCwbList[i - 1].cwbNo
                        }
                    }
                }
            }
            releaseLogicalLock(this.axios, {
                awbNo: originAwb,
                cwbNo: originCwb,
                userCd: loginStore.getters.getAuthPermission.userCd,
                override: false
            })
            getLogicalLockWithModal(this.axios, this.$vaModal, {
                awbNo: nextAwb,
                cwbNo: nextCwb,
                userCd: loginStore.getters.getAuthPermission.userCd,
                override: false
            }, this.selectMecData, { awbNo: nextAwb, cwbNo: nextCwb })

        },
        beforeClose(hide) {
            releaseLogicalLock(this.axios, {
                awbNo: this.detailData.awbNo,
                cwbNo: this.detailData.cwbNo,
                userCd: loginStore.getters.getAuthPermission.userCd,
                override: false
            })
            this.showModalWithFixedLayout = !this.showModalWithFixedLayout
        },
        onChangeIdaCheck() {
            if (this.detailData.edaCheckFlag === '1') {
                this.detailData.edaCheckFlag = '0'
            } else {
                this.detailData.edaCheckFlag = '1'
            }
        },
        saveMecData() {
            this.changeLoading()
            this.axios.post('/api/oaet001/saveMecData', this.detailData)
                .then(res => {
                    if (res.data.errFlg === 'error') {
                        notificationError(res.data.message)
                    } else {
                        notificationSuccess(res.data.message, "", 3)
                    }
                })
                .catch(error => {
                    notificationError(error)
                }).finally(() => {
                    this.changeLoading()
                })
        },
        saveEdaData() {
            this.changeLoading()
            const edaData = {
                headerData: this.detailData,
                detailDataList: this.edaSubList,
                edaCheckFlag: this.edaCheckFlag
            }
            this.axios.post('/api/oaet001/saveEdaData', edaData)
                .then(res => {
                    if (res.data.errFlg === 'error') {
                        notificationError(res.data.message)
                    } else {
                        notificationSuccess(res.data.message, "", 3)
                    }
                })
                .catch(error => {
                    notificationError(error)
                }).finally(() => {
                    this.changeLoading()
                })

        },
    },

};
</script>