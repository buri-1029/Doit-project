package com.ssafy.doit.repository.feed;

import com.ssafy.doit.model.feed.Feed;
import com.ssafy.doit.model.feed.FeedUser;
import com.ssafy.doit.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FeedUserRepository extends JpaRepository<FeedUser, Long> {
    Optional<FeedUser> findByFeedAndUser(Feed feed, User user);
    @Modifying
    @Transactional
    @Query(value = "delete from feed_has_user where feed_pk = ?1",nativeQuery = true)
    void deleteByFeedPk(Long feedPk);
}
