import { createRouter, createWebHistory } from "vue-router";
import Errors from "../views/Errors.vue";
import Tags from "@/views/Tags.vue";
import HighReputationTags from "@/views/HighReputationTags.vue";
import Analyze from "@/views/Analyze.vue";
import Main from "@/views/Main.vue";

const routes = [
    {
        path: "/errors",
        name: "Errors",
        component: Errors,
    },
    {
        path: "/tags",
        name: "Tags",
        component: Tags,
    },
    {
        path: "/tags/reputation/:score",
        name: "HighReputationTags",
        component: HighReputationTags,
    },
    {
        path: "/analyze",
        name: "Analyze",
        component: Analyze,
    },
    {
        path: "/main",
        name: "Main",
        component: Main,
    },
    {
        path: "/:pathMatch(.*)*", // 这个路径用于捕捉所有未匹配的路径
        redirect: "/main", // 将所有未匹配的路径重定向到 /main
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

export default router;
