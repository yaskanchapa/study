<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable indent -->
<template>
  <va-card style="height:fit-content;">
    <div class="flex w-full">
      <div class="grid flex-grow card place-items-left" style="border-radius: revert; max-width: 50%;">
        <div class="row justify-start">
          <div class="item flex xs12">
            <span style="padding: 0.3rem;">登録条件</span>
          </div>
        </div>
        
        <div class="row justify-start">
          <div class="item flex xs6">
            <span style="padding: 0.3rem;">通関業者</span>
            <va-select  style="width: 260px;" v-model="insertCondition.trader" :options="traderList"
            :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
            :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
            :value-by="(option) => option.VALUE"
            placeholder="--Please choose an option--" />
          </div>
          <div class="item flex xs6">
              <span style="padding: 0.3rem;">通関場所</span>
              <va-input style="padding: 0.1rem;" v-model="insertCondition.customsCd" />
            </div>
        </div>

        <div class="row justify-start">
          <div class="row justify-start"></div>
        </div>

        <div class="row justify-start">
          <div class="item flex xs6">
            <span style="padding: 0.3rem;">登録内容選択</span>
            <va-select  style="width: 260px;" v-model="insertCondition.register" :options="registerList"
              :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
              :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
              :value-by="(option) => option.VALUE"
              placeholder="--Please choose an option--" />
          </div>
          <div class="item flex xs6">
            <span style="padding: 0.3rem;">通関状態</span>
            <va-select  style="width: 260px;" v-model="insertCondition.condition" :options="conditionList"
                :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
                :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
                :value-by="(option) => option.VALUE"
                placeholder="--Please choose an option--" />
          </div>
        </div>
        <div class="row justify-start">
          <div class="row justify-start"></div>
        </div>

        <div class="row justify-start">
          <div class="item flex xs2">
            <span style="padding: 0.3rem;">コメント</span>
          </div>
        </div>
        <div class="row justify-start">
          <div class="item flex xs1" align="left"></div>
          <div class="item flex xs10" align="left">
              <va-input type="textarea" v-model="insertCondition.comment" class="mb-6"   />
          </div>
          <div class="item flex xs1" align="left"></div>
        </div>



        <div class="row justify-start">
          <!-- <table class="table-auto border border-collapse border-solid border-slate-300 w-80">
            <thead>
              <tr>
                <th class="border border-collapse border-solid border-slate-300 w-10 py-2 text-center">確認内容</th>
                <th class="border border-collapse border-solid border-slate-300 w-20 py-2 text-center">確認項目詳細</th>
                <th class="border border-collapse border-solid border-slate-300 w-20 py-2 text-center">確認先</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                  <td class="border border-collapse border-solid border-slate-300 text-center">
                    <va-select  style="width: 90%;" v-model="insertCondition.condition" :options="conditionList"
                  :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
                  :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
                  :value-by="(option) => option.VALUE"
                  placeholder="--Please choose an option--" />
                  </td>
                  <td class="border border-collapse border-solid border-slate-300 py-2 pl-2">
                      <va-select  style="width: 90%;" v-model="insertCondition.condition" :options="conditionList"
                  :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
                  :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
                  :value-by="(option) => option.VALUE"
                  placeholder="--Please choose an option--" />
                  </td>
                  <td class="border border-collapse border-solid border-slate-300 py-2 pl-2">
                        <va-select  style="width: 90%;" v-model="insertCondition.condition" :options="conditionList"
                  :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
                  :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
                  :value-by="(option) => option.VALUE"
                  placeholder="--Please choose an option--" />
                  </td>
              </tr>
              
            </tbody>
          </table> -->
        </div>

        

      </div>

      <div class="grid flex-grow card place-items-left" style="border-radius: revert;">
        <div class="row justify-start">
          <div class="item flex xs12">
            <h1 style="padding: 0.3rem;">HAWBリスト</h1>
          </div>
        </div>

        <div class="row justify-start">
          <div class="m-6">
            <table class="table-auto border border-collapse border-solid border-slate-300 w-80">
              <thead>
                <tr>
                  <th class="border border-collapse border-solid border-slate-300 w-10"></th>
                  <th class="border border-collapse border-solid border-slate-300 w-20 py-2 text-center">HAWBNo</th>
                  <th class="border border-collapse border-solid border-slate-300 w-20 py-2 text-center">枝番</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in insertCondition.hawbList" :key="item">
                    <td class="border border-collapse border-solid border-slate-300 text-center">{{ index + 1 }}</td>
                    <td class="border border-collapse border-solid border-slate-300 py-2 pl-2">
                        <input class="w-100" type="text" v-model="item.cwbNo"  @input="addRow(index)"/>
                    </td>
                    <td class="border border-collapse border-solid border-slate-300 py-2 pl-2">
                          <input class="w-100" type="text" v-model="item.deptCd"  />
                      </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="row justify-start">
          <!-- <div class="item flex xs2">
            <span style="padding: 0.3rem;">コメント</span>
          </div> -->
        </div>
        <div class="row justify-start">
          <!-- <div class="item flex xs1" align="left"></div>
          <div class="item flex xs10" align="left">
              <va-input type="textarea" v-model="insertCondition.comment" class="mb-6"   />
          </div>
          <div class="item flex xs1" align="left"></div> -->
        </div>

        <div class="row justify-end" style="padding: 0.5rem;">
          <div class="item flex xs3">
            <va-button size="medium" color="info" type="submit" @click="insertOneShot">
              登録
            </va-button>
          </div>
          <div class="item flex xs3">
            <va-button size="medium" color="info" type="submit" @click="deleteOneShot">
              解除
            </va-button>
          </div>
        </div>

        <div class="row justify-end" style="padding: 0.5rem;"></div>
        <div class="row justify-end" style="padding: 0.5rem;"></div>
      </div>
    </div>
  </va-card>
</template>
<script>
// eslint-disable-next-line no-unused-vars
import axios from 'axios'

export default {
  name: 'App',
  props: {
    traderList: Array,
    registerList: Array,
    conditionList: Array,
    insertCondition: Object,
    getOneShotDefVal: Function,
    insertOneShot: Function,
    deleteOneShot: Function,
  },
  data() {
    return {
      
    }
  },
  methods: {
    addRow(index) { 
      if (index === this.insertCondition.hawbList.length - 1) {
        // eslint-disable-next-line vue/no-mutating-props
        this.insertCondition.hawbList.push({ cwbNo: "", deptCd: "" });
      }
    
      if (this.insertCondition.hawbList[index].cwbNo === '') { 
        // eslint-disable-next-line vue/no-mutating-props
        this.insertCondition.hawbList.splice(index, 1)

      }
    }
  }
}

</script>
<style>
.bar-color1-red {
  --tw-bg-opacity: 1;
  background-color: #538ce2;
}

.bar-color2-red {
  --tw-bg-opacity: 1;
  background-color: #538ce2;
}

.ant-notification-top {
  margin-top: 30%;
}

.bar-color1-red {
  --tw-bg-opacity: 1;
  background-color: #2b89e7;
}

.bar-color2-red {
  --tw-bg-opacity: 1;
  background-color: #337ecc
}

.bar-color3 {
  --tw-bg-opacity: 1;
  background-color: #858d96
}</style>