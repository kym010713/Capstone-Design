package com.jang.ykk.login.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jang.ykk.login.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	
	@Query("SELECT c FROM Comment c WHERE c.board.id = :boardId ORDER BY c.createdAt DESC")
	   List<Comment> findByBoardIdOrderByCreatedAtDesc(@Param("boardId") Long boardId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.board.id = :boardId")
    int countByBoardId(@Param("boardId") Long boardId);
}