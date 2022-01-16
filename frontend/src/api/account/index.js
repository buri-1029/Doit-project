import http from '../../http-common';
import store from '../../store/index';

const response = {
  UNAUTHORIZED : 401,
  FOBBIDEN : 403
}

const api = {
  login(payload) {
    return http.post('/user/login', payload);
  },

  logout() {
    return http.get('/user/logout')
  },

  onUnauthorized() {
    store.commit("LOGOUT");
    console.log
    alert("로그인 만료");
    
  },

  onFobbiden() {
    if (store.getters.getAccessToken)
      alert("권한이 없음");
    else alert("로그인 필요");
  }, 
};

export { api, response }
