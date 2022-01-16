package com.ssafy.doit.model.group;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`group_has_tag`")
public class GroupHashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "tag_pk")
    @NonNull
    private HashTag hashTag;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "group_pk")
    @NonNull
    private Group group;

}