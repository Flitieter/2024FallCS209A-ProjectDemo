import { createRouter, createWebHistory } from "vue-router";
import Errors from "../views/Errors.vue";

const routes = [
    {
        path: "/errors",
        name: "Errors",
        component: Errors,
    },
    {
        path: "/:catchAll(.*)",
        redirect: "/errors",
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

export default router;
