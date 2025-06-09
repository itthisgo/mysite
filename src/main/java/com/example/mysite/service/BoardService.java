package com.example.mysite.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.mysite.dao.BoardDao;
import com.example.mysite.dao.UploadFileDao;
import com.example.mysite.dto.Board;
import com.example.mysite.dto.UploadFile;

@Service
public class BoardService {

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private UploadFileDao uploadFileDao;

    // 게시글 전체 목록 조회
    public List<Board> findAll() {
        return boardDao.findAll();
    }

    // 게시글 + 파일 함께 저장
    public void save(Board board, MultipartFile[] files, String uploadPath) throws IOException {
    	boardDao.save(board);  // board.id 생성됨

    	if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    saveFile(board.getId(), file, uploadPath);
                }
            }
        }
    }

    public void saveFile(int boardId, MultipartFile file, String uploadPath) throws IOException {
    	String originalName = file.getOriginalFilename();
    	String savedName = UUID.randomUUID() + "_" + originalName;
    	File dest = new File(uploadPath, savedName);
    	file.transferTo(dest);

    	UploadFile uploadFile = new UploadFile();
    	uploadFile.setBoardId(boardId);
    	uploadFile.setOriginalFilename(originalName);
    	uploadFile.setSavedFilename(savedName);
    	uploadFile.setFilePath("/upload/" + savedName);  // 브라우저에서 접근 가능한 경로
    	uploadFile.setFileSize(file.getSize());

    	uploadFileDao.save(uploadFile);
    }

    // 게시글 상세 조회
 	public Board findByIdWithFiles(int id) {
		Board board = boardDao.findById(id);
		List<UploadFile> files = uploadFileDao.findByBoardId(id);
		board.setFiles(files);  // Board 클래스에 List<UploadFile> 필드 추가 필요
		return board;
	}

    // 게시글 수정
 	public void update(Board board, MultipartFile[] files, String uploadPath) throws IOException {
 		boardDao.update(board);  // 제목/내용 수정
 		
 		if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    saveFile(board.getId(), file, uploadPath);
                }
            }
        }
 	}


    // 게시글 삭제
    public void delete(String uploadPath, int boardId) {
    	// 1. 게시글에 연결된 첨부파일 목록 조회
        List<UploadFile> files = uploadFileDao.findByBoardId(boardId);

        // 2. 실제 파일 삭제
        for (UploadFile file : files) {
            String fullPath = uploadPath + File.separator + file.getSavedFilename();
            File realFile = new File(fullPath);
            if (realFile.exists()) {
                realFile.delete();
            }
        }

        // 3. 게시글 삭제 (upload_file은 ON DELETE CASCADE로 자동 삭제)
        boardDao.delete(boardId);
    }
    
    // 페이징 처리
    public List<Board> findPage(int page, int size) {
    	int offset = (page - 1) * size;
    	return boardDao.findPage(offset, size);
    }

    public int getTotalCount() {
    	return boardDao.countAll();
    }

    public void deleteFile(String uploadPath, int fileId) {
        // 1. 파일 정보 조회
        UploadFile file = uploadFileDao.findById(fileId);
        if (file == null) return;

        // 2. 실제 파일 삭제
        File realFile = new File(uploadPath, file.getSavedFilename());
        if (realFile.exists()) {
            realFile.delete();
        }

        // 3. DB에서 정보 삭제
        uploadFileDao.deleteById(fileId);
    }



}
