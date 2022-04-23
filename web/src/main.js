
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import $ajax from './ajax';
import store from './store';
import Vue from 'vue'

Vue.prototype.$ajax = $ajax;
Vue.config.productionTip = false
Vue.use(ElementUI);

router.beforeEach((to, from, next) => {
    /* 路由发生变化修改页面title */
    if (to.meta.title) {
        document.title = to.meta.title
    }
    next()
})

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')
