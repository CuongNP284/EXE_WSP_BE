package com.wsp.workshophy.repository;

import com.wsp.workshophy.entity.OrganizerProfile;
import com.wsp.workshophy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizerProfileRepository extends JpaRepository<OrganizerProfile, Long> {
    Optional<OrganizerProfile> findByIdAndActive(Long id, Boolean active);

    List<OrganizerProfile> findAllByActive(Boolean active);

    Optional<OrganizerProfile> findByUserAndActive(User user, Boolean active);
}
