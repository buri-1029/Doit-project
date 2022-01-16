package com.ssafy.doit.model.notification;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_pk")
    private Long id;

    private Long userPk;
    private NotiType notiType;
    private Long targetId;
    private LocalDateTime notiDate;

    @Transient
    private Object target;

    @PrePersist
    private void init(){
        this.notiDate = LocalDateTime.now();
    }
}
