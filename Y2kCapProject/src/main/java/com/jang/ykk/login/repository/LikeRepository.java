package com.jang.ykk.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jang.ykk.login.entity.CapBoard;
import com.jang.ykk.login.entity.Like;
import com.jang.ykk.login.entity.Resident;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostAndUser(CapBoard post, Resident user);
    int countByPost(CapBoard post);
}
