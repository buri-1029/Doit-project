<template>
  <div>
    <v-row class="mx-1 mx-sm-3 mb-5 mt-0">
      <h4>Doit에서 가장 많이 나온 키워드를 확인해보세요!</h4>
    </v-row>
    <v-row class="mx-0 px-3 px-sm-0">
      <!-- <img 
        src="@/assets/img/wordcloud.png"  
        alt="word-cloud"
        class="word-cloud-img"
      > -->
       <vuewordcloud
        :data="defaultWords"
        nameKey="name"
        valueKey="value"
        :color="myColors"
        :showTooltip="false"
        font="NanumSquareRound"
        :rotate="{from: 0, to: 90, numOfOrientation: 0 }"
        :margin="{top: 0, right: 0, bottom: 0, left: 0 }"
        :wordClick="wordClickHandler"
        class="word word-cloud-img"
        >
        </vuewordcloud>
    </v-row>
  </div>
</template>

<script>
import vuewordcloud from 'vue-wordcloud';
import http from "../http-common";

export default {
    name: "WordCloud",
    data() {
      return {
        words : [],
        defaultWords :[],
        // myColors: ['#1f77b4', '#629fc9', '#94bedb', '#c9e0ef'],
        myColors: ['#FA8223', '#FD9A24', '#FBB124', '#FCCF3E'],
      }
    },
    components : {
      vuewordcloud,
    },
    mounted() {
      this.getWord()
    },
    methods: {
      getWord() {
        http.get(`/data/wordCloud`)
        .then((res)=>{
          this.words = res.data.object
          for (var word in this.words) {
            var tempword = {'name': word, 'value': (this.words[word]+1)}
            this.defaultWords.push(tempword)
          }
          // console.log(this.defaultWords)
        })
      },
      wordClickHandler(name, value) {
        console.log('wordClickHandler', name, value);
        this.$router.push({name: "GroupList",params: {word: name, page:1}})
      }
   },
}
</script>

<style>

.word-cloud-img {
  width: 100%;
  height: 100%;
}

.word:hover {
  cursor: pointer;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .word-cloud-img {
    width: 100%;
    height: 100%;
  }
}
</style>