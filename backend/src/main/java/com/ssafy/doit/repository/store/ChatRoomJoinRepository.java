package com.ssafy.doit.repository.store;

import com.ssafy.doit.model.store.ChatRoomJoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomJoinRepository extends JpaRepository<ChatRoomJoin, Long> {
    Optional<ChatRoomJoin> findChatRoomJoinByUser_IdAndChatRoom_Product_Id(Long uid, Long pid);
    Optional<ChatRoomJoin> findChatRoomJoinByUser_IdAndChatRoom_Id(Long uid, Long rid);
}
