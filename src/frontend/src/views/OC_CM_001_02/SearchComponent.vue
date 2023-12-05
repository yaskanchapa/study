<template>
    <va-card>
        <div class="px-3">
            <div class="row justify-start items-center">
                <div class="flex items-center py-[6px] xs7">
                    <span style="min-width: 15%">業者No</span>
                    <va-input ref="customerNo" v-model.trim="searchCondition.customerNo" :maxlength="13" />
                    <span class="mx-2">ー</span>
                    <va-input ref="deptCd" v-model.trim="searchCondition.deptCd" :maxlength="4" />
                    <span class="mx-2">ー</span>
                    <va-select
                        v-model.trim="searchCondition.impExpFlag"
                        :options="initData?.listImportAndExportFlag"
                        :text-by="(option) => option.value + ' : ' + option.name"
                        :track-by="(option) => option.value + ' : ' + option.name"
                        :value-by="(option) => option.value"
                        clearable
                    />
                    <span class="mx-2">ー</span>
                    <va-select
                        v-model.trim="searchCondition.shiyoBashoFlag"
                        :options="initData?.listPlaceOfUse"
                        :text-by="(option) => option.value + ' : ' + option.name"
                        :track-by="(option) => option.value + ' : ' + option.name"
                        :value-by="(option) => option.value"
                        clearable
                    />
                </div>
                <div class="flex items-center py-[6px] xs5">
                    <span style="max-width: 18%">和文会社名</span>
                    <va-input v-model.trim="searchCondition.customerNamej" :maxlength="80" />
                </div>
            </div>

            <div class="row justify-start items-center">
                <div class="flex items-center py-[6px] xs7">
                    <span style="max-width: 15%">英文会社名</span>
                    <va-input v-model.trim="searchCondition.customerNamee" :maxlength="70" />
                </div>
                <div class="flex items-center py-[6px] xs5">
                    <span style="max-width: 18%">和文部署名</span>
                    <va-input v-model.trim="searchCondition.division" :maxlength="60" />
                </div>
            </div>

            <div class="row justify-start items-center">
                <div class="flex items-center py-[6px] xs7">
                    <span style="max-width: 15%">郵便番号</span>
                    <va-input v-model.trim="searchCondition.zipCd" :maxlength="7" />
                </div>
                <div class="flex items-center py-[6px] xs5">
                    <span style="max-width: 18%">和文住所</span>
                    <va-input v-model.trim="searchCondition.customerAddej" :maxlength="100" />
                </div>
            </div>

            <div class="row justify-start items-center">
                <div class="flex items-center py-[6px] xs7">
                    <span style="min-width: 15%">英文住所</span>
                    <va-input v-model.trim="searchCondition.customerAdde1" :maxlength="15" />
                    <div class="px-3"></div>
                    <va-input v-model.trim="searchCondition.customerAdde2" :maxlength="35" />
                    <div class="px-3"></div>
                    <va-input v-model.trim="searchCondition.customerAdde3" :maxlength="35" />
                </div>
                <div class="flex items-center py-[6px] xs5">
                    <span style="max-width: 18%">担当者名</span>
                    <va-input v-model.trim="searchCondition.charger" :maxlength="40" />
                </div>
            </div>

            <div class="row justify-start items-center">
                <div class="flex items-center py-[6px] xs7">
                    <span style="max-width: 15%"></span>
                    <va-input v-model.trim="searchCondition.customerAdde4" :maxlength="70" />
                </div>
                <div class="flex items-center py-[6px] xs5">
                    <span style="min-width: 18%">TEL NO</span>
                    <va-input v-model.trim="searchCondition.telNo" :maxlength="14" />
                    <span style="min-width: 10%; margin-left: 15px">FAX NO</span>
                    <va-input v-model.trim="searchCondition.faxNo" :maxlength="14" />
                </div>
            </div>

            <div class="row justify-start items-center">
                <div class="flex items-center py-[6px] xs7">
                    <span style="max-width: 15%">英文住所(一括)</span>
                    <va-input v-model.trim="searchCondition.customerAddeblAnket" :maxlength="105" />
                </div>
                <div class="flex items-center py-[6px] xs5">
                    <span style="max-width: 18%">メールアドレス</span>
                    <va-input v-model.trim="searchCondition.emailAdd" :maxlength="100" />
                </div>
            </div>

            <div class="row justify-end" style="padding: 0.5rem">
                <div class="item flex xs1">
                    <va-button v-show="!isModal" @click="handleShowModal('register', null)" color="info" size="medium">新規登録</va-button>
                </div>
                <div class="item flex xs1">
                    <va-button color="info" size="medium" @click="handleSearchBtnClicked">検索</va-button>
                </div>
            </div>
        </div>
    </va-card>
</template>

<script>
export default {
    props: {
        initData: Object,
        isModal: Boolean,
        parentProps: Object,
    },
    data() {
        return {
            searchCondition: {
                customerNo: '',
                deptCd: '0000',
                impExpFlag: '',
                shiyoBashoFlag: '',
                customerNamee: '',
                zipCd: '',
                customerAdde1: '',
                customerAdde2: '',
                customerAdde3: '',
                customerAdde4: '',
                customerAddeblAnket: '',
                customerNamej: '',
                division: '',
                customerAddej: '',
                charger: '',
                telNo: '',
                faxNo: '',
                emailAdd: '',
            },
        }
    },
    watch: {
        parentProps: {
            // the callback will be called immediately after the start of the observation
            immediate: true,
            handler() {
                if (this.parentProps !== undefined) {
                    this.searchCondition.customerNo = this.parentProps.customerNo ? this.parentProps.customerNo : ''
                    this.searchCondition.deptCd = this.parentProps.deptCd ? this.parentProps.deptCd : ''
                    this.searchCondition.customerNamee = this.parentProps.customerNamee ? this.parentProps.customerNamee : ''
                }
            },
        },
    },
    methods: {
        handleShowModal(action, params) {
            this.$emit('openDetailModal', action, params)
        },
        handleSearchBtnClicked() {
            this.$emit('searchImportData', this.searchCondition)
        },
    },
}
</script>
