<template>
  <div>
    <v-container>
    <h2 class="my-10"> 👷‍♂Doit 관리자 페이지👷‍♂</h2>
      <v-row>
        <v-col>
          <h3 >그룹 정보</h3>
          <span>해당 셀을 누르면 그룹으로 이동이 가능합니다.</span>
          <GroupList></GroupList>
        </v-col>
      </v-row>
      <v-row class="mt-10">
        <v-col>
          <h3>회원 정보</h3>
          <span>해당 셀을 누르면 회원 강퇴여부를 결정할 수 있습니다.</span>
          <MemberList></MemberList>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import http from "../http-common"
import GroupList from "@/components/admin/GroupList"
import MemberList from "@/components/admin/MemberList"
export default {
  components:{
    GroupList, MemberList
  },
  created () {
    http.get('user/detailUser')
    .then((res)=>{
      console.log(res)
    })

    
  }
}
</script>

<style scoped>
  h2, h3{
  color: #F9802D;
}

</style>