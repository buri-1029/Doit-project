package com.ssafy.doit.model.store;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatMessage implements Serializable {
    private String message;
    private Long roomPk;
    private Long userPk;
    private LocalDateTime localDateTime;
}
