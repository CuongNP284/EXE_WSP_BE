package com.wsp.workshophy.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;

    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String email;

    String password;
    String firstName;
    LocalDate dob;
    String lastName;

    @Column(name = "avatar")
    String avatar;
    @Column(name = "gender")
    String gender;
    @Column(name = "phone_number")
    String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    Address address;

    @ManyToMany
    Set<Role> roles;

    @ManyToMany
    @JoinTable(
            name = "user_interests",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    List<WorkshopCategory> interests;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    OrganizerProfile organizerProfile;

    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    Set<User> following = new HashSet<>(); // Danh sách các user mà user hiện tại đang theo dõi

    @ManyToMany(mappedBy = "following")
    Set<User> followers = new HashSet<>(); // Danh sách các user đang theo dõi user hiện tại
}