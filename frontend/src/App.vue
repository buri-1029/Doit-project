<template>
  <v-app>
    <Header></Header>
    <div>
      <router-view></router-view>
    </div>
    <Footer></Footer>
  </v-app>
</template>

<script>
import "./assets/css/main.css";
import Header from "@/components/common/Header.vue";
import Footer from "@/components/common/Footer.vue";

export default {
  name: 'App',

  components: {
    Header,
    Footer
    },
  

  data: () => ({
    //
  }),
};
</script>
