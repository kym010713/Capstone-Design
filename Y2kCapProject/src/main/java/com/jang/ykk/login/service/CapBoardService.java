package com.jang.ykk.login.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jang.ykk.login.entity.CapBoard;
import com.jang.ykk.login.entity.Comment;
import com.jang.ykk.login.entity.Like;
import com.jang.ykk.login.entity.Resident;
import com.jang.ykk.login.repository.CapBoardRepository;
import com.jang.ykk.login.repository.CategoryRepository;
import com.jang.ykk.login.repository.CommentRepository;
import com.jang.ykk.login.repository.LikeRepository;
import com.jang.ykk.login.repository.ResidentRepository;

@Service
public class CapBoardService {

    @Autowired
    private CapBoardRepository capBoardRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private LikeRepository likeRepository;
    
    @Autowired
    private ResidentRepository residentRepository;
    
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByBoardIdOrderByCreatedAtDesc(postId); // 댓글 최신순 정렬
    }
    
    // 검색
    public Page<CapBoard> searchBoards(String keyword, int page, int size) {
        int startRow = page * size;
        int endRow = startRow + size;

        List<CapBoard> boards = capBoardRepository.searchWithPagination(keyword, "N", startRow, endRow);
        int totalElements = capBoardRepository.countByDelYn("N");

        return new PageImpl<>(boards, PageRequest.of(page, size), totalElements);
    }

    public Page<CapBoard> getBoards(int page, int size) {
        int startRow = page * size;
        int endRow = startRow + size;

        List<CapBoard> boards = capBoardRepository.findWithPagination("N", startRow, endRow);
        
        // 각 게시글의 댓글 수 설정
        boards.forEach(board -> board.setCommentCount(commentRepository.countByBoardId(board.getId())));

        int totalElements = capBoardRepository.countByDelYn("N");
        return new PageImpl<>(boards, PageRequest.of(page, size), totalElements);

    }
    
    public boolean hasUserLiked(Long postId, Long userId) {
        CapBoard post = capBoardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 게시글 ID"));

        Resident user = residentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID"));

        return likeRepository.findByPostAndUser(post, user).isPresent();
    }
    
    // 조회 수 카운트
    public int getViewCount(Long postId) {
        CapBoard post = capBoardRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        return post.getViews();
    }



    // 카테고리별 게시글 조회
    public Page<CapBoard> getBoardsByCategory(Long categoryId, int page, int size) {
        int startRow = page * size;
        int endRow = startRow + size;

        List<CapBoard> boards = capBoardRepository.findByCategoryWithPagination("N", categoryId, startRow, endRow);
        
        // 각 게시글의 댓글 수 설정
        boards.forEach(board -> board.setCommentCount(commentRepository.countByBoardId(board.getId())));

        int totalElements = capBoardRepository.countByDelYnAndCategoryId("N", categoryId);
        return new PageImpl<>(boards, PageRequest.of(page, size), totalElements);
    }
    
    // 게시글 ID를 기준으로 게시글 정보 조회 및 조회수 증가
    @Transactional
    public CapBoard getPostById(Long postId) {
        CapBoard post = capBoardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));
        
        // 조회수 증가
        post.setViews(post.getViews() + 1);
        capBoardRepository.save(post);
        
        return post;
    }
    
    //좋아요 수 증가
    public int getLikeCount(Long postId) {
        CapBoard post = capBoardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 게시글 ID"));
        return likeRepository.countByPost(post); // 해당 게시글의 좋아요 수 반환
    }

    
    // 새 글 생성 메서드
    public void createPost(Long categoryId, String title, String content, String fileName, String author) {
        CapBoard post = new CapBoard();
        post.setCategory(categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리 ID")));
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author); // 로그인된 사용자의 정보를 작성자로 설정
        post.setDelYn("N");
        post.setCreatedAt(LocalDateTime.now());
        post.setFileName(fileName); // 파일이 있다면 파일 이름 저장
        capBoardRepository.save(post);
    }
    
    // 게시글 수정
    public void updatePost(Long postId, String title, String content, String fileName) {
        CapBoard post = capBoardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        post.setTitle(title);
        post.setContent(content);
        post.setFileName(fileName); // 필요한 경우 파일 이름도 업데이트
        capBoardRepository.save(post);
    }


    
    
    // 댓글 작성 메서드
    @Transactional
    public void addComment(Long postId, String content, String author) {
        CapBoard post = capBoardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 게시글 ID"));

        Comment comment = new Comment();
        comment.setBoard(post);
        comment.setAuthor(author); // 로그인된 사용자의 정보를 작성자로 설정
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);
    }


    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long id) {
        CapBoard board = capBoardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));
        board.setDelYn("Y");
        capBoardRepository.save(board);
    }
    
    // 댓글 삭제
    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
    
    // 댓글 작성자 확인
    public boolean isCommentAuthor(Long commentId, String nickname) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 댓글 ID"));
        return comment.getAuthor().contains(nickname); // 댓글 작성자가 현재 사용자와 일치하는지 확인
    }


    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        System.out.println("toggleLike 메서드 호출됨 - postId: " + postId + ", userId: " + userId);
        
        CapBoard post = capBoardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 게시글 ID"));

        Resident user = residentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID"));

        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.setLikes(post.getLikes() - 1);
            capBoardRepository.save(post);
            System.out.println("좋아요 취소됨");
            return false;
        } else {
            Like newLike = new Like();
            newLike.setPost(post);
            newLike.setUser(user);
            likeRepository.save(newLike);
            post.setLikes(post.getLikes() + 1);
            capBoardRepository.save(post);
            System.out.println("좋아요 추가됨");
            return true;
        }
    }


}
