<template>
  <v-row class="d-flex jusity-center">

    <v-card
      class=" mb-12 d-flex flex-column align-center justify-center member-card mt-7"
      xs="12"
    >
        <div class="mx-6 my-4 align-self-end">
          <span class="mx-1">팀 스코어 : </span>
          <span class="team-info mr-4">{{score}}</span>
          <span class="mx-1">그룹원 수 : </span>
          <span class="team-info">{{totalNum}}</span>
        </div>
        <div style="height:auto"      
        class="pa-5 content-style"
        >
          {{content}}
        </div>
        <v-carousel :show-arrows="false"
        hide-delimiter-background
        light
        width="100%"
        height="auto">
          <v-carousel-item
            v-for="(page,i) in pageCount"
            :key="i"
          >
            <GroupMemberCarousel :page="page" :groupPk="Number(groupPk)"></GroupMemberCarousel>
          </v-carousel-item>
        </v-carousel>
    </v-card>
  </v-row>
</template>

<script>
import GroupMemberCarousel from '@/components/group/GroupMemberCarousel.vue'
import http from "../../http-common";

  export default {
    data: () => ({
      loading: false,
      users :[],
      category:'',
      content:'',
      endDate:'',
      createDate:'',
      leader:'',
      groupName:'',
      totalNum:'',
      score:'',
    }),
    components: {
      GroupMemberCarousel,
    },
    props: {
      groupPk: String,
    }, 
    computed: {
      pageCount () {
        let listLeng = this.users.length,
            listSize = 4,
            page = Math.floor(listLeng / listSize);
        if (listLeng % listSize > 0) page += 1;
        return page;
      },
    },
    watch:{
      groupPk(){
        http.get(`/group/detailGroup?groupPk=${this.groupPk}`)
        .then((res)=>{
        console.log(res.data.object)
        this.users = res.data.object.users
        this.category = res.data.object.category
        this.score = res.data.object.score
        this.content = res.data.object.content
        this.leader = res.data.object.leader
        this.groupName = res.data.object.name
        this.totalNum = res.data.object.totalNum
        this.endDate = res.data.object.endDate
        this.createDate = res.data.object.createDate
      })
      }
    },
    created() {
      http.get(`/group/detailGroup?groupPk=${this.groupPk}`)
      .then((res)=>{
        console.log(res.data.object)
        this.users = res.data.object.users
        this.category = res.data.object.category
        this.score = res.data.object.score
        this.content = res.data.object.content
        this.leader = res.data.object.leader
        this.groupName = res.data.object.name
        this.totalNum = res.data.object.totalNum
        this.endDate = res.data.object.endDate
        this.createDate = res.data.object.createDate
      })
    },
  }
</script>

<style scoped>
  @media (min-width:800px){
    .member-card {
      width: 680px;
    }
  }

  .member-card {
    border-radius: 20px;

  }

  .team-info {
    color:#F9802D;
  }

  .content-style {
    width: 80%;
    min-height: 200px;
    overflow: auto;
    border: 1px solid #E0E0E0;
    white-space: pre-wrap;
  }

  @media only screen and (min-width: 300px) and (max-width: 599px) {
    .content-style {
      width: 90%;
      min-height: 180px;
      overflow: auto;
      border: 1px solid #E0E0E0;
      white-space: pre-wrap;
    }
  }
</style>