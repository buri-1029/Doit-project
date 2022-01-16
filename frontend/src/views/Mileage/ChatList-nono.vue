<template>
<div>
  <Header></Header>
  <!-- <NavBar></NavBar> -->
  <v-container class="container-width">
    <v-card>
      <v-row>
        <v-col cols="11" class="pl-10">
          <h2>나의 채팅 목록</h2>
        </v-col>
      </v-row>
      <div v-for="(chatting, idx) in chattings" :key="idx">
        <v-row class="ma-3 d-flex align-center">
          <v-col cols="3" sm="2" class="pl-6">
            <img 
              :src="chatting.otherUser.image" 
              alt="other-profile"
              class="other-user-img"
            >
          </v-col>
          <v-col cols="6" sm="5">
            {{ chatting.otherUser.nickname }}
          </v-col>
          <v-col cols="3" sm="2" class="pr-6">
            <img 
              :src="chatting.product.image" 
              alt="product-img"
              class="list-prd-img"
            >
          </v-col>
          <v-col cols="12" sm="3" class="d-flex justify-center">
            <v-dialog
              scrollable
              persistent
              class="notice-dialog"
              max-width="600px"
              v-model="dialog"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  class="deal-btn"
                  text
                  v-bind="attrs"
                  v-on="on"
                  @click="enterRoom(chatting.id)"
                >채팅방 들어가기</v-btn>
              </template>
              <template v-slot:default="dialog">
              <v-card>
                <v-card-title>
                  <v-container>
                    <v-row>
                      <v-col cols="3" sm="2" class="mt-1">
                        <img 
                          :src="productImg" 
                          alt="product-img"
                          class="prd-img"
                        >    
                      </v-col>
                      <v-col cols="6" sm="8" class="d-flex flex-column justify-center">
                        <v-row class="prd-name">
                          <v-col class="">
                            {{ productName }}  
                          </v-col>
                        </v-row>
                        <v-row class="prd-mileage">
                          <v-col class="">
                          {{ productPrice }} 마일리지
                          </v-col>
                        </v-row>
                      </v-col>
                      <v-col cols="3" sm="2" class="d-flex flex-column justify-center">
                        <v-btn
                          text
                          @click="[ dialog.value = false, close() ]"
                        >닫기</v-btn>                          
                      </v-col>
                    </v-row>
                  </v-container>
                </v-card-title>
                <v-divider></v-divider>
                <v-card-text class="card-style" style="padding-top: 24px;" id="app_chat_list">
                  <div v-for="(m, idx) in msg" :key="idx">
                    <!-- 메세지 보낸 사람이 현재 유저일 경우(user1일 경우) user1의 닉네임 표시 -->
                    <div v-if="m.userPk==user1['userPk']" class="d-flex flex-column align-end">
                      <v-row class="mb-3 mr-3">
                        {{user1['userNick']}}
                      </v-row>
                      <v-row class="mb-6 mr-3 my-message-box">
                        {{m.message}}
                      </v-row>
                    </div>
                    <!-- 메세지 보낸 사람이 상대방일 경우(user2일 경우) user2의 닉네임 표시 -->
                    <div v-else class="d-flex flex-column align-start">
                      <v-row class="ml-3 mb-3">
                        {{user2['userNick']}}
                      </v-row>
                      <v-row class="ml-3 mb-6 other-message-box">
                        {{m.message}}
                      </v-row>
                    </div>
                  </div>
                </v-card-text>
                <v-divider></v-divider>
                <v-card-actions>
                  <v-container>
                    <v-row class="">
                      <v-col cols="10">
                        <v-text-field
                          label="메세지를 입력하세요."
                          v-model="content" 
                          single-line
                          outlined
                          @keyup.enter="sendMessage()"
                          background-color="white"
                        ></v-text-field>                        
                      </v-col>
                      <v-col cols="2" class="d-flex justify-center">
                        <div class="send-icon">
                          <font-awesome-icon 
                            :icon="['far', 'paper-plane']"
                            @click="sendMessage()"                         
                          /> 
                        </div>                        
                      </v-col>
                    </v-row>
                  </v-container>
                </v-card-actions>
              </v-card>
              </template>
            </v-dialog>            
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-divider></v-divider>
          </v-col>
        </v-row>
      </div>
    </v-card>
  </v-container>
  <Footer></Footer>
</div>
  
</template>

