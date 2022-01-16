<template>
  <div class="d-flex flex-column justify-center">
    <v-row class="mx-1 mb-2 mt-16 mt-sm-0">
      <h4>Doit 그룹랭킹👑</h4>
    </v-row>
    <v-card class="px-4 mr-sm-3" v-if="groups.length >0" style="min-height:400px" >
      <v-row v-for="(group, idx) in groups" :key="idx" class="my-3" @click="moveToGroup(group)">
        <v-col cols="2" class="text-center">
          <span class="rank-num">{{group.ranking}}</span>
        </v-col>
        <v-col cols="2" class="d-flex flex-column justify-center">
          <img v-if="group.image" class="rank-image box" :src="group.image" alt="그룹이미지">
          <img v-else src="@/assets/img/group_default.png" class="rank-image box" alt="group-img">
        </v-col>
        <v-col cols="8">
          <div class="group-name">{{group.groupName}}</div>
        </v-col>
      </v-row>
    </v-card>
    <v-card class="px-4 mr-sm-3" v-else >
      <v-row class="my-3 zero-list d-flex flex-column align-center justify-center">
          <div>아직 일간 그룹 순위가 생성되지 않았어요!</div>
          <div>참여한 그룹에 인증글을 올려</div>
          <div>인증마크를 획득하고 그룹 랭킹을 높여보세요!</div>
          <div
            class="d-flex flex-column align-center justify-end auth-icon">
            <font-awesome-icon class="fa-3x " :icon="['far', 'smile']" />
            <div style="font-size:14px;">오늘인증</div>
          </div>
      </v-row>
    </v-card>
  </div>
</template>

<script>
import http from "../http-common";


export default {
  data() {
    return {
      isHover:false,
      groups:[],
      baseImg : 'https://ssafydoit.s3.ap-northeast-2.amazonaws.com/',
    }
  },
  created() {
    this.getRanking()
  },
  methods: {
    getRanking() {
      http.get(`/data/rankingGroup`)
      .then((res)=>{
        this.groups = res.data.object
        console.log(this.groups)
        this.groups.map(group => {
          if (group.image) {
            group.image = this.baseImg + group.image
          }
        })
      })
    },
    moveToGroup(group) {
      this.$router.push({ name: 'Community', params: {groupPk: group.groupPk} })
    }
  }
}
</script>

<style scoped>

.zero-list {
  min-height:400px;
  color : rgb(102, 95, 95);
}

.approved:hover {
  transform: scale(1.2);
}

.rank-num {
  font-size: x-large;
  height: 56px;
  line-height: 56px;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .rank-num {
    font-size: x-large;
    height: 56px;
    line-height: 56px;
  }
}
  
.group-name {
  font-size: medium;
  vertical-align: middle;
  height: 56px;
  line-height: 56px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .group-name {
    font-size: medium;
    height: 56px;
    line-height: 56px;   
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap; 
    cursor: pointer;
  }
}

.rank-image {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  /* z-index : 0; */
  position:relative;
  cursor: pointer;
  border: 3px solid orange;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .rank-image {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    cursor: pointer;
  }
}

.box:hover {
  position: relative;
  border: 5px solid orange;
  display: block;
  border-color: orange;
  animation-name: flash_border;
  animation-duration: 2s;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  -webkit-animation-name: flash_border;
  -webkit-animation-duration: 2s;
  -webkit-animation-timing-function: linear;
  -webkit-animation-iteration-count: infinite;
  -moz-animation-name: flash_border;
  -moz-animation-duration: 2s;
  -moz-animation-timing-function: linear;
  -moz-animation-iteration-count: infinite;
  /* animation-delay: $randomNumber + s; */
}



@keyframes flash_border {
  0% {
    border-color: #5f86f2;
  }
  14% {
    border-color:#a65ff2;
  }
  28% {
    border-color: #f25fd0;
  }
  42% {
    border-color: #f25f61;
  }
  56% {
    border-color: #f2cb5f;
  }
  70% {
    border-color:#abf25f;
  }
  84% {
    border-color:#5ff281;
  }
  100% {
    border-color:#5ff2f0;
  }
}

.spin {
  width: 100px;
  height: 100px;
  position:absolute;
  top: 0;
  left:0;
  /* z-index:10; */
  border: transparent;
}


.spin:hover {
  border-top-color: orange;
  border-right-color: orange;
  border-bottom-color: orange;
  transition-duration: 5s;
  transition: border-top-color 0.15s linear, 
              border-right-color 0.15s linear 0.1s, 
              border-bottom-color 0.15s linear 0.2s;
  border-top: 5px solid orange;
  border-left-width: 5px;
  border-right-width: 5px;
  transform: rotate(8000deg);
  transition: transform 20s linear 0s, border-left-width 3s linear 0.35s;
}

.scale:hover {
  transform : scale(1.1);
}

.auth-icon {
  color:#5dc9b6;
}

</style>