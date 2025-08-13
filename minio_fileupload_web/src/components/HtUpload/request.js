import axios from "axios";
import {ElNotification} from "element-plus";

const baseUrl = "/ht-upload";

const http = axios.create({
  baseURL: baseUrl,
});

http.interceptors.request.use((request) => {
  return request;
});

http.interceptors.response.use(
  (response) => {
    if (response.data.code !== "00000") {
      if (response.request.responseType == "blob") {
        //可能为json
        const contentType = response.headers["content-type"];
        if (
          response.data.type === "application/json" ||
          contentType?.startsWith("application/json")
        ) {
          const reader = new FileReader();
          return new Promise(function (resolve, reject) {
            reader.readAsText(response.data, "utf-8");
            reader.onload = function () {
              const res = JSON.parse(reader.result);
              console.log(res);
              if (!res.IsSuccess) {
                ElNotification({
                  message: res.ErrorMessage || "加载失败",
                  type: "error",
                  duration: 5 * 1000,
                });
                return reject(res);
              }
              return resolve(res);
            };
          });
        } else {
          //文件流
          const contentDisposition = response.headers["content-disposition"];
          const filename =
            (contentDisposition &&
              decodeURI(contentDisposition.match(/(filename=(.*))/)[2])) ||
            new Date().getTime() + ".pdf";
          return {
            data: response.data,
            filename: filename.match(/^("|')(.*?)("|')$/)?.[2] || filename,
          };
        }
      }
      ElNotification({
        title: "失败",
        message: response.data.description,
        type: "error",
      });
      return Promise.reject(response.data);
    }
    return response.data;
  },
  (error) => {
    // NotifyCus({ type: 'danger', message: error.response.statusText });
    console.error(error);
    return Promise.reject(error);
  }
);

export default http;
