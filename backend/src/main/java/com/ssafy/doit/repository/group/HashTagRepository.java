package com.ssafy.doit.repository.group;

import com.ssafy.doit.model.group.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    Optional<HashTag> findByName(String name);
    List<HashTag> findTop50ByOrderByCntDesc();
}
