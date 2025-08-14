<template>
  <div>
    <div>
          <!-- 使用 WideQuillEditor 组件 -->
    <WideQuillEditor
      v-model="editorContent"
      :width="'80%'"
      :height="'400px'"
    />
    </div>
    <button style="margin-top:100px" @click="updateContent">更新内容</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import WideQuillEditor from '@/components/quill/QuillEditor.vue';
const editorContent = ref('k');

// 定义更新内容的函数
const updateContent = () => {
  console.log('更新内容:', editorContent.value);
};
</script>