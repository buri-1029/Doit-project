<template>
  <v-container class="grass">
    <v-row> 
      <v-col class="px-0">
       <span class="text-h4">금잔디 🏆 </span> <p>잔디를 진하게 채워나가며 성취감을 가져보세요.</p>
      </v-col> 
    </v-row>
    <v-row d-flex>
      <v-col v-for="(cnt,idx) in size" :key="idx"  cols="1" class="pa-0 mr-2 mb-2">
        <div class="box" :class="{ color1: check1(cnt), color2: check2(cnt), color3: check3(cnt) }"></div>
      </v-col>
    </v-row>
    <v-row> 
      <v-col class="px-0">
       <span >30일 동안의 {{name}}님의 기록입니다. </span> 
      </v-col> 
    </v-row>
  </v-container>
</template>

<script>
import http from "../http-common"


export default {
  data: () =>({
    size: [],
    name: "",
  }),
  created () {
    this.name = this.$store.getters.getName;
    http.get(`data/grass?userPk=${sessionStorage.getItem("userpk")}`)
    .then((res)=> {
      this.size = res.data.object
    })
  },
  methods:{
    check1 (cnt){
      if (cnt==1) return true
      return false
    },
    check2 (cnt){
      if (cnt==2) return true
      return false
    },
    check3 (cnt){
      if (cnt>=3) return true
      return false
    }
  }
}
</script>

<style scoped>
  span{
    color:#FB8C00
  }
  .grass{
    width: 350px;
  }
  @media (min-width:700px) {
    .grass{
      width: 500px;
    }
  }
  .color1{
    background: #FFE0B2
  }
  .color2{ 
    background:#FFB74D
  }
  .color3{
    background: #FB8C00
  }
  .box{
    border: 1px solid rgb(2, 2, 2, 0.3);
    padding-bottom:100%;
  }
</style>