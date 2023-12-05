<template>
    <header class="navbar navbar-dark navbar-height sticky-top flex-md-nowrap p-0 shadow" style="z-index: 999; background-color: #091540">
        <div style="width: 800px" align="left">
            <td>
                <a class="navbar-brand col-md-3 col-lg-2 me-0 px-3" href="/main">
                    <img src="https://www.kokusaiexpress.com/jpn/wp-content/themes/kokusaiexpress-official-html/dist/assets/images/logo-white.png" width="200" height="200" />
                </a>
            </td>
            <td>
                <va-button @click="menuOnOff()" color="warning" class="px-3" style="float: left; margin-left: 25px;">
                    <menu-icon />
                </va-button>
            </td>
            <td>
                <span style="color: #ffffff; font-size: large; margin-left: 20px;">{{ menuName }}</span>
            </td>
        </div>
        <div style="width: 800px; display: inline-block" Align="right">
            <tr  height="50">
                <!-- 部署名出力 -->
                <td colspan="2" style="vertical-align: bottom;">
                    <va-button color="warning" class="px-3" style="float: right; margin-right: 5px">
                        <AccountIcon class="none-max-width"/>
                        {{ userName }}
                    </va-button>
                </td>
                <td style="vertical-align: bottom; padding: 0px 20px 0px 0px;">
                    <template v-if="loginFlg">
                        <va-button @click="logout()" class="px-3" style="float: right;">ログアウト</va-button>
                    </template>
                    <template v-else>
                        <va-button @click="login()" class="px-3" style="float: right">ログイン</va-button>
                    </template>
                </td>
            </tr>
            <tr>
                <!-- 部署名出力 -->
                <td colspan="3" style="padding: 0px 20px 0px 0px;">
                    <span style="color: #ffffff; margin-right: 10px;">Company:{{ companyCd }}</span>
                    <span style="color: #ffffff;">Department:{{ departmentCd }}({{ departmentName }})</span>
                </td>
            </tr>
        </div>
    </header>
</template>
<script>
import setAuthHeader from '@/api/Request'
import 'vue-material-design-icons/styles.css'
import MenuIcon from 'vue-material-design-icons/Menu.vue'
import AccountIcon from 'vue-material-design-icons/Account.vue'
import loginStore from '@/store/OC_CS_003/loginUserInfoStore'
import menuNameStore from '@/store/OC_CS_006/selectedMenuNameStore'
import { releaseAllLockByUserCd } from '@/components/HawbLogicalLock/HawbLogicalLock.vue' // eslint-disable-line
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../components/Notification/NotificationApi.vue' // eslint-disable-line


export default {
    name: 'MainHeader',
    components: {
        MenuIcon,
        AccountIcon,
    },
    data() {
        const userName = loginStore.getters.getAuthPermission.userName
        const companyCd = loginStore.getters.getAuthPermission.userCompanyCd
        const departmentCd = loginStore.getters.getAuthPermission.departmentCd
        const departmentName = loginStore.getters.getAuthPermission.departmentName
        const enabled = true
        const menuOnOffInfo = {
            enabled: true,
            sidebarWidth: '15%',
            bodyWidth: '80%',
        }
        const menuName = menuNameStore.getters.getMenuName
        return {
            userName,
            value: [false],
            enabled,
            menuOnOffInfo,
            companyCd,
            departmentCd,
            departmentName,
            menuName,
            userCd: '',
        }
    },
    computed: {
        userCdStore() {
            return loginStore.getters.getAuthPermission.userCd
        },
        loginFlg() {
            return !!this.userCdStore
        },
    },
    mounted() {
        loginStore.subscribe((mutation, state) => {
            if (mutation.type === 'setAuthPermission') {
                this.userCd = state.authPermission.userCd
            }
        })
        this.emitter.on('menuName',this.selectedMenuName)
    },
    methods: {
        login() {
            this.$router.push('/')
        },
        // LocalStorageデータ全削除、ログインページへ移動
        logout() {
            setAuthHeader(localStorage.getItem('access_token'))
            releaseAllLockByUserCd(this.axios, loginStore.getters.getAuthPermission.userCd)

            this.axios
                .get('/api/logout')
                .then((res) => {
                    if (res.data) {
                        notificationSuccess("ログアウトに成功しました。", '', 2)
                    }
                })
                .catch((error) => {
                    console.log(error)
                    this.$router.push('/500error')
                })
            localStorage.clear()
            delete this.axios.defaults.headers.Authorization
            menuNameStore.commit('setMenuName','')
            this.$router.push('/')
        },
        menuOnOff() {
            this.enabled = !this.enabled
            if (this.enabled === true) {
                this.menuOnOffInfo.enabled = this.enabled
                this.menuOnOffInfo.sidebarWidth = '15%'
                this.menuOnOffInfo.bodyWidth = '80%'
                this.emitter.emit('menuOnOffInfoData', this.menuOnOffInfo)
            } else {
                this.menuOnOffInfo.enabled = this.enabled
                this.menuOnOffInfo.sidebarWidth = '0%'
                this.menuOnOffInfo.bodyWidth = '95%'
                this.emitter.emit('menuOnOffInfoData', this.menuOnOffInfo)
            }
        },
        selectedMenuName(menuName){
            this.menuName = menuName
        },
        refreshPage() {
         window.location.reload();
    }
    },
}
</script>
<style>
.va-button:after {
    background-color: #212529;
    /* opacity: unset; */
}

.btn-center {
    width: 100px;
    margin: auto;
    display: block;
}
th,
td {
    text-align: center;
}
.navbar-height {
    height: 75px;
}
.none-max-width{
    max-width: none;
}
</style>