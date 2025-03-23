package com.wsp.workshophy.entity;

import com.wsp.workshophy.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "profile_ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "organizer_profile_id"})
})
public class Rating extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user; // Người đánh giá (CUSTOMER)

    @ManyToOne
    @JoinColumn(name = "organizer_profile_id", nullable = false)
    OrganizerProfile organizerProfile; // OrganizerProfile được đánh giá

    @Column(name = "rating", nullable = false)
    Double rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    String comment;
}
