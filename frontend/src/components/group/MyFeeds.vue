<template>
  <v-card
    :loading="loading"
    class="mx-auto my-8"
    width="95%"
  >
    <template slot="progress">
      <v-progress-linear
        color="deep-purple"
        height="10"
        indeterminate
      ></v-progress-linear>
    </template>
    <!-- 모달 : 수정 삭제 -->
    <v-card-title >
      <v-spacer></v-spacer>
      <v-menu
        bottom
        left
      >
        <template v-slot:activator="{ on, attrs }">
          <v-btn
            text-dark
            icon
            v-bind="attrs"
            v-on="on"
          ><v-icon>mdi-dots-vertical</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-btn class="green--text modal-btn" @click="feedUpdate" text width="100%">피드 수정 </v-btn>
          <v-btn class="red--text modal-btn" @click="feedDelete" text width="100%">피드 삭제 </v-btn>    
        </v-list>
      </v-menu>
    </v-card-title>
    <v-img
      height="250"
      v-if="card.media"
      :src="`http://ssafydoit.s3.ap-northeast-2.amazonaws.com/` +card.media"
    ></v-img>
    <v-img v-else gradient="to bottom, rgba(0,0,0,.1), rgba(0,0,0,.5)" height="250px" ></v-img>

    <v-card-title>{{this.card.content}}</v-card-title>

    <v-card-text>
      <v-row>
       <v-col class="col-8">
          작성자:<span> {{this.card.writer}}</span>
         <div> 작성날짜: {{this.card.createDate.slice(0,10)}}</div>
        </v-col>
        <v-col  class="col-4 d-flex justify-end">
          <img v-if="check" src="@/assets/img/stamp.png" class="stamp">
        </v-col>
      </v-row>
      <!-- 본인이면 피드 수정 --> 
    </v-card-text>
    <v-divider class="mx-4"></v-divider>
  </v-card>
</template>

<script>
import http from '../../http-common'

  export default {
    props: {
      card: Object
    },
    data: () => ({
      loading: false,
      selection: 1,
      message: "미인증",
      auth: false, // 인증 피드 확인 변수
      writer: false, // 본인 여부 판단 변수
      check: false
    }),
    created(){
      this.check = this.card.authCheck
    },
    methods: {
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
        this.$router.push({name:"FeedUpdate",params:{feedPk:Number(this.card.feedPk),"content":this.card.content, "writer":this.card.writer}})
      }
    }
  }
</script>

<style scoped>
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

  .modal-btn {
    text-align: center;
  }
</style>