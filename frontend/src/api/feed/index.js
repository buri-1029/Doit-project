import http from '../../http-common';

function createFeed(params, success, fail) {
  http
  .post(`feed/createFeed`, params)
  .then(success)
  .catch(fail);
} 
function updateFeed(params, success, fail) {
  http
  .put(`feed/updateFeed`,params)
  .then(success)
  .catch(fail);
} 

export { createFeed, updateFeed }