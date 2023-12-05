<template>
    <va-card>
        <div class="px-3">
            <div class="row justify-start items-center py-2">
                <div class="flex items-center xs7">
                    <span style="min-width: 15%">仕出仕向人No</span>
                    <va-input ref="customerNo" v-model.trim="searchCondition.customerNo" :maxlength="13" />
                    <span class="mx-2">ー</span>
                    <va-input ref="deptCd" v-model.trim="searchCondition.deptCd" :maxlength="4" />
                    <span class="mx-2">ー</span>
                    <va-select
                        ref="csrCseFlag"
                        v-model.trim="searchCondition.csrCseFlag"
                        :options="initData?.listSenderFlag"
                        :text-by="(option) => option.value + ' : ' + option.name"
                        :track-by="(option) => option.value + ' : ' + option.name"
                        :value-by="(option) => option.value"
                        clearable
                    />
                    <span class="mx-2">ー</span>
                    <va-select
                        ref="shiyoBashoFlag"
                        v-model.trim="searchCondition.shiyoBashoFlag"
                        :options="initData?.listPlaceOfUse"
                        :text-by="(option) => option.value + ' : ' + option.name"
                        :track-by="(option) => option.value + ' : ' + option.name"
                        :value-by="(option) => option.value"
                        clearable
                    />
                </div>
                <div class="flex items-center xs5">
                    <span style="max-width: 18%">英文会社名</span>
                    <va-input ref="customerNamee" v-model.trim="searchCondition.customerNamee" :maxlength="70" />
                </div>
            </div>

            <div class="row justify-start items-center py-2">
                <div class="flex items-center xs7">
                    <span style="max-width: 15%">国連国コード</span>
                    <va-input ref="countryCd" v-model.trim="searchCondition.countryCd" :maxlength="2" />
                    <span style="max-width: 10%; margin-left: 20px">郵便番号</span>
                    <va-input ref="zipCd" v-model.trim="searchCondition.zipCd" :maxlength="9" />
                </div>
                <div class="flex items-center xs5">
                    <span style="max-width: 18%">英文部署名</span>
                    <va-input ref="division" v-model.trim="searchCondition.division" :maxlength="60" />
                </div>
            </div>

            <div class="row justify-start items-center py-2">
                <div class="flex items-center xs7">
                    <span style="max-width: 15%">英文住所（一括）</span>
                    <va-input v-model.trim="searchCondition.customerAddeBlanket" :maxlength="105" />
                </div>
                <div class="flex items-center xs5">
                    <span style="max-width: 18%">担当者名</span>
                    <va-input v-model.trim="searchCondition.charger" :maxlength="40" />
                </div>
            </div>
            <div class="row justify-start items-center py-2">
                <div class="flex items-center xs7">
                    <span style="min-width: 15%">英文住所</span>
                    <va-input ref="customerAdde1" v-model.trim="searchCondition.customerAdde1" :maxlength="35" />
                    <div class="px-3"></div>
                    <va-input ref="customerAdde2" v-model.trim="searchCondition.customerAdde2" :maxlength="35" />
                    <div class="px-3"></div>
                    <va-input ref="customerAdde3" v-model.trim="searchCondition.customerAdde3" :maxlength="35" />
                </div>
                <div class="flex items-center xs5">
                    <span style="min-width: 18%">TEL NO</span>
                    <va-input ref="telNo" v-model.trim="searchCondition.telNo" :maxlength="20" input-class="hide-input-number-arrow" />
                    <span style="min-width: 10%; margin-left: 15px">FAX NO</span>
                    <va-input ref="faxNo" v-model.trim="searchCondition.faxNo" :maxlength="20" input-class="hide-input-number-arrow" />
                </div>
            </div>

            <div class="row justify-start items-center py-2">
                <div class="flex items-center xs7">
                    <span style="max-width: 15%"></span>
                    <va-input ref="customerAdde4" v-model.trim="searchCondition.customerAdde4" :maxlength="35" />
                </div>
                <div class="flex items-center xs5">
                    <span style="max-width: 18%">メールアドレス</span>
                    <va-input ref="emailAdd" v-model.trim="searchCondition.emailAdd" :maxlength="100" />
                </div>
            </div>

            <div class="row justify-end p-2">
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
                csrCseFlag: '',
                shiyoBashoFlag: '',
                countryCd: '',
                division: '',
                zipCd: '',
                customerNamee: '',
                customerAdde1: '',
                customerAdde2: '',
                customerAdde3: '',
                customerAdde4: '',
                customerAddeBlanket: '',
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
            this.$emit('searchImportData', this.searchCondition, this.searchCustomer)
        },
    },
}
</script>
