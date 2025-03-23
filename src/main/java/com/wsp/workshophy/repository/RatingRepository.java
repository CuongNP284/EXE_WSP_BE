package com.wsp.workshophy.repository;

import com.wsp.workshophy.entity.OrganizerProfile;
import com.wsp.workshophy.entity.Rating;
import com.wsp.workshophy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndOrganizerProfile(User user, OrganizerProfile organizerProfile);

    List<Rating> findByUser(User user);

    List<Rating> findByOrganizerProfile(OrganizerProfile organizerProfile);
}
