<template>
  <div>
    <!-- <div>
    </div> -->
    <v-footer 
      color="white"
      class="foot d-flex flex-column justify-center py-6 black--text">
      <v-row style="width:100%" class="mb-5">
        <div class="navbar-line" role="presentation"></div>
      </v-row>
      <v-row
        justify="center"
        no-gutters
        class="mb-2"
      >
        <div class="mr-3 text-style">만든사람들 : </div>
        <span v-for="(member,idx) in members" :key="idx" class="d-flex align-center">
          <font-awesome-icon v-if="idx !== 0" class="circle mx-2" icon="circle"/>
          <span class="text-style">{{member}}</span>
        </span>
      </v-row >
      <v-row no-gutters>
        <span class="mr-3 text-style">소속 : </span>
        <span class="text-style">삼성청년소프트웨어아카데미(SSAFY) 4기 광주 1반</span>
      </v-row>
      <v-row no-gutters>
        <v-col
          class="py-4 text-center text-style"
          cols="12"
        >
          {{ new Date().getFullYear() }} — <strong>Doit</strong>
        </v-col>
      </v-row>
    </v-footer>

  </div>

</template>

<script>
  export default {
    data: () => ({
      members:['김부희', '김소정','박다솔','이승준','최예은','한지현'],
    }),
  }
</script>

<style scoped>

.foot {
  display:absolute;
  bottom:-100px;
  /* font-size: 80%; */
  /* background-color: darkorange;
  color: black; */
  /* text-color: black; */
  /* font-color: black; */
}

.text-style {
  font-size: 90%;
  color: #616161;
}

.circle{
  width: 0.25rem;
  height: 0.25rem;
}

.navbar-line {
  width: 100%;
  border-bottom: 1px solid black;
  opacity: 0.2;
}
</style>