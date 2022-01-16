<template>
  <div>
      <v-container class="pa-0 pa-md-3">
        <br>
        <v-row class="mb-3">
          <v-col cols="12" sm="6" xl="8" class="d-flex flex-column justify-center">
            <h3 class="pl-5 pl-sm-16"><span class="keycolor">{{ category_k }}</span> 카테고리</h3>
          </v-col>
          <v-col cols="12" sm="6" xl="4">
            <v-btn text  @click="clickCategory('공부')" v-bind:class="{keycolor: selected['study']}">공부</v-btn>
            <v-btn text  @click="clickCategory('다이어트')" v-bind:class="{keycolor: selected['diet']}">다이어트</v-btn>
            <v-btn text  @click="clickCategory('운동')" v-bind:class="{keycolor: selected['exercise']}">운동</v-btn>
            <v-btn text  @click="clickCategory('독서')" v-bind:class="{keycolor: selected['book']}">독서</v-btn>
            <v-btn text  @click="clickCategory('생활습관')" v-bind:class="{keycolor: selected['life']}">생활습관</v-btn>
            <v-btn text  @click="clickCategory('취미')" v-bind:class="{keycolor: selected['hobby']}">취미</v-btn>
            <v-btn text  @click="clickCategory('기타')" v-bind:class="{keycolor: selected['etc']}">기타</v-btn> 
          </v-col>
        </v-row>
        <div v-if="!empty" class="pl-1 pl-sm-16">
          총 <span class="keycolor">{{ length }}</span> 개의 그룹이 있습니다.
        </div>
        <GroupCard :page="page" :category="category"></GroupCard>
        <div v-if="empty" class="d-flex align-center flex-column">
          <h3 class="mt-16">'{{ category_k }}' 카테고리에 해당하는 그룹이 아직 없습니다.</h3>
          <br>
          <br>
          <h4>'{{ category_k }}' 관련 그룹을 만들고 싶으시다면?</h4>
          <v-btn text class="text-h6" color="#F9802D" @click="createGroup" v-if="member">
            그룹 만들기
          </v-btn>
          <h3 class="login" v-else>
            로그인
          </h3>
          
        </div>
        <!-- </div> -->
        <div v-else class="d-flex align-center flex-column">
          <v-pagination
            v-if="pageCount>=1"
            class="mt-12 mb-2"
            color="orange"
            v-model="page"
            :length="pageCount"
            :total-visible="7"
          ></v-pagination>
          <h4 class="mt-14">'{{ category_k }}' 관련 그룹을 만들고 싶으시다면?</h4>
          <v-btn text class="text-h6" color="#F9802D" @click="createGroup" v-if="member">
            그룹 만들기
          </v-btn>
          <h3 class="login" v-else>
            로그인
          </h3>
        </div>

      </v-container>
  </div>
</template>

<script>
import GroupCard from "@/components/group/GroupCard.vue";
import { categoryGroup } from "@/api/group/index.js"


export default {
  name: 'CategoryList',
  props:{
    // category_e:{ type: String, default: ""},
    // category_k:{ type: String, default: ""}
    category: {type: String, default: ""}
  },
  data() {
    return {
      member:false,
      results: {},
      page:1,
      pageCount:0,
      groups:[],
      empty: '',
      length: 0,
      category_k: '',
      categories: {'공부': 'study', '운동': 'exercise', '다이어트': 'diet', '취미': 'hobby', '독서': 'book', '생활습관': 'life', '기타': 'etc'},
      categories2: {'study':'공부', 'exercise':'운동', 'diet':'다이어트', 'hobby':'취미', 'book':'독서', 'life':'생활습관', 'etc':'기타'},
      selected: {'study': false, 'exercise':false, 'diet':false, 'hobby':false, 'book':false, 'life':false, 'etc':false}
    }
  },
  created() {
    this.member = sessionStorage.getItem("accessToken")
    categoryGroup(
      {
        "category":this.category,
        "direction":"ASC",
        "page":this.page,
        "size":9,
      },
      (res) =>{
        if (res.status){
        this.groups = res.data.object.content
        this.pageCount = parseInt(res.data.object.totalPages)
        this.empty = res.data.object.empty
        this.length = res.data.object.content.length
        this.category_k = this.categories2[this.category]
        this.selected[this.category] = true
        }
      },
      (err) =>{
        console.log(err)
        alert("검색 결과 가져오기 실패")
      }
    )
  },
  updated(){
    categoryGroup(
      {
        "category":this.category,
        "direction":"ASC",
        "page":this.page,
        "size":9,
      },
      (res) =>{
        if (res.status){
        this.groups = res.data.object.content
        this.pageCount = parseInt(res.data.object.totalPages)
        this.empty = res.data.object.empty
        this.length = res.data.object.content.length
        this.category_k = this.categories2[this.category]
        }
      },
      (err) =>{
        console.log(err)
        alert("검색 결과 가져오기 실패")
      }
    )
  },  
  components: { 
      GroupCard 
  },
  methods: {
    createGroup() {
      this.$router.push('/group/groupcreate')
    },
    clickCategory(v) {
      this.category = this.categories[v]
      this.category_k = v
      this.selected['study'] = false
      this.selected['diet'] = false
      this.selected['exercise'] = false
      this.selected['book'] = false
      this.selected['life'] = false
      this.selected['hobby'] = false
      this.selected['etc'] = false
      this.selected[this.categories[v]] = true
      this.page = 1
    }
  }
}
</script>

<style scoped>
.keycolor {
  color: #F9802D;
}
.login{
   color: #F9802D;
 }
</style>