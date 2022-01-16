package com.ssafy.doit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssafy.doit.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mileage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mileage_pk")
    private Long id;

    private String content;
    private String mileage;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_pk")
    @JsonIgnore
    private User user;
}
