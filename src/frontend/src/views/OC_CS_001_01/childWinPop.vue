<template>
  <va-card style="height:fit-content;">
    <div class="flex w-full">
      <div class="grid flex-grow card place-items-left" v-bind:style="leftStyle" style="border-radius:revert;">
        <PDFViewer v-if="showViewer" style="height: 100vh; width: 100%"  :source="pdfDataLeft" :defaultScale="2" :hide-sidebar="true" />
        <div v-if="!showViewer">
          <h1>{{ nullMessage }}</h1>
        </div>
      </div>
      <div class="grid flex-grow card place-items-left" v-bind:style="rightStyle">
        <PDFViewer v-if="showViewerRight" style="height: 100vh; width: 100%"  :source="pdfDataRight" />
        <div v-if="!showViewerRight">
          <h1>{{ nullMessage }}</h1>
        </div>
      </div>
    </div>
  </va-card>
</template>

<script>
import PDFViewer from 'pdf-viewer-vue'

export default {
  name: "ChildWinPop",
  mounted() {
    if (opener) {
      // eslint-disable-next-line no-unused-vars
      const self = this
      opener.gWinpop = this;
    } 
    const query = window.location.search;
    if (query == null || query === "") {
      let nativeUrl = window.location.href
      nativeUrl = nativeUrl.replace('/#', '')
      const url = new URL(nativeUrl)
      const urlParams = url.searchParams
      this.param1 = urlParams.get('param1')
      this.param2 = urlParams.get('param2')
    } else {
      const param = new URLSearchParams(query)
      if (param != null) {
        this.param1 = param.get('param1')
        this.param2 = param.get('param2')
      } 
    }

    this.calledFromOpener(this.param1,this.param2)
  },
  methods: {
    fetchPdfData() {
      
    },
    calledFromOpener(val, val2) {
    
      if (val === '' || val === null || val === undefined) {
        this.param1 = null
        this.pdfDataLeft = ''
        this.showViewer = false
        this.leftStyle = this.zeroStyle
      } else { 
        this.param1 = val
        this.leftStyle = this.basicStyle
        this.getPdfLeft()
      }

      if (val2 === '' || val2 === null || val2 === undefined) {
        this.param2 = null
        this.pdfDataRight = ''
        this.showViewerRight = false
        this.rightStyle = this.zeroStyle
        this.leftStyle = this.allStyle
      } else {
        this.rightStyle = this.basicStyle
        this.param2 = val2
        this.getPdfRight()
      }
    },
    getPdfLeft() {
      this.axios({
        method: 'post',
        url: '/api/oait001/getPdf',
        data: { pdfUrl: this.param1 },
        responseType: 'arraybuffer',
      })
        .then(response => {
          const file = new Blob([(response.data)], { type: 'application/pdf' })
          const fileUrl = URL.createObjectURL(file)
          this.pdfDataLeft = fileUrl
          this.showViewer = true
        })
    },
    getPdfRight() {
        this.axios({
          method: 'post',
          url: '/api/oait001/getPdf',
          data: { pdfUrl: this.param2 },
          responseType: 'arraybuffer',
        }).then(response => {
          const file = new Blob([(response.data)], { type: 'application/pdf' })
          const fileUrl = URL.createObjectURL(file)
          this.pdfDataRight = fileUrl
          this.showViewerRight = true
        })
    },
  },
  data() {
    return {
      message: '',
      param1: null,
      param2: null,
      url: '',
      seq: 1,
      pdfDataLeft: null,
      pdfDataRight: null,
      showViewer: true,
      showViewerRight: true,
      nullMessage: 'NO PDF FILE ',
      leftStyle: 'border-radius: revert; max-width: 50%;',
      rightStyle: 'border-radius: revert; max-width: 50%;',
      basicStyle: 'border-radius: revert; max-width: 50%;',
      allStyle: 'border-radius: revert; max-width: 100%;',
      zeroStyle: 'border-radius: revert; max-width: 0%;'
    }
  },
  components: {
    PDFViewer,
  }
}
</script>
