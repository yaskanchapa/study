<template>
    <div  class="text-center">
        <h1>Error Test Page</h1>
        <va-button @click="submitErrorBtn()" class="mt-3"> Error発生 </va-button>
    </div>
</template>
<script>
import setAuthHeader from '@/api/Request.js'
import errorSample from '@/error/errorPageHandler.js'

export default {
    data() {
        const status = ''
        return {
            status,
        }
    },
    mounted() {
        // throw new Error('Error')
    },
    methods: {
        submitErrorBtn() {
            setAuthHeader(localStorage.getItem('access_token'))
            this.axios.get('/api/errortest')
            .then((res) => {
                console.log(res.data)
            })
            .catch(function(error) {
                errorSample(error)
            }
            )
        },
    },
}
</script>
<style>
/* .bd-placeholder-img {
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
} */
</style>
