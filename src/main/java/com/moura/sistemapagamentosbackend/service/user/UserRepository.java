package com.moura.sistemapagamentosbackend.service.user;

import com.moura.sistemapagamentosbackend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByDocument(String document);

    List<User> findUserByIdIn(List<Long> userIdList);

    @Query("select count(id) from users")
    long countById();
}
