<template>
    <div>
        <!-- 그룹 간략 소개 시작 -->
        <v-container class="pa-3 pa-sm-16">
            <v-card  class="d-flex align-center flex-column my-15 mx-sm-auto px-5 col-sm-10" width=100% max-width="600">
                <h3 class="mt-5 mb-3">피드 수정</h3>
                <v-divider style="width:100%;"></v-divider>
                 <v-container cols="12" lg="8" xl="6" class="my-2">
                 <!-- <v-col cols="12" lg="8" xl="6" class="my-5"> -->
                    <v-row class="text-left">
                        <v-col cols="3" sm="2">
                            <v-img
                            v-if="profile"
                            id="profile"
                            :src="'http://ssafydoit.s3.ap-northeast-2.amazonaws.com/'+profile"
                            alt="profile-img"
                            class="profile-img my-auto mx-auto"
                            max-height="50"
                            max-width="50"
                            />
                            <v-img
                            v-else
                            id="profile"
                            src="@/assets/img/profile_temp.png"
                            alt="profile-img"
                            class="profile-img my-auto mx-auto"
                            max-height="50"
                            max-width="50"
                            />
                        </v-col>
                        <v-col cols="9" sm="10" class="pa-0 my-auto">
                            <div>
                               {{nick}}
                            </div>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-container fluid class="py-5">
                            <v-textarea
                                label="인증 내용 입력"
                                v-model="new_content"
                                auto-grow
                                outlined
                                rows="3"
                                row-height="23"
                            ></v-textarea>                            
                        </v-container>
                    </v-row>
                    <v-row class="d-flex mt-5">
                        <v-col cols="12" sm="6" class="my-auto">
                        <v-select
                            :items="items"
                            label="피드 종류 선택"
                            dense
                            outlined
                            v-model="selected"
                        ></v-select><!-- 기존피드값으로 기본값 설정하기-->                            
                        </v-col>                        
                        <!-- 이미지 첨부 -->
                        <v-col cols="12" sm="6" class="mb-5 d-flex align-center justify-end " >
                            <input type="file" ref="imageInput" hidden  @change="onImages"  accept="image/*">
                            <v-btn outlined type="button"
                            @click="onClickImageUpload">인증 이미지 </v-btn>
                        </v-col>
                    </v-row>
                    <v-row>
                      <!-- 이미지 띄우기 -->
                        <v-col>
                          <v-img v-if="imageUrl" :src="imageUrl"></v-img>
                          <v-img v-else :src="image"> </v-img>
                        </v-col>
                    </v-row>
                    <v-row>
                      <v-col cols="12" sm="2" class="ml-auto">
                           <v-btn
                          @click="update"
                          outlined
                          class="complete-btn text-size"
                          >수정 완료</v-btn>
                      </v-col>
                    </v-row>
                <!-- </v-col> -->
                </v-container>

            </v-card>
        </v-container>
    </div>
</template>

<script>
import { updateFeed } from "@/api/feed/index.js"
import http from "../../http-common";

export default {
    name :"FeedUpdate",
    components: {
    },
    props:{
        feedPk: Number,
        content: String,
        writer: String,
        image: String
    },
    created() {
        console.log(this.feedPk);
        this.feed= this.feedPk;
        this.nick = this.writer;
        this.new_content = this.content;
        this.image = 'http://ssafydoit.s3.ap-northeast-2.amazonaws.com/' + this.image
    },
    data() {
      return {
        imageUrl: null,
        feed: 0,
        nick: "",
        new_content: "",
        selected : '인증',
        items : [
            '인증',
            '정보 공유'
        ], 
        authCheck: false,
        profile: "",
        authDate: '',
        createDate: new Date(), // 변경X
        feedType: true,
        media: "",
        status: true,
        updateDate: new Date(), // 이게 업데이트 후 변경! 
        userPk: '',
        file: {},
        // image: '',
      }
    },
    watch: {
      selected: function() {
        if (this.selected == "인증") {
            this.feedType = true
        } else {
            this.feedType = false
        }
      }
    },
    methods: {
      onImages(e) {
      this.file = e.target.files[0];
      console.log(this.file)
      this.imageUrl = URL.createObjectURL(this.file)
      
      },
      onClickImageUpload() {
      console.log(this.$refs.imageInput)
      this.$refs.imageInput.click();
      },
      update() {
        const formData = new FormData()
        formData.append("file",this.file)
        updateFeed( //if 백엔드 URL 완성되면 그 이름에 맞춰서 변경예정
          {
            "content": this.new_content,
            "feedPk": this.feed,
            "feedType": this.feedType,
          },
          (res) =>{
            if (res.status){
              alert("피드가 수정되었습니다.")
              http.post(`feed/updateImg?feedPk=${this.feed}`,formData)
              .then((res)=>{
                console.log(res);
                this.$router.go(-1)
              })
            }
          },
          (err) =>{
            console.log(err)
            alert("수정 실패")
          }
        )
      },
    }
}
</script>

<style scoped>
.complete-btn {
    width: 100%;
    border: 1px solid #F9802D;
    color: #F9802D;
}

.text-size {
    font-size: 80%;
}
.profile-img{
    width: 200px;
    height: 200px;
    border-radius: 70%;
    overflow: hidden;
    border: 1px solid #FFB685
}
</style>