<template>
    <div>
        <v-container class="pa-3 pa-sm-16" >
            <v-card  class="d-flex align-center flex-column mb-12 mx-sm-auto px-5 col-sm-10" width=100% max-width=600>
              <h3 class="my-4" style="width:100%">그룹 만들기</h3>
              <v-divider style="width:100%;"></v-divider>
              <v-container class="d-flex flex-column">
                <v-row >
                  <v-col v-if="imageUrl" cols="4">
                    <!-- 이미지 뛰우기 -->
                    <v-img :src="imageUrl" class="profile-img"></v-img>
                    <!-- <v-img v-else src="@/assets/img/logo.png" class="profile-img"> </v-img> -->
                  </v-col>
                  <v-col cols="4" class="d-flex align-end">
                    <!-- 이미지 첨부 버튼 -->
                    <div class="pt-3 ">
                      <input type="file" ref="imageInput" hidden  @change="onImages"  accept="image/*">
                      <v-btn class="mt-4" outlined type="button" @click="onClickImageUpload">그룹 이미지</v-btn>
                    </div>
                  </v-col>
                </v-row>
                  <v-row class="mb-5">
                    <v-col cols="12" sm="12" md="6" class="text-left">
                      <v-text-field 
                      v-model="name"
                      hide-details=""
                      label="그룹명"></v-text-field>
                    </v-col>
                    <!-- 달력 시작-->
                    <v-col cols="12" md="6" sm="12">
                      <!-- <span class="mr-3">|마감|</span> -->
                        <v-menu
                          v-model="menu"
                          :close-on-content-click="false"
                          transition="scale-transition"
                          offset-y
                          max-width="290px"
                          min-width="auto"
                          hide-details=""
                        >
                          <template v-slot:activator="{ on, attrs }">
                            <v-text-field
                              v-model="endDate"
                              hide-details=""
                              label="만료 날짜"
                              prepend-icon="mdi-calendar"
                              v-bind="attrs"
                              autocomplete="off"
                              v-on="on"
                            ></v-text-field>
                          </template>
                          <v-date-picker
                            v-model="endDate"
                            @input="menu = false"
                          ></v-date-picker>
                        </v-menu>
                      <!-- 달력 끝 -->
                    </v-col>
                  </v-row>
                  <v-row class="mb-3">
                    <v-col cols="12" md="6" sm="12">
                      <span>최대 멤버수: {{maxNum}}</span>
                      <v-slider
                        v-model="maxNum"
                        hide-details=""
                        color="orange"
                        min="3"
                        max="16"
                        thumb-label
                      ></v-slider>
                    </v-col>
                    <v-col class="pt-1">
                      <v-select
                        hide-details=""
                        :items="items"
                        label="카테고리"
                        v-model="tmp"
                      ></v-select>
                    </v-col>
                  </v-row>
                <v-row class="mb-2">
                  <v-col cols="12">
                    <v-textarea
                      label="그룹 소개글"
                      v-model="content"
                      hide-details=""
                      auto-grow
                      outlined
                      rows="3"
                      row-height="23"
                    ></v-textarea>                            
                  </v-col>
                </v-row>
                <v-row >
                  <v-col cols="12" class="d-flex justify-start">
                    <v-text-field 
                      v-model="word"
                      hide-details=""
                      @keyup.enter="add"
                      label="해쉬태그"></v-text-field>
                    <v-btn
                      outlined
                      small
                      @click="add"
                      class ="mt-4 ml-3"
                    >추가</v-btn>
                  </v-col>
                  <v-col cols="12" class="d-flex flex-wrap mb-5 py-0">
                    <ul>
                      <li v-for="(tag,idx) in hashtag" :key="idx" ># {{ tag }}
                        <button @click="remove(idx)" class="hashtag-del-btn">                     
                          <!-- |x| -->
                          <font-awesome-icon icon="times-circle"/>
                        </button>  
                      </li> 
                    </ul>
                  </v-col>
                </v-row>
                
              </v-container>
            </v-card>
          <v-row>
            <v-col class="d-flex justify-center">
              <v-btn
                outlined
                @click="create"
                class="group-create-btn"
              >그룹 생성</v-btn>
            </v-col>
          </v-row>
        </v-container>
    </div>
</template>

