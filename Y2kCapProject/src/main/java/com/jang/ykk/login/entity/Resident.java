package com.jang.ykk.login.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "residents")
public class Resident {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resident_seq")
    @SequenceGenerator(name = "resident_seq", sequenceName = "resident_seq", allocationSize = 1)
    private Long id;

    private String password;
    private String apartmentName;
    private String buildingNumber;
    private String unitNumber;
    private String name;
    private String nickname;
    private String userid;
    private String phoneNumber;
    private String headOfHousehold;
    private String approvalCode;
    private String role;

    // 승인 상태를 "Y" 또는 "N"으로 저장할 수 있도록 String 타입으로 변경
    @Column(columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String approvalStatus = "N";

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 엔티티가 처음 저장될 때 createdAt과 updatedAt을 현재 시간으로 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 엔티티가 업데이트될 때 updatedAt을 현재 시간으로 갱신
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
