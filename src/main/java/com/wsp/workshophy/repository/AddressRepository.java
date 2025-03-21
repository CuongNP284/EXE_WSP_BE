package com.wsp.workshophy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wsp.workshophy.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}