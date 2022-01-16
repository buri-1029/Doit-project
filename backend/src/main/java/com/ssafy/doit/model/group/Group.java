package com.ssafy.doit.model.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "`group`")
public class Group{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupPk;

    private String name;
    private String content;
    private String category;
    private String image;
    private int score;
    private int totalNum;
    private int maxNum;
    private Long leader;
    private LocalDate createDate;
    private String endDate;
    private String status;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "group")
    public List<GroupHashTag> tagList; // 태그리스트

    @JsonIgnore
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "group")
    public List<GroupUser> userList; // 유저리스트

    @Builder
    public Group(String name, String content, String category, String image, int maxNum,
                 Long leader, LocalDate createDate, String endDate){
        this.name = name;
        this.content = content;
        this.category = category;
        this.image = image;
        this.totalNum = 0;
        this.maxNum = maxNum;
        this.score = 0;
        this.createDate = createDate;
        this.endDate = endDate;
        this.leader = leader;
        this.status = "true";
    }
}
