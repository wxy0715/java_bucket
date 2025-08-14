<template>
  <div>
    <HtUpload v-model="fileList" :objectId="objectId" :objectType="objectType" :thumbnailEnable=true />
  </div>
</template>

<script setup>
import HtUpload from "@/components/HtUpload/index.vue";
import { ref } from "vue";

const fileList = ref([]);
const objectId = ref(10)
const objectType = ref('11')

</script>

<style scoped>
</style>
