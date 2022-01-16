package com.ssafy.doit.repository.feed;
import com.ssafy.doit.model.feed.CommitUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CommitUserRepository extends JpaRepository<CommitUser, Long> {
    Optional<CommitUser> findByUserPkAndDate(Long userPk, LocalDate now);
    List<CommitUser> findTop30ByUserPkAndDateBetweenOrderByDate(Long userPk, LocalDate before, LocalDate today);

    @Modifying
    @Transactional
    @Query(value = "delete from commit_user where user_pk = ?1",nativeQuery = true)
    void deleteByUserPk(Long userPk);
}
