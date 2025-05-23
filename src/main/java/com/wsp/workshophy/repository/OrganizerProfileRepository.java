package com.wsp.workshophy.repository;

import com.wsp.workshophy.entity.OrganizerProfile;
import com.wsp.workshophy.entity.User;
import com.wsp.workshophy.entity.WorkshopCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizerProfileRepository extends JpaRepository<OrganizerProfile, Long> {
    Optional<OrganizerProfile> findByIdAndActive(Long id, Boolean active);

    List<OrganizerProfile> findAllByActive(Boolean active);

    Optional<OrganizerProfile> findByUserAndActive(User user, Boolean active);

    Optional<OrganizerProfile> findByUserIdAndActive(String userId, Boolean active);

    List<OrganizerProfile> findByNameContainingIgnoreCaseAndActive(String name, Boolean active);

    @Query("SELECT op FROM OrganizerProfile op JOIN op.categories c WHERE c = :category AND op.active = true AND op.id != :excludeId")
    List<OrganizerProfile> findByCategoryAndActiveTrueAndNotId(@Param("category") WorkshopCategory category, @Param("excludeId") Long excludeId);
}
