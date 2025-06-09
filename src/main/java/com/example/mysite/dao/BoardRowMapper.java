package com.example.mysite.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.mysite.dto.Board;

public class BoardRowMapper implements RowMapper<Board> {
	@Override
	public Board mapRow(ResultSet rs, int rowNum) throws SQLException {
		Board b = new Board();
		b.setId(rs.getInt("id"));
		b.setTitle(rs.getString("title"));
		b.setContent(rs.getString("content"));
		b.setWriter(rs.getString("writer"));
		b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		return b;
	}
}
