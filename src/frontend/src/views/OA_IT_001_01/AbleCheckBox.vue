<template>

    <div>
        <input type="checkbox" :checked="isChecked" :disabled="isReadonly" @change="onChange">
    </div>

</template>

<script>
import { notificationSuccess, notificationError } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

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
            colVal : this.params.value
        }
    },
    methods: {
        refresh() {
            // this.isChecked = this.params.getValue()
            return true
        },
        onChange() {
            this.changeValue()
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