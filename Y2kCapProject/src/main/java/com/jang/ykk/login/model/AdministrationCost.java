package com.jang.ykk.login.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AdministrationCost {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_cost_seq")
    @SequenceGenerator(name = "admin_cost_seq", sequenceName = "admin_cost_seq", allocationSize = 1)
    private Long id;

	private String year;    	// 부과연도
    private String month;   	// 부과월
    private String type;    	// 관리비 형태
    private int amount;     	// 금액
    private String memo; // 메모
  
    // @Column(name = "CREATED_DATE")
    private LocalDate createdDate; 	// 등록일
    
    // @Column(name = "BUILDING_NUMBER")
    private String buildingNumber;	// 동 번호
    
    // @Column(name = "UNIT_NUMBER")
    private String unitNumber;		// 호수
    
    private String payment;			// 납부여부
    
    public AdministrationCost() {
        this.createdDate = LocalDate.now();
     }
    
    public AdministrationCost(String year, String month, String type, int amount, String memo, String buildingNumber, String unitNumber, String payment) {
        this.year = year;
        this.month = month;
        this.type = type;
        this.amount = amount;
        this.memo = memo;
        this.createdDate = LocalDate.now();
        this.buildingNumber = buildingNumber;
        this.unitNumber = unitNumber;
        this.payment = payment;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}
	
	public void setBuildingNumber(String buildingNumber) {
		this.buildingNumber = buildingNumber;
	}
	
	public String getBuildingNumber(){
		return buildingNumber;
	}
	
	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}
	
	public String getUnitNumber() {
		return unitNumber;
	}
	
	public void setPayment(String payment) {
		this.payment = payment;
	}
	
	public String getPayment() {
		return payment;
	}
}
