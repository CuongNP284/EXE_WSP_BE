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
@Table(name = "paragraphs")
public class Paragraph extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Thay đổi sang IDENTITY
    Long id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    String content;

    @Column(name = "image")
    String image;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    Post post;
}
