package com.ssafy.doit.repository.store;

import com.ssafy.doit.model.store.ChatRoom;
import com.ssafy.doit.model.store.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByChatRoomJoins_User_IdAndProduct_StatusNot(Long uid, ProductStatus productStatus);
}
