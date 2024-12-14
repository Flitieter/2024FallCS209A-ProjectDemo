import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import axios from "axios";

// 设置 axios 的基础路径
axios.defaults.baseURL = "http://localhost:9091";

createApp(App).use(router).mount("#app");
