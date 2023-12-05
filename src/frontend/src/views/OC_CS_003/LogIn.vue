<template>
    <div class="test2">
        <div class="test">
            <div class="form-signin text-center" style="padding-top: 60px; padding-bottom: 30px;">
                <!-- <img src="@/assets/kse_logo.png" width="50" /> -->
                <h1 class="login-text">LOGIN</h1>
            </div>
            <div class="text-center">
                <va-input style="padding-bottom: 15px;" label="UserCode" v-model="userInfo.userCd" :rules="[(value) => (value && userInfo.userCd.length > 0) || 'Field is required']" />
                <br />
                <va-input style="padding-bottom: 15px;" class="mt-3" type="password" label="Password" v-model="userInfo.password" :rules="[(value) => (value && userInfo.password.length > 0) || 'Field is required']" />
                <br />
                <va-button @click="submitBtn()" class="mt-3" style="width:245px;"> ログイン </va-button>
            </div>
        </div>
    </div>
</template>
<script>
import loginStore from '@/store/OC_CS_003/loginUserInfoStore'
import setAuthHeader from '@/api/Request.js'
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line


export default {
    data() {
        const userInfo = {
            userCd: '',
            password: '',
        }
        return {
            userInfo,
        }
    },
    mounted() {
        localStorage.clear()
        setAuthHeader(null)
    },
    methods: {
        submitBtn() {
            setAuthHeader(localStorage.getItem('access_token'))
            this.axios.post('/api/login', this.userInfo).then((res) => {
                if (!res.data.rst) {
                    // UserCode,Password不一致アラート出力
                    notificationError(res.data.msg)
                } else {
                    notificationSuccess(res.data.msg, '', 2)
                    // localStorage, ヘッダにトークン保存
                    const token = res.data.accessToken
                    localStorage.setItem('access_token', token)
                    setAuthHeader(token)
                    // storeにLoginUser権限情報セット
                    loginStore.commit('setAuthPermission', res.data.userInfoDto)
                    this.$router.push('/main')
                }
            }).catch(error => {
                console.log(error)
                this.$router.push('/500error')
            })
        },
    },
}
</script>
<style>
.bd-placeholder-img {
    font-size: 1.125rem;
    text-anchor: middle;
    -webkit-user-select: none;
    -moz-user-select: none;
    user-select: none;
}

@media (min-width: 768px) {
    .bd-placeholder-img-lg {
        font-size: 3.5rem;
    }
}
.form-signin {
    width: 100%;
    max-width: 215px;
    padding: 15px;
    margin: auto;
}
img,
svg {
    vertical-align: middle;
}
.text-center {
    text-align: center !important;
}
.test2{
    width: flex;
    height: 100vh;
    background-image: url('../../assets/backgroundLogin.png');
    background-size: cover;
    /* padding-top: 100px; */
}
.test{
    width: 400px;
    height: 100vh;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
    margin: 0px auto 0px 0px;
    /* background-image: url('../../assets/backgroundLogin.jpg'); */
    background-color: rgb(0, 0, 0, 0.5);
    background-size: cover;
}
.login-text{
    color: white;
    font-size: 30px;
}
</style>
