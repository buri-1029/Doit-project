<template>
<div>
  <v-container class="container-width">
    <v-card min-height="600">
      <v-row>
        <v-col cols="11" class="pl-10">
          <h2>나의 채팅 목록</h2>
        </v-col>
      </v-row>
      <div v-for="(chatting, idx) in chattings" :key="idx">
        <v-row class="ma-3 d-flex align-center">
          <v-col cols="3" sm="2" class="pl-6 d-flex justify-end">
            <v-avatar>
              <img
                v-if="chatting.otherUser.image !== null"
                :src="baseImg + chatting.otherUser.image" 
                alt="other-profile"
                class="other-user-img"
                style="width:100%; height:100%;"
              >
              <img
                v-else
                src="@/assets/img/profile_temp.png"
                alt="other-profile"
                class="other-user-img"
                style="width:100%; height:100%;"
              >
            </v-avatar>
          </v-col>
          <v-col cols="6" sm="2">
            {{ chatting.otherUser.nickname }}
          </v-col>
          <v-col cols="3" sm="2" class="pr-6">
            <!-- <router-link 
              :to="{name: 'ProductDetail', params: {product_id: chatting.id}}"
            > -->
              <v-avatar 
                class=""             
              >
                <img 
                  :src="baseImg + chatting.product.image" 
                  alt="product-img"
                  class="list-prd-img"
                  style="width:100%; height:100%;"
                >               
              </v-avatar>
            <!-- </router-link> -->
          </v-col>
          <v-col cols="6" sm="3" class="deal--btn">
            <v-btn
              v-if="chatting.button==`sale`"
              text
              @click="reservation(chatting.id)"
            >판매예약</v-btn>
            <!-- <div > -->
            <v-btn
              v-else-if="chatting.button==`waiting`"
              text
              class="cancel"
              @click="cancel(chatting.id)"
            >예약취소</v-btn>
            <v-row v-else-if="chatting.button==`purchase`">
              <v-col cols="6" sm="6">
                <v-btn               
                  text
                  class="confirm px-2"
                  @click="confirm(chatting.id)"
                >구매확정</v-btn>
              </v-col>
              <v-col cols="6" sm="6">
                <v-btn               
                  text
                  class="cancel"
                  @click="cancel(chatting.id)"
                >구매취소</v-btn>
              </v-col>
            </v-row>
          </v-col>          
          <v-col cols="6" sm="2" class="d-flex justify-center">
            <v-dialog
              scrollable
              persistent
              class="notice-dialog"
              max-width="600px"
              v-model="dialog"
              :retain-focus="false"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  class="enter-chatting"
                  text
                  v-bind="attrs"
                  v-on="on"
                  @click="enterRoom(chatting.id)"
                >채팅방</v-btn>
              </template>
              <template v-slot:default="dialog">
              <v-card>
                <v-card-title class="py-1 px-1">
                  <v-container>
                    <v-row>
                      <v-col cols="2" sm="2" class="mt-1 d-flex justify-center align-center">
                        <v-avatar>
                          <img 
                            :src="baseImg + productImg" 
                            alt="product-img"
                            class="prd-img"
                          >    
                        </v-avatar>
                      </v-col>
                      <v-col cols="6" sm="6" class="d-flex flex-column justify-center">
                        <v-row class="prd-name">
                          <v-col class="">
                            {{ productName }}  
                          </v-col>
                        </v-row>
                        <v-row class="prd-mileage">
                          <v-col class="pb-0">
                          {{ productPrice }} 마일리지
                          </v-col>
                        </v-row>
                      </v-col>
                      <v-col cols="4" sm="4" class="d-flex flex-column justify-center">
                        <v-row class="d-flex justify-end mb-4">
                          <v-btn
                            text
                            icon
                            large
                            class="mr-1 mt-1"
                            @click="[ dialog.value = false, close() ]"
                          ><v-icon>mdi-close</v-icon></v-btn>                          
                        </v-row>
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
                <v-card-actions class="pb-0">
                  <v-container>
                    <v-row class="">
                      <v-col cols="12" class="py-0">
                        <v-text-field
                          label="메세지를 입력하세요."
                          v-model="content" 
                          single-line
                          outlined
                          @keyup.enter="sendMessage()"
                          background-color="white"
                          dense
                          append-icon="mdi-send"
                        ></v-text-field>
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
</div>
  
