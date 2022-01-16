<template>
<div>
  <Header></Header>
  <NavBar></NavBar>
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
              max-width="600px"
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
                          @click="dialog.value = false"
                        >닫기</v-btn>                          
                      </v-col>
                    </v-row>
                  </v-container>
                </v-card-title>
                <v-divider></v-divider>
                <v-card-text class="card-style" style="padding-top: 24px;">
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
                  <!-- <v-btn
                    color="blue darken-1"
                    text
                  >
                    Save
                  </v-btn> -->
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
import NavBar from "@/components/common/NavBar.vue";
import Footer from "@/components/common/Footer.vue";
import http from '../../http-common'
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

export default {
  name: "ChatList",
  components: {
    Header,
    NavBar,
    Footer,
  },
  data() {
    return {
      chattings: [], 
      product: '',
      user : 'nickname',
      seller: '',
      roomid : 0,
      id : 84,
      productImg: '',
      idx:0,
      msg:[],
      room: '',
      content:'',
      stompClient:null,
      productName: '',
      productPrice: '',
      user1: {'userPk' : 0, 'userNick' : ''},
      user2: {'userPk' : 0, 'userNick' : ''},
      roomCheck: false
    }
  },
  created() {
    http.get('/chat/getList')
    .then(res => {
      this.chattings = res.data.object
      console.log(this.chattings)
    })
  },
  methods: {
    sendMessage(){
     if(this.content.trim() !='' && this.stompClient!=null) {
        let chatMessage = {
          'message': this.content,
          'roomPk' : this.roomid,
        }
        this.stompClient.send("/publish/chat", JSON.stringify(chatMessage),{"accessToken": this.$store.getters.getAccessToken})
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
          this.user1['userPk'] = res.data.object.currentUser.userPk
          this.user1['userNick'] = res.data.object.currentUser.nickname
          this.user2['userPk'] = res.data.object.other.userPk
          this.user2['userNick'] = res.data.object.other.nickname
          this.connect();
      }) 
    },
    connect() {
      const serverURL = "http://localhost:8080/ws";
      let socket = new SockJS(serverURL);
      this.stompClient = Stomp.over(socket);
      console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`)

      var headers = {
        'accessToken': this.$store.getters.getAccessToken
        };
       this.stompClient.connect(
        headers,
        frame => {
          // 소켓 연결 성공
          this.connected = true;
          console.log('소켓 연결 성공', frame);
          // console.log(this.roomid)
          // 서버의 메시지 전송 endpoint를 구독합니다.
          // 이런형태를 pub sub 구조라고 합니다.
          this.stompClient.subscribe("/subscribe/chat/room/"+ this.roomid , res => {
            // console.log(res)
            console.log('메시지',JSON.parse(res.body));
            this.msg.push(JSON.parse(res.body));
          });
        },
        error => {
          // 소켓 연결 실패
          console.log('소켓 연결 실패', error);
          this.connected = false;
        }
      );
    },
  }

}


</script>

<style scoped>
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

.toolbar {
  position: fixed;
  color: black;
  width: 600px;
  /* height: 55px; */
  border-bottom: 1px solid grey;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
.toolbar {
  position: fixed;
  color: black;
  width: 330px;
  /* height: 55px; */
  border-bottom: 1px solid grey;
}
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