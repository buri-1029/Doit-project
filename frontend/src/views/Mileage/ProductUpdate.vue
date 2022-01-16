<template>
<div>
    <v-container class="d-flex flex-column justify-center align-center pa-0" sm="10" md="6" style="max-width:600px;">
        <v-card
          elevation="2"
          outlined
          shaped
          tile
          class="content-card pt-3 pb-6"
        >
          <h3 class="my-4" style="width:100%;">물품 수정</h3>
          <v-divider style="width:100%;"></v-divider>
          <v-row class="d-flex flex-column mt-3" >
            <v-col cols="12">
              <v-text-field
                hide-details=""
                outlined
                :label="title"
                clearable
                v-model="product.title"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row class="d-flex align-center mt-3">
            <v-col cols="4">카테고리 선택</v-col>
            <v-col cols="8">
              <v-select
                hide-details=""
                :items="categories"
                label="카테고리 선택"
                outlined
                v-model="product.category"
              ></v-select>
            </v-col>
          </v-row>
          <v-row class="d-flex align-center mt-3">
            <v-col cols="4">판매 금액 설정</v-col>
            <v-col cols="8" class="d-flex align-center">
              <v-text-field
                hide-details=""
                label="판매금액"
                outlined
                v-model="product.mileage"
              ></v-text-field>
              <span class="ml-2" style="word-break: keep-all">마일리지</span>
            </v-col>
          </v-row>
          <v-row class="my-3">
            <v-col cols="12">내용 입력</v-col>
            <v-textarea
              outlined
              name="input-7-4"
              label="판매 물품에 대한 설명"
              hide-details=""
              v-model="product.content"
            ></v-textarea>
          </v-row>
          <v-row class="my-2">
            <v-col cols="12">사진 첨부</v-col>
            <v-col cols="12" class="d-flex">
              <!-- 이미지 첨부 버튼 -->
              <div>
                <input type="file" ref="imageInput" hidden  @change="onImages"  accept="image/*">
                <v-btn class="mt-4" outlined type="button" @click="onClickImageUpload">물품 이미지</v-btn>
                <input
                  ref="uploader"
                  class="d-none"
                  type="file"
                  accept="image/*"
                  @change="onFileChanged"
                >
    
              </div>
            </v-col>
            <v-col cols="4">
              <!-- 이미지 띄우기 -->
              <v-img v-if="imageUrl" :src="imageUrl" class="profile-img"></v-img>
              <v-img v-else :src="`http://ssafydoit.s3.ap-northeast-2.amazonaws.com/`+ product.image" class="profile-img"> </v-img>
            </v-col>
          </v-row>
        </v-card>

        <v-btn class="submit-btn mt-12" 
          outlined
          @click="update"
          >
          수정하기
        </v-btn>
    </v-container>

</div>
</template>

<script>
import { updateProduct } from "@/api/mileage/index.js"
import http from "../../http-common"

export default {
  name: "ProductUpdate",
  components: {
  },
  props: {
    product_id: String,
  },
  data() {
    return {
      file: {},
      image: "",
      imageUrl: null,
      title : "상품명 및 제목을 입력하세요.",
      categories : ['음식', '책', '운동', '의류', '기타', ],
      mileage: "300",
      product : {
        id: 0,
        category : '',
        content : '',
        image : '',
        title : '',
        userPk : 0,
        mileage:'',
      },
    }
  },
  created() {
    http.get(`/product/${this.product_id}`)
    .then((res) => {
      console.log(res);
      this.product.title = res.data.object.title
      this.product.category = res.data.object.category
      this.product.mileage = res.data.object.mileage
      this.product.content = res.data.object.content
      this.product.image = res.data.object.image
      // console.log(this.product.image);
    })
  },
  methods: {
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
    update() {
      const formData = new FormData()
      formData.append("file",this.file)
      
      updateProduct(
        this.product_id,
        {
          "category": this.product.category,
          "content":  this.product.content,
          "image": this.product.image,
          "title":  this.product.title,
          "mileage": this.product.mileage,
        },
        (res) =>{
          if (res.data.status){
            // console.log(res);
            alert('물품 수정 완료')
            http.post(`product/image?pid=${res.data.object.id}`, formData)  
            this.$router.push(`/mileageshop/product/${res.data.object.id}`)
          }
        },
        (err) =>{
          console.log(err)
          alert("물품 등록 실패")
        }
      )
    },
  }
};
</script>

<style scoped>

.title-wrapper {
  width:100%;
}

  .profile-img{
    min-width: 350px;
    min-height: 150px;
  }
.content-card {
  width:100%;
  padding : 30px;
}

.submit-btn {
  margin:30px;
  border: 2px solid orange;
  border-radius: 15px;
  background-color:white;

}

</style>


