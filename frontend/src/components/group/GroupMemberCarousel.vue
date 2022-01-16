<template>
  <v-row
    class="mt-6 mb-12 mx-sm-12 d-flex align-center flex-wrap justify-space-between"
  >
    <v-col cols="12" sm="5" md="5" lg="5" class="profile-card mx-xs-6" v-for="(item, idx) in paginatedData" :key="idx">
      <div class="d-flex align-center">
          <img v-if="!item.image" src="@/assets/img/profile_temp.png" class="profile-image">
          <img v-else :src="`http://ssafydoit.s3.ap-northeast-2.amazonaws.com/` + item.image" class="profile-image">
        <span class="mx-2">{{item.nickname}}</span>
        <v-btn text v-if="loginUser==leader && item.userPk != leader" normal x-small class="px-0 kick" @click="kickout(item.userPk)">강퇴</v-btn>
        <span v-if="item.userPk == leader" class="groupleader"><font-awesome-icon icon="crown"/></span>
      </div>
      <div v-if="item.auth"
        class="d-flex flex-column align-center justify-end auth-icon ml-8">
        <font-awesome-icon class="fa-3x " :icon="['far', 'smile']" />
        <div style="font-size:14px;">오늘인증</div>
      </div>
    </v-col>
  </v-row>
</template>

<script>
import http from "../../http-common";
import { notiType, sendNotify } from "../../api/notification/index"

  export default {
    data: () => ({
      loginUser: '',
      loading: false,
      selection: 1,
      users :[],
      category:'',
      content:'',
      leader:'',
      groupName:'',
      totalNum:'',
      endDate:'',
      createDate:'',
      authUser:[],
    }),
    props: {
      page:Number,
      groupPk:Number,
    }, 
    created() {
      this.getAuthUser()
    },
    watch: {
      groupPk(){
        http.get(`/group/detailGroup?groupPk=${this.groupPk}`)
        .then((res)=>{
        console.log(res);
        this.users = res.data.object.users
        this.category = res.data.object.category
        this.content = res.data.object.content
        this.leader = res.data.object.leader
        this.groupName = res.data.object.name
        this.totalNum = res.data.object.totalNum
        this.endDate = res.data.object.endDate
        this.createDate = res.data.object.createDate
        this.loginUser = this.$store.state.account.userpk
      })
      }
    },
    computed: {
      paginatedData () {
        const start = (this.page-1) * 4,
              end = start + 4;
        // console.log(this.users.slice(start, end))
        return this.users.slice(start, end);
      }
    },
    methods : {
      getAuthUser() {
        http.get(`/feed/todayAuthUser?groupPk=${this.groupPk}`)
        .then((res)=>{
          const tempList = res.data.object
          tempList.forEach(user => {
            this.authUser.push(user.userPk)
          })
          http.get(`/group/detailGroup?groupPk=${this.groupPk}`)
          .then((res)=>{
            this.users = res.data.object.users
            this.category = res.data.object.category
            this.content = res.data.object.content
            this.leader = res.data.object.leader
            this.groupName = res.data.object.name
            this.totalNum = res.data.object.totalNum
            this.endDate = res.data.object.endDate
            this.createDate = res.data.object.createDate
            this.loginUser = this.$store.state.account.userpk
            this.users.map(user => {
              if (JSON.parse(JSON.stringify(this.authUser)).includes(user.userPk)) {
                user.auth = true;
              } else {
                user.auth = false;
              }
              return user
            })
          })

        })
      },
      kickout (userPk) {
        const delIdx = this.users.findIndex(i => i.userPk === userPk);
        // console.log(delIdx)
        this.users.splice(delIdx, 1)
        http.delete(`/group/kickOutGroupUser?groupPk=${this.groupPk}&userPk=${userPk}`)
        .then((res)=>{
          console.log(res)
          sendNotify({
            "notiType": notiType.KICKOUT,
            "userPk": userPk,
            "targetId": this.groupPk,
          })
        })
      }
    }
  }
</script>

<style scoped>

.profile-card {
  width: 100%;
  height: 50px;
  margin: 10px 20px;
  padding: 0px 20px;
  position:relative;
}

.profile-image {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 1px solid #F9802D;
}

.groupleader {
  color:#F9802D;
}

.auth-icon {
  color:green;
  opacity: 0.2;
  text-align: center;
  transform: rotate(-30deg);
  position:absolute;
  top:0;
  left:110px;
  z-index: -3;
}

.kick {
  background-color: orange;
  color:white;
}

.kick:hover {
  background-color: red;
  transform : scale(1.05);
  
}

</style>