import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify';
import "../fontAwesomeIcon.js"

Vue.config.productionTip = false;

new Vue({
    router,
    store,
    vuetify,
    render: h => h(App),
    created() {
        const accessToken = sessionStorage.getItem("accessToken");
        const email = sessionStorage.getItem("email");
        const name = sessionStorage.getItem("name");
        const userpk = sessionStorage.getItem("userpk");

        console.log(accessToken);
        if (accessToken) {
            this.$store.commit("SET_ACCESSTOKEN", { accessToken, email, name, userpk});
        }
    }
}).$mount('#app');


