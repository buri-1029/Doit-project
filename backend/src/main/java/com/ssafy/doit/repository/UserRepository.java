package com.ssafy.doit.repository;

import com.ssafy.doit.model.user.User;
import com.ssafy.doit.model.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndAuthKey(String email, String authKey);
    Optional<User> findByNicknameAndUserRole(String nickname, UserRole us1);
    Optional<User> findByEmailAndUserRole(String email, UserRole withdraw);
    List<User> findByUserRole(UserRole user);
}
