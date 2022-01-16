package com.ssafy.doit.repository;

import com.ssafy.doit.model.notification.NotiType;
import com.ssafy.doit.model.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotiRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserPkOrderByNotiDateDesc(Long id);
    Optional<Notification> findByUserPkAndNotiTypeAndTargetId(Long uid, NotiType notiType, Long targetid);
}
