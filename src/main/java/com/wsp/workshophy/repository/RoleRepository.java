package com.wsp.workshophy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wsp.workshophy.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
