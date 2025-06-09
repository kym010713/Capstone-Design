package com.jang.ykk.login.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.jang.ykk.login.entity.SharedEvent;
import com.jang.ykk.login.model.Event;

@Repository
public class EventRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EventRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Event> eventRowMapper = (rs, rowNum) -> {
        Event event = new Event();
        event.setId(rs.getLong("id"));
        event.setUserId(rs.getLong("user_id")); // userId 추가
        event.setEventDate(rs.getDate("event_date").toLocalDate());
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setDelYn(rs.getString("del_yn"));
        return event;
    };
    
 // 특정 날짜와 사용자 ID에 맞는 이벤트를 찾는 메서드
    public List<Event> findByDateAndUserId(LocalDate date, Long userId) {
        String sql = "SELECT * FROM capcalendar WHERE event_date = :eventDate AND user_id = :userId AND del_yn = 'n'";
        Map<String, Object> params = Map.of(
            "eventDate", java.sql.Date.valueOf(date),
            "userId", userId
        );
        return jdbcTemplate.query(sql, params, eventRowMapper);
    }

    // 특정 연도와 월, 사용자 ID에 맞는 이벤트를 찾는 메서드
    public List<Event> findByMonthAndUserId(Long userId, int year, int month) {
        String sql = "SELECT * FROM capcalendar WHERE TO_CHAR(event_date, 'YYYY') = :year AND TO_CHAR(event_date, 'MM') = :month AND user_id = :userId AND del_yn = 'n'";
        Map<String, Object> params = Map.of(
            "year", String.valueOf(year),
            "month", String.format("%02d", month),
            "userId", userId
        );
        return jdbcTemplate.query(sql, params, eventRowMapper);
    }

    public void saveResidentEvent(Event event) {
        if (event.getId() == null) {
            String sql = "INSERT INTO capcalendar (user_id, event_date, title, description, del_yn) VALUES (:userId, :eventDate, :title, :description, :delYn)";
            Map<String, Object> params = Map.of(
                "userId", event.getUserId(),
                "eventDate", java.sql.Date.valueOf(event.getEventDate()),
                "title", event.getTitle(),
                "description", event.getDescription(),
                "delYn", event.getDelYn()
            );
            jdbcTemplate.update(sql, params);
        } else {
            String sql = "UPDATE capcalendar SET title = :title, description = :description, del_yn = :delYn WHERE id = :id AND user_id = :userId";
            Map<String, Object> params = Map.of(
                "title", event.getTitle(),
                "description", event.getDescription(),
                "delYn", event.getDelYn(),
                "id", event.getId(),
                "userId", event.getUserId() // 사용자 ID를 확인하여 본인 일정만 업데이트 가능하도록 함
            );
            jdbcTemplate.update(sql, params);
        }
    }

    public void saveAdminEvent(SharedEvent sharedEvent) {
        if (sharedEvent.getId() == null) {
            String sql = "INSERT INTO shared_event (event_date, title, description) VALUES (:eventDate, :title, :description)";
            Map<String, Object> params = Map.of(
                "eventDate", java.sql.Timestamp.valueOf(sharedEvent.getEventDate()),
                "title", sharedEvent.getTitle(),
                "description", sharedEvent.getDescription()
            );
            jdbcTemplate.update(sql, params);
        } else {
            String sql = "UPDATE shared_event SET title = :title, description = :description WHERE id = :id";
            Map<String, Object> params = Map.of(
                "title", sharedEvent.getTitle(),
                "description", sharedEvent.getDescription(),
                "id", sharedEvent.getId()
            );
            jdbcTemplate.update(sql, params);
        }
    }

    public Event findById(Long id) {
        String sql = "SELECT * FROM capcalendar WHERE id = :id";
        Map<String, Object> params = Map.of("id", id);
        return jdbcTemplate.queryForObject(sql, params, eventRowMapper);
    }

    public void delete(Long id) {
        String sql = "UPDATE capcalendar SET del_yn = 'y' WHERE id = :id";
        Map<String, Object> params = Map.of("id", id);
        jdbcTemplate.update(sql, params);
    }
}