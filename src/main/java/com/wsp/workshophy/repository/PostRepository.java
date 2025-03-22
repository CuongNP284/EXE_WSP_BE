package com.wsp.workshophy.repository;

import com.wsp.workshophy.constant.PostStatus;
import com.wsp.workshophy.entity.Post;
import com.wsp.workshophy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByAuthorAndActive(User author, boolean active);
    List<Post> findAllByActiveAndStatus(boolean active, PostStatus status);
    Optional<Post> findByIdAndActive(Long id, boolean active);
    List<Post> findAllByActive(boolean active);
}
