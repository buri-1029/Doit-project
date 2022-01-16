import http from '../../http-common';

function createProduct(params, success, fail) {
  http
  .post(`product/createProduct`, params)
  .then(success)
  .catch(fail);
} 

function updateProduct(product_id, params, success, fail) {
  http
  .put(`product/${product_id}`,params)
  .then(success)
  .catch(fail);
} 

export { createProduct, updateProduct }