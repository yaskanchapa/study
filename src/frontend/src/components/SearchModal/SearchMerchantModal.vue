<template>
    <!-- eslint-disable vue/no-multiple-template-root -->
    <va-button color="info" size="medium" @click="openSearchMerchantModal">
        <slot>Open Modal</slot>
    </va-button>
    <va-modal v-model.trim="showSearchMerchantModal" hide-default-actions fixed-layout no-outside-dismiss size="large">
        <template #header>
            <div class="flex justify-between items-center w-full px-3 pb-3">
                <span class="m-0 text-lg w-fit flex-grow-0">検索</span>
                <va-icon name="va-clear" class="flex-grow-0" @click="closeSearchMerchantModal" />
            </div>
            <div class="bg-slate-200 h-[1px]"></div>
        </template>
        <template #default>
            <OCCM00102Main isModal :enableClickToSearch="enableClickToSearch" @handleClickToSearchAction="handleClickToSearchAction" :parentProps="parentProps" />
        </template>
    </va-modal>
</template>

<script>
import OCCM00102Main from '../../views/OC_CM_001_02/OCCM00102Main.vue'

export default {
    inheritAttrs: false,
    emits: ['handleClickToSearchAction'],
    components: {
        OCCM00102Main,
    },
    props: {
        enableClickToSearch: Boolean,
        parentProps: Object,
    },
    data() {
        return {
            showSearchMerchantModal: false,
        }
    },
    methods: {
        openSearchMerchantModal() {
            this.showSearchMerchantModal = true
        },

        closeSearchMerchantModal() {
            this.showSearchMerchantModal = false
        },

        handleClickToSearchAction(selectedRowData) {
            this.$emit('handleClickToSearchAction', selectedRowData)
            this.closeSearchMerchantModal()
        },
    },
}
</script>