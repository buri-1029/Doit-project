import axios from "axios";
import http from '../../http-common';

const API_BASE_URL = "http://localhost:8080/group";

function createInstance(){
  const instance = axios.create({
    baseURL: API_BASE_URL,
    headers:{
      'Content-type': 'application/json',
    }
  });
  return instance
}

const instance = createInstance()

function createGroup(params,success,fail){
  instance
  .post(`createGroup`, params)
  .then(success)
  .catch(fail);
}

function searchGroup(query, success, fail) {
  http
  .get(`group/searchGroup?direction=${query.direction}&page=${query.page}&size=${query.size}&tag=${query.tag}`)
  .then(success)
  .catch(fail);
} 

function categoryGroup(query, success, fail) {
  http
  .get(`group/categoryGroup?category=${query.category}&direction=${query.direction}&page=${query.page}&size=${query.size}`)
  .then(success)
  .catch(fail);
} 


export { createGroup, searchGroup, categoryGroup }