<template>
  <v-container >
    <div v-for="(group, idx) in paginatedData" :key="idx">
      <v-expansion-panels class="px-2 py-2 px-sm-16">
        <v-expansion-panel class="panel">
          <v-expansion-panel-header>
            <template v-slot>
              <v-row no-gutters>
                <v-col cols="2">
                  <div class="group-image">
                    <img src="" alt="">
                  </div>
                </v-col>
                <v-col
                  cols="8"
                  class="text--secondary ml-sm-0 ml-11"
                >
                  <p class="my-6">{{group.name}}</p>
                  <span v-for="(tag,idx) in group.tags" :key="idx" class="mr-2"># {{tag}}</span>
                </v-col>
              </v-row>
            </template>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <v-row no-gutters>
              <v-col class= "mx-auto" cols="10">
                <p class="my-5"> 그룹 목표 및 소개</p>
                <div class="group-intro">
                  {{group.content}}
                </div>
              </v-col>
            </v-row>
            <v-card-actions class= "pr-16">
              <v-spacer></v-spacer>
              <v-btn
                text
                color="secondary"
                @click="moveCommunity(idx)"
              >
                그룹으로 이동
              </v-btn>
              <v-btn
                text
                color="primary"
              >
                가입신청
              </v-btn>
            </v-card-actions>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </div>
     <v-pagination
        color="orange"
        v-model="page"
        :length="pageCount"
        :total-visible="7"
      ></v-pagination>
  </v-container>
 
</template>

<script>
import { searchGroup } from "@/api/group/index.js"
export default {
    props:{
      page: Number,
      word: String,
    },
    // created(){
    //   console.log(this.group)  
    // },
    data: () => ({
      date: null,
      trip: {
        name: '',
      },
      groups: [],
      pageCount:0,
    }),
    methods: {
      moveCommunity(idx){
        console.log(this.groups[idx])
        this.$router.push({name:"Community",params:{groupPk:String(this.groups[idx].groupPk)}})
      }  
    },
    created() {
      searchGroup(
        {
          "direction":"ASC",
          "page":this.page,
          "size":10,
          "tag":this.word,
        },
        (res) =>{
          console.log(res)
          if (res.status){
          console.log('getgroup',res.data.object.content[0])
          this.groups = res.data.object.content // 배열로 집어넣기
          console.log('group',this.groups)
          let listLeng = this.groups.length,
            listSize = 10;
          this.pageCount = Math.floor(listLeng / listSize);
          if (listLeng % listSize > 0) this.pageCount += 1;
          }
        },
        (err) =>{
          console.log(err)
          alert("검색 결과 가져오기 실패")
        }
      )
    },
    computed: {
      paginatedData () {
        const listSize = 10
        const start = (this.page -1) * listSize,
              end = start + listSize;
        return this.groups.slice(start, end);
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
</style>