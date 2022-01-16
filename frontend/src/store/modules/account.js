import { api } from '../../api/account/index'

const state = {
  accessToken: null,
  name: null,
  email: null,
  userpk: null,
  userimg: null,
  role:""
};

const getters = {
  isAuthenticated(state) {
    return !!state.accessToken;
  },
  getAccessToken(state) {
    return state.accessToken;
  },
  getEmail(state){
    return state.email;
  },
  getName(state){
    return state.name;
  },
  getImg(state){
    return state.userimg;
  }

};

const mutations = {
  SET_ACCESSTOKEN(state, { accessToken, email, name, userpk, userimg }) {
    state.accessToken = accessToken;
    state.email = email;
    state.name = name;
    state.userimg = userimg
    state.userpk = userpk;
  },
  SET_NAME(state, { name }){
    state.name = name;
    sessionStorage.name = name;
  },  
  LOGIN(state, { data }) {
    if (!data.status) return;
    state.accessToken = true;
    state.name = data.object.nickname;
    state.email = data.object.email;
    state.userpk = data.object.id;
    state.userimg = data.object.image
    state.role = data.object.role
    
    sessionStorage.accessToken = state.accessToken;
    sessionStorage.name = data.object.nickname;
    sessionStorage.email = data.object.email;
    sessionStorage.userpk = data.object.id;
    sessionStorage.userimg = data.object.image;
  },
  LOGOUT(state) {
    state.accessToken = null;
    state.name = null;
    state.email = null;
    state.userpk = null;
    state.userimg = null;

    sessionStorage.removeItem("accessToken");
    sessionStorage.removeItem("userimg");
    sessionStorage.removeItem("name");
    sessionStorage.removeItem("email");
    sessionStorage.removeItem("userpk");

  },
};

const actions = {
  async LOGIN({ commit }, payload) {
    const response = await api.login(payload);
    console.log(response)
    commit('LOGIN', response);
    return response;
  },
  async LOGOUT({ commit }) {
    const response = await api.logout();
    commit('LOGOUT');
    return response;
  },
};

export default {
  state,
  getters,
  mutations,
  actions,
};
