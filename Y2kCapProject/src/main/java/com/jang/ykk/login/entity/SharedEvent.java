package com.jang.ykk.login.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "shared_event")
@SequenceGenerator(name = "shared_event_seq", sequenceName = "shared_event_seq", allocationSize = 1)
public class SharedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shared_event_seq")
    private Long id;

    private String title;
    private String description;
    private LocalDateTime eventDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String delYn;

    // 기본 생성자
    public SharedEvent() {}

    // 생성자 추가: eventDate, title, description
    public SharedEvent(LocalDateTime eventDate, String title, String description) {
        this.eventDate = eventDate;
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 생성자 추가: id, eventDate, title, description
    public SharedEvent(Long id, LocalDateTime eventDate, String title, String description) {
        this.id = id;
        this.eventDate = eventDate;
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


}