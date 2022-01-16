package com.ssafy.doit.model.store;

import com.ssafy.doit.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chatroom_join")
public class ChatRoomJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_pk")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_pk")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_pk")
    private ChatRoom chatRoom;
}
