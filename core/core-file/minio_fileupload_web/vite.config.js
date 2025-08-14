import {fileURLToPath, URL} from "node:url";

import {defineConfig} from "vite";
import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue(), vueJsx()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: {
    host: true,
    open: true,
    port: 3005,
    proxy: {
      "/ht-upload": {
        target: "http://192.168.30.94:8080", // 后端实际域名
        changeOrigin: true, // 允许跨域
        rewrite: (path) => path.replace(new RegExp(`^/ht-upload`), ""),
      },
    },
  },
});
