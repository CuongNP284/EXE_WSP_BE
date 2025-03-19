package com.wsp.workshophy.entity;

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
@Table(name = "roles")
public class Role extends BaseEntity {
    @Id
    String name;

    String description;

}