<script>
import Header from "@/components/common/Header.vue";
// import NavBar from "@/components/common/NavBar.vue";
import Footer from "@/components/common/Footer.vue";
import http from '../../http-common'
import { notiType, sendNotify } from '../../api/notification/index'


export default {
  name: "ChatList",
  components: {
    Header,
    // NavBar,
    Footer,
  },
  data() {
    return {

      chattings: [], 
      productImg: '',
      productName: '',
      productPrice: '',
      msg:[],
      room: '', // 서버에서 {}타입으로 받음, id, product가 담김
      content:'',
      user1: {'userPk' : 0, 'userNick' : ''},
      user2: {'userPk' : 0, 'userNick' : ''},
      bottom_flag: true,
      subscribe: '',
      dialog:false,
      otherUser: [],
      currentUser: [],
    }
  },
  props : {
    chatPk: String,
  },
  created() {
    http.get('/chat/getList')
    .then(res => {
      this.chattings = res.data.object
      console.log('resfrom chattings', res)
    })
    this.enterRoom(this.chatPk)
    this.dialog = true;
    console.log('props',this.chatPk)
  },
  watch: {
      // app_chat_list 의 변화가 발생할때마다 수행되는 영역
    msg(){
      var objDiv = document.getElementById("app_chat_list");
      if(this.bottom_flag){
        // 채팅창 스크롤 바닥 유지
          objDiv.scrollTop = objDiv.scrollHeight;
      }
    },
  },
  methods: {
    sendMessage(){
     if(this.content.trim() !='' && this.$store.getters.getStompClient!=null) {
        let chatMessage = {
          'message': this.content,
          'roomPk' : this.room.id,
        }
        this.$store.getters.getStompClient.send("/publish/chat", JSON.stringify(chatMessage),{"accessToken": this.$store.getters.getAccessToken})
        sendNotify({
          "notiType": notiType.NEWCHAT,
          "userPk": this.user2['userPk'],
          "targetId": this.room.id
        })        
        this.content=''
    }
    },    
    enterRoom(v) {
      http.get(`/chat/room/${v}`)
      .then(res => {
          this.productImg = res.data.object.room.product.image
          this.productName = res.data.object.room.product.title
          this.productPrice = res.data.object.room.product.mileage
          this.msg = res.data.object.messages;
          this.room = res.data.object.room;
          // this.user1['userPk'] = res.data.object.currentUser.userPk
          // this.user1['userNick'] = res.data.object.currentUser.nickname
          // this.user2['userPk'] = res.data.object.other.userPk
          // this.user2['userNick'] = res.data.object.other.nickname
          this.currentUser = res.data.object.currentUser
          this.otherUser = res.data.object.other

          this.subscribe = this.$store.getters.getStompClient.subscribe("/subscribe/chat/room/"+ this.room.id , res => {
            this.msg.push(JSON.parse(res.body));
          });
      }) 
    },
    close() {
      this.$store.getters.getStompClient.unsubscribe(this.subscribe.id);
    }
  }
}
</script>

<style scoped>
.container-width {
width: 55%; 
/* margin-top: 80px; */
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
    .container-width {
      width: 98%;
    }
}

.list-prd-img {
  width: 50%;
  height: auto;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
.list-prd-img {
  width: 100%;
  height: auto;
}
}

.other-user-img {
  width: 50%;
  height: auto;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
.other-user-img {
  width: 100%;
  height: auto;
}
}

.deal-btn {
  /* margin:30px; */
  border: 2px solid orange;
  border-radius: 15px;
  background-color:white;
}

.prd-img {
  /* margin-top: 24px; */
  width: 90%;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .prd-img {
    width:100%;
  /* margin-top: 24px; */

  }
}
.prd-name {
  font-size: 90%;
}
.prd-mileage {
  color: grey;
  font-size: 70%;
}
.other-message-box {
  background-color: #EEEEEE;
  border-radius: 20px;
  max-width: 40%;
  font-size: 110%;
  padding: 10px;
  word-break: break-all;
}

.my-message-box {
  background-color: #F9802D;
  border-radius: 20px;
  max-width: 40%;
  font-size: 110%;
  padding: 10px;
  color: white;
  word-break: break-all;
}

.send-icon {
  font-size: 40px;
  cursor: pointer;
  /* color: black; */
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .send-icon {
    font-size: 30px;
    cursor: pointer;
    padding-top: 8px;
  }
}

.card-style {
  height: 900px;
}
</style>