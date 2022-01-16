<template>
<div>
    <v-container class="container-width">
        <v-row>
          <v-col v-if="productImg" class="img-wrapper">
            <img class="product-img" :src="defaultAddress+productImg" alt="product image">
          </v-col>
        </v-row>
        <v-row class="my-3 px-6 d-flex align-center">
          <v-col class="d-flex align-center">
            <span class="profile-wrapper pa-0 d-flex">
              <img 
              v-if="profile" 
              class="profile-img" 
              :src="defaultAddress + profile"
              alt="글쓴이 이미지"
              >
              <img 
              v-else
              class="profile-img" 
              src="@/assets/img/user.png"
              alt="글쓴이 이미지"
              >
            </span>

            <span class="pl-4">
              {{product.nickname}}
            </span>

            <div v-if="product.status==`SOLDOUT`" class="ml-5 keycolor status-style">
              판매완료
            </div>
            <div v-else-if="product.status==`WAITING`" class="ml-5 keycolor status-style">
              판매 예약중
            </div>
            <div v-else-if="product.status==`ONSALE`" class="ml-5 keycolor status-style">
              판매중
            </div>
          </v-col>
          <v-col cols="2" sm="1" v-if="id==seller">
            <v-menu offset-y>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  text
                  v-bind="attrs"
                  v-on="on"
                  icon
                >
                  <v-icon>mdi-dots-vertical</v-icon>
                </v-btn>
              </template>
              <v-list>
                <v-list-item @click="updateProduct">
                  <v-list-item-title>수정하기</v-list-item-title>
                </v-list-item>
                <v-list-item @click="deleteProduct">
                  <v-list-item-title>삭제하기</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>               
          </v-col>
        </v-row>
        <v-row class="d-flex justify-center mt-2">
          <hr class="mb-4 line">
        </v-row>
        <v-row class="d-flex flex-column align-start mx-0 mx-sm-1">
          <v-row class="prd-title pb-0">
              {{product.title}}
          </v-row>
          <v-row class="prd-category">카테고리: {{product.category}}</v-row>
          <v-row class="prd-mileage">{{product.mileage}} 마일리지</v-row>
          <v-row class="prd-content py-5">{{product.content}}</v-row>
        </v-row>
        <v-row class="d-flex justify-center">
          <hr class="mt-4 line">
        </v-row>
        <v-row v-if="product.status!==`SOLDOUT`" class="d-flex justify-center">
          <v-col class="d-flex justify-center">
            <!-- <ChatRoom :product_id="product_id"></ChatRoom> -->
            <v-dialog
              scrollable
              persistent
              max-width="600px"
              :retain-focus="false"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  class="deal-btn"
                  text
                  v-bind="attrs"
                  v-on="on"
                  @click="chatting()"
                >채팅으로 거래하기</v-btn>
              </template>
              <template v-slot:default="dialog">
              <v-card>
                <v-card-title class="py-1 px-1">
                  <v-container>
                    <v-row>
                      <v-col cols="2" sm="2" class="mt-1 d-flex justify-center align-center">
                        <v-avatar>
                          <img 
                            :src="defaultAddress + productImg" 
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
                        <v-row class="chat-prd-mileage">
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
                        <!-- <v-row class="d-flex justify-end">
                          <v-btn
                            text
                            color="#F9802D"
                            class="mr-1"
                            @click="sell"
                          >거래버튼 전송</v-btn>
                        </v-row>                          -->
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
    </v-container>
</div>
</template>

<script>
import http from "../../http-common";
import { notiType, sendNotify } from '../../api/notification/index'

