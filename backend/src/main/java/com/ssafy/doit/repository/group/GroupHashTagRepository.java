package com.ssafy.doit.repository.group;

import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.group.GroupHashTag;
import com.ssafy.doit.model.group.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GroupHashTagRepository extends JpaRepository<GroupHashTag, Long> {
    Optional<GroupHashTag> findByGroupAndHashTag(Group group, HashTag HashTag);
    List<GroupHashTag> findAllByGroup(Group group);

    @Modifying
    @Transactional
    @Query(value = "delete from group_has_tag where group_pk = ?1",nativeQuery = true)
    void deleteByGroupPk(Long groupPk);
}
