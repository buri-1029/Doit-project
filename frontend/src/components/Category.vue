<template>
  <v-container class="px-3 px-sm-16"> 
    <v-row class="mx-1 mx-sm-16 mb-5">
      <h4>카테고리</h4>
    </v-row>
    <v-card class="mx-auto mx-3 mx-sm-16">
      <v-row class="py-8 px-5 d-flex justify-space-around">
        <v-col cols="4" sm="1" class="d-flex flex-column align-center category-icon-effect px-0">
          <img 
            src="@/assets/icon/study.png" 
            alt="study-icon" 
            class="category-icon"
            @click="clickCategory('공부')"
          >
          <div class="group-name mt-2">공부</div>
        </v-col>
        <v-col cols="4" sm="1" class="d-flex flex-column align-center category-icon-effect px-0">
          <img 
            src="@/assets/icon/diet.png" 
            alt="diet-icon" 
            class="category-icon"
            @click="clickCategory('다이어트')"
          >
          <div class="group-name mt-2">다이어트</div>
        </v-col>
        <v-col cols="4" sm="1" class="d-flex flex-column align-center category-icon-effect px-0">
          <img 
            src="@/assets/icon/exercise.png" 
            alt="exercise-icon" 
            class="category-icon"
            @click="clickCategory('운동')"
          >
          <div class="group-name mt-2">운동</div>
        </v-col>
        <v-col cols="3" sm="1" class="d-flex flex-column align-center category-icon-effect px-0">
          <img 
            src="@/assets/icon/book.png" 
            alt="book-icon" 
            class="category-icon"
            @click="clickCategory('독서')"
          >
          <div class="group-name mt-2">독서</div>
        </v-col>
        <v-col cols="3" sm="1" class="d-flex flex-column align-center category-icon-effect px-0">
          <img 
            src="@/assets/icon/life.png" 
            alt="life-icon" 
            class="category-icon"
            @click="clickCategory('생활습관')"
          >
          <div class="group-name mt-2">생활습관</div>
        </v-col>
        <v-col cols="3" sm="1" class="d-flex flex-column align-center category-icon-effect px-0">
          <img 
            src="@/assets/icon/hobby.png" 
            alt="hobby-icon" 
            class="category-icon"
            @click="clickCategory('취미')"
          >
          <div class="group-name mt-2">취미</div>
        </v-col>
        <v-col cols="3" sm="1" class="d-flex flex-column align-center category-icon-effect px-0">
          <img 
            src="@/assets/icon/more.png" 
            alt="etc-icon" 
            class="category-icon"
            @click="clickCategory('기타')"
          >
          <div class="group-name mt-2">기타</div>
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script>
export default {
  name: 'Category',
  data () {
    return {
      categories: {'공부': 'study', '운동': 'exercise', '다이어트': 'diet', '취미': 'hobby', '독서': 'book', '생활습관': 'life', '기타': 'etc'},
      // category: '',
    }
  },
  created() {
    // var category = document.getElementById('study');
    // console.log(category.innerText);
  },
  methods: {
    clickCategory(v) {
      // this.$router.push({name: 'CategoryList', params: {category_e: this.categories[v], category_k: v}})
      this.$router.push({name: 'CategoryList', params: {category: this.categories[v]}})
    }
  }
}
</script>

<style scoped>

.category-icon {
  list-style: none;
  display: inline-block;
  width: 50px;
  height: 50px;
  cursor: pointer;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .category-icon {
    list-style: none;
    display: inline-block;
    width: 30px;
    height: 30px;
    cursor: pointer;
  }
}

.group-name {
  text-align:center;
  font-size: medium;
}

@media only screen and (min-width: 300px) and (max-width: 599px) {
  .group-name {
    text-align:center;
    font-size: medium;
  }
}

.category-icon-effect img{
    -webkit-transform:scale(1);
    -moz-transform:scale(1);
    -ms-transform:scale(1); 
    -o-transform:scale(1);  
    transform:scale(1);
    -webkit-transition:.3s;
    -moz-transition:.3s;
    -ms-transition:.3s;
    -o-transition:.3s;
    transition:.2s;
}
.category-icon-effect:hover img {
    -webkit-transform:scale(1.2);
    -moz-transform:scale(1.2);
    -ms-transform:scale(1.2);   
    -o-transform:scale(1.2);
    transform:scale(1.2);
}
</style>