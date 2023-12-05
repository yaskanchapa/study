<!-- eslint-disable vue/no-parsing-error -->
<script setup>
import oait001store from '@/store/OA_IT_001/OAIT001Store'
import MIC from '../OA_IT_001_03/modal.mic.vue'
import IDA from '../OA_IT_001_02/modal.ida.vue'
import loginStore from '@/store/OC_CS_003/loginUserInfoStore'
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
import { getLogicalLock, getLogicalLockWithModal, releaseLogicalLock } from '../../components/HawbLogicalLock/HawbLogicalLock.vue' // eslint-disable-line
</script>

<template align="center" >
    
    <div style="align-items: center; justify-content: center">
        <va-button style="width:100%;"  @click="checkLogicalLock"> {{ data }} </va-button>
    </div>

    <div v-show="false">
        <va-modal v-model="showModalWithFixedLayout" :z-index="1021" max-width="1500px" 
            hide-default-actions without-transitions no-outside-dismiss fixed-layout :before-close="beforeClose">
            <template  #header>
                <div class="bg-[#cce0ff]" v-if="detailData.editFlg == '0' || detailData.editFlg == ''">
                    <va-icon name='va-clear' style="float: right;" @click="beforeClose" />
                    <h2 style="text-align: left;font-size: x-large;"> {{ title }}  [ 未登録 ]</h2>    
                </div>
                <div class="bg-[#ffcccc]" v-if="detailData.editFlg == '1'">
                    <va-icon name='va-clear' style="float: right;" @click="beforeClose" />
                    <h2 style="text-align: left;font-size: x-large;"> {{ title }}  [ 登録済 ]</h2>    
                </div>
            </template>

            <va-inner-loading :loading="isLoading" :size="60">
                <va-card>
                    <div class="container" style="width:1400px">
                        <component :is="currentView" 
                            v-model:detailData="detailData" 
                            v-model:cwbList="cwbList" 
                            v-model:idaSubList="idaSubList" 
                            v-bind:changeModalLayout="changeModalLayout"
                            v-model:insertModalData="insertModalData"
                            v-model:changeDetailDataIDA="changeDetailDataIDA"
                            v-model:changeDetailDataMIC="changeDetailDataMIC"
                            v-model:writeIDA="writeIDA"
                            v-model:errorRowData="errorRowData"
                            v-model:prevFlg="prevFlg"
                            v-model:nextFlg="nextFlg"
                            v-model:saveModalData="saveModalData"
                            v-model:disabledFlg="disabledFlg"/>
                    </div>
                </va-card>
            </va-inner-loading>
        </va-modal>
    </div>
</template>

