<!-- eslint-disable no-undef -->
<!-- eslint-disable no-undef -->
<!-- eslint-disable camelcase -->
<template>
  <div>
  </div>
</template>

<script>




export default {
  name: "WinPopup",
  model: {

  },
  props: {
  },
  watch: {

  },
  mounted() {

  },
  methods: {
    openWinPop(uri, width, height,imagePath) {
      if (this.windowRef != null) {
        this.closeWinPop();
      }

      const left = (screen.width) ? (screen.width - width) / 2 : 0;
      const top = (screen.height) ? (screen.height - height) / 2 : 0;

      const attr = 'top=' + top + ', left=' + left + ', width=' + width + ', height=' + height + ', resizable=no,status=no';

      this.windowRef = window.open(uri, "", attr);
      if (this.windowRef != null) {
        this.windowRef.addEventListener('beforeunload', this.evtClose);
      } else {
        alert("window.open fail!!!");
      }
    },
    closeWinPop() {
      if (this.windowRef) {
        this.windowRef.close();
        this.windowRef = null;
      }
    },
    evtClose() {
      if (this.windowRef) {
        this.windowRef.close();
        this.windowRef = null;
        this.$emit('onClose');
      }
    },
    sendEvtToChild(evt, evt2) {
      if (this.windowRef === 'undefined' || this.windowRef === null) {
        return;
      } 
      
      // eslint-disable-next-line no-undef
      if (gWinpop === null) {
        return;
      }
      // eslint-disable-next-line no-undef
      if (this.windowRef.closed) { 
        return;
      }
      // eslint-disable-next-line no-undef
      this.windowRef.opener.gWinpop.calledFromOpener(evt,evt2)
    },
    calledFromOpener(evt) {
      if (evt.msg === "N") {
        this.seq = this.seq + 1;
      } else {
        this.seq = this.seq - 1;
      }
      this.url = "../../../images/pdf/test" + this.seq + ".pdf";
      this.message = evt.msg;
    }
  },
  data() {
    return {
      windowRef: null,
    }
  }
}
</script>

<style scoped>

</style>