<template>
    <!-- eslint-disable vue/no-multiple-template-root -->
    <va-button color="info" size="medium" @click="openSearchConsignorConsigneeModal">
        <slot>Open Modal</slot>
    </va-button>
    <va-modal v-model.trim="showSearchConsignorConsigneeModal" hide-default-actions fixed-layout no-outside-dismiss size="large">
        <template #header>
            <div class="flex justify-between items-center w-full px-3 pb-3">
                <span class="m-0 text-lg w-fit flex-grow-0">検索</span>
                <va-icon name="va-clear" class="flex-grow-0" @click="closeSearchConsignorConsigneeModal" />
            </div>
            <div class="bg-slate-200 h-[1px]"></div>
        </template>
        <template #default>
            <OCCM00103Main isModal :enableClickToSearch="enableClickToSearch" :parentProps="parentProps" @handleClickToSearchAction="handleClickToSearchAction" />
        </template>
    </va-modal>
</template>

<script>
import OCCM00103Main from '../../views/OC_CM_001_03/OCCM00103Main.vue'

export default {
    inheritAttrs: false,
    emits: ['handleClickToSearchAction'],
    components: {
        OCCM00103Main,
    },
    props: {
        enableClickToSearch: Boolean,
        parentProps: Object,
    },
    data() {
        return {
            showSearchConsignorConsigneeModal: false,
        }
    },
    methods: {
        openSearchConsignorConsigneeModal() {
            this.showSearchConsignorConsigneeModal = true
        },

        closeSearchConsignorConsigneeModal() {
            this.showSearchConsignorConsigneeModal = false
        },

        handleClickToSearchAction(selectedRowData) {
            this.$emit('handleClickToSearchAction', selectedRowData)
            this.closeSearchConsignorConsigneeModal()
        },
    },
}
</script>