package com.example.mysite.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.mysite.dto.UploadFile;

@Repository
public class UploadFileDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void save(UploadFile file) {
        String sql = "INSERT INTO upload_file (board_id, original_filename, saved_filename, file_path, file_size) " +
                     "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            file.getBoardId(),
            file.getOriginalFilename(),
            file.getSavedFilename(),
            file.getFilePath(),
            file.getFileSize());
    }

	public UploadFile findById(int id) {
	    String sql = "SELECT * FROM upload_file WHERE id = ?";
	    return jdbcTemplate.queryForObject(sql, new UploadFileRowMapper(), id);
	}
	
    public List<UploadFile> findByBoardId(int boardId) {
        String sql = "SELECT * FROM upload_file WHERE board_id = ?";
        return jdbcTemplate.query(sql, new UploadFileRowMapper(), boardId);
    }
    
    public void deleteById(int id) {
        String sql = "DELETE FROM upload_file WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

}
