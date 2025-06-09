package com.jang.ykk.login.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Event {

	private Long id;
	private LocalDate eventDate;
	private String title;
	private String description;
	private String delYn;
	
	// 사용자 ID 필드 추가
    private Long userId;
	
	// 기본 생성자
	public Event() {
	}
	
	public Event(Long id, LocalDate eventDate, String title, String description, String delYn, Long userId) {
		this.id = id;
		this.eventDate = eventDate;
		this.title = title;
		this.description = description;
		this.delYn = delYn;
        this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getEventDate() {
		return eventDate;
	}

	public void setEventDate(LocalDate eventDate) {
		this.eventDate = eventDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDelYn() {
		return delYn;
	}

	public void setDelYn(String delYn) {
		this.delYn = delYn;
	}
	
	
	
}
