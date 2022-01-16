package com.ssafy.doit.controller;

import com.ssafy.doit.model.store.ChatMessage;
import com.ssafy.doit.model.store.ChatRoom;
import com.ssafy.doit.model.store.ChatRoomJoin;
import com.ssafy.doit.model.response.ResponseBasic;
import com.ssafy.doit.model.response.ResponseChatRoom;
import com.ssafy.doit.service.ChatService;
import com.ssafy.doit.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final ChatService chatService;
    @Autowired
    private final RedisTemplate<String, ChatMessage> redisTemplate;

    private final String MESSAGE_KEY = "Room_idx:";

    @ApiOperation(value = "채팅방 생성")
    @PostMapping("/createRoom")
    public Object CreateRoom(@RequestParam("product_pk") Long pid){
        ResponseBasic result = null;
        try{
            Long uid = userService.currentUser();
            ChatRoom resRoom = chatService.createRoom(uid, pid);
            result = new ResponseBasic(true, "success", resRoom);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "현재 로그인한 유저의 채팅방 리스트")
    @GetMapping("/getList")
    public Object getList(){
        ResponseBasic result = null;
        try{
            Long uid = userService.currentUser();
            List<ResponseChatRoom> chatRooms = chatService.getList(uid);
            result = new ResponseBasic(true, "success", chatRooms);
        }catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "채팅방 입장")
    @GetMapping("/room/{roomPk}")
    public Object joinRoom(@PathVariable Long roomPk){
        ResponseBasic result = null;
        try{
            Long currentUser = userService.currentUser();
            Optional<ChatRoomJoin> opt = chatService.checkByRoom(currentUser, roomPk);

            if(!opt.isPresent()) throw new Exception("유저 불일치");

            ChatRoom chatRoom = chatService.getRoom(roomPk);
            List<ChatRoomJoin> chatRoomJoins = chatRoom.getChatRoomJoins();
            Long other = chatRoomJoins.get(0).getUser().getId() == currentUser ? chatRoomJoins.get(1).getUser().getId() : chatRoomJoins.get(0).getUser().getId();

            ListOperations<String, ChatMessage> listOperations = redisTemplate.opsForList();
            List<ChatMessage> messages = listOperations.range(MESSAGE_KEY + roomPk, 0, -1);

            Map<String, Object> res = new HashMap<>();
            res.put("room", chatRoom);
            res.put("messages", messages);
            res.put("currentUser", userService.detailUser(currentUser));
            res.put("other", userService.detailUser(other));
            System.out.println(messages);
            result = new ResponseBasic(true, "success", res);
        } catch (Exception e){
            e.printStackTrace();
            result = new ResponseBasic(false, e.getMessage(), null);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
