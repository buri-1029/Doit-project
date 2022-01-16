<template>
  <div class="header-navbar">
    <header class="header pl-0">
      <div class="d-flex align-center">
        <!-- navigation drawer 시작-->
        <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
        <v-navigation-drawer
          v-model="drawer"
          absolute
          temporary
          style="height: 100vh;"
        >
          <v-list-item>
            <v-list-item-avatar>
              <v-img v-if="img!=`undefined`" :src="`http://ssafydoit.s3.ap-northeast-2.amazonaws.com/`+ img" ></v-img>
              <v-img v-else src="@/assets/img/profile_temp.png"></v-img>
            </v-list-item-avatar>

            <v-list-item-content>
              <v-list-item-title v-if="nickname">{{ nickname }}</v-list-item-title>
              <v-list-item-title v-else>로그인 해주세요</v-list-item-title>
            </v-list-item-content>
          </v-list-item>

          <v-divider></v-divider>

        <v-list v-if="nickname">
          <v-list-group
            v-for="item in items"
            :key="item.title"
            v-model="item.active"
            :prepend-icon="item.action"
            no-action
          >
            <template v-slot:activator>
              <v-list-item-content>
                <v-list-item-title v-text="item.title"></v-list-item-title>
              </v-list-item-content>
            </template>
            <!-- 가입 그룹 리스트 -->
              <v-list-item
                v-for="subItem in item.items"
                :key="subItem.groupPk"
                @click="group(subItem.groupPk)"
                class="px-4"
              >
                <v-list-item-action class="group-image">
                  <v-img
                    class="profile"
                    v-if="subItem.image"
                    :src="`http://ssafydoit.s3.ap-northeast-2.amazonaws.com/`+ subItem.image"
                  ></v-img>
                  <v-img class="profile" v-else src=""> </v-img>
                </v-list-item-action>

                  <v-list-item-content>
                    <v-list-item-title> {{ subItem.name }}</v-list-item-title>
                  </v-list-item-content>
              </v-list-item>
          </v-list-group>
        </v-list>
          <div v-else class="my-auto navi-style d-flex flex-column justify-center align-center">
            <div class="mb-3">
              로그인이 필요한 기능입니다.
            </div>
            <v-btn
              class="login"
              outlined
              @click="dialog=!dialog"
            >로그인</v-btn>
          </div>      
        </v-navigation-drawer>
        <!-- navigation drawer 끝-->  
        <img 
          src="@/assets/img/logo.png" 
          alt="Logo"
          class="logo"
          @click="logoClick"
        >
      </div>
      <div class="d-flex justify-end align-center">
        <!-- 로그인창 시작 -->
        <v-dialog
          v-model="dialog"
          persistent
          width="600"
          v-if="!this.$store.state.account.accessToken"
        >
          <template v-slot:activator="{on}">
            <v-btn
            text
            v-on="on"
            class= "px-0"
            >
            로그인
            </v-btn>
          </template>

          <v-card class="mx-auto">
            <v-container>
              <v-row>
                <v-col class="d-flex justify-end pt-2 pb-0">
                  <v-btn 
                    class=""
                    text
                    @click="close"
                  >
                    <font-awesome-icon icon="times-circle"/>
                  </v-btn>
                </v-col>
              </v-row>
            </v-container>

            <v-container class="px-5 px-sm-16 pb-8 pb-sm-16">
              <v-row>
                <v-col cols="12" class="d-flex justify-center">
                  <!-- <v-card-title> -->
                  <h3 class="">로그인</h3>
                  <!-- </v-card-title> -->
                </v-col>
              </v-row>
              <v-row class="d-flex justify-center">
                <v-col cols="11" sm="8" class="pb-0">
                  <v-text-field
                  v-model="email"
                  :error-messages="emailErrors"
                  label="E-mail"
                  required
                  clearable
                  @input="$v.email.$touch()"
                  @blur="$v.email.$touch()"
                  ></v-text-field>
                </v-col>
                <v-col cols="11" sm="8" class="pt-0">
                  <v-text-field
                    v-model="password"
                    :error-messages="passwordErrors"
                    label="비밀번호"
                    clearable
                    type="password"
                    required
                    @input="$v.password.$touch()"
                    @blur="$v.password.$touch()"
                  ></v-text-field>
                </v-col>
            </v-row>
            <v-row class="d-flex justify-center my-6">
              <v-btn
                color="#F9802D"
                class="login input" 
                text
                @click="login()"
              >로그인
              </v-btn>

            </v-row>
            <v-row class="d-flex justify-center mt-5">
              <v-col cols="8" sm="5" class="font-small-style">
                아직 회원이 아니신가요?
              </v-col>
              <v-col cols="3" sm="3" class="d-flex justify-end">
                <v-btn
                  color="#F9802D"
                  @click="signup" 
                  text
                  small
                >회원 가입</v-btn>
              </v-col>
            </v-row>
            <v-row class="d-flex justify-center">
              <v-col cols="8" sm="4" class="font-small-style">
                비밀번호를 잊으셨나요?
              </v-col>
              <v-col cols="3" sm="4" class="d-flex justify-end">
                <v-btn
                  color="#F9802D"
                  text
                  small
                  @click="passwordFind"
                >비밀번호 재설정</v-btn>
              </v-col>
            </v-row>
            </v-container>
          </v-card>
        </v-dialog>
        <!-- 로그인창 끝 -->

        <v-btn
          text
          @click="signup"
          class= "px-0"
          v-if="!this.$store.state.account.accessToken"
        >
          회원가입
        </v-btn>

        <v-btn
          text
          @click="mypage"
          class= "px-0"
          v-if="this.$store.state.account.accessToken"
        >
          마이페이지
        </v-btn>

        <v-btn
          text
          @click="logout"
          class="px-0"
          v-if="this.$store.state.account.accessToken"
        >
          로그아웃
        </v-btn>
        <!-- 여기부터 알림! -->
        <v-menu
          bottom
          offset-y
           v-if="this.$store.state.account.accessToken"
        >
          <template v-slot:activator="{ on, attrs }" >
            <v-btn
              text
              v-bind="attrs"
              v-on="on"
            >
            <v-col v-if="notiCount" >
              <v-badge :content="notiCount" color="pink" offset-x="-2" offset-y="-2"></v-badge>
              <font-awesome-icon  class="bell fa-lg" icon="bell"/>
            </v-col>
            <v-col v-else>
              <font-awesome-icon class="bell fa-lg" :icon="['far', 'bell']" />
            </v-col>



            </v-btn>
          </template>
          <v-list max-height="400">
            <v-subheader>나의 알림</v-subheader>
            <v-list-item
              v-for="(notice, i) in noti"
              :key="i"
              @click="noticeConfirm(notice)"
            >
              <v-list-item-title>{{ content[notice.notiType] }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
    </header>
    <v-container fluid class="nav-style">
      <v-row class="d-flex justify-center">
        <v-col cols="5" sm="2" class="d-flex justify-center">
          <v-btn 
            text 
            large
            @click="introduction"
          >
            Doit을 소개합니다
          </v-btn>
        </v-col>
        <v-col cols="4" sm="2" class="d-flex justify-center">
          <v-btn 
            text 
            large
            @click="goToCategory"
          >
            그룹 둘러보기
          </v-btn>
        </v-col>
        <v-col cols="3" sm="2" class="d-flex justify-center">
          <v-btn 
            text 
            large
            @click="goToShop"
          >
            Shop
          </v-btn>
        </v-col>
      </v-row>
      <v-row>
        <div class="navbar-line" role="presentation"></div>
      </v-row>
    </v-container>  
  </div>

</template>

<script>
import http from '../../http-common'
import { validationMixin } from 'vuelidate'
import { required, minLength, email } from 'vuelidate/lib/validators'
import { mapActions } from 'vuex'

export default {
    name: "Header",
    mixins: [validationMixin],
    validations: {
      email: { required, email },
      password:{ required, minLength: minLength(8)},
    },

    data: () => ({
      dialog: false,
      email: "",
      password: "",
      nickname: "",
      img: false,
      notiCount: 0,
      items: [
        {
          action: '',
          title: '가입된 그룹',
          active: true,
          items: [
            
          ],
        },
      ],
      content: {
        0: '그룹에 새 피드가 올라왔습니다.',
        1: '새로운 채팅이 있습니다.',
        2: '피드가 인증을 받았습니다.',
        3: '그룹에서 강퇴 당했습니다.',
        4: '그룹장으로 위임 됐습니다.',
        5: '피드에 새로운 댓글이 달렸습니다.',
      },
      drawer: null,
      miniVariant: true,
      noti: [], 
    }),
    computed: {
      emailErrors () {
        const errors = []
        if (!this.$v.email.$dirty) return errors
        !this.$v.email.email && errors.push('이메일 양식을 입력해주세요.')
        !this.$v.email.required && errors.push('이메일을 입력해주세요.')
        return errors
      },
      passwordErrors (){
        const errors = []
        if(!this.$v.password.$dirty) return errors
        !this.$v.password.minLength && errors.push('8자 이하입니다')
        !this.$v.password.required && errors.push('비밀번호는 필수입니다')
        return errors
      },
      getConnected(){
        return this.$store.getters.getConnected;
      }
    },
    watch: {
      getConnected(val){
        if(val == true){
          this.$store.getters.getStompClient.subscribe('/subscribe/noti/user/' + this.$store.state.account.userpk, () => {
            // notification 받기
            http.get('/noti/getList')
            .then((res) => {
                this.noti = res.data.object;
                // console.log(res)
                console.logt('watch',this.noti)
                this.notiCount = this.noti.length
            })
          })
        }
      }

    },
    created(){
      // console.log(this.$store.state.account.accessToken)
      this.nickname = this.$store.getters.getName; 
      this.img = sessionStorage.getItem("userimg")
      // 현재 로그인 한사람의 가입 그룹 리스트
      if(this.$store.getters.getAccessToken){
          http.get('/group/currentUserGroup')
            .then((res)=>{
            this.items[0].items = res.data.object;
          })
          http.get('/noti/getList')
          .then((res) => {
              this.noti = res.data.object;
              // console.log(this.noti)
              this.notiCount = this.noti.length
          })
          // 소켓 연결
          this.socketConnect();
        }
    },
    methods: {
      ...mapActions(['LOGIN', 'LOGOUT', 'CONNECT', 'DISCONNECT']),

      socketConnect() {
        this.CONNECT()
      },
      moveToChat(chatPk) {
        this.$router.push({ name: 'ChatList', params : {chatPk:String(chatPk), notiChat : true}})
      },
      moveToGroup(groupPk) {
        this.$router.push({ name: 'Community', params: { groupPk: String(groupPk) }}).catch(error => {
          if (error.name === "NavigationDuplicated") {
            this.$router.go({ name: 'Community', params: { groupPk: String(groupPk) }})
          }
        })
      },
      moveToGroupFeed(notiInfo) {
        this.$router.push({ name: 'Community', params: { groupPk: String(notiInfo.groupPk), notiFeed: true, notiInfo: notiInfo }}).catch(error => {
           if (error.name === "NavigationDuplicated") {
            this.$router.go({ name: 'Community', params: { groupPk: String(notiInfo.groupPk), notiFeed: true, notiInfo: notiInfo  }})
          }
        })
      },
      noticeConfirm(notice) {
        
        http.get(`noti/confirm?id=${notice.id}`)
        .then((res)=>{
          this.noti = res.data.object
          this.notiCount = this.noti.length
          // console.log('res', res)
        })
        // const index = this.noti.indexOf(notice)
        // this.noti.splice(index, 1)
        // this.notiCount = this.notiCount -1
        if (notice.notiType==="1") {
          let chatPk = notice.target.id
          this.moveToChat(chatPk)
        } else if (notice.notiType === "3" || notice.notiType ==="4"){
          let groupPk = notice.target.groupPk
          this.moveToGroup(groupPk) 
        } else {
          let notiInfo = notice.target
          this.moveToGroupFeed(notiInfo)
        }
      },
      signup() {
        this.$router.push("/user/join")
        this.dialog = false;  
      },
      logoClick() {
        this.$router.push("/")
      },
      mypage() {
        this.$router.push("/user/profile")
      },
      login() {
        if(this.$v.$invalid) alert("계정정보가 틀렸습니다.💥")
        else{this.LOGIN({
          "email": this.email,
          "password": this.password
        })
        .then((response)=>{
          // console.log(response);
          if(response.data.status){
            this.dialog = false;  
            this.$router.go()
          }
          else alert("가입하지 않은 아이디거나 잘못된 비밀번호 입니다.");
        })}
      },
      group(no) {
        this.$router.push(`/group/community/${no}`);
      },
      logout() {
        this.LOGOUT()
        .then(() => {
          alert("로그아웃 되었습니다😒");
          // console.log(response)
          this.DISCONNECT();
          this.$router.push('/')
          this.$router.go()
        })
      },
      close(){
        this.dialog=false
        this.email=""
        this.password=""
        this.$v.$reset();
      },
      passwordFind(){
        this.$router.push("/user/pwdfind")
        this.dialog = false;  
      },
      goToShop() {
        this.$router.push("/mileageshop")
      },
      goToCategory() {
        this.$router.push({name: 'CategoryList', params: {category: 'study'}})
      },
      introduction() {
        this.$router.push("/introduction")
      }
    }
}
</script>

<style scoped>
  .header-navbar {
    width: 100vw;
    /* position: fixed; */
    /* background-color: white; */
    /* z-index: 1; */
    /* margin-bottom: 80px; */
  }

  .nav-up {
    top: -114px;
  }

  .group-image {
    width: 50px;
    height: 50px;
    border-radius: 70%;
    overflow: hidden;
    border: 1px solid #FFB685
  }
  .profile {
    width:100%;
    height:100%;
    object-fit: cover;
  }  
  .login.input{
    height: 150%;
    border: 2px solid #F9802D;
    border-radius: 8px;
    color: #F9802D;
  }

  .login {

    height: 6%;
    border: 2px solid #F9802D;
    border-radius: 8px;
    color: #F9802D;
  }
  .btn-header {
    padding : 0
  }
  
  .navi-style {
    height: 50%;
  }

  .diaglog-width {
    width: 600px;
  }

  @media only screen and (min-width: 300px) and (max-width: 599px) {
    .diaglog-width {
      width: 200px;
    }
  }

  .font-small-style {
    font-size: 80%;
    height: 52px;
    line-height: 28px;
  }

  .drawer-height {
    height: 100vh;
  }

  .navbar-line {
    width: 100%;
    border-bottom: 1px solid black;
    opacity: 0.2;
  }

  .nav-style {
    /* position: fixed; */
    background-color: white;
    margin-bottom: 50px;
    /* z-index: 1; */
  }
  
  .profile{
    width: 100px;
    height: 100px;
  }

  .bell {
    color : #F9802D;
  }


</style>


