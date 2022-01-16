<template>
  <div>
    <v-container class="d-flex flex-column justify-center pa-0 pa-md-3">
      <v-row class="d-flex flex-wrap align-center px-md-16 mt-3">
        <v-col 
        cols="12" xs="12" sm="5" md="5" lg="5" xl="5"
        class="mx-6 mx-sm-0 mx-md-0 mx-lg-0 mx-xl-0 pt-0"> 
          <h1 style="display:inline-block; word-break: keep-all;">마일리지 Shop</h1>
        </v-col>
        <v-col v-if="joined" class="d-flex justify-space-between align-center">
          <v-col cols="4" sm="4" class="pt-0 px-0">
            <v-btn
              text
              x-large
              @click="chatList"
              class="px-0"
            >나의 채팅</v-btn>
          </v-col>
          <v-col 
            class="d-flex justify-center align-center pl-6 px-sm-0 px-md-0 px-lg-0 px-xl-0 pt-0" 
            cols="7" xs="6" sm="6" md="6" lg="6" xl="6"> 
            <span v-show="joined"  style="word-break: keep-all;">나의 마일리지</span>
            <v-btn v-show="joined" elevation="0" color="transparent" class="mileage mx-1 pa-0 orange--text"
            router-link :to="{name: 'MileageHistory'}">{{mileage}}</v-btn>
          </v-col>
          <v-col 
            cols="12" xs="4" sm="2" md="2" lg="2" xl="2"
            class="d-flex justify-end pr-6 px-sm-0 px-md-0 px-lg-0 px-xl-0 pt-0"> 
            <v-btn 
              outlined 
              class="write-btn"
              router-link :to="{name: 'ProductWrite'}"
              >물품 등록하기</v-btn>
          </v-col>

        </v-col>
      </v-row >
      <v-row>
        
      </v-row>
      <!--카테고리 클릭시 페이지 1로 초기화 하고 프로덕트 리스트도 다시 바꿔주기 -->
      <v-row class="mt-3 py-3 mx-3 mx-sm-16 d-flex justify-end">
        <v-btn 
          depressed
          text
          class="d-flex justify-center ma-1 category "
          v-for="(category, idx) in categories"
          :key="idx"
          value ="category"
          @click="keywordSearch(idx)"
        >
          {{category}}
        </v-btn>
      </v-row>
      <div v-if="!empty" class="pl-sm-16 my-3">
        총 <span style="color: #F9802D">{{ totalElements }}</span> 개의 검색 결과가 있습니다.
      </div>
      <v-row v-if="totalElements==0" style="min-height:40vh" class="d-flex flex-column justify-center align-ceter">
        <div style="text-align:center">검색 결과가 없어요</div>
        <div v-if="joined" style="text-align:center; color:orange;" router-link :to="{name: 'ProductWrite'}">물품 등록 하러가기</div>
      </v-row>
      <ProductPage :page="page" :keyword="keyword" :option="option"></ProductPage>

      <v-pagination
        v-if="totalPages>= 1"
        color="orange"
        v-model="page"
        :length="totalPages"
        :total-visible="7"
        class="mt-12 mb-5"
      ></v-pagination>

    </v-container>
  </div>
</template>

<script>
import ProductPage from "@/components/mileage/ProductPage.vue";
import http from "../../http-common";

export default {
  name: "MileageShop",
  components: {
    ProductPage,
  },
  data() {
    return {
      categories:[
        '전체','음식', '책', '운동', '취미', '의류', '기타', 
      ],
      page:1,
      products:[],
      direction:'DESC',
      keyword:'',
      option:'',
      mileage:0,
      joind:false,
      selectedKeyword:'전체',
      category: '',
      totalElements:0,
      totalPages:0,
      empty:true,
    }
  },
  created() {
    if(sessionStorage.accessToken=="true") {
      this.getMileage()
    }
    this.getProducts()
    this.keywordclass()
  },
  watch : {
    selectedKeyword: function (){
      this.keywordclass()
    }
  },
  methods: {
    getProducts() {
      http.get(`/product/search?direction=${this.direction}&keyword=${this.keyword}&option=${this.option}&pg=${this.page}`)
      .then((res)=>{
        this.products = res.data.object.content
        this.totalPages = res.data.object.totalPages
        this.totalElements = res.data.object.totalElements
        this.empty = res.data.object.sort.empty
      })
    },
    keywordclass () {
      const categoriesBtn = document.querySelectorAll('.category')
      categoriesBtn.forEach(category => {
        if (category.innerText === this.selectedKeyword) {
          category.classList.add('selelctedKeyword')
        } else {
          category.classList.remove('selelctedKeyword')
        }
      })
    },
    keywordSearch(idx) {
      this.selectedKeyword = this.categories[idx]
      const categoriesBtn = document.querySelectorAll('.category')
      categoriesBtn.forEach(category => {
        if (category.innerText === this.selectedKeyword) {
          category.classList.add('selelctedKeyword')
        } else {
          category.classList.remove('selelctedKeyword')
        }
      })
      if (this.selectedKeyword ==="전체") {
        this.keyword = ""
        this.option = ""
        this.getProducts()
      } else {
        this.keyword = this.selectedKeyword
        this.option = 'category'
        this.getProducts()
      }
      this.page = 1
    },
    getMileage() {
      http.get(`/user/detailUser`)
      .then((res)=>{
        this.mileage = res.data.object.mileage
      })
    },
    chatList() {
      this.$router.push('/chatlist')
    }
  },

  computed : {
    joined () {
      return this.$store.getters.isAuthenticated
    }
  },
};
</script>

<style scoped>
.write-btn {
  border : 2px solid grey;
  border-radius: 15px;
}

.write-btn:hover {
  border: 2px solid #F9802D;
  color: orange;
}

.mileage {
  color :  #F9802D;
}

.selelctedKeyword {
  color: #F9802D;
  /* background-color: rgb(248, 227, 188) !important */
}
</style>