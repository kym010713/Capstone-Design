package com.jang.ykk.login.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jang.ykk.login.model.AdministrationCost;

public interface AdministrationCostRepository extends JpaRepository<AdministrationCost, Long> {
	List<AdministrationCost> findByBuildingNumberAndUnitNumber(String buildingNumber, String unitNumber);
	List<AdministrationCost> findByBuildingNumber(String buildingNumber);
}