<script>
import http from "../../http-common"
const date=new Date()
export default {
    name :"GroupCreate",
    components: {
    },
    data(){
      return{
      file: {},
      image: "",
      submit: false,
      imageUrl: null,
      menu: false,
      date: new Date().toISOString().substr(0,10),
      minLength : false,
      name: "",
      maxNum : 0,
      endDate: new Date().toISOString().substr(0,10),
      content :"",
      word:"",
      hashtag : [],
      tmp: "",
      category : "",
      year: date.getFullYear(),
      month: date.getMonth()+1,
      day :date.getDate(),
      items: ['운동', '공부', '다이어트', '취미', '독서', '생활습관', '기타'],
      change :{
        "운동" : "exercise",
        "공부" : "study",
        "다이어트" : "diet",
        "취미" : "hobby",
        "독서" : "book",
        "생활습관" : "life",
        "기타" : "etc"
      }
      }
    },
    watch: {
      endDate(){
        let END = new Date(this.endDate)
        const TODAY = new Date()
        let check = Math.ceil((END.getTime() - TODAY.getTime())/(1000*3600*24));
        if (check<7){
          alert('최소 일주일 이후 날짜를 선택해주세요.')
          // this.endDate = `${}`
          this.endDate =`${this.year}-${String(this.month).padStart(2, "0")}-${String(this.day).padStart(2, "0")}`
          this.submit = false
        }else if(check>=7){
          this.submit = true
        } 
      },
      content(){
        if (this.content.length>=20){
          this.minLength = true;
        }else this.minLength = false;
      },
      tmp(){
        this.category = this.change[this.tmp]
      }
    },
    methods: {
      formatDate (date){
        if(!date) return null
        const [year,month, day] = date.split('-')
        return `${year}-${month}-${day}`
      },
      parseDate (date) {
        if (!date) return null

        const [month, day, year] = date.split('-')
        return `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`
      },
      add(){
        if (this.word){
          this.hashtag.push(this.word)
          this.word=""
        }else{
          alert('단어를 입력!')
        }
      },
      remove(idx){
        this.hashtag.splice(idx,1)
      },
      // 이미지 관련 메서드 시작
      onFileChanged(e) {
        this.selectedFile = e.target.files[0]
        this.uploadImg = URL.createObjectURL(this.selectedFile)
       },
      onImages(e) {
        this.file = e.target.files[0];
        this.imageUrl = URL.createObjectURL(this.file)
      },
      onClickImageUpload() {
        this.$refs.imageInput.click();
      },
      // 이미지 관련 메서드 끝

      create(){
        const formData = new FormData()
        formData.append("file",this.file)
        if (this.minLength && this.name &&this.submit&&this.category.length>0&&this.hashtag.length>0){
          http.post('/group/createGroup',
          {
            "name": this.name,
            "maxNum": this.maxNum,
            "endDate": this.endDate,
            "content":this.content,
            "hashtags": this.hashtag,
            "category": this.category
          })
          .then((res) =>{
            console.log(res.data.object)
            alert('생성완료')
            http.post(`/group/updateImg?groupPk=${res.data.object}`,formData)
            this.$router.push({name:"Community", params:{groupPk:res.data.object}})
          })
        }else{
          if(this.name.length<1) alert('그룹명을 입력해주세요')
          else if(this.submit ===false) alert('만료 날짜를 선택해주세요')
          else if(this.category.length==0) alert('카테고리를 골라주세요')
          else if(this.hashtag.length==0) alert('해시태그를 1개 이상 추가해주세요')
          else {alert(`그룹 소개글을 20자 이상 작성해주세요 ` )}
        }
      }
  }
    
}
</script>

<style scoped>
.complete-btn {
    width: 100%;
    border: 1px solid #F9802D;
    color: #F9802D;
}

.text-size {
    font-size: 80%;
}
.calender {
  border: 1px groove;
}
.numberBox {
  width:"50%";
  border: 1px solid
}
ul{
   list-style:none;
   padding-left:0px;
}
ul li {
  float: left;
  margin-right: 15px;
}

.hashtag-del-btn {
  color:#FFE0B2;
  outline: transparent;
}

.group-create-btn {
  border: 2px solid #F9802D;
  color: #F9802D;
  border-radius: 15px;
}
.profile-img{
  width:150px;
  height:150px;
}

</style>