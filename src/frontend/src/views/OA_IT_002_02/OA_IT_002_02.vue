<!-- eslint-disable indent -->
<template>
    <va-card>
        <div>
            <!--body-->
            <div class="p-6 pb-0">
                <label class="mr-2">対象：</label>
                <span v-for="(item, index) in initRdoObject" :key="index">
                    <input class="mr-2" name="rdoObject" type="radio" v-model="rdoObject" :value="index" :id="'rdoObject-' + item" />
                    <label class="mr-2" :for="'rdoObject-' + item">
                        {{ item }}
                    </label>
                </span>
            </div>
            <div class="">
                <div class="m-6">
                    <table class="table-auto border border-collapse border-solid border-slate-300 w-80">
                        <thead>
                            <tr>
                                <th class="border border-collapse border-solid border-slate-300 w-10"></th>
                                <th class="border border-collapse border-solid border-slate-300 w-20 py-2 text-center">HAWBNo</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(item, index) in listHawbDisplay" :key="index">
                                <td class="border border-collapse border-solid border-slate-300 text-center">{{ index + 1 }}</td>
                                <td class="border border-collapse border-solid border-slate-300 py-2 pl-2">
                                    <input class="w-100" type="text" @blur="checkHawb($event, index)" :value="item.name" @focus="hawbFocus" />
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="row justify-end" style="padding: 0.5rem">
                    <div class="item flex xs1">
                        <va-button color="info" size="medium" @click="clickUpdateHawb" :disabled="isHawbInputting">登録</va-button>
                    </div>
                </div>
            </div>
            <!--footer-->
        </div>
    </va-card>
</template>

<script>
import { DUPLICATE_HAWBNO_MESSAGE } from '@/utils/messages'
import { notificationError } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

export default {
    props: {
        changeLoading: Function,
        updateHawb: Function,
    },
    data() {
        return {
            initRdoObject: ['印刷済', '入力を対象', '入力を除外'],
            rdoObject: 0,
            listHawbDisplay: [{ index: null, name: '' }],
            isHawbInputting: false,
        }
    },
    methods: {
        hawbFocus() {
            this.isHawbInputting = true
        },
        compareStr(name) {
            const isSplit = name.split('/')
            let strTemp = '000'
            if (isSplit[1]) {
                strTemp = isSplit[1]
            }
            return isSplit[0] + '/' + strTemp
        },

        checkIsSelf(name, index) {
            return this.listHawbDisplay.find((item) => this.compareStr(item.name) === this.compareStr(name) && item.index === index)
        },

        checkUpdate(name, index) {
            const objectUpdate = this.listHawbDisplay.find((item) => item.name !== name && item.index === index)
            if (objectUpdate) {
                return true
            }
            return false
        },

        checkExist(name, e) {
            const isExist = this.listHawbDisplay.find((item) => this.compareStr(item.name) === this.compareStr(name))
            if (isExist) {
                if (name !== '') {
                    this.setError(DUPLICATE_HAWBNO_MESSAGE, e)
                }
                return true
            }
            return false
        },

        setError(error, event) {
            event.target.value = ''
            notificationError(error)
        },

        updateList(isUpdate, index) {
            if (isUpdate) {
                const listTemp = this.listHawbDisplay.filter((item) => item.index !== index)
                this.listHawbDisplay = listTemp
            }
        },

        checkHawb(e, index) {
            this.isHawbInputting = false
            const hawbNo = e.target.value.trim()
            if (this.checkIsSelf(hawbNo, index)) return
            const isUpdate = this.checkUpdate(hawbNo, index)
            const isExists = this.checkExist(hawbNo, e)
            if (hawbNo) {
                if (!isExists) {
                    this.changeLoading()
                    this.axios({
                        url: 'api/oait002/hawb-no/check',
                        method: 'get',
                        params: { hawbNo },
                    })
                        .then((res) => {
                            if (res.data.countRecord === 1) {
                                if (isUpdate) {
                                    const indexUpdate = this.listHawbDisplay.findIndex((item) => item.index === index)
                                    this.listHawbDisplay[indexUpdate].name = hawbNo
                                } else {
                                    this.listHawbDisplay.push({ name: hawbNo, index })
                                    const emptyItem = this.listHawbDisplay.filter((item) => item.index === null)
                                    const listTemp = this.listHawbDisplay.filter((item) => item.index !== null)
                                    this.listHawbDisplay = [...listTemp, emptyItem[0]]
                                }
                            } else {
                                this.updateList(isUpdate, index)
                                this.setError('入力したHAWBは存在しません。', e)
                            }
                        })
                        .catch(() => {
                            this.setError('入力したHAWBは存在しません。', e)
                        })
                        .finally(() => {
                            this.changeLoading()
                        })
                } else {
                    this.updateList(isUpdate, index)
                }
            } else {
                this.updateList(isUpdate, index)
            }
        },

        clickUpdateHawb() {
            // update list
            const listHawbTemp = []
            for (const obj of this.listHawbDisplay) {
                if (obj.name) {
                    const isSplit = obj.name.split('/')
                    let strTemp = '000'
                    if (isSplit[1]) {
                        strTemp = isSplit[1]
                    }
                    obj.name = isSplit[0] + '/' + strTemp
                }
                listHawbTemp.push(obj)
            }
            this.listHawbDisplay = listHawbTemp

            // list request
            const listHawb = []
            for (const obj of listHawbTemp) {
                if (obj.name) {
                    listHawb.push(obj.name)
                }
            }

            if (this.rdoObject === 0) {
                if (listHawb.length > 0) {
                    this.changeLoading()
                    this.axios({
                        url: 'api/oait002/hawb-no/update',
                        method: 'put',
                        data: { listHawb },
                    })
                        .then((res) => {
                            this.listHawbDisplay = [{ name: '', index: null }]
                            this.updateHawb([], '')
                        })
                        .catch((error) => {
                            console.log(error)
                        })
                        .finally(() => {
                            this.changeLoading()
                        })
                }
            } else {
                this.updateHawb(listHawb, this.rdoObject)
            }
        },
    },
}
</script>