package com.jang.ykk.login.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jang.ykk.login.entity.CapBoard;
import com.jang.ykk.login.entity.Category;
import com.jang.ykk.login.repository.CategoryRepository;
import com.jang.ykk.login.service.CapBoardService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private CapBoardService capBoardService;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping
    public String getBoards(@RequestParam(defaultValue = "0") int page, Model model) {
        int size = 15; // 한 페이지당 게시글 수
        Page<CapBoard> boards = capBoardService.getBoards(page, size);
        
     // 전체 카테고리 목록을 가져와서 모델에 추가
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("boards", boards);
        
        model.addAttribute("boards", boards);
        return "board/list";
    }
    
    // 검색 메서드
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page, Model model) {
        int size = 15; // 한 페이지당 게시글 수
        Page<CapBoard> searchResults = capBoardService.searchBoards(keyword, page, size);

        // 카테고리 목록을 모델에 추가
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        model.addAttribute("boards", searchResults);
        model.addAttribute("keyword", keyword);
        return "board/list"; // 검색 결과를 기존 게시판 목록 페이지에 표시
    }
    
    // 카테고리별 게시글 조회
    @GetMapping("/category/{id}")
    public String getBoardsByCategory(@PathVariable("id") Long categoryId, @RequestParam(defaultValue = "0") int page, Model model) {
        int size = 15;
        Page<CapBoard> boards = capBoardService.getBoardsByCategory(categoryId, page, size);

        // 전체 카테고리 목록을 모델에 추가
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        model.addAttribute("boards", boards);
        model.addAttribute("selectedCategoryId", categoryId); // 선택된 카테고리 ID를 모델에 추가
        return "board/list";
    }
    
    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable("id") Long postId, Model model) {
        // 게시글 상세 정보 가져오기
        CapBoard post = capBoardService.getPostById(postId);

        // 전체 카테고리 목록 가져오기
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("post", post);

        return "board/view";
    }
    
    // 글 등록 페이지로 이동
    @GetMapping("/new")
    public String newPost(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "board/new";
    }
    // 새 글 생성
    @PostMapping("/create")
    public String createPost(@RequestParam Long categoryId, @RequestParam String title, 
                             @RequestParam String content, @RequestParam("file") MultipartFile file,
                             HttpSession session) {
        String nickname = (String) session.getAttribute("nickname");
        String author = nickname + " (" + session.getAttribute("buildingNumber") + ")";

        // 세션에서 가져온 nickname과 author 정보 로그로 확인
        System.out.println("게시글 작성 닉네임: " + nickname);
        System.out.println("게시글 작성 Author: " + author);
    		String fileName = null;
		if (!file.isEmpty()) {
			try {
				// 파일 경로 확인 및 폴더 생성
				Path directoryPath = Paths.get(uploadDir);
				if (!Files.exists(directoryPath)) {
					Files.createDirectories(directoryPath);
				}

				// 파일 저장
				fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename(); // 파일명에 타임스탬프 추가
				Path filePath = directoryPath.resolve(fileName);
				Files.write(filePath, file.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
				return "redirect:/board/new?error=fileUpload"; // 오류가 발생한 경우 에러 메시지 전달
			}
		}

		// 파일명을 포함하여 게시글 생성 서비스 호출
		capBoardService.createPost(categoryId, title, content, fileName, author);
	    return "redirect:/board";
	}
    
    
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable("id") Long postId, 
                             @RequestParam String content, HttpSession session) {
        String nickname = (String) session.getAttribute("nickname");
        String author = nickname + " (" + session.getAttribute("buildingNumber") + ")";

        // 세션에서 가져온 nickname과 author 정보 로그로 확인
        System.out.println("댓글 작성 닉네임: " + nickname);
        System.out.println("댓글 작성 Author: " + author);

        capBoardService.addComment(postId, content, author);
        return "redirect:/board/view/" + postId;
    }
    
    @GetMapping("/{id}/like/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable("id") Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        boolean hasLiked = false;

        if (userId != null) {
            hasLiked = capBoardService.hasUserLiked(postId, userId);
        }

        int likeCount = capBoardService.getLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("hasLiked", hasLiked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable("id") Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        boolean isLiked = capBoardService.toggleLike(postId, userId);
        int likeCount = capBoardService.getLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", isLiked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/like/count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable("id") Long postId) {
        int likeCount = capBoardService.getLikeCount(postId);
        return ResponseEntity.ok(likeCount);
    }

    // 조회 수 카운트
    @GetMapping("/{id}/viewCount")
    public ResponseEntity<Integer> getViewCount(@PathVariable("id") Long postId) {
        int viewCount = capBoardService.getViewCount(postId);
        return ResponseEntity.ok(viewCount);
    }
    
    // 파일 다운로드
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 게시글 수정, 삭제
    // 게시글 수정 페이지로 이동 (GET 요청)
    @GetMapping("/edit/{id}")
    public String editPost(@PathVariable("id") Long postId, Model model, HttpSession session) {
        CapBoard post = capBoardService.getPostById(postId);

        // 작성자 확인
        String nickname = (String) session.getAttribute("nickname");
        if (!post.getAuthor().contains(nickname)) {
            return "redirect:/board/view/" + postId; // 작성자가 아닌 경우 수정 페이지 접근 제한
        }

        model.addAttribute("post", post);
        return "board/edit"; // 수정 페이지로 이동
    }

    // 게시글 수정 처리 (POST 요청)
    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable("id") Long postId,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(required = false) MultipartFile file,
                             HttpSession session) {
        String nickname = (String) session.getAttribute("nickname"); // 현재 로그인된 사용자 닉네임
        
        CapBoard post = capBoardService.getPostById(postId);

        if (!post.getAuthor().contains(nickname)) {
            return "redirect:/board/view/" + postId; // 작성자가 아니면 접근 차단
        }
        String fileName =  post.getFileName(); // 기존 파일명으로 초기화
     // 새로운 파일이 업로드된 경우 파일명 변경
        if (file != null && !file.isEmpty()) {
            try {
                Path directoryPath = Paths.get(uploadDir);
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }

                fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename(); // 파일명에 타임스탬프 추가
                Path filePath = directoryPath.resolve(fileName);
                Files.write(filePath, file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/board/edit/" + postId + "?error=fileUpload"; // 오류가 발생한 경우 에러 메시지 전달
            }
        }

        // 파일명을 포함하여 게시글 업데이트
        capBoardService.updatePost(postId, title, content, fileName);
        return "redirect:/board/view/" + postId;
    }



    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") Long postId, HttpSession session) {
        String nickname = (String) session.getAttribute("nickname"); // 현재 로그인된 사용자 닉네임
        CapBoard post = capBoardService.getPostById(postId);

        if (!post.getAuthor().contains(nickname)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 작성자가 아니면 403 Forbidden
        }

        capBoardService.deleteBoard(postId);
        return ResponseEntity.ok().build();
    }
    
    // 댓글 삭제 메서드
    @PostMapping("/comment/delete/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, HttpSession session) {
        String nickname = (String) session.getAttribute("nickname"); // 현재 로그인된 사용자 닉네임
        boolean isAuthor = capBoardService.isCommentAuthor(id, nickname);

        if (!isAuthor) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 작성자가 아니면 403 Forbidden 반환
        }

        capBoardService.deleteComment(id);
        return ResponseEntity.ok().build();
    }


}
