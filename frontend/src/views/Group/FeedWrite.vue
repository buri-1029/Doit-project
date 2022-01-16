<template>
    <div>
        <v-container class="pa-3 pa-sm-16">
            <v-card  class="d-flex align-center flex-column mb-15 mx-sm-auto px-5 col-sm-10" width=100% max-width="600">
                <h3 class="my-4" style="width:100%">피드 작성</h3>
                <v-divider style="width:100%;"></v-divider>
                 <v-container cols="12" lg="8" xl="6" class="my-2">
                    <v-row class="text-left">
                        <v-col cols="3" sm="2" class="d-flex align-center">
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
                                {{this.$store.getters.getName}}
                            </div>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-container fluid class="py-5">
                            <v-textarea
                              label="인증 내용 입력"
                              hide-details=""
                              v-model="content"
                              auto-grow
                              outlined
                              rows="3"
                              row-height="50"
                            ></v-textarea>                            
                        </v-container>
                    </v-row>
                    <v-row class="d-flex mt-5">
                        <v-col cols="12" sm="6" class="my-auto">
                        <v-select
                            :items="items"
                            hide-details=""
                            label="피드 종류 선택"
                            dense
                            outlined
                            v-model="selected"
                        ></v-select>                            
                        </v-col>    
                        <!-- 이미지 첨부 -->
                        <v-col cols="12" sm="6" class="mb-5 d-flex justify-end" >
                            <input type="file" ref="imageInput" hidden  @change="onImages"  accept="image/*">
                            <v-btn class="mt-4" outlined type="button" @click="onClickImageUpload">인증 이미지 </v-btn>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col>
                          <v-img v-if="imageUrl" :src="imageUrl"></v-img>
                        </v-col>
                    </v-row>
                </v-container>
            </v-card>
            <v-row>
                <v-col class=" d-flex justify-center">
                    <v-btn
                    @click="write"
                    outlined
                    class="complete-btn text-size"
                    >작성 완료</v-btn>
                </v-col>
            </v-row>
        </v-container>
    </div>
</template>

<script>
import { createFeed } from "@/api/feed/index.js"
import http from "../../http-common";
import { notiType, sendNotify } from "../../api/notification/index"

export default {
    name :"FeedWrite",
    props:{
        groupPk: Number,
    },
    components: {
    },
    data() {
      return {
        imageUrl: null,
        selected : '인증',
        content :'',
        items : [
            '인증',
            '정보 공유'
        ], 
        feedPk:'',
        feedType: true,
        file: {},
        status: true,
        profile:""
      }
    },
    created(){
      // this.selected = "인증"
      this.profile = sessionStorage.getItem("userimg")
      
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
      write() {
        const formData = new FormData()
        formData.append("file",this.file)
        createFeed(
          {
            "content": this.content,
            "feedType": this.feedType,
            "groupPk": this.groupPk,
            // "status": this.status,
          },
          (res) =>{
            if (res.data.status){
              alert("피드가 생성되었습니다.")
              const feedPk = (res.data.object)
              http.post(`feed/updateImg?feedPk=${feedPk}`,formData)
              .then((res)=>{
                console.log(res);
                if(this.feedType){
                  sendNotify({
                    "notiType": notiType.NEWFEED,
                    "targetId": feedPk,
                  })
                }
                this.$router.go(-1)
              })
            }
          },
          (err) =>{
            console.log(err)
            alert("생성 실패")
          }
        )
      },
      onImages(e) {
        this.file = e.target.files[0];
        console.log(this.file)
        this.imageUrl = URL.createObjectURL(this.file)
        
      },
      onClickImageUpload() {
        console.log(this.$refs.imageInput)
        this.$refs.imageInput.click();
      },
    }
}
</script>

<style scoped>

.profile-img{
    width: 200px;
    height: 200px;
    border-radius: 70%;
    overflow: hidden;
    border: 1px solid #FFB685
}

.complete-btn {
    border: 2px solid #F9802D;
    color: #F9802D;
    border-radius: 15px;
}

.text-size {
    font-size: 80%;
}
</style>