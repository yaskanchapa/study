<template>
    <div>
        <input type="checkbox" :checked="isChecked" :disabled="isReadonly" @change="onChange">
    </div>
</template>

<script>
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

export default {
    data() {
        return {
            isChecked: false,
            isReadonly: false,
            searchCond: {
                awbNo: this.params.data.awbNo,
                cwbNo: this.params.data.cwbNo,
                cwbNoDeptCd: this.params.data.cwbNoDeptCd,
                targetCol: this.params.column.colId,
                setVal: this.params.value
            },
        }
    },
    methods: {
        /**
         * 更新が行われた際に、セルの値をチェック状態としてセットする。
         */
        refresh() {
            // this.isChecked = this.params.getValue()
            return true
        },
        /**
         * changeイベント時にチェック状態をセルの値としてセットする。
         * セルのチェック状態に応じてヘッダーも更新したいため、
         * api.refreshHeader()を呼び出す。
         */
        onChange() {

            this.changeValue()
            this.axios({
                url: '/api/oaet001/changeColumnValue',
                method: 'get',
                params: this.searchCond,
                enctype: 'multipart/form-data'
            })
                .then(res => {
                    if (res.data.errFlg === "false") {
                        notificationSuccess(res.data.message, "", 3)
                    } else {
                        this.changeValue()
                        notificationError(res.data.message)
                    }
                }).catch(error => {
                    this.changeValue()
                    notificationError("処理でエラーが発生しました。", error.message)
                })
        },
        changeValue() {
            this.isChecked = !this.isChecked;
            if (this.isChecked) {
                this.searchCond.setVal = "1"
            } else {
                this.searchCond.setVal = "0"
            }
            this.params.setValue(this.searchCond.setVal);
            this.params.api.refreshHeader();
        }
    },
    /**
     * コンポーネントパラメータの受け取り。
     */
    beforeMount() {
        if (this.params.value === '1') { 
            this.isChecked = true    
        }
    }
}
</script>   

<style scoped>
div {
    text-align: center;
}
</style>