<template>
  <v-container class="px-0 px-md-16">
    <v-row
      class="d-flex align-center flex-wrap justify-center justify-sm-start justify-md-start justify-lg-start justify-xl-start"
    > 
       <!-- <span v-if="!member" class="login">상세히 보고 싶다면 "로그인"해주세요.</span> -->
      <v-col
        xs="12"
        sm="4"
        md="4"
        v-for="(product, idx) in products"
        :key="idx"
        class="d-flex justify-center pa-4"
      >
          <v-card           
            height="100%" width="100%"
          >
            <v-img
              :src="`http://ssafydoit.s3.ap-northeast-2.amazonaws.com/` + product.image"
              class="white--text align-end"
              gradient="to bottom, rgba(0,0,0,.1), rgba(0,0,0,.5)"
              height="200px"
            >
              <!-- <v-card-title v-text="item.title"></v-card-title> -->
            </v-img>
            <v-card-actions  class="card-text">
              <v-card-text v-if="token">
                <v-btn plain router-link :to="{name: 'ProductDetail', params: {product_id: product.id}}">
                  {{product.title}}
                </v-btn>
              <span v-if="product.status==`SOLDOUT`" class="ml-5 keycolor status-style">
                판매완료
              </span>
              <span v-else-if="product.status==`WAITING`" class="ml-5 keycolor status-style">
                판매 예약중
              </span>
              <span v-else-if="product.status==`ONSALE`" class="ml-5 keycolor status-style">
                판매중
              </span>
              </v-card-text>
              <v-card-text v-else>
                <span>로그인을 하면 더 자세한 정보를 확인 하실수 있습니다.</span>
              </v-card-text>
            </v-card-actions>

          </v-card>

      </v-col>
    </v-row>
  </v-container>
  
</template>

<script>
import http from "../../http-common";

  export default {
    data: () => ({
      products:[],
      direction:'DESC',
      totalElements:0,
      totalPages:0,
      token: '',
      member: false,
    }),
    props: {
      page:Number,
      keyword:String,
      option:String,
    }, 
    created() {
      this.token = this.$store.state.account.accessToken
      this.member = sessionStorage.getItem("accessToken")
      this.getProducts()
    },
    updated() {
      http.get(`/product/${this.product_id}`)
      .then((res) => {
        this.product = res.data.object
      })
    },
    methods: {
      getProducts() {
        http.get(`/product/search?direction=${this.direction}&keyword=${this.keyword}&option=${this.option}&pg=${this.page}`)
        .then((res)=>{
          this.products = res.data.object.content
          this.empty = res.data.object.sort.empty
        })
      }
    },
    watch: {
      keyword: function () {
        this.getProducts()
      },
      page: function () {
        this.getProducts()
      }
    }
  }
</script>

<style scoped>
.product-image {
  width: 100%;
  height:100%;
}

.keycolor {
  color: #F9802D;
}

.status-style {
  font-size: 10%;
}
.login{
  color: #F9802D;
}
</style>