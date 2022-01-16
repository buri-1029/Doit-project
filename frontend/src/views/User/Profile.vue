<template>
  <div>
    <v-container class="pa-3 pa-sm-16">
      <v-row class="py-3 ma-0 d-flex justify-center" style="width:100%">
        <!-- 개인 이미지 + 닉네임 시작 -->
        <v-col
          cols="12" sm="12" md="4" lg="4" xl="6"
          class="d-flex justify-center align-center flex-column image"
        >
          <div class= "group-image">
            <img v-if="image==null_image" src="@/assets/img/profile_temp.png" class="profile-img">
            <img v-else
            :src="image"
            alt="profile-img"
            class="profile-img"
            />
          </div>
          <div class="mt-2 mb-4 ml-1 text-h5">{{user.nickname}} </div>
          <v-btn 
            text
            @click="moveUpdate"
          > 
            회원정보 수정 >
          </v-btn>
        </v-col>
        <!-- 개인 이미지 + 닉네임 끝 -->
        <!-- 잔디 시작 -->
        <v-col cols="12" sm="8" md="8" lg="8" xl="6">
          <Grass></Grass>
        </v-col>
        <!-- 잔디 끝 -->
      </v-row>
      <!-- 프로필 버튼 시작 -->
      <v-row>
        <v-col class="mx-3 mx-sm-16" > 
          <v-row class="d-flex justify-center">
            <v-btn 
              text
              @click="moveChatList"
              class=" ma-2 mr-sm-8 profile-btn"
            > 
            채팅 리스트
            </v-btn>
            <v-btn 
              text
              @click="moveMileage"
              class="ma-2 mr-sm-8 profile-btn"
            > 
            마일리지 내역
            </v-btn>
          </v-row>
        </v-col>
      </v-row >
      <!-- 프로필 버튼 끝 -->
      <v-row class="py-16 d-flex justify-center">
        <v-col 
          cols="defined"
          sm="7"
          md="6"
          class="ml-3 ml-sm-16 mr-3 mr-sm-0 pa-0"
        > 
        <div class="mb-2">
          내가 작성한 피드
        </div>  
          <FeedList></FeedList>
        </v-col>
        <v-col 
          cols="defined"
          sm="3"
          class="mr-3 mr-sm-16 ml-3 ml-sm-13 mt-8 mt-sm-0 pa-0"
        > 
        <div class="mb-2">
          가입한 그룹 리스트
        </div>
        <JoinedGroupList></JoinedGroupList>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import FeedList from "@/components/group/FeedList.vue";
import JoinedGroupList from "@/components/group/JoinedGroupList.vue";
import http from "../../http-common";
import Grass from '../../components/Grass.vue';

export default {
  name: "Profile",
  components: {
    FeedList,
    JoinedGroupList,
    Grass,
  },data() {
    return {
      null_image: "http://ssafydoit.s3.ap-northeast-2.amazonaws.com/null",
      user: "",
      feed: "",
      image: "http://ssafydoit.s3.ap-northeast-2.amazonaws.com/" 
    }
  },
  created() {
      http.get('/user/detailUser')
      .then((res)=>{
          this.user = res.data.object;
          this.image = this.image + this.user.image
          console.log(this.image)
      })
  },
  methods: {
    moveUpdate(){
      this.$router.push('/user/update');
    },
    moveMileage() {
      this.$router.push('/user/mileagehistory')
    },
    moveChatList() {
      this.$router.push('/chatlist')
    }
  }
};
</script>

<style scoped>
  .group-image {
    width: 200px;
    height: 200px;
    border-radius: 70%;
    overflow: hidden;
    border: 1px solid #FFB685
  }
  .profile-btn {
    border : 2px solid #F9802D;
    border-radius: 15px;
  }
  .profile-img{
    width:100%;
    height:100%;
    object-fit: cover;
  }
   /* class="d-flex justify-center align-center flex-column */
  @media (max-width:500px){
    .group-image {
      width: 150px;
      height: 150px;
      border-radius: 70%;
      overflow: hidden;
      border: 1px solid #FFB685
    }
  }
</style>