package com.jang.ykk.login.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jang.ykk.login.entity.CapBoard;

public interface CapBoardRepository extends JpaRepository<CapBoard, Long> {

	// 결합된 author 형식을 사용하여 게시글 조회
    List<CapBoard> findByAuthor(String author);
	
	// 전체 게시글을 최신순으로 페이징 조회
    @Query(
        value = "SELECT * FROM (SELECT a.*, ROWNUM r__ FROM (SELECT * FROM CAPBOARD WHERE DEL_YN = :delYn ORDER BY CREATED_AT DESC) a WHERE ROWNUM <= :endRow) WHERE r__ > :startRow",
        nativeQuery = true
    )
    List<CapBoard> findWithPagination(@Param("delYn") String delYn, @Param("startRow") int startRow, @Param("endRow") int endRow);

    // 삭제되지 않은 게시글의 총 개수 조회
    @Query(
        value = "SELECT COUNT(*) FROM CAPBOARD WHERE DEL_YN = :delYn",
        nativeQuery = true
    )
    int countByDelYn(@Param("delYn") String delYn);

    // 특정 카테고리의 게시글을 최신순으로 페이징 조회
    @Query(
        value = "SELECT * FROM (SELECT a.*, ROWNUM r__ FROM (SELECT * FROM CAPBOARD WHERE DEL_YN = :delYn AND CATEGORY_ID = :categoryId ORDER BY CREATED_AT DESC) a WHERE ROWNUM <= :endRow) WHERE r__ > :startRow",
        nativeQuery = true
    )
    List<CapBoard> findByCategoryWithPagination(@Param("delYn") String delYn, @Param("categoryId") Long categoryId, @Param("startRow") int startRow, @Param("endRow") int endRow);

    // 특정 카테고리의 삭제되지 않은 게시글의 총 개수 조회
    int countByDelYnAndCategoryId(String delYn, Long categoryId);

    // 검색 기능: 제목 또는 작성자에서 키워드를 포함하는 게시글 검색
    // 네이티브 쿼리를 사용한 검색 기능 구현 (Oracle 11 호환)
    @Query(value = "SELECT * FROM (SELECT a.*, ROWNUM r__ FROM (SELECT * FROM CAPBOARD WHERE (TITLE LIKE %:keyword% OR AUTHOR LIKE %:keyword%) AND DEL_YN = :delYn ORDER BY CREATED_AT DESC) a WHERE ROWNUM <= :endRow) WHERE r__ > :startRow",
           countQuery = "SELECT COUNT(*) FROM CAPBOARD WHERE (TITLE LIKE %:keyword% OR AUTHOR LIKE %:keyword%) AND DEL_YN = :delYn",
           nativeQuery = true)
    List<CapBoard> searchWithPagination(@Param("keyword") String keyword, @Param("delYn") String delYn, @Param("startRow") int startRow, @Param("endRow") int endRow);
}