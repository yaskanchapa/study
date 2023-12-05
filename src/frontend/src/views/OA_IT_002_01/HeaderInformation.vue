<!-- eslint-disable vue/no-mutating-props -->
<template>
    <div class="flex items-center">
        <div class="xs-1">
            <label>IDA申告件数</label>
            <va-input class="mx-1 w-[70px]" input-class="text-right" :placeholder="resultData?.numberOfIDA ? resultData.numberOfIDA : '0'" type="text" readonly />
            件
        </div>
        <div class="xs-1">
            <label>MIC申告件数</label>
            <va-input class="mx-1 w-[70px]" input-class="text-right" :placeholder="resultData?.numberOfMIC ? resultData.numberOfMIC : '0'" type="text" readonly />
            件
        </div>
        <div class="xs-1">
            <label>保留登録件数</label>
            <va-input class="mx-1 w-[70px]" input-class="text-right" :placeholder="resultData?.numberOfPending ? resultData.numberOfPending : '0'" type="text" readonly />
            件
        </div>
        <div class="xs-1">
            <label>未申告件数</label>
            <va-input class="mx-1 w-[70px]" input-class="text-right" :placeholder="resultData?.numberOfUndeclared ? resultData.numberOfUndeclared : '0'" type="text" readonly />
            件
        </div>
        <div class="xs-1">
            <label>総件数</label>
            <va-input class="mx-1 w-[70px]" input-class="text-right" :placeholder="resultData?.theTotalNumberOfCase ? resultData.theTotalNumberOfCase : '0'" type="text" readonly />
            件
        </div>
        <div class="xs-1">
            <label>総個数</label>
            <va-input class="mx-1 w-[70px]" input-class="text-right" :placeholder="resultData?.totalNumber ? resultData.totalNumber : '0'" type="text" readonly />
            個
        </div>
        <div class="xs-1">
            <va-button class="w-full" color="info" size="medium" @click="handleSearchBtnClicked">検索</va-button>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        resultData: Object,
    },

    methods: {
        handleSearchBtnClicked() {
            this.$emit('handleSearchBtnClicked')
        },
    },
}
</script>
