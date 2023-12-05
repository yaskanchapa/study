<template>
    <va-card>
        <div class="px-3">
            <div class="row justify-start items-center">
                <div class="flex items-center py-3 xs4 gap-4">
                    <span style="max-width: 20%">開始日</span>
                    <Datepicker v-model.trim="startDate" auto-apply :format="DatePickerFormat" :enable-time-picker="false" :clearable="false" :state="v$.startDate.$invalid ? false : null" />
                    <va-button color="info" size="medium" @click="searchImportData">検索 </va-button>
                </div>
            </div>
        </div>
    </va-card>
</template>

<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import { useVuelidate } from '@vuelidate/core'
import { notificationError } from '../../components/Notification/NotificationApi.vue'
import { requiredRules } from '@/utils/validator'
import { DATE_PICKER_FORMAT, currentDateTime } from '@/utils/utilities'
import { MISSING_DATE_MESSAGE } from '@/utils/messages'

export default {
    components: {
        Datepicker,
    },
    data() {
        return {
            v$: useVuelidate(),
            startDate: currentDateTime(),
            DatePickerFormat: DATE_PICKER_FORMAT,
        }
    },
    methods: {
        async searchImportData() {
            await this.v$.$validate()
            if (!this.v$.startDate.$invalid) this.$emit('searchImportData', this.startDate)
            else notificationError(MISSING_DATE_MESSAGE)
        },
    },
    validations() {
        return {
            startDate: { required: requiredRules('日付') },
        }
    },
}
</script>
