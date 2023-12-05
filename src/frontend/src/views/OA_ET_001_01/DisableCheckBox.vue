<template>
    <div>
        <input type="checkbox" :checked="isChecked" :disabled="isReadonly" @change="onChange">
    </div>
</template>

<script>

export default {
    data() {
        return {
            isChecked: false,
            isReadonly: true
        }
    },
    methods: {
        /**
         * 更新が行われた際に、セルの値をチェック状態としてセットする。
         */
        refresh() {
            this.isChecked = this.params.getValue()
            return true
        },
        /**
         * changeイベント時にチェック状態をセルの値としてセットする。
         * セルのチェック状態に応じてヘッダーも更新したいため、
         * api.refreshHeader()を呼び出す。
         */
        onChange() {
            this.isChecked = !this.isChecked;
            this.params.setValue(this.isChecked);
            this.params.api.refreshHeader();
        },
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