import http from "./request";

/**
 * 初始化分片任务
 */
const initPart = async (param) => {
  return http.post("/XSpring/initPart", param);
};

/**
 * 判断分片是否上传完成
 */
const checkPart = async (param) => {
  return http.post("/XSpring/checkPart", param);
};

/**
 * 分片上传
 */
const uploadPart = async (param, process) => {
  return http.post("/XSpring/uploadPart", param, {
    onUploadProgress: process,
  });
};

/**
 * 文件上传
 */
 const uploadList = async (param) => {
  return http.post("/XSpring/uploadList", param);
};

/**
 * 文件上传
 */
 const upload = async (param) => {
  return http.post("/XSpring/upload", param);
};

/**
 * 合并分片
 */
const mergePart = async (param) => {
  return http.post("/XSpring/mergePart", param);
};

/**
 * 预览
 */
 const presigned = async (param) => {
  return http.post("/XSpring/presigned", param);
};

/**
 * 删除文件
 */
const deleteFile = async (param) => {
  return http.post("/XSpring/delete", param);
};

/**
 * 下载文件
 */
const downloadFile = async (url) => {
  return http({
    url: `/XSpring/download?url=${url}`,
    method: "post",
    responseType: "blob",
  });
};

export { upload,initPart, checkPart, uploadPart, mergePart, deleteFile, downloadFile,presigned };
