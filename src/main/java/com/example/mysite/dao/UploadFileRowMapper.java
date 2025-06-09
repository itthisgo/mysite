package com.example.mysite.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.mysite.dto.UploadFile;

public class UploadFileRowMapper implements RowMapper<UploadFile> {
    public UploadFile mapRow(ResultSet rs, int rowNum) throws SQLException {
        UploadFile file = new UploadFile();
        file.setId(rs.getInt("id"));
        file.setBoardId(rs.getInt("board_id"));
        file.setOriginalFilename(rs.getString("original_filename"));
        file.setSavedFilename(rs.getString("saved_filename"));
        file.setFilePath(rs.getString("file_path"));
        file.setFileSize(rs.getLong("file_size"));
        file.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
        return file;
    }
}
