<template>
  <div>
    <v-container class="container-width">
      <v-row class="mb-5">
        <v-col>
          <h2>탈퇴 안내</h2>
        </v-col>
      </v-row>
      <v-row class="mb-5">
        <v-col class="keep-all">
          회원탈퇴를 신청하기 전에 안내 사항을 꼭 확인해주세요.
        </v-col>
      </v-row>
      <v-row>
        <v-col class="keep-all">
          ✔ 사용하고 계신 아이디 ({{ email }})는 탈퇴할 경우 재사용 및 복구가 불가능합니다.
        </v-col>
      </v-row>
      <v-row class="mb-7">
        <v-col class="ml-5 keep-all">
          <span class="important">탈퇴한 이메일은 본인과 타인 모두 재사용 및 복구가 불가</span>하오니 신중하게 선택하시기 바랍니다.
        </v-col>
      </v-row>
      <v-row>
        <v-col class="keep-all">
          ✔ 탈퇴 후 회원정보 및 서비스 이용기록은 모두 삭제됩니다.
        </v-col>
      </v-row>
      <v-row class="mb-7">
        <v-col class="ml-5 keep-all">
          회원정보 및 그룹 가입 이력, 잔디 정보 등 서비스 이용 기록은 모두 삭제되며, 삭제된 데이터는 복구되지 않습니다.
        </v-col>
      </v-row>
      <v-row>
        <v-col class="keep-all">
          ✔ 탈퇴 후에도 그룹 내에서 올린 인증 피드 및 게시글은 그대로 남아 있습니다.
        </v-col>
      </v-row>
      <v-row class="mb-7">
        <v-col class="ml-5 keep-all">
          그룹 내에 올린 게시글 및 댓글은 탈퇴 시 자동 삭제되지 않고 그대로 남아있습니다.
          삭제를 원하는 게시글이 있다면 반드시 탈퇴 전 삭제하시기 바랍니다.
          탈퇴 후에는 회원정보가 삭제되어 본인 여부를 확인할 수 있는 방법이 없어, 게시글을 임의로 삭제해드릴 수 없습니다.
        </v-col>
      </v-row>
      <v-row>
        <v-col class="important mb-7 keep-all">
          탈퇴 후에는 아이디 {{ email }} 으로 다시 가입할 수 없으며 아이디와 데이터는 복구할 수 없습니다. 게시판형 서비스에 남아 있는 게시글 및 댓글은 탈퇴 후 삭제할 수 없습니다.
        </v-col>
      </v-row>
      <v-row class="mb-3">
        <v-col class="d-flex justify-center">
          <v-checkbox 
            v-model="checkbox" 
            label="안내 사항을 모두 확인하였으며, 이에 동의합니다."
            ></v-checkbox>     
        </v-col>
      </v-row>
      <v-row class="mb-10">
        <v-col class="d-flex justify-center">
          <v-btn
            outlined
            class="check-btn"
            @click="deleteUser"
          >
            확인
          </v-btn>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import http from '../../http-common'
import { mapActions } from 'vuex'

export default {
  name: "Delete",
  components: {
  },
  data() {
    return {
      email: "",
      checkbox: false,
    }
  },
  computed: {
  },
  created () {
    this.email = this.$store.getters.getEmail
  },
  methods: {
    ...mapActions(['LOGOUT', 'DISCONNECT']),

    deleteUser () {
      if(!this.checkbox) {
        alert("탈퇴 안내를 확인하고 동의해 주세요.")
      }
      else {
        if(confirm("정말 Doit을 탈퇴하시겠습니까?")==true) {
          http.put('/user/deleteUser')
  
          this.LOGOUT()
          .then(() => {
            this.DISCONNECT();
            this.$router.push("/user/delete/complete")
          })
        }      
      }

    }
  }
}
</script>

<style scoped>
  .container-width {
  width: 55%; 
  margin-top: 50px;
  }

  @media only screen and (min-width: 300px) and (max-width: 599px) {
      .container-width {
        width: 90%;
      }
  }

  .check-btn {
    width: 50%;
  }

  .important {
    color: red;
  }
  
  .keep-all {
    word-break: keep-all;
  }
</style>