<template>
    <va-modal ref="confirmationModal" v-model="isVisible" v-model.trim="showConfirmationModal" no-padding hide-default-actions background-color="#000000">
        <template #header>
            <div class="pt-3 px-3">
                <h2 class="text-white">確認</h2>
            </div>
            <div class="bg-slate-200 h-[1px] my-3"></div>
        </template>
        <template #default>
            <div class="px-4 py-2">
                <h2 class="text-white">
                    <slot>{{ deleteConfirmMessage }}</slot>
                </h2>
            </div>
        </template>
        <template #footer>
            <div class="flex justify-between gap-6 pb-3">
                <va-button @click="_confirm">OK</va-button>
                <va-button @click="_cancel">キャンセル</va-button>
            </div>
        </template>
    </va-modal>
</template>

<script>
import { DELETE_CONFIRM_ACTION_MESSAGE } from '@/utils/messages'

export default {
    data: () => ({
        isVisible: false,
        resolvePromise: undefined,
        rejectPromise: undefined,
        defaultDeleteConfirmMessage: DELETE_CONFIRM_ACTION_MESSAGE,
    }),

    methods: {
        openConfirmationModal() {
            this.isVisible = true
            // Return promise so the caller can get results
            return new Promise((resolve, reject) => {
                this.resolvePromise = resolve
                this.rejectPromise = reject
            })
        },
        _confirm() {
            this.isVisible = false
            this.resolvePromise(true)
        },
        _cancel() {
            this.isVisible = false
            this.resolvePromise(false)
        },
    },
}
</script>