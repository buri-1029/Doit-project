<template>
  <div>
     <v-card  class="d-flex align-center flex-column my-15 mx-auto py-5 px-3 card-width">
    <h3 class="my-10">비밀번호 재설정</h3>
    <h5 class="mb-5">비밀번호를 재설정 하려는 이메일을 입력해주세요</h5>
    <div>
      <v-container class="px-0">
        <v-row no-gutters class="d-flex flex-nowrap">
          <v-col cols="10" sm="11">
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
            <v-btn text @click="checkEmail"  v-bind:class="{check : c_Email }" class="mt-4">
                <font-awesome-icon icon="check-circle"/>
            </v-btn>
        </v-row>
      </v-container>
     
      <v-card-actions class="d-flex align-center"> 
        <v-row>
          <v-col class="d-flex flex-column align-center">
            <v-btn 
              @click="sendEmail" 
              class="next my-5"
              text
            >
            다음
            </v-btn>
            <div class="col-12 text-center">
              <span><router-link to="/" class="font-style">메인페이지로 돌아가기</router-link></span>
            </div>
          <br>

          </v-col>
        </v-row>
        </v-card-actions>
    </div>
  </v-card>
  </div>
</template>

<script>
import { validationMixin } from 'vuelidate';
import { required, email } from 'vuelidate/lib/validators'
import http from '../../http-common'

export default {
  mixins: [validationMixin],
  validations: {
    email: { required, email },
    error:{
      email: false,
    },
  },
  data() {
    return {
      email: "",
      c_Email: false,
      error: {
        email: false,
      }
    }
  },
  watch: {
    email(){
      this.check()
    }
  },
  computed: {
    emailErrors () {
        const errors = []
        if (!this.$v.email.$dirty) return errors
        !this.$v.email.email && errors.push('이메일 양식을 입력해주세요.')
        !this.$v.email.required && errors.push('이메일을 입력해주세요.')
        return errors
    },
  },
  methods: {
    check() {
        // 수정시에 다시 체크
        this.c_Email = false
    },

    sendEmail(){
      if (this.email==="") {
        alert("이메일을 입력해주세요!")
      }
      else if (this.c_Email===false) {
        alert("이메일 확인 버튼을 눌러주세요!")
      }
      else {
      http.post('/user/sendChangePwEmail', {
        "email": this.email
      })
      .then((res)=> {
        if(res.data.status)
          this.$router.push(`/user/mailcheck?email=${this.email}&option=p`)
      })
    }
    }
    ,

    checkEmail(){
        http.post("/user/confirmEmail", this.email)
        .then((res)=>{
          console.log(res);
          if(res.data.status) this.c_Email = true
          else
            alert("가입하지 않은 이메일입니다.")
        })
      }
  }
}
</script>

<style scoped>
  .check {
    color: #F9802D;
  }
  .font-style {
    font-size: 80%;
  }
  .next {
    border: 2px solid #F9802D;
    color: #F9802D;
    border-radius: 8px;
  }
  .card-width {
    width: 35%;
  }
  
  @media only screen and (min-width: 300px) and (max-width: 599px) {
      .card-width {
        width: 90%;
      }
  }
</style>