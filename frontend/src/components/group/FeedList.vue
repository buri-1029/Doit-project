<template>
    <v-card
      class="mx-auto"
      height="600"
    >
      <div class="pa-0 ma-0 d-flex flex-nowrap flex-column justify-start align-center scrollBar">
          <MyFeeds v-for="(card,idx) in cards" :key="idx" :card="card"></MyFeeds>
      </div>        
    </v-card>
</template>

<script>
import MyFeeds from '@/components/group/MyFeeds.vue'
import http from "../../http-common";


  export default {
    components: {
      MyFeeds,
    },
    created(){
      // http.get(`feed/userFeed?end=60&start=0&userPk=${sessionStorage.getItem("userpk")}`)
      http.get(`feed/userFeed?&userPk=${sessionStorage.getItem("userpk")}`)
      .then((res)=>{
        this.cards = res.data.object
      })
    }
    ,
    data: () => ({
      cards:[]
    }),
  }
</script>

<style scoped>
  .scrollBar{
    overflow-y: scroll;
    width: 100%;
    height:100%;
    padding: 20px;
  }


</style>
