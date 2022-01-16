package com.ssafy.doit.repository.store;

import com.ssafy.doit.model.store.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByChatRoom_Product_Id(Long pid);
    Optional<Sale> findByChatRoom_Id(Long rid);
}