</template>

<script>
import http from '../../http-common'
import { notiType, sendNotify } from '../../api/notification/index'


export default {
  name: "ChatList",
  components: {
  },
  data() {
    return {
      // product: '',
      // user : 'nickname',
      // seller: '',
      // id : 84,
      // idx:0,
      // roomCheck: false,
      baseImg : 'https://ssafydoit.s3.ap-northeast-2.amazonaws.com/',
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
    }
  },
  props : {
    chatPk: String,
    notiChat: Boolean,
  },
  created() {
    http.get('/chat/getList')
    .then(res => {
      this.chattings = res.data.object
      // console.log(res);
    })
    if(this.notiChat) {
      this.enterRoom(this.chatPk)
      this.dialog = true;
      // console.log('props',this.chatPk)
      this.notiChat = false
    }
  },
  updated() {
    http.get('/chat/getList')
    .then(res => {
      this.chattings = res.data.object
      // console.log(res);
    })
  },
  mounted() {
    
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
        this.$store.getters.getStompClient.send("/publish/chat", JSON.stringify(chatMessage),{})
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
          this.user1['userPk'] = res.data.object.currentUser.userPk
          this.user1['userNick'] = res.data.object.currentUser.nickname
          this.user2['userPk'] = res.data.object.other.userPk
          this.user2['userNick'] = res.data.object.other.nickname

          this.subscribe = this.$store.getters.getStompClient.subscribe("/subscribe/chat/room/"+ this.room.id , res => {
            this.msg.push(JSON.parse(res.body));
          });
      }) 
    },
    close() {
      this.$store.getters.getStompClient.unsubscribe(this.subscribe.id);
    },
    reservation(v) {
      if(confirm("판매 예약하시겠습니까?")==true) {
        http.get(`product/requestSell?roomPk=${v}`)
      }      
    },      
    confirm(v) {
      if(confirm("구매를 확정하시겠습니까?")==true) {
        http.get(`product/purchase?roomPk=${v}`)       
      }
      // alert('구매를 확정하시겠습니까?')
    },
    cancel(v) {
      if(confirm("판매 예약을 취소하시겠습니까?")==true) {
        http.get(`product/cancel?roomPk=${v}`)
      }        
    },
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
  /* cursor: pointer; */
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
.list-prd-img {
  width: 100%;
  height: auto;
  /* cursor: pointer; */
}
}

.list-prd-img-effect {
    -webkit-transform:scale(1);
    -moz-transform:scale(1);
    -ms-transform:scale(1); 
    -o-transform:scale(1);  
    transform:scale(1);
    -webkit-transition:.3s;
    -moz-transition:.3s;
    -ms-transition:.3s;
    -o-transition:.3s;
    transition:.05s;
}
.list-prd-img-effect:hover {
    -webkit-transform:scale(1.2);
    -moz-transform:scale(1.2);
    -ms-transform:scale(1.2);   
    -o-transform:scale(1.2);
    transform:scale(1.05);
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
  /* border-radius: 15px; */
  background-color:white;
}

.deal--btn {
  /* display: flex; */
  justify-content: end;
  padding-left: 0;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .deal--btn {
    /* display: flex; */
    justify-content: center;
    padding-left: 12px;
  }
}

.enter-chatting {
  color: #F9802D;
}

.prd-img {
  width: 90%;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .prd-img {
    width:90%;
    height: 90%;
  }
}
.prd-name {
  font-size: 90%;
}
@media only screen and (min-width: 300px) and (max-width: 599px) {
  .prd-name {
    font-size: 70%;
  }
}
.prd-mileage {
  color: grey;
  font-size: 70%;
}
@media only screen and (min-width: 300px) and (max-width: 599px) {
  .prd-mileage {
    color: grey;
    font-size: 50%;
  }
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

.confirm {
  color: green;
}

.cancel {
  color: red;
}
</style>