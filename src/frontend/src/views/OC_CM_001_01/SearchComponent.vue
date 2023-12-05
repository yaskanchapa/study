<template>
    <va-card>
        <div class="px-3">
            <div class="row justify-start items-center pt-2">
                <div class="flex items-center py-2 xs6">
                    <span style="max-width: 20%">ユーザーコード</span>
                    <va-input v-model.trim="searchCondition.userCd" :maxlength="20" />
                </div>
                <div class="flex xs1"></div>
                <div class="flex items-center py-2 xs5">
                    <span style="max-width: 15%">管理権限</span>
                    <va-select
                        v-model.trim="searchCondition.usermanagementAuthorityCd"
                        :options="initData?.listAdministrativeAuthority"
                        :text-by="(option) => option.value + ' : ' + option.name"
                        :track-by="(option) => option.value + ' : ' + option.name"
                        :value-by="(option) => option.value"
                        clearable
                    />
                </div>
            </div>
            <div class="row justify-start items-center">
                <div class="flex items-center py-2 xs6">
                    <span style="max-width: 20%">ユーザー名(漢字)</span>
                    <va-input v-model.trim="searchCondition.username" :maxlength="30" />
                </div>
                <div class="flex xs1"></div>
                <div class="flex items-center py-2 xs5">
                    <span style="max-width: 15%">業務権限</span>
                    <va-select
                        v-model.trim="searchCondition.userAuthorityCd"
                        :options="initData?.listBusinessAuthority"
                        :text-by="(option) => option.value + ' : ' + option.name"
                        :track-by="(option) => option.value + ' : ' + option.name"
                        :value-by="(option) => option.value"
                        clearable
                    />
                </div>
            </div>

            <div class="row justify-start items-center">
                <div class="flex items-center py-2 xs6">
                    <span style="max-width: 20%">ユーザー名(カナ)</span>
                    <va-input v-model.trim="searchCondition.usernameSyllabary" :maxlength="30" />
                </div>
                <div class="flex xs1"></div>
                <div class="flex items-center py-2 xs5">
                    <span style="max-width: 15%">所属会社</span>
                    <va-select
                        v-model.trim="searchCondition.userCompanyCd"
                        :options="initData?.listAffiliatedCompany"
                        :text-by="(option) => option.value + ' : ' + option.name"
                        :track-by="(option) => option.value + ' : ' + option.name"
                        :value-by="(option) => option.value"
                        clearable
                    />
                </div>
            </div>

            <div class="row justify-start items-center">
                <div class="flex items-center py-2 xs6">
                    <span style="max-width: 20%">ユーザー名(英)</span>
                    <va-input v-model.trim="searchCondition.usernameEng" :maxlength="60" />
                </div>
                <div class="flex xs1"></div>
                <div class="flex items-center py-2 xs5">
                    <span style="max-width: 15%">所属部署</span>
                    <va-select
                        v-model.trim="searchCondition.departmentCd"
                        :options="initData?.listBelongingDepartment"
                        :text-by="(option) => option.value + ' : ' + option.name"
                        :track-by="(option) => option.value + ' : ' + option.name"
                        :value-by="(option) => option.value"
                        clearable
                    />
                </div>
            </div>

            <div class="row justify-end" style="padding: 0.5rem">
                <div class="item flex xs1">
                    <va-button @click="handleShowModal('register', null)" color="info" size="medium">新規登録</va-button>
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
    },
    data() {
        return {
            searchCondition: {
                userCd: '',
                username: '',
                usernameSyllabary: '',
                usernameEng: '',
                usermanagementAuthorityCd: '',
                userAuthorityCd: '',
                userCompanyCd: '',
                departmentCd: '',
            },
        }
    },
    methods: {
        handleShowModal(action, params) {
            this.$emit('openDetailModal', action, params)
        },
        async handleSearchBtnClicked() {
            this.$emit('searchImportData', this.searchCondition)
        },
    },
}
</script>
