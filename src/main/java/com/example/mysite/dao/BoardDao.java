package com.example.mysite.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.mysite.dto.Board;

@Repository
public class BoardDao {
	@Autowired 
	JdbcTemplate jdbcTemplate;

	public List<Board> findAll() {
		return jdbcTemplate.query("SELECT * FROM board ORDER BY id DESC", new BoardRowMapper());
	}
	
	public void save(Board board) {
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO board(title, content, writer, created_at) VALUES (?, ?, ?, NOW())",
				Statement.RETURN_GENERATED_KEYS
			);
			ps.setString(1, board.getTitle());
			ps.setString(2, board.getContent());
			ps.setString(3, board.getWriter());
			return ps;
		}, keyHolder);

		// 자동 생성된 ID 설정
		Number key = keyHolder.getKey();
		if (key != null) {
			board.setId(key.intValue());
		}
	}
	
	public Board findById(int id) {
		return jdbcTemplate.queryForObject("SELECT * FROM board WHERE id = ?", new BoardRowMapper(), id);
	}
	
	public void update(Board board) {
		String sql = "UPDATE board SET title = ?, content = ? WHERE id = ?";
		jdbcTemplate.update(sql, board.getTitle(), board.getContent(), board.getId());
	}
	
	public void delete(int id) {
		jdbcTemplate.update("DELETE FROM board WHERE id = ?", id);
	}
	
	// 페이징 처리
	public List<Board> findPage(int offset, int limit) {
		String sql = "SELECT * FROM board ORDER BY id DESC LIMIT ? OFFSET ?";
		return jdbcTemplate.query(sql, new BoardRowMapper(), limit, offset);
	}

	public int countAll() {
		String sql = "SELECT COUNT(*) FROM board";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

}
