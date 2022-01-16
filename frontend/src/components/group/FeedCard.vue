<template>
  <v-card
    :loading="loading"
    class="mx-auto mb-12 noticard"
    width="550"
    name="noticard"
  >
    <template slot="progress">
      <v-progress-linear
        color="deep-purple"
        height="10"
        indeterminate
      ></v-progress-linear>
    </template>
    <v-img
      v-if="!image"
      height="250"
      gradient="to bottom, rgba(0,0,0,.1), rgba(0,0,0,.5)"
    ></v-img>
    <v-img v-else :src="DefaultURL + image"></v-img>
    <span v-if="!feedType" class="ma-3 text-h6 title">정보</span>
    <span v-else class="ma-3 text-h6 title">인증</span>
    <v-card-title class="card_content">{{this.card.content}} </v-card-title>

    <v-card-text>
      <v-row>
        <v-col class="col-8">
          작성자:<span> {{this.card.writer}}</span>
         <div> 작성날짜: {{this.card.createDate.slice(0,10)}}</div>
        </v-col>
        <v-col class="col-4 d-flex flex-column align-center justify-end auth-icon">
          <!-- <img v-if="check" src="@/assets/img/stamp.png" class="stamp"> -->
            <font-awesome-icon v-if="check" class="fa-3x  " :icon="['far', 'smile']" />
            <div v-if="check" style="font-size:14px;" class="">인증</div>
        </v-col>
      </v-row>
      <v-row class="d-flex justify-space-between  align-center mx-3">
        <!-- 본인이면 피드 수정 --> 
        <div v-if="!writer && feedType ">
          <v-btn outlined class="my-3 btn" v-if="!auth && !check" @click="accept" >인증</v-btn>
          <span class="my-3" v-else-if="auth" >인증을 해주셨습니다.</span>
          <!-- 다른 유저면 인증/미인증으로 구분 -->
        </div>
       
        <div v-if="writer">
          <v-btn outlined class="my-3 mr-5 btn green-btn" @click="feedUpdate">피드 수정 </v-btn>
          <v-btn outlined class="my-3 btn red-btn" @click="feedDelete">피드 삭제 </v-btn>    
        </div>
        <v-btn
        icon
        @click="show=!show">
          <v-icon>{{ show ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
        </v-btn>
      </v-row>
    </v-card-text>
     <v-expand-transition>
      <div v-show="show">
        <v-divider></v-divider>
        <v-card-text>
          <Comment :card="card" :groupPk="groupPk"></Comment>
        </v-card-text>
      </div>
    </v-expand-transition>
    <v-divider class="mx-4"></v-divider>
  </v-card>
</template>

<script>
import Comment from '../../components/group/Comment.vue'
import http from '../../http-common'
import { notiType, sendNotify } from "../../api/notification/index"

  export default {
    props: {
      card: Object,
      groupPk: String,
    },
    components: {
      Comment
    },
    data: () => ({
      loading: false,
      selection: 1,
      message: "미인증",
      auth: false, // 인증 피드 확인 변수
      writer: false, // 본인 여부 판단 변수
      check: false,
      show:false,
      feedType: false,
      DefaultURL: "http://ssafydoit.s3.ap-northeast-2.amazonaws.com/",
      image: "" 
    }),
    created(){
      this.image = this.card.media
      this.feedType = this.card.feedType === "true" ? true:false
      this.check= (this.card.authCheck ==='true' ? true:false) 
      // 본인 여부
      if(this.card.userPk == sessionStorage.getItem("userpk")) {
        this.writer = true
      }
      // 인증 확인 여부
      this.auth = this.card.authUsers.some((res)=>{
        if(sessionStorage.getItem("userpk") == res.userPk){
          return true
        }
      })
      // 알림
      // if (this.notiFeed && this.notiInfo.feedPk == this.card.feedPk) {
      //   const noticard = document.getElementsByName('noticard')
      //   const location = noticard.item(0).offsetTop
      //   const location = noticard.item(0)

      //   console.log('lolo',location)
      //   this.$emit('scroll', location)
      // }
    },
    watch:{
      card(){
        this.image = this.card.media
        console.log(this.image)
        this.feedType = this.card.feedType === "true" ? true:false
        this.check= (this.card.authCheck ==='true' ? true:false) 
      // 본인 여부
        if(this.card.userPk == sessionStorage.getItem("userpk")) {
          this.writer = true
        }
        // 인증 확인 여부
        this.auth = this.card.authUsers.some((res)=>{
        if(sessionStorage.getItem("userpk") == res.userPk){
          return true
        }
      })
      }
    },
    methods: {
      accept(){
        http.get(`feed/authCheckFeed?feedPk=${this.card.feedPk}`)
        .then((res)=> {
          // console.log(res)
          if(res.data.status){
            this.auth= true
          }else{
            alert("이미 인증된 피드입니다.")
          }
          if(res.data.object.authCheck){
            console.log(this.card.userPk + " " + this.card.groupPk)
            sendNotify({
              "notiType": notiType.CONFIRMFEED,
              "userPk": this.card.userPk,
              "targetId": res.data.object.feedPk
            })
          }
        })
        
      },
      feedDelete(){
      if(confirm("피드를 삭제하시겠습니까?")==true) {
        http.delete(`feed/deleteFeed?feedPk=${this.card.feedPk}`)
        .then(()=>{
          alert("피드가 삭제되었습니다.")
          this.$router.go()
          }
        )
      }         
        
      },
      feedUpdate(){
        this.$router.push({name:"FeedUpdate",params:{feedPk:Number(this.card.feedPk),"content":this.card.content, "writer":this.card.writer, "image":this.card.media}})
      }
    }
  }
</script>

<style scoped>
.title{
  border: 1px solid #F9802D;
  border-radius: 10%;
}
.stamp{
  width: 50px;
}
span{
color: #F9802D
}
.btn{
  border: 1px solid #F9802D;
  color: #F9802D
}
.green-btn:hover {  
  color:green;
  border:1px solid green;
}

.red-btn:hover {  
  color:red;
  border:1px solid red;
}
.card_content{
  white-space: pre-wrap;
}

.auth-icon {
  color:#5dc9b6;
  /* opacity: 0.2; */
  text-align: center;
  transform: rotate(-30deg);
  top:0;
  left:110px;
  z-index: 2;
}




</style>