<script>
export default {
    components: {
        mic: MIC, // eslint-disable-line
        ida: IDA  // eslint-disable-line
    },
    data: function () {
        return {
            showConfirm: false,
            message:'hehe',
            currentView: '',
            detailData: {},  
            idaSubList: [],
            cwbList: [],
            data: this.params.value,
            showModalWithFixedLayout: false,
            title: '',
            isLoading: false,
            errorRowData: [],
            category: '',
            prevFlg: true,
            nextFlg: true,
            pdfFile: null,
            disabledFlg: '0',
            bgColor: 'rgb(62 65 72)'
        }
    },
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },
        checkLogicalLock() { 
            getLogicalLockWithModal(this.axios, this.$vaModal, {
                awbNo: this.params.data.awbNo,
                cwbNo: this.params.data.cwbNo,
                userCd: loginStore.getters.getAuthPermission.userCd,
                override: false
            }, this.showDetailModal, this.params)
        },
        showDetailModal() {
            if (this.params.data.idaFlg === '1') {
                this.selectIdaData({awbNo: this.params.data.awbNo, cwbNo: this.params.data.cwbNo })
                this.category = 'IDA'
            } else {
                this.selectMicData({awbNo: this.params.data.awbNo , cwbNo: this.params.data.cwbNo })
                this.category = 'MIC'
            }
        },
        selectMicData(params) {
            this.changeLoading()
            this.axios.get('/api/oait001/selectMICDetail', {
                params: {
                    awbNo: params.awbNo,
                    cwbNo: params.cwbNo
                }
            }).then(res => {
                if (res.data.errorFlg === 'error') {
                    notificationError(res.data.message)
                } else { 
                    this.detailData = res.data

                    this.disabledFlg = res.data.disabledFlg

                    this.detailData.category = 'MIC'
                    const tmpCwbList = oait001store.getters.getMicCwbList
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
                    this.currentView = 'mic'
                    this.title = 'MIC登録'
                    if (!this.showModalWithFixedLayout) {
                        this.showModalWithFixedLayout = !this.showModalWithFixedLayout
                    }
                }
            // eslint-disable-next-line n/handle-callback-err
            }) .catch(error => {
                    notificationError("MIC登録画面表示処理でエラーが発生しました。")
            }).finally(() => {
                this.changeLoading()
                return true
            })
            
        },
        selectIdaData(params) {
            this.changeLoading()
            this.axios.get('/api/oait001/selectIDADetail', {
                params: {
                    awbNo: params.awbNo,
                    cwbNo: params.cwbNo
                }
            }).then(res => {
                if (res.data.errorFlg === 'error') {
                    notificationError(res.data.message)
                } else { 
                    this.detailData = res.data.mainDao

                    this.disabledFlg = res.data.mainDao.disabledFlg

                    this.detailData.category = 'IDA'
                    this.idaSubList = res.data.subDao
                    
                    const tmpCwbList = oait001store.getters.getIdaCwbList
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
                    this.currentView = 'ida'
                    this.title = 'IDA登録'
                    if (!this.showModalWithFixedLayout) {
                        this.showModalWithFixedLayout = !this.showModalWithFixedLayout
                    }
                }
                // eslint-disable-next-line n/handle-callback-err
            }).catch(error => {
                notificationError("IDA登録画面表示処理でエラーが発生しました。")
            }).finally(() => {
                this.changeLoading()
                return true
            })
            
        },
        changeModalLayout() { 
            if (this.params.data.idaFlg === '1') {
                this.params.data.idaFlg = '0'
            } else {
                this.params.data.idaFlg = '1'
             }
            this.showModalWithFixedLayout = !this.showModalWithFixedLayout
            this.showDetailModal()
        },
        invokeParentMethod() {
            this.params.context.componentParent.methodFromParent(
                `Row: ${this.params.node.rowIndex}, Col: ${this.params.colDef.headerName}`
            );
        },
        insertModalData(idaFlg) { 
            if (this.params.data.idaFlg === '1') {
                getLogicalLockWithModal(this.axios, this.$vaModal, {
                    awbNo: this.detailData.awbNo,
                    cwbNo: this.detailData.cwbNo,
                    userCd: loginStore.getters.getAuthPermission.userCd,
                    override: false
                }, this.updateIdaData, idaFlg)
                // this.updateIdaData(idaFlg)
            } else {
                getLogicalLockWithModal(this.axios, this.$vaModal, {
                    awbNo: this.detailData.awbNo,
                    cwbNo: this.detailData.cwbNo,
                    userCd: loginStore.getters.getAuthPermission.userCd,
                    override: false
                }, this.updateMicData, '')
                // this.updateMicData()
            }
        },
        updateMicData() { 
            const msg = this.checkMicData()
            // eslint-disable-next-line eqeqeq
            if (msg != "") { 
                notificationError(msg)
                // eslint-disable-next-line no-useless-return
                return
            }
            this.changeLoading()
            this.axios.post('/api/oait001/insertMicData', this.detailData)
                .then(res => {
                    if (res.data.errorFlg === 'error') {
                        notificationError(res.data.msg)
                    } else {
                        notificationSuccess(res.data.msg)
                     }
                })
                .catch(error => {
                    notificationError(error)
                }).finally(() => {
                    this.changeLoading()
                })
        },
        checkMicData() { 
            const userDept = loginStore.getters.getAuthPermission.departmentCd
            // eslint-disable-next-line eqeqeq
            if (userDept != this.params.data.customsPlaceCd) {
                return "他の場所で通関申告中です。"
            }
            if (this.detailData.impCustomerTelNo.length > 11) { 
                return "輸入者電話番号,１１文字以下を入力してください。"
            }
            return ""
        },
        updateIdaData(idaFlg) {
            const msg = this.checkIdaData()
            // eslint-disable-next-line eqeqeq
            if (msg != "") {
                notificationError(msg)
                // eslint-disable-next-line no-useless-return
                return
            }

            const idaData = {
                mainDao:this.detailData,
                subDao: this.idaSubList
            }
            this.changeLoading()
            this.axios.post('/api/oait001/insertIdaData', idaData)
                .then(res => {
                    if (res.data.errorFlg === 'error') {
                        notificationError(res.data.msg)
                    } else if (idaFlg) {
                        this.writeIDA(idaFlg)
                    } else { 
                        notificationSuccess(res.data.msg)
                        this.detailData = res.data.obj.mainDao
                        this.idaSubList = res.data.obj.subDao
                    }
                })
                .catch(error => {
                    if (error.response.status === 401) {
                        notificationError('本機能の利用権限がないです。');
                    } else { 
                        notificationError("登録処理でエラーが発生しました。", error.message)
                    }
                }).finally(() => {
                    this.changeLoading()
                })
        },
        checkIdaData() { 
            const userDept = loginStore.getters.getAuthPermission.departmentCd
            // eslint-disable-next-line eqeqeq
            if (userDept != this.params.data.customsPlaceCd) {
                return "他の場所で通関申告中です。"
            }
            if (this.detailData.impCustomerTelNo.length > 11) {
                return "輸入者電話番号,１１文字以下を入力してください。"
            }
            return ""
        },
        changeDetailDataIDA(msg, originAwb, originCwb) {
            let nextAwb
            let nextCwb

            if (msg === 'S') {
                const tmpCwbList = oait001store.getters.getIdaCwbList
                for (let i = 0; i < tmpCwbList.length; i++) {
                    if (tmpCwbList[i].cwbNo === this.detailData.cwbNo) {
                        nextAwb = tmpCwbList[i].awbNo
                        nextCwb = tmpCwbList[i].cwbNo
                    }
                }
            } else if (msg === 'N') {
                const tmpCwbList = oait001store.getters.getIdaCwbList
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
                const tmpCwbList = oait001store.getters.getIdaCwbList
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
            }, this.selectIdaData, { awbNo: nextAwb, cwbNo: nextCwb })
        },
        changeDetailDataMIC(msg, originAwb, originCwb) {
            let nextAwb
            let nextCwb

            if (msg === 'S') {
                const tmpCwbList = oait001store.getters.getMicCwbList
                for (let i = 0; i < tmpCwbList.length; i++) {
                    if (tmpCwbList[i].cwbNo === this.detailData.cwbNo) {
                        nextAwb = tmpCwbList[i].awbNo
                        nextCwb = tmpCwbList[i].cwbNo
                    }
                }
            } else if (msg === 'N') {
                const tmpCwbList = oait001store.getters.getMicCwbList
                for (let i = 0; i < tmpCwbList.length; i++) {
                    if (tmpCwbList[i].cwbNo === this.detailData.cwbNo) {
                        if (i === tmpCwbList.length - 1) {
                            notificationError("最終のデータです。")
                            return true
                        } else {
                            nextAwb = tmpCwbList[i + 1].awbNo
                            nextCwb = tmpCwbList[i + 1].cwbNo
                        }
                    }
                }
            } else if (msg === 'P') {
                const tmpCwbList = oait001store.getters.getMicCwbList
                for (let i = 0; i < tmpCwbList.length; i++) {
                    if (tmpCwbList[i].cwbNo === this.detailData.cwbNo) {
                        if (i === 0) {
                            this.prevFlg = false
                            notificationError("最初のデータです。")
                            return true
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
            }, this.selectMicData, {awbNo: nextAwb,cwbNo: nextCwb })
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
        writeIDA(idaFlg) { 
            const idaData = {
                mainDao: this.detailData,
                subDao: this.idaSubList,
                idaCheckFlag: idaFlg
            }
            this.changeLoading()
             this.axios({
                method: 'post',
                url: '/api/oait001/writeIDA',
                data: idaData, 
                responseType: 'arraybuffer',
             }).then(response => {
                const fileLeng = response.headers['content-length']
                if (fileLeng !== '0') {
                    const fileName = response.headers['content-disposition'].replaceAll("\"", "").split('filename=')[1]
                    const url = window.URL.createObjectURL(new Blob([response.data]));
                    const link = document.createElement('a');
                    link.href = url;
                    link.setAttribute('download', fileName);
                    document.body.appendChild(link);
                    link.click();
                    }
            // eslint-disable-next-line n/handle-callback-err
            }).catch(error => {
                notificationError("IDA電文ファイル作成でエラーが発生しました。管理者にお問い合わせください。")
            }).finally(() => {
                this.changeLoading()
            })
        },
        saveModalData() {
            if (this.params.data.idaFlg === '1') {
                getLogicalLockWithModal(this.axios, this.$vaModal, {
                    awbNo: this.detailData.awbNo,
                    cwbNo: this.detailData.cwbNo,
                    userCd: loginStore.getters.getAuthPermission.userCd,
                    override: false
                }, this.saveIdaData, '')
                // this.saveIdaData()
            } else {
                getLogicalLockWithModal(this.axios, this.$vaModal, {
                    awbNo: this.detailData.awbNo,
                    cwbNo: this.detailData.cwbNo,
                    userCd: loginStore.getters.getAuthPermission.userCd,
                    override: false
                }, this.saveMicData, '')
                // this.saveMicData()
            }
        },
        saveMicData() {
            const msg = this.checkMicData()
            if (msg !== "") {
                notificationError(msg)
                return
            }
            this.changeLoading()
            this.axios.post('/api/oait001/saveMicData', this.detailData)
            .then(res => {
                if (res.data.errorFlg === 'error') {
                    notificationError(res.data.msg)
                } else {
                    notificationSuccess(res.data.msg)
                }
            }).catch(error => {
                notificationError(error)
            }).finally(() => {
                this.changeLoading()
            })
        },
        saveIdaData() {
            const msg = this.checkIdaData()
            // eslint-disable-next-line eqeqeq
            if (msg != "") {
                notificationError(msg)
                // eslint-disable-next-line no-useless-return
                return
            }
            const idaData = {
                mainDao: this.detailData,
                subDao: this.idaSubList
            }
            this.changeLoading()
            this.axios.post('/api/oait001/saveIdaData', idaData)
            .then(res => {
                if (res.data.errorFlg === 'error') {
                    notificationError(res.data.msg)
                } else {
                    notificationSuccess(res.data.msg)
                    this.detailData = res.data.obj.mainDao
                    this.idaSubList = res.data.obj.subDao
                }
            }).catch(error => {
                if (error.response.status === 401) {
                    notificationError('本機能の利用権限がないです。');
                } else {
                    notificationError("登録処理でエラーが発生しました。", error.message)
                }
            }).finally(() => {
                this.changeLoading()
            })
        },
    },
};
</script>