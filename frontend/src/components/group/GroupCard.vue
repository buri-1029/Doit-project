<template>
  <v-container class="pa-0 px-md-16">
    <v-row
      class="d-flex align-center flex-wrap justify-start"
    > 
      <v-col
        xs="12"
        sm="4"
        md="4"
        lg="4"
        v-for="(group, idx) in groups" 
        :key="idx"
        class="d-flex justify-center px-4 py-4"
      >
          <v-card height="100%" width="100%" >
            <v-img
              :src="`http://ssafydoit.s3.ap-northeast-2.amazonaws.com/` + group.image"
              class="white--text align-end"
              gradient="to bottom, rgba(0,0,0,.1), rgba(0,0,0,.5)"
              height="200px"
            >
            <v-card-title v-text="group.name" class="py-1"></v-card-title>
            <div class="mx-2 py-1"><span style="word-break:keep-all;" v-for="(tag,idx) in group.tags" :key="idx" class="mx-1" >#{{tag}}</span></div>
            </v-img>
            <v-card-actions  class="card-text">
              <v-card-text v-if="token"  class="d-flex justify-space-around py-1">
                <v-btn plain class="btn" router-link :to="{name: 'Community', params: {groupPk:Number(group.groupPk)}}">상세보기</v-btn>
                <!-- <v-btn plain class="btn">가입신청</v-btn> -->
              </v-card-text>
              <v-card-text v-else class="d-flex justify-space-around py-1">
                <span>로그인을 하면 더 자세한 정보를 확인 하실수 있습니다.</span>
              </v-card-text>
            </v-card-actions>
          </v-card>
      </v-col>
    </v-row>
    <!-- <span v-for="(tag,idx) in group.tags" :key="idx" class="mr-2"># {{tag}}</span> -->
   
  </v-container >
</template>

<script>
import { searchGroup, categoryGroup } from "@/api/group/index.js"
export default {
    props:{
      page: Number,
      word: String,
      category: String,
    },
    data: () => ({
      date: null,
      trip: {
        name: '',
      },
      groups: [],
      pageCount:0,
      image: ""
    }),
    watch: {
      page:{
        handler: function () {
          console.log('page', this.category, this.page)
          if (this.category==null) {
            searchGroup(
              {
                "direction":"DESC",
                "page":this.page,
                "size":9,
                "tag":this.word,
                "token": ""
              },
              (res) =>{
                if (res.status){
                this.groups = res.data.object.content // 배열로 집어넣기
                }
              },
              (err) =>{
                console.log(err)
                alert("검색 결과 가져오기 실패")
              }
            )
          } else {
            categoryGroup(
              {
                "category":this.category,
                "direction":"DESC",
                "page":this.page,
                "size":9,
              },
              (res) =>{
                console.log(res)
                if (res.status){
                this.groups = res.data.object.content // 배열로 집어넣기
                }
              },
              (err) =>{
                console.log(err)
                alert("검색 결과 가져오기 실패")
              }
            )
          }
        }
      },
      word:{
        handler: function () {
          searchGroup(
            {
              "direction":"DESC",
              "page":this.page,
              "size":9,
              "tag":this.word,
            },
            (res) =>{
              console.log(res)
              if (res.status){
              this.groups = res.data.object.content // 배열로 집어넣기
              }
            },
            (err) =>{
              console.log(err)
              alert("검색 결과 가져오기 실패")
            }
          )
        }
      },
      category:{
        handler: function () {
        categoryGroup(
          {
            "category":this.category,
            "direction":"DESC",
            "page":this.page,
            "size":9,
          },
          (res) =>{
            console.log(res)
            if (res.status){
            this.groups = res.data.object.content // 배열로 집어넣기
            }
          },
          (err) =>{
            console.log(err)
            alert("검색 결과 가져오기 실패")
          }
        )
        }
      }
    },
    created() {
      this.token = this.$store.state.account.accessToken
      // console.log(this.category);
      if (this.category==undefined) {
        searchGroup(
          {
            "direction":"DESC",
            "page":this.page,
            "size":9,
            "tag":this.word,
          },
          (res) =>{
            if (res.status){
            this.groups = res.data.object.content // 배열로 집어넣기
            console.log(this.groups)
            }
          },
          (err) =>{
            console.log(err)
            alert("검색 결과 가져오기 실패")
          }
        )
      }
      else if (this.category!==undefined) {
        categoryGroup(
          {
            "category":this.category,
            "direction":"DESC",
            "page":this.page,
            "size":9,
          },
          (res) =>{
            console.log(res)
            if (res.status){
            this.groups = res.data.object.content // 배열로 집어넣기
            }
          },
          (err) =>{
            console.log(err)
            alert("검색 결과 가져오기 실패")
          }
        )
      }
    },
}
</script>

<style scoped>
.group-image {
    width: 90px;
    height: 90px;
    border-radius: 70%;
    overflow: hidden;
    background-color: #ffffbb
}
.group-intro {
  width : 100%;
  height : 250px;
  border: 1px solid orange;
}

.btn {
  border-radius: 15px;

  color: grey;
}

.btn:hover {
  color: orange;
}
</style>