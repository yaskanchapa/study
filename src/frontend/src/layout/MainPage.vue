<template>
    <!-- ヘッダー -->
    <MainHeader />
    <div class="container-fluid">
        <div class="row mainwidth">
            <!-- SideBar -->
            <nav id="sidebarMenu" class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse sidebarwidth sidebarpadding">
                <div style="height: 100vh;">
                    <va-sidebar v-model="enabled" color="#62A8AC" text-color="#009600" minimized-width="64px" width="14rem" class="">
                        <va-sidebar-item active-color="#62A8AC">
                            <SideBar />
                        </va-sidebar-item>
                    </va-sidebar>
                </div>
            </nav>
            <!-- Body -->
            <main class="col-md-9 col-lg-10 bodywidth sidebarpadding">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center border-bottom">
                    <router-view />
                </div>
            </main>
        </div>
    </div>
</template>
<script>
import MainHeader from '@/layout/MainHeader.vue'
import SideBar from '@/layout/SideBar.vue'

export default {
    data() {
        const enabled = true
        const menuOnOffInfo = {
            enabled: true,
            sidebarWidth: '15%',
            bodyWidth: '83%'
        }
        const menuName = ''

        return {
            enabled,
            menuOnOffInfo,
            menuName
        }
    },
    name: 'LoginSucess',
    components: {
        MainHeader,
        SideBar,
    },
    mounted(){
        this.emitter.on('menuOnOffInfoData',this.menuOnBtn)
    },
    methods: {
        menuOnBtn(menuOnOffInfo) {
            this.menuOnOffInfo.enabled = menuOnOffInfo.enabled
            this.menuOnOffInfo.sidebarWidth = menuOnOffInfo.sidebarWidth
            this.menuOnOffInfo.bodyWidth = menuOnOffInfo.bodyWidth
            this.enabled = menuOnOffInfo.enabled
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
        margin-left: unset;
    }
}

.menubtn {
    z-index: 1003;
    padding-top : 0px;
}

.sidebarwidth {
    flex: 0 0 auto;
    width: v-bind(sidebarWidth);
}
.bodywidth {
    flex: 0 0 auto;
    width: v-bind(bodyWidth);
    min-width: 1600px;
}
.sidebarpadding {
    padding-left: 0;
    padding-right: 0;
}
.mainwidth{
    width: 2000px;
    /* width: 100%; */
}
.container{
    padding-right: 0;
    padding-left: 0;
    margin-right: 0;
    margin-left: 0;
}
.border-bottom {
    border-bottom: none!important;
}
</style>