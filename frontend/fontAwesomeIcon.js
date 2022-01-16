// npm i --save-dev @fortawesome/fontawesome-svg-core 
// npm i --save-dev @fortawesome/free-solid-svg-icons
// npm i --save-dev @fortawesome/vue-fontawesome
// 해주세요
import Vue from "vue";
// 설치했던 fontawesome-svg-core와 vue-fontawesome
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
// 설치했던 아이콘파일에서 해당 아이콘만 불러옵니다.
// 1. 아이콘 등록
import { faTimesCircle, faCheckCircle, faRssSquare, faUsers, faTimes, faCrown, faCircle, faCommentDots, faBell as fBell} from "@fortawesome/free-solid-svg-icons"; // fas로 시작하는것
import { faTrashAlt, faPaperPlane, faBell, faSmile} from "@fortawesome/free-regular-svg-icons";
// 불러온 아이콘을 라이브러리에 담습니다.
library.add(faTrashAlt, faPaperPlane, faBell, faSmile);
library.add( faTimesCircle, faCheckCircle, faRssSquare, faUsers, faTimes, faCrown, faCircle, faCommentDots, fBell);
// 쓸 때는  <font-awesome-icon icon="times-circle"/>  
// fontawesome아이콘을 Vue탬플릿에 사용할 수 있게 등록해 줍니다.
Vue.component("font-awesome-icon", FontAwesomeIcon );
