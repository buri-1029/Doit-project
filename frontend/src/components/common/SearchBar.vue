<template>
  <v-container class="pa-0 px-sm-16 py-sm-3"> 
    <v-row class="d-flex justify-center py-3">
      <v-col
        cols="11"
        sm="6"
      >
        <v-text-field
            v-model="word"
            solo
            label="만들고 싶은 습관을 검색해보세요!"
            clearable
            color=""
            @keyup.enter="submit"
        ></v-text-field>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
    data () {
      return {
        word: ""
      }
    },
    methods:{
      submit(){
        // console.log(this.word)
        this.$router.push({name: "GroupList",params: {word: this.word, page: 1,}})
        // this.$router.go()
      }}
  }
</script>

<style scoped>

</style>