package com.ssafy.doit.repository.group;

import com.ssafy.doit.model.group.Group;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query(value = "select g from Group g where exists (select h from GroupHashTag h " +
            "where h.group = g and h.hashTag.name like %:hashtag% ) and g.status = :status")
    Page<Group> findAllByHashTagAndStatus(@Param("hashtag") String hashtag, String status, Pageable pageable);
    Page<Group> findAllByCategoryAndStatus(String category, String status, Pageable pageable);
    Page<Group> findAllByStatus(String status, Pageable pageable);
    Optional<Group> findByGroupPkAndStatus(Long groupPk, String status);

}
