<template>
    <div class="checkbox-center">
        <va-checkbox v-model="value" readonly />
    </div>
</template>

<script>
export default {
    data() {
        return {
            value: false, // boolean
        }
    },
    mounted() {
        this.value = this.params.data.optionSelectedFlag === 1
    },
}
</script>

<style scoped>
.checkbox-center {
    margin-top: 10px;
    display: flex;
    justify-content: center;
}
</style>