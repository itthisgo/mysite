package com.example.mysite.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.mysite.dto.Board;
import com.example.mysite.dto.User;
import com.example.mysite.service.BoardService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/board")
public class BoardController {

	private BoardService boardService;
	
	@Value("${upload.path}")
	private String uploadPath;
	
	public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }
	
	@PostConstruct
    public void init() {
        File uploadDir = new File(uploadPath);
        System.out.println("업로드 경로:" + uploadDir.getAbsolutePath());
        if (!uploadDir.exists()) {
            try {
                uploadDir.mkdirs();
                System.out.println(uploadDir.getAbsolutePath() + " 업로드 디렉터리 생성");
            } catch (Exception e) {
                System.out.println(uploadDir.getAbsolutePath() + " 업로드 디렉터리 생성 실패");
                System.out.println(e.getMessage());
            }
        } else if (!uploadDir.isDirectory()) {
            System.out.println("오류: 업로드 경로가 디렉터리가 아닙니다.");
        } else {
            System.out.println(uploadDir.getAbsolutePath() + " 업로드 디렉터리 존재");
        }
    }
	
	//@GetMapping("/list")
	public String list(Model model) {
		List<Board> boards = boardService.findAll();
		model.addAttribute("boardList", boards);
		return "board/boardList";
	}
	
	// 페이징 적용
	@GetMapping("/list")
	public String list(@RequestParam(name="page", defaultValue="1") int page, Model model) {
		int pageSize = 5;
		int blockSize = 5;

		List<Board> boardList = boardService.findPage(page, pageSize);
		int totalCount = boardService.getTotalCount();
		int totalPages = (int) Math.ceil((double) totalCount / pageSize);

		int currentBlock = (int) Math.ceil((double) page / blockSize);
		int startPage = (currentBlock - 1) * blockSize + 1;
		int endPage = Math.min(startPage + blockSize - 1, totalPages);

		model.addAttribute("boardList", boardList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("hasPrev", startPage > 1);
		model.addAttribute("hasNext", endPage < totalPages);

		return "board/boardList";
	}


	@GetMapping("/write")
	public String writeForm(HttpSession session) {
		var user = (User) session.getAttribute("loginUser");
		if (user == null) {
			return "redirect:/user/login";
		}
		return "board/boardForm";
	}

	@PostMapping("/write")
	public String write(
		@ModelAttribute("board") Board board,
		@RequestParam("file") MultipartFile[] files,
		HttpSession session
	) throws IOException {
		User user = (User)session.getAttribute("loginUser");
		if (user == null) return "redirect:/user/login";

		board.setWriter(user.getUsername());
		
		boardService.save(board, files, uploadPath);

		return "redirect:/board/list";
	}

	@GetMapping("/view")
	public String view(@RequestParam("id") int id, Model model, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");
		if (user == null) {
			return "redirect:/user/login";
		}
		Board board = boardService.findByIdWithFiles(id);
		model.addAttribute("board", board);
		return "board/boardView";
	}

	@GetMapping("/delete")
	public String delete(@RequestParam("id") int id, HttpSession session) {
		var user = (User) session.getAttribute("loginUser");
		if (user == null) {
			return "redirect:/user/login";
		}
		boardService.delete(uploadPath, id);
		return "redirect:/board/list";
	}
	
	@GetMapping("/edit")
	public String editForm(@RequestParam("id") int id, Model model, HttpSession session) {
		var user = (User) session.getAttribute("loginUser");
		if (user == null) {
			return "redirect:/user/login";
		}
		Board board = boardService.findByIdWithFiles(id);
		model.addAttribute("board", board);
		return "board/boardEdit";
	}

//	@PostMapping("/edit")
//	public String edit(@ModelAttribute("board") Board board) {
//		boardService.update(board);
//		return "redirect:/board/view?id=" + board.getId();
//	}
	
	@PostMapping("/edit")
	public String edit(
	    @ModelAttribute("board") Board board,
	    @RequestParam(value = "file", required = false) MultipartFile[] files,
	    @RequestParam(value = "deleteFile", required = false) Integer[] deleteFileId,
	    HttpSession session
	) throws IOException {
	    var user = (User) session.getAttribute("loginUser");
	    if (user == null) {
	        return "redirect:/user/login";
	    }

	    // 게시글 수정
	    boardService.update(board, files, uploadPath);

	    // 파일 삭제 처리
	    if (deleteFileId != null) {
	    	for(Integer id : deleteFileId) {
	    		boardService.deleteFile(uploadPath, id);
	    	}
	    }

	    return "redirect:/board/view?id=" + board.getId();
	}

	
	@GetMapping("/upload/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable("filename") String filename) throws IOException {
	    Path file = Paths.get(uploadPath).resolve(filename);
	    Resource resource = new UrlResource(file.toUri());

	    if (!resource.exists()) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
	        .body(resource);
	}


}
