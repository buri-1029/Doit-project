<template>
  <div>
    <v-container class="pa-8 pa-sm-16">
      <form>
        <v-col cols="12">
          <v-row justify="center">
            <v-col cols="12" sm="3">
              <div class="text-center d-flex align-center flex-column" >
                <v-img v-if="imageUrl" :src="imageUrl" class="profile-img"></v-img>
                <v-img v-else-if="image !=='undefined' " :src="`http://ssafydoit.s3.ap-northeast-2.amazonaws.com/`+ image" class="profile-img"> </v-img>
                <v-img v-else src="@/assets/img/profile_temp.png" class="profile-img"> </v-img>
                <!-- 사진변경 버튼 시작 -->
                <div class="pt-3 text-center">
                  <input type="file" ref="imageInput" hidden  @change="onImages"  accept="image/*">
                  <v-btn class="mt-4" outlined type="button" @click="onClickImageUpload">업로드</v-btn>
                  <input
                    ref="uploader"
                    class="d-none"
                    type="file"
                    accept="image/*"
                    @change="onFileChanged"
                  >
                  <!-- <v-btn class="mt-4" v-if="imageUrl" outlined @click="saveImg">저장</v-btn> -->
                </div>
                <!-- 사진변경 버튼 끝 -->
              </div>
            </v-col>

            <v-col cols="12" sm="4" class="mt-3">
              <v-row no-gutters class="d-flex flex-nowrap">
                <v-col cols="4" sm="3" class="">
                  <v-subheader>닉네임</v-subheader>
                </v-col>
                <v-col cols="8" sm="7" class="">
                  <v-text-field
                    v-model="name"
                    required
                    clearable
                    :counter="8"
                    class="mt-0 pt-1"
                  ></v-text-field>
                </v-col>
                <v-col cols="2">
                  <v-btn
                    text
                    @click="checkNick"
                    v-bind:class="{ check: c_Nick }"
                    class="mt-1 mx-0"
                  >
                    <font-awesome-icon icon="check-circle" />
                  </v-btn>
                </v-col>
              </v-row>
              <v-row no-gutters class="d-flex flex-nowrap">
                <v-col cols="4" sm="3">
                  <v-subheader>이메일</v-subheader>
                </v-col>
                <v-col cols="8" sm="7">
                  <v-text-field
                    v-model="email"
                    required
                    disabled
                    @input="$v.name.$touch()"
                    @blur="$v.name.$touch()"
                    class="mt-0 pt-1"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-row no-gutters class="d-flex flex-nowrap">
                <v-col cols="4" sm="4">
                  <v-subheader>비밀번호</v-subheader>
                </v-col>
                <v-col cols="8" sm="6">
                  <div class="pt-2 text-center pt-0 d-flex justify-start">
                    <v-btn @click="changePW" outlined text rounded class="mt-0"
                      >비밀번호 변경 ></v-btn>
                  </div>
                </v-col>
              </v-row>
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="12" md="9" lg="6" class="text-end mt-10">
              <v-btn 
                outlined 
                @click="save"
                class="save-btn-style"
              > 변경사항 저장 
              </v-btn>
            </v-col>
          </v-row>
          <v-row class="d-flex justify-center">
            <v-col cols="12" md="9" lg="6" >
              <v-row class="d-flex justify-end">
                <v-col cols="9" sm="8" class="font-size font-align">
                  Doit을 더이상 사용하지 않는다면?
                </v-col>
                <v-col 
                  cols="3" 
                  sm="2" 
                  class="font-size font-align"
                >
                <div @click="deleteUser" class="delete-user">
                  회원탈퇴
                </div>
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-col>
      </form>
    </v-container>
  </div>
</template>

<script>
import http from "../../http-common";
import defaultImg from "@/assets/img/profile_temp.png"

export default {
  name: "Update",
  components: {
  },
  data() {
    return {
      file: "",
      imageUrl: null,
      name: "",
      email: "",
      c_Nick: false,
      defaultButtonText: '사진변경',
      selectedFile: null,
      isSelecting: false,
      uploadImg: "",
      image: ""
    };
  },
  computed: {
    buttonText() {
      return this.selectedFile ? this.selectedFile.name : this.defaultButtonText
    }
  },
  watch: {
    name() {
      this.check();
    },
  },
  created() {
    this.name = this.$store.getters.getName;
    this.email = this.$store.getters.getEmail;
    this.image =  sessionStorage.getItem("userimg")
    this.uploadImg = defaultImg
    // http.get('/user/detailUser')
    // .then((res)=> {
    //     this.name = res.data.object.nickname;
    //     this.email = res.data.object.email;
    // })
  },
  methods: {
    changhPhoto() {},
    changePW() {
      this.$router.push("/user/pwchange");
    },
    save() {
      // 이미지를 담아서 저장 -> 성공하면 -> 
      const formData = new FormData()
      formData.append('file',this.file)
      http.post('user/updateImg',formData)
      .then(()=>{
        if (this.c_Nick) {
        http.put(`/user/updateInfo`, {nickname:this.name})
          .then((res) => {
            if (res.data.status) {
              alert("회원정보가 변경되었습니다");
              const name = this.name;
              this.$store.commit("SET_NAME", { name });
              this.$router.push("/user/profile");
            }
          })
      }
      else {
        alert("중복체크를 하지 않았습니다.🤦‍♂️");
      }
     })
    },
    checkNick() {
      http.post("/user/profile/checkNick", this.name).then((res) => {
        console.log(res);
        if (res.data.status) this.c_Nick = true;
        else {
          alert("중복 닉네임입니다");
        }
      });
    },
    check() {
      this.c_Nick = false;
    },
    deleteUser() {
      this.$router.push("/user/delete")
    },
    onButtonClick() {
      this.isSelecting = true
      window.addEventListener('focus', () => {
        this.isSelecting = false
      }, { once: true })

      this.$refs.uploader.click()
    },
    onFileChanged(e) {
      this.selectedFile = e.target.files[0]
      this.uploadImg = URL.createObjectURL(this.selectedFile)
    },
     onImages(e) {
        this.file = e.target.files[0];
        this.imageUrl = URL.createObjectURL(this.file)
        
      },
      onClickImageUpload() {
        this.$refs.imageInput.click();
      },
  },
};
</script>

<style scoped>
.check {
  color: #f9802d;
}
.font-size {
  font-size: 70%;
}
.font-align {
  text-align: end;
}
.delete-user {
  cursor: pointer;
}
.save-btn-style {
  width: 100%;
}
.profile-img{
  width:150px;
  height:150px;
}
</style>