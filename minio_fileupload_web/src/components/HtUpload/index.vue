<!--
  @description 通用上传组件
  @date 2024/04/12 13:09:39
!-->
<template>
  <div class="ht-upload">
    <el-upload
      :file-list="fileList"
      v-bind="attrs"
      class="upload-demo"
      action="#"
      :http-request="handleHttpRequest"
      multiple
      :show-file-list="false"
    >
      <el-tooltip
          content="<span>文件超过10M会进行分片,不会进行存储缩略图</span>"
          raw-content
        >
        <el-button size="small" type="primary" v-loading.fullscreen.lock="fullscreenLoading">上传附件</el-button>
      </el-tooltip>
    </el-upload>

    
    <div class="file-list">
      <div class="file-item" v-for="(file, index) in fileList" :key="index">
        <!-- 进度条或图片预览 -->
        <el-progress :width="80"  v-if="!(file.process >= 100)" type="circle" :percentage="file.process"></el-progress>
        <img id="file-preview" v-if="isPreviewReady(file)" :src="getPreviewUrl(file)" @click="showFilePreview(file)" class="file-preview" />
        <!-- 文件名 -->
        <div class="file-name" @click="download(file.url)">{{ file.originalFilename }} </div>
        <!-- 删除按钮 -->
        <button class="remove-button" @click="__remove__(file.url, index)">×</button>
      </div>
    </div>

    <!-- 文件预览对话框 -->
    <el-dialog 
        v-model="dialogVisible"
        width="800px" 
      >
      <iframe :src="previewImageUrl" width="100%" height="500px"></iframe>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="openInNewTab">新页面打开</el-button>
    </el-dialog>

  </div>
</template>

<script setup>
import { caculate, useUpload, downloadBlobFile,executePromisesInSequentialBatches } from "./htFileUploadUtil";
import { onBeforeUnmount,computed, ref,nextTick, useAttrs } from "vue";
import { ElNotification,ElDialog } from "element-plus";
import {Base64} from "js-base64";
import {
  upload,
  initPart,
  checkPart,
  uploadPart,
  mergePart,
  deleteFile,
  downloadFile,
  presigned
} from "./api";

const props = defineProps({
  //上传文件列表
  modelValue: {
    type: Array,
    default: () => [],
  },
  // 关联对象id(通常是保存后返回的主键id)
  objectId:{
    type: Number,
    default: 0,
  },
  // 关联对象类型
  objectType:{
    type: String,
    default: '',
  },
  // 是否开启缩略图预览
  thumbnailEnable:{
    type: Boolean,
    default: false,
  }
});

// 控制对话框显示状态
const dialogVisible = ref(false);
const previewImageUrl = ref('');
const fullscreenLoading = ref(false);

const fileList = computed({
  get() {
    return props.modelValue;
  },
  set(val) {
    emit("update:modelValue", val);
  },
});

const emit = defineEmits(["update:modelValue"]);
const attrs = useAttrs();

// 下载文件
const download = async (url) => {
  try {
    fullscreenLoading.value = true;
    const res = await downloadFile(url);
    downloadBlobFile(new Blob([res.data], { type: res.data.type }), res.filename);
    ElNotification({
      message: "文件下载成功",
      type: "success",
    });
  } catch (error) {
    ElNotification({
      message: "文件下载失败",
      type: "error",
    });
  } finally {
    fullscreenLoading.value = false;
  }
};

// 上传文件
const handleHttpRequest = async ({ file, onProgress, onSuccess, onError }) => {
  // 开始时间
  const startTime = Date.now();
  // 下于10M,直接上传不需要分片
  if (file.size <= 10 * 1024 * 1024) {
    const formdata = new FormData();
    formdata.append('file',file);
    formdata.append('thumbnailEnable',props.thumbnailEnable);
    formdata.append('objectId',props.objectId);
    formdata.append('objectType',props.objectType);
    try {
      fullscreenLoading.value = true;
      const { data } = await upload(formdata);
      // 设置上传状态和进度条，1：初始化完成，2：上传完成 
      const fileInfo = data?.[0];
      fileInfo.process = 100;
      const fileIndex = fileList.value.length;
      fileInfo.fileIndex = fileIndex;
      fileList.value = [...fileList.value, fileInfo];
    } catch (error) {
      ElNotification({
        message: "文件上传失败",
        type: "error",
      });
    } finally {
      fullscreenLoading.value = false;
    }
    return;
  }
  //获取文件的md5值
  const { md5, splitChunk } = await useUpload(file);
  // 初始化分片上传任务
  const { data } = await initPart({
    originalFilename: file.name,
    md5,
    length: file.size,
  });
  // 设置上传状态和进度条，1：初始化完成，2：上传完成 
  const fileInfo = data?.[0];
  fileInfo.process = fileInfo.uploadStatus === 2 ? 100 : 0;
  const fileIndex = fileList.value.length;
  fileInfo.fileIndex = fileIndex;
  fileList.value = [...fileList.value, fileInfo];
  if (fileInfo.uploadStatus === 2) {
    console.log("秒传成功");
    return;
  }
  await nextTick();
  try {
    console.log("开始分割文件");
    const chunks = await splitChunk(file);
    console.log("开始检查分片上传");
    const checks = await executePromisesInSequentialBatches(
      chunks.map((currentValue,index) => ({
        handle: () => checkPart({ url: fileInfo.url, index: index + 1 }),
        options: { chunkIndex: index + 1 },
      }))
    );
    const toUploadIndexs = checks.reduce((r, item) => {
      if (item.data[0] === -1) r.push(item.chunkIndex); //将未上传的分片添加到索引数组，返回的值为-1，说明该分片未上传过
      return r;
    }, []);
    const chunkProcessMap = new Map();
    console.log("上传分片",toUploadIndexs);
    await executePromisesInSequentialBatches(
      toUploadIndexs.map((toUploadIndex) => {
        const chunk = chunks[toUploadIndex - 1];
        let formdata = new FormData();
        formdata.append("file", chunk);
        formdata.append("index", toUploadIndex);
        formdata.append("url", fileInfo.url);
        return () =>
          uploadPart(formdata, (process) => {
            chunkProcessMap.set(toUploadIndex, process.loaded);
            const totalLoaded = [...chunkProcessMap.values()].reduce(
              (a, b) => caculate(a).add(b).val,
              0
            );
            const realProcess = caculate(totalLoaded)
              .divide(file.size)
              .multiply(100).val;
            fileList.value[fileInfo.fileIndex].process =
              realProcess >= 100 ? 100 : +realProcess.toFixed(2);
            console.log(fileList.value[fileInfo.fileIndex].process);
          });
      })
    );
    console.log("合并分片");
    await mergePart({ url: fileInfo.url });
  } catch (error) {
    fileList.value = fileList.value.toSpliced(fileInfo.fileIndex, 1);
    ElNotification({
      message: `${fileInfo.originalFilename}上传失败`,
      type: "error",
    });
  }
  // 结束时间
  const endTime = Date.now();
  // 总耗时
  console.log(`上传耗时：${(endTime - startTime) / 1000}s`,'文件大小',file.size/1024/1024,'M');
};

// 删除文件
const __remove__ = async (url, index) => {
  try {
    fullscreenLoading.value = true;
    await handleRemove(url);
    fileList.value = fileList.value.toSpliced(index, 1);
  } catch (error) {
  } finally {
    fullscreenLoading.value = false;
  }
};

const handleRemove = async (url) => {
  if (!url) return console.error("删除文件失败：参数url不能为空");
  await deleteFile({ url });
};

// 是否显示缩略图
const isPreviewReady = (file) => {
  return file.process >= 100 && file.attr?.thPresignedByte;
};

// 确保只有当进度达到100时才生成图片URL
const getPreviewUrl = (file) => {
  return file.process >= 100 ? createImageUrl(file) : null;
};

// 将字节数组转换为可显示的图片URL
function createImageUrl(file) {
  const url = ""
    try {
      // 确保Base64字符串中不包含"data:image/jpeg;base64,"这样的前缀
      let base64Data = file.attr.thPresignedByte;
      if (base64Data.startsWith('data:')) {
        base64Data = base64Data.split(',')[1];
      }
      // 检查并修复Base64字符串长度，使其成为4的倍数
      const paddingLength = base64Data.length % 4;
      if (paddingLength > 0) {
        base64Data += '='.repeat(4 - paddingLength);
      }
      // 将Base64字符串解码为字节数组
      const byteCharacters = atob(base64Data);
      const byteNumbers = new Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      const byteArray = new Uint8Array(byteNumbers);
      // 创建Blob对象并生成对象URL
      const blob = new Blob([byteArray], { type: 'image/jpeg' });
      url = URL.createObjectURL(blob);
      file.previewUrl = url
    } catch (error) {
      console.error('base64转换错误,进行直接赋值');
      file.previewUrl = 'data:image/png;base64,' + file.attr.thPresignedByte;
    } finally {
      // 3s后释放URL
      setTimeout(() => {
        URL.revokeObjectURL(url);
      }, 3000);
    }
    return file.previewUrl;
}

// 显示文件预览
const showFilePreview = async (file) => {
  // 获取后端接口的预览URL
  const res = await presigned({ path:file.url});
  const url = res.data[0].url;
  const kkFileViewUrl = res.data[0].kkFileViewUrl;
  if (kkFileViewUrl || url) {
    dialogVisible.value = true;
    previewImageUrl.value = kkFileViewUrl + '?url='+encodeURIComponent(Base64.encode(decodeURIComponent(url)));
    console.log('kkFileViewUrl', previewImageUrl.value);
  } else {
    ElNotification({
      message: "无法预览文件,未配置预览地址",
      type: "error",
    });
  }
};

// 新标签也打开预览
const openInNewTab = () => {
  window.open(previewImageUrl.value, '_blank');
  dialogVisible.value = false;
}

onBeforeUnmount(() => {
  // 组件卸载前释放所有URL
  if(fileList && fileList.value) {
    fileList.value.forEach(file => {
      URL.revokeObjectURL(file.previewUrl);
    });
  }
});

defineExpose({
  download,
  remove: handleRemove,
  createImageUrl,
  isPreviewReady,
  getPreviewUrl,
  showFilePreview,
  openInNewTab
});
</script>

<style scoped>
.file-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 10px;
}

.file-item {
  position: relative; /* 使子元素可以相对于此元素进行绝对定位 */
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 300px; /* 设置固定宽度 */
  height: 80px;
  word-break: break-word; /* 如果有文本内容，允许长单词断行 */
  overflow-wrap: break-word; /* 允许长单词和URL断行 */
  overflow: hidden; /* 隐藏溢出的内容 */
}

/* 定义图片的最大宽度 */
.file-preview {
  width: 80px; /* 设置你想要的最大宽度 */
  height: 80px; /* 维持图片的宽高比例 */
  cursor: pointer;
}

.file-name {
  flex: 1;
  cursor: pointer;
}

/* 删除按钮样式 */
.remove-button {
  position: absolute;
  top: 1px;
  right: 1px;
  background: none;
  border: none;
  font-size: 25px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
}

/* 当鼠标悬停在 .file-item 上时显示删除按钮 */
.file-item:hover .remove-button {
  opacity: 1;
}
</style>
