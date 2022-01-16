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
          label: '그룹번호',
          field: 'groupPk',
          type: 'number'
        },
        {
          label: '카테고리',
          field: 'category',
        },
        {
          label: '그룹명',
          field: 'name',
        },
        {
          label: '팀스코어',
          field: 'score',
          type: 'number'
        },
        {
          label: '총인원수',
          field: 'totalNum',
          type: 'number'
        },
        ],
      rows: []
    }
  },
  created(){
    http.get("/admin/searchAllGroup")
    .then((res)=>{
      this.rows = res.data.object
    })
  },
  methods: {
    onCellClick(v){
      var message = confirm(`${v.row.name}(${v.row.groupPk}) 페이지로 이동하시겠습니까?`) 
      if (message == true){
        this.$router.push({name:"Community", params:{groupPk: v.row.groupPk}})

      }
    }
  }
}
</script>

<style>

</style>