<template>
  <v-card
    max-width="400"
    class="mx-auto"
  >
    <v-virtual-scroll
      :bench="benched"
      :items="items" 
      max-height="600"
      class="joined-size"
      item-height="64"
    >
      <template v-slot="{ item }">
        <v-list-item :key="item.groupPk" @click="group(item.groupPk)">
            <v-list-item-avatar>
              <!-- 연결하면 유저 이미지로 사용 -->
              <v-img v-if="item.image" :src="`http://ssafydoit.s3.ap-northeast-2.amazonaws.com/` + item.image"></v-img>
              <!-- 이미지 없으면 랜덤 사진, 나중에 사진 바꾸기! -->
              <v-img v-else src="" class="default"></v-img>
            </v-list-item-avatar>

          <v-list-item-content>
            <v-list-item-title>
              {{ item.name }}
            </v-list-item-title>
          </v-list-item-content>
        </v-list-item>

        <v-divider></v-divider>
      </template>
    </v-virtual-scroll>
  </v-card>

</template>

<script>
import http from '../../http-common'

export default {
  name: "JoinedGroupList",
  data: () => ({
    benched: 0,
    items: [],
  }),
  created() {
    // 현재 로그인 한사람의 가입 그룹 리스트
    if(this.$store.getters.getAccessToken){
        http.get('/group/currentUserGroup')
          .then((res)=>{
          this.items = res.data.object;
      })
    }    
  },
  methods: {
    group(no) {
      this.$router.push({name:"Community",params:{groupPk:Number(no)}});
    },
  }
}
</script>

<style>
 .default{
   border: 1px solid #FFB685  
  }
  .joined-size {
   width: 100%;
   height: 600px !important;
  }

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .joined-size {
    width: 120%;
    height: 300px  !important;
  }
}



</style>