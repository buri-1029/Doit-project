package com.ssafy.doit.repository.feed;

import com.ssafy.doit.model.feed.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByFeedPk(Long feedPk);

    @Modifying
    @Transactional
    @Query(value = "delete from comment where feed_pk = ?1",nativeQuery = true)
    void deleteByFeedPk(Long feedPk);
}
