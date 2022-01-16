package com.ssafy.doit.model.group;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name= "`hashtag`")
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagPk;

    @Column(nullable = false)
    private String name;
    private int cnt;
}
