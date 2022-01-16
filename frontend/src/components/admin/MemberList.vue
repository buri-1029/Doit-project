<template>
  <div>
    <vue-good-table
      :columns="columns"
      max-height="500px"
      :fixed-header="true"
      @on-cell-click="onCellClick"
      :rows="rows"/>
  </div>
</template>

<script>
import http from "../../http-common"
import { VueGoodTable } from 'vue-good-table';
import 'vue-good-table/dist/vue-good-table.css'
export default {
  components: {
    VueGoodTable
  },
  data(){
    return{
    columns:[
      {
        label: '유저번호',
        field: 'id',
      },
      {
        label: '이메일',
        field: 'email',
      },
      {
        label: '닉네임',
        field: 'nickname',
      },
      {
        label: '마일리지',
        field: 'mileage',
        type: 'number'
      }],
      rows: []
      }
  },
  created(){
    http.get('admin/searchAllUser')
    .then((res)=>{
      this.rows=res.data.object
    })
  },
  methods: {
    onCellClick(v){
      var message = confirm(`${v.row.nickname}(${v.row.id}) 유저를 강퇴시키겠습니까?`) 
      if (message == true){
        http.put(`/admin/deleteUserByAdmin?userPk=${v.row.id}`)
        .then(()=> alert("해당 유저를 강퇴시켰습니다."))
      }
    }
  }
}
</script>

<style>

</style>