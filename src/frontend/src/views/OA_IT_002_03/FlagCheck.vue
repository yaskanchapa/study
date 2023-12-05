<template>
    <div class="h-full flex items-center">
        <va-checkbox v-model="checked" @update:model-value="checkboxClicked" />
    </div>
</template>

<script>
export default {
    data() {
        return {
            checked: false,
        }
    },
    beforeMount() {
        this.checked = this.params.data.aposManagementFlag === '1'
    },
    methods: {
        checkboxClicked() {
            let updatedAPOSManagementFlag = this.params.data.aposManagementFlag
            this.checked ? (updatedAPOSManagementFlag = '1') : (updatedAPOSManagementFlag = '0')
            this.params.handleEditData(this.params.data, updatedAPOSManagementFlag)
        },
    },
}
</script>