export default {
  name: "ProductDetail",
  components: {
  },
  data() {
    return {
      product: '',
      user : 'nickname',
      chattings: [], 
      seller: '',
      roomid : 0,
      id : 84,
      productImg: '',
      idx:0,
      msg:[],
      room: '',
      content:'',
      productName: '',
      productPrice: '',
      user1: {'userPk' : 0, 'userNick' : ''},
      user2: {'userPk' : 0, 'userNick' : ''},
      roomCheck: false,
      // bottom_flag: true,
      subscribe: '',
      defaultAddress: 'http://ssafydoit.s3.ap-northeast-2.amazonaws.com/',
      image: '',
      profile: ''
    }
  },
  props:{
    product_id: Number,
  },
  created() {
    this.id = this.$store.state.account.userpk;
    http.get(`/product/${this.product_id}`)
    .then((res)=>{
      this.product = res.data.object
      // console.log(this.product);
      this.seller = this.product.user_pk
      this.productImg =  this.product.image
      this.profile = this.product.profile
    })   
    // var objDiv = document.getElementById("scroll"); 
    // objDiv.scrollTop = objDiv.scrollHeight;
  },
  updated() {
    http.get(`/product/${this.product_id}`)
    .then((res) => {
      this.product = res.data.object
    })
  },
  watch: {
      // app_chat_list 의 변화가 발생할때마다 수행되는 영역
    msg(){
      var objDiv = document.getElementById("app_chat_list");
      // if(this.bottom_flag){
        // 채팅창 스크롤 바닥 유지
      objDiv.scrollTop = objDiv.scrollHeight;

      // }
    }
  },
  methods: {
    sendMessage(){
     if(this.content.trim() !='' && this.$store.getters.getStompClient != null) {
        let chatMessage = {
          'message': this.content,
          'roomPk' : this.roomid,
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
            // console.log(res)
            console.log('메시지',JSON.parse(res.body));
            this.msg.push(JSON.parse(res.body));
          });
      })       
    },
    close() {
      this.$store.getters.getStompClient.unsubscribe(this.subscribe.id);
    },
    chatting() {
      http.get('chat/getList')
      .then(res => {
        this.roomCheck = res.data.object.some((res) => {
          if(this.product.id == res.product.id) {
            this.roomid = res.id
            return true
          }
        })
      })
      // console.log(this.roomCheck)

      if(this.roomCheck == true) {
        this.enterRoom(this.roomid)
      }
      else {
        // console.log(this.seller, Number(this.id))
        if (this.seller !== Number(this.id)) {
          http.post(`chat/createRoom?product_pk=${this.product.id}`)
          .then((res) => {
            // console.log(this.product.id)
            const val = res.data.object.id
            this.enterRoom(val)
          })
        }
        else {
          this.$router.push('/chatlist')
        }
      }
    },
    deleteProduct() {
      if(confirm("등록하신 물품을 삭제하시겠습니까?")==true) {
        http.delete(`/product/${this.product_id}`)
        .then(()=>{
            alert("해당 물품이 삭제되었습니다.")
            this.$router.push('/mileageshop')
          }
        )
      }      
    },
    updateProduct() {
      this.$router.push({name:"ProductUpdate",params:{product_id:this.product_id}})
    },
    sell() {

    },
  }
};
</script>

<style scoped>
.container-width {
width: 600px; 
/* margin-top: 50px; */
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
    .container-width {
      width: 370px;
    }
}

.img-wrapper {
  width:600px;
  height:400px;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .img-wrapper {
    width:370px;
    height:270px;
  }
}

.line {
  width:570px;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .line {
    width:340px;
  }
}

.product-img {
  width: 100%;
  height:100%;
  /* max-height: 500px; */
  overflow: hidden;
  object-fit: cover;
  border-radius: 5px;
}

.profile-img {
  width: 50px;
  height: 50px;
  object-fit:cover;
  border-radius: 50%;
  border : 3px solid orange;
}

.prd-title {
  font-size: 20px;
  font-weight:bold;
  padding:12px;
  margin:6px;
  /* width: 100%; */
}

.prd-category {
  font-size: 13px;
  padding: 12px;
  /* margin-bottom: 6px; */
  margin-left: 6px;
  color: grey;
}

.prd-mileage {
  font-size : 13px;
  padding-left:12px;
  margin:6px;
  height: 100%;
  line-height: 44px;
}

.prd-content {
  font-size : 15px;
  padding-left: 12px;
  padding-right: 12px;
  margin:6px;
  white-space: pre-wrap;
}


.deal-btn {
  margin:30px;
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
    width: 100%;
  /* margin-top: 24px; */

  }
}
.prd-name {
  font-size: 90%;
}
.chat-prd-mileage {
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

.keycolor {
  color: #F9802D;
}

.status-style {
  font-size: 80%;
}
</style>