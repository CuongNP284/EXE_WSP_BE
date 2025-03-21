package com.wsp.workshophy.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wsp.workshophy.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameAndActive(String username, Boolean active);

    Optional<User> findByIdAndActive(String id, Boolean active);

    List<User> findAllByActive(Boolean active);

    Optional<User> findByEmailAndActive(String email, Boolean active);
}
