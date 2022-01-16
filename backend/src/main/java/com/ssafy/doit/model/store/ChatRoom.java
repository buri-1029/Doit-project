package com.ssafy.doit.model.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chatroom")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_pk")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_pk")
    private Product product;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ChatRoomJoin> chatRoomJoins;
}
