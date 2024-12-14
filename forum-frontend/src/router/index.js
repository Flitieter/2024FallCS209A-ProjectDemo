import { createRouter, createWebHistory } from "vue-router";
import Errors from "../views/Errors.vue";
import Tags from "@/views/Tags.vue";
import HighReputationTags from "@/views/HighReputationTags.vue";

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
        path: "/:pathMatch(.*)*", // 这个路径用于捕捉所有未匹配的路径
        redirect: "/errors", // 将所有未匹配的路径重定向到 /errors
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

export default router;
