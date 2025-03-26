package com.wsp.workshophy.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wsp.workshophy.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameAndActive(String username, Boolean active);

    Optional<User> findByIdAndActive(String id, Boolean active);

    List<User> findAllByActive(Boolean active);

    Optional<User> findByEmailAndActive(String email, Boolean active);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u JOIN u.following f WHERE u.id = :followerId AND f.id = :followingId")
    boolean existsByFollowerIdAndFollowingId(@Param("followerId") String followerId, @Param("followingId") String followingId);

    List<User> findByUsernameContainingIgnoreCaseAndActive(String username, Boolean active);

    Optional<User> findByVerificationToken(String token);
}
