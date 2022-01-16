package com.ssafy.doit.repository;

import com.ssafy.doit.model.Mileage;
import com.ssafy.doit.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MileageRepository extends JpaRepository<Mileage,Long> {
    @Query(value = "select m from Mileage m where m.content = :content and m.user = :user " +
            "and substring(m.date, 1, 10) = :now")
    Optional<Mileage> findByContentAndDateAndUser(String content, String now, User user);
    List<Mileage> findAllByUser(User user);
}
