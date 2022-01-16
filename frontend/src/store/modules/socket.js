import { connect, disconnect } from '../../api/notification/index'

const state = {
  stompClient: null,
  isConnected: false,
};

const getters = {
  getStompClient(state) {
    return state.stompClient;
  },
  getConnected(state) {
    return state.isConnected;
  }
};

const mutations = {
  SET_CONNECTED(state, flag) {
    state.isConnected = flag;   
  },
  CONNECT(state, response) {
    state.stompClient = response;
  },
  DISCONNECT(state) {
    state.stompClient = null;
  },
};

const actions = {
  async CONNECT({ commit }) {
    commit('SET_CONNECTED', false);
    const response = await connect();
    commit('CONNECT', response);
    return response;
  },
  async DISCONNECT({ commit }) {
    const response = await disconnect();
    commit('DISCONNECT');
    return response;
  },
};

export default {
  state,
  getters,
  mutations,
  actions,
};
