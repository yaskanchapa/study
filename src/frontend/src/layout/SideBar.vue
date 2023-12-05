<template>
    <aside class="bd-aside sticky-xl-top text-muted align-self-start mb-3 mb-xl-5 px-2 text-left">
        <h6 class="h6 pt-4 pb-3 border-bottom textblack" style="font-weight: bolder;font-size: large;"><va-icon name="flight_land" /> 航空輸入</h6>
        <nav class="small" id="toc">
            <ul class="list-unstyled">
                <li class="my-2">
                    <button
                        class="btn1 d-inline-flex align-items-center collapsed content-left textblack"
                        data-bs-toggle="collapse"
                        aria-expanded="false"
                        data-bs-target="#i-tsukan-collapse"
                        aria-controls="i-tsukan-collapse"
                        style="width: 100%"
                        
                    >
                       <va-icon name="summarize" /> 通関
                    </button>
                    <ul class="list-unstyled ps-3 collapse" id="i-tsukan-collapse">
                        <li class="submenu1"><router-link to="/airimport001/oait001main" class="textblack" v-on:click="menuClick('輸入申告データ編集')">輸入申告データ編集</router-link></li>
                        <!-- <li class="submenu1" v-show="userAuthPermission == '02'"><router-link to="/airimport001/oait001main" class="textblack">輸入申告データ編集</router-link></li> -->
                        <li class="submenu1"><router-link to="/airimport002/oait002main" class="textblack" v-on:click="menuClick('輸入申告データ照会')">輸入申告データ照会</router-link></li>
                        <li class="submenu1"><router-link to="/airimport003/oait003main" class="textblack" v-on:click="menuClick('輸入情報検索')">輸入情報検索</router-link></li>
                        <li class="submenu1"><router-link to="/airimport004/oait004main" class="textblack" v-on:click="menuClick('通関書類状況登録')">通関書類状況登録</router-link></li>
                    </ul>
                </li>
                <li class="my-2">
                    <button
                        class="btn1 d-inline-flex align-items-center collapsed content-left textblack"
                        data-bs-toggle="collapse"
                        aria-expanded="false"
                        data-bs-target="#i-hozei-collapse"
                        aria-controls="i-hozei-collapse"
                        style="width: 100%"
                    >
                        <va-icon name="warehouse" />保税
                    </button>
                    <ul class="list-unstyled ps-3 collapse" id="i-hozei-collapse">
                        <li class="submenu1"><router-link to="/oaiw001Main" class="textblack" v-on:click="menuClick('搬入データ編集')">搬入データ編集</router-link></li>
                        <li class="submenu1"><router-link to="/oaiw002Main" class="textblack" v-on:click="menuClick('搬出データ編集')">搬出データ編集</router-link></li>
                        <li class="submenu1"><router-link to="/oaiw003Main" class="textblack" v-on:click="menuClick('インベントリー指示')">インベントリー指示</router-link></li>
                        <li class="submenu1"><router-link to="/airImportW005/oaiw005Main" class="textblack" v-on:click="menuClick('上屋データ照会')">上屋データ照会</router-link></li>
                        <!-- <li class="submenu1"><router-link to="/nonepage" class="textblack">輸入申告データ照会</router-link></li> -->
                    </ul>
                </li>
            </ul>
        </nav>
        <h6 class="h6 pt-4 pb-3 border-bottom textblack" style="font-weight: bolder;font-size: large;"><va-icon name="flight_takeoff" /> 航空輸出</h6>
        <!-- <h2 class="h6 pt-4 pb-3 border-bottom"><va-icon name="flight_takeoff" /> 航空輸出</h2> -->
        <nav class="small" id="toc">
            <ul class="list-unstyled">
                <li class="my-2">
                    <button
                        class="btn1 d-inline-flex align-items-center collapsed content-left textblack"
                        data-bs-toggle="collapse"
                        aria-expanded="false"
                        data-bs-target="#e-tsukan-collapse"
                        aria-controls="e-tsukan-collapse"
                        style="width: 100%;"
                    >
                        <va-icon name="summarize" />通関
                    </button>
                    <ul class="list-unstyled ps-3 collapse" id="e-tsukan-collapse">
                        <li class="submenu1"><router-link to="/airexport001/oaet001main" class="textblack" v-on:click="menuClick('輸出申告データ編集')">輸出申告データ編集</router-link></li>
                        <li class="submenu1"><router-link to="/airexport002/oaet002main" class="textblack" v-on:click="menuClick('輸出申告データ照会')">輸出申告データ照会</router-link></li>
                        <li class="submenu1"><router-link to="/exportSearch/oaet003main" class="textblack" v-on:click="menuClick('輸出情報検索')">輸出情報検索</router-link></li>
                    </ul>
                </li>
                <li class="my-2">
                    <button
                        class="btn1 d-inline-flex align-items-center collapsed content-left textblack"
                        data-bs-toggle="collapse"
                        aria-expanded="false"
                        data-bs-target="#e-hozei-collapse"
                        aria-controls="e-hozei-collapse"
                        style="width: 100%"
                    >
                        <va-icon name="warehouse" />保税
                    </button>
                    <ul class="list-unstyled ps-3 collapse" id="e-hozei-collapse">
                        <li class="submenu1"><router-link to="/airExportW001/oaew001Main" class="textblack" v-on:click="menuClick('搬入伝票照会')">搬入伝票照会</router-link></li>
                        <li class="submenu1"><router-link to="/airexport002/oaet002main" class="textblack" v-on:click="menuClick('輸出申告データ照会')">輸出申告データ照会</router-link></li>
                        <li class="submenu1"><router-link to="/exportSearch/oaet003main" class="textblack" v-on:click="menuClick('輸出情報検索')">輸出情報検索</router-link></li>
                        <li class="submenu1"><router-link to="/oaew002Main" class="textblack" v-on:click="menuClick('コンテナ登録')">コンテナ登録</router-link></li>
                    </ul>
                </li>
            </ul>
        </nav>
        <h2 class="h6 pt-4 pb-3 border-bottom textblack" style="font-weight: bolder;font-size: large;"><va-icon name="settingsapplications" style="width: 25px;"/>マスタ管理</h2>
        <nav class="small" id="toc">
            <ul class="list-unstyled">
                <li class="my-2">
                    <button
                        class="btn1 d-inline-flex align-items-center collapsed content-left textblack"
                        data-bs-toggle="collapse"
                        aria-expanded="false"
                        data-bs-target="#master-collapse"
                        aria-controls="master-collapse"
                        style="width: 100%"
                    >
                    <va-icon name="book"  style="width: 25px;"/>各種マスタ
                    </button>
                    <ul class="list-unstyled ps-3 collapse" id="master-collapse">
                        <li class="submenu1"><router-link to="/usermaster/occm00101main" class="content-left textblack" v-on:click="menuClick('ユーザーマスタ')">ユーザーマスタ</router-link></li>
                        <li class="submenu1"><router-link to="/usermaster/occm00102main" class="textblack" v-on:click="menuClick('業者Noマスタ')">業者Noマスタ</router-link></li>
                        <li class="submenu1"><router-link to="/usermaster/occm00103main" class="textblack" v-on:click="menuClick('仕出人・仕向人マスタ')">仕出人・仕向人マスタ</router-link></li>
                        <!-- <li class="submenu1"><router-link to="/nonepage" class="textblack" v-on:click="menuClick('品目マスタ')">品目マスタ</router-link></li> -->
                        <li class="submenu1"><router-link to="/usermaster/occm00104main" class="textblack" v-on:click="menuClick('レートマスタ')">レートマスタ</router-link></li>
                    </ul>
                </li>
            </ul>
        </nav>
        <h2 class="h6 pt-4 pb-3 border-bottom textblack" style="font-weight: bolder;font-size: large;"><va-icon name="web" />その他メニュー</h2>
        <nav class="small" id="toc">
            <ul class="list-unstyled">
                <li class="my-2">
                    <button
                        class="btn1 d-inline-flex align-items-center collapsed content-left textblack"
                        data-bs-toggle="collapse"
                        aria-expanded="false"
                        data-bs-target="#common-collapse"
                        aria-controls="common-collapse"
                        style="width: 100%"
                    >
                    <va-icon name="summarize" />共通機能
                    </button>
                    <ul class="list-unstyled ps-3 collapse" id="common-collapse">
                        <li class="submenu1"><router-link to="/common/occs007main" class="content-left textblack" v-on:click="menuClick('INVOICEアップロード')">INVOICEアップロード</router-link></li>
                    </ul>
                </li>
            </ul>
        </nav>
    </aside>
