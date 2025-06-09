package com.jang.ykk.login.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "LIKES")
public class Like {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "likes_seq_generator")
    @SequenceGenerator(name = "likes_seq_generator", sequenceName = "likes_seq", allocationSize = 1)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private CapBoard post;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Resident user;
    
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
