<template>
  <div>
    <v-container class="d-flex flex-column justify-center pa-0 pa-md-3 table-wrapper">
      <v-card>
        <v-card-title>
          <span>전체 마일리지</span>
          <span class="ml-2 mileage">{{totalMileage}}</span>
          <!-- <v-text-field
            v-model="search"
            append-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
          ></v-text-field> -->
        </v-card-title>
        <v-data-table

          :headers="headers"
          :items="mileageList"
          :search="search"
          :sort-by.sync="sortBy"
          :sort-desc.sync="sortDesc"
        ></v-data-table>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import http from "../../http-common";

export default {
  name: "MileageShop",
  components: {
    // Header,
    // Footer,
  },
  data() {
    return {
      totalMileage:0,
      sortBy: 'date',
      sortDesc: true,
      search: '',
      headers: [
        {
          text: '구분',
          align: 'start',
          filterable: false,
          sortable:false,
          value: 'content',
        },
        { text: '적립날짜', value: 'date' },
        { text: '마일리지',
          sortable:false,
         value: 'mileage' },
      ],
      mileageList: [
        {
          content:'',
          date: '',
          mileage:'',
        },
      ],
    loginUser:'',
    }
  },
  created(){ 
    this.loginUser = this.$store.state.account.userpk
    this.getHistory()
    this.getMileage()
  },
  methods: {
    getHistory() {
      http.get(`/user/mileageList?userPk=${this.loginUser}`)
      .then((res)=>{
        this.mileageList = res.data.object
      })
    },
    getMileage() {
      http.get(`/user/detailUser`)
      .then((res)=>{
        this.totalMileage = res.data.object.mileage
      })
    },
  },
};
</script>

<style scoped>
.table-wrapper {
  max-width: 750px;
}

.mileage {
  color:#F9802D;
}
</style>