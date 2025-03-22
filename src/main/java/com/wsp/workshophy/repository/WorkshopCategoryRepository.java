package com.wsp.workshophy.repository;

import com.wsp.workshophy.entity.WorkshopCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkshopCategoryRepository extends JpaRepository<WorkshopCategory, Long> {
    Optional<WorkshopCategory> findByIdAndActive(Long id, boolean active);

    List<WorkshopCategory> findAllByActive(Boolean active);

    Optional<WorkshopCategory> findByNameAndActive(String name, Boolean active);
}