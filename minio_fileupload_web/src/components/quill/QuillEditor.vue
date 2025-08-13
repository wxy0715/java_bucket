<template>
  <div :style="editorStyle">
    <QuillEditor
      v-model:content="localContent"
      :contentType="contentType"
      :toolbar="toolbarOptions"
      :theme="theme"
      @ready="onEditorReady"
      @text-change="onTextChange"
      @selection-change="onSelectionChange"
    />
  </div>
</template>

<script>
import { QuillEditor } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css';

export default {
  components: {
    QuillEditor
  },
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    contentType: {
      type: String,
      default: 'html'
    },
    toolbarOptions: {
      type: Array,
      default: () => [
        ['bold', 'italic', 'underline', 'strike'],
        [{ 'header': 1 }, { 'header': 2 }],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        [{ 'script': 'sub' }, { 'script': 'super' }],
        [{ 'indent': '-1' }, { 'indent': '+1' }],
        [{ 'direction': 'rtl' }],
        [{ 'size': ['small', false, 'large', 'huge'] }],
        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'font': [] }],
        [{ 'align': [] }],
        ['clean'],
        ['link', 'image', 'video']
      ]
    },
    theme: {
      type: String,
      default: 'snow'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data() {
    return {
      localContent: this.modelValue
    };
  },
  watch: {
    modelValue(newValue) {
      if (this.localContent!== newValue) {
        this.localContent = newValue;
      }
    }
  },
  computed: {
    editorStyle() {
      return {
        width: this.width,
        height: this.height
      };
    }
  },
  methods: {
    onEditorReady(editor) {
      this.$emit('ready', editor);
    },
    onTextChange() {
      this.$emit('update:modelValue', this.localContent);
    },
    onSelectionChange(...args) {
      this.$emit('selection-change',...args);
    }
  }
};
</script>

<style scoped>
/* 可选：为组件添加自定义样式 */
</style>