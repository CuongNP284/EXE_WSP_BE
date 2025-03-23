package com.wsp.workshophy.entity;

import com.wsp.workshophy.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "organizer_profiles")
public class OrganizerProfile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "theme")
    String theme;

    @Column(name = "follower_count", nullable = false)
    Integer followerCount = 0;

    @Column(name = "workshop_count")
    Integer workshopCount;

    @Column(name = "establishment_date")
    LocalDate establishmentDate;

    @ManyToMany
    @JoinTable(
            name = "organizer_profile_categories",
            joinColumns = @JoinColumn(name = "organizer_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    List<WorkshopCategory> categories;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;
}
