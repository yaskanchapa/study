<script setup>
import SearchCondition1 from './SearchCondition1.vue'
import SearchCondition2 from './SearchCondition2.vue'
import SearchResult from './SearchResult.vue'
</script>

<template>
  <div class="container">
    <div class="w-90%">
      <!--検索条件-->
      <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
        <div>
          <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">検索条件</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
              <va-tabs v-model="currentView">
                <template #tabs>
                  <va-tab  v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
                </template>
              </va-tabs>
              <component :is="currentView"></component>
            </div>
        </div>
      </div>

      <!-- 検索結果 -->
      <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
        <div>
          <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">検索結果</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
              <SearchResult />
            </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const TABS = [
  { title: '検索1', name: 'searchCondition1', color: 'success' },
  { title: '検索2', name: 'searchCondition2', color: 'success' }
]

export default {
  components: {
    searchCondition1: SearchCondition1, // eslint-disable-line
    searchCondition2: SearchCondition2 // eslint-disable-line
  },
  data: () => ({
    tabs: TABS,
    currentView: 'searchCondition1'
  }),
  computed: {
    currentTab () {
      return this.tabs.find(({ title }) => title === this.tabValue)
    }
  }
}

</script>

<style>
.bar-color1-red {
  --tw-bg-opacity: 1;
  background-color: #f33636;
}
.bar-color2-red {
  --tw-bg-opacity: 1;
  background-color: #db4040;
}
</style>
