package com.jang.ykk.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jang.ykk.login.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
