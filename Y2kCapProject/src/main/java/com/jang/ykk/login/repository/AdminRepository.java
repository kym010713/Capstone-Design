package com.jang.ykk.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jang.ykk.login.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsernameAndPassword(String username, String password);
}
