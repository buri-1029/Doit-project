package com.ssafy.doit.repository.store;

import com.ssafy.doit.model.store.Product;
import com.ssafy.doit.model.response.ResponseProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<ResponseProduct> findAllByTitleContaining(String title, Pageable pageable);
    Page<ResponseProduct> findAllBy(Pageable pageable);
    Page<ResponseProduct> findAllByUserNicknameContaining(String nickname, Pageable pageable);
    Page<ResponseProduct> findAllByCategory(String Category, Pageable pageable);
    ResponseProduct getById(Long id);

}
