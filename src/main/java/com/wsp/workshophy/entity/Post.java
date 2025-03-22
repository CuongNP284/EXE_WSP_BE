package com.wsp.workshophy.entity;

import com.wsp.workshophy.base.BaseEntity;
import com.wsp.workshophy.constant.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Thay đổi sang IDENTITY
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "description")
    String description;

    @Column(name = "image")
    String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    PostStatus status;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<Paragraph> paragraphs;
}

