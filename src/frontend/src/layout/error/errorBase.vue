<template>
<div class="text-center">
    <h6 class="title text-center">エラーが発生しました。</h6>
    <p>
        Error Status: {{ ErrorInfo.status }}
    </p>
    <p>
        Error Code: {{ ErrorInfo.errorCode }}
    </p>
    <p>
        Error Messgage: {{ ErrorInfo.message }}
    </p>
    <img src="@/assets/error.jpg" width="800" height="full">
    <h1 class="text-center"><a href="https://kr.freepik.com/free-vector/oops-404-error-with-a-broken-robot-concept-illustration_13315300.htm#query=error%20page&position=23&from_view=search&track=sph">Designed by Freepik</a></h1>
</div>
</template>
<script>
import errorStore from '@/store/ERROR_STORE/ErrorInfoStore.js'


export default {
    data() {
        const ErrorInfo = {
            status: errorStore.getters.getErrorInfo.status,
            errorCode: errorStore.getters.getErrorInfo.errorCode,
            message: errorStore.getters.getErrorInfo.message,
        }
        return {
            ErrorInfo,
        }
    },
    mounted() {
    },
    methods: {
    },
}
</script>
<style>
img{
    margin-left: auto;
    margin-right: auto;
}
.text-center {
    text-align: center !important;
}
</style>