</template>
<script>
import loginStore from '../store/OC_CS_003/loginUserInfoStore'
import menuNameStore from '@/store/OC_CS_006/selectedMenuNameStore'

export default {
    name: 'SideBar',
    data() {
        const userAuthPermission = loginStore.getters.getAuthPermission.userManagementAuthorityCd
        const menuName = ''
        return {
            userAuthPermission,
            menuName,
        }
    },
    methods: {
    menuClick(parm) {
        this.menuName = parm
        this.emitter.emit('menuName', this.menuName)
        menuNameStore.commit('setMenuName', this.menuName)
    }
  }
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
.content-left {
    justify-content: left;
}
.text-left {
    text-align: left;
}
.bd-aside .btn {
    padding: 0.25rem 0.5rem;
    font-weight: 600;
    color: rgba(0, 0, 0, 0.65);
    border: 0;
}
.align-items-center {
    align-items: center !important;
}
.d-inline-flex {
    display: inline-flex !important;
}
.btn1 {
    display: inline-block;
    font-weight: 800;
    line-height: 1.5;
    color: #212529;
    text-align: center;
    text-decoration: none;
    vertical-align: middle;
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    user-select: none;
    background-color: transparent;
    border: 1px solid transparent;
    padding: 0.375rem 0.75rem;
    font-size: 1rem;
    border-radius: 0.25rem;
    transition: color 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}
.bd-aside .btn::before {
    width: 1.25em;
    line-height: 0;
    /* content: url(data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3e%3cpath fill='none' stroke='rgba%280,0,0,.5%29' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M5 14l6-6-6-6'/%3e%3c/svg%3e); */
    transition: transform.35s ease;
    transform: rotate(180deg) translateX(-2px);
    transform-origin: 0.5em 50%;
}
.submenu1 {
    padding: 0px 0px 10px 0px;
    font-size: 15px;
}
.textblack {
    /* color: #212529; */
    color: white;
}
</style>
