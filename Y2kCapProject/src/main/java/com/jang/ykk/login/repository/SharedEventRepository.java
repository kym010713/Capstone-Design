package com.jang.ykk.login.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jang.ykk.login.entity.SharedEvent;

public interface SharedEventRepository extends JpaRepository<SharedEvent, Long> {
    List<SharedEvent> findByEventDate(LocalDateTime eventDate);
}