<template>
  <div>
      <v-container class="pa-8 pa-sm-16 d-flex align-center flex-column mx-auto">
          <v-img
            src="@/assets/img/mail.png"
            alt="mail"
            class="my-3"
            max-height="60"
            max-width="60"
          />
          
          <h1 class="my-3 text-center">인증 메일이 발송되었습니다</h1>
          <div class="my-3">
            <div class="text-center">
              <subtitle-1>메일함에서(<a href='#'>{{ email }}</a>) 인증 메일을 확인 바랍니다.</subtitle-1>
            </div>
            <div class="text-center">
              <subtitle-1>이메일의 인증 링크를 선택하면 {{ text }}이 완료됩니다.</subtitle-1>
            </div>
          </div>

          <div class="pa-10 my-3 sub-box">
            <h4 class="mb-3 text-center">유의사항</h4>
            <div class="my-3">
                <subtitle-1>
                    1. 인증 메일은 발송 시점으로부터 24시간 동안만 유효하며, 재발송 시
                    기존 인증 코드는 만료됩니다. 반드시 마지막에 수신된 메일을 확인 바랍니다.
                </subtitle-1>
            </div>
            <div class="my-3">
                <subtitle-1>
                    2. 메일이 도착하지 않았다면 스팸함을 확인해 주시기 바랍니다.
                </subtitle-1>
            </div>
            <div class="pt-3 text-center">
                <v-btn 
                    @click="resend"
                    color=#F9802D
                    outlined
                    rounded
                >인증 메일 재발송</v-btn>
            </div>
          </div>
      </v-container>
  </div>
</template>

<script>
import http from '../../http-common'

export default {
    name: "MailCheck",
    components: {
    },
    data() {
        return {
            email: "",
            option : "",
            text: "",
        }
    },
    created() {
      this.email = this.$route.query.email;
      this.option = this.$route.query.option

      if(this.option == 's') 
        this.text = "회원가입";
      else this.text = "비밀번호 변경";
    },
    methods: {
      resend () {
          let url = "/user/";
          if(this.option == 'p') url += "sendChengePwEmail";
          else url += "sendSignupEmail";
          
          http.post(url, {
            "email": this.email
          })
          .then(()=>{
            alert("메일을 재전송 했습니다.");
          })
      },
    },
}
</script>

<style>
    .sub-box{
        background:#f7f7f7;
        height: auto;
        max-width: 600px;
    }
</style>