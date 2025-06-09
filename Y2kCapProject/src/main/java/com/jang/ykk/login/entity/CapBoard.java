package com.jang.ykk.login.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "CAPBOARD")
public class CapBoard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "CLOB")
    private String content;
    
    @Column(name = "AUTHOR", nullable = false, length = 100)
    private String author;

    @Column(name = "CREATED_AT", columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(name = "UPDATED_AT", columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDate updatedAt;

    @Column(name = "DEL_YN", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String delYn = "N";

    @Column(name = "LIKES", nullable = true, columnDefinition = "NUMBER DEFAULT 0")
    private Integer likes = 0;
    
    @Column(name = "VIEWS", nullable = false, columnDefinition = "NUMBER DEFAULT 0")
    private Integer views = 0; // 조회수 필드 추가
    
    // 댓글 리스트 추가
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
    
	// 댓글 수 필드
    @Transient
    private int commentCount;
    
    @Column(name = "FILE_NAME", nullable = true)
    private String fileName;

}
