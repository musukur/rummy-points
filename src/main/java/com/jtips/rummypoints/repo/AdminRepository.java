package com.jtips.rummypoints.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jtips.rummypoints.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {

}
