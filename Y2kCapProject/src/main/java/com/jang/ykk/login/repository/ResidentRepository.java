package com.jang.ykk.login.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jang.ykk.login.entity.Resident;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {
    Resident findByNameAndApprovalCodeAndApprovalStatus(String name, String approvalCode, String approvalStatus);

    List<Resident> findByApprovalStatus(String approvalStatus);

    Optional<Resident> findByUseridAndPasswordAndApprovalStatus(String userid, String password, String approvalStatus);

    Resident findByApartmentNameAndBuildingNumberAndUnitNumberAndNameAndApprovalCodeAndApprovalStatus(
            String apartmentName, String buildingNumber, String unitNumber, String name, String approvalCode, String approvalStatus);
    
    // 세대주를 조회하는 쿼리 (role이 'head'인 사용자)
    @Query("SELECT r FROM Resident r WHERE r.apartmentName = :apartmentName AND r.buildingNumber = :buildingNumber AND r.unitNumber = :unitNumber AND r.role = 'head'")
    Resident findHeadOfHousehold(@Param("apartmentName") String apartmentName, 
                                 @Param("buildingNumber") String buildingNumber, 
                                 @Param("unitNumber") String unitNumber);

    // 세대원 목록을 조회하는 쿼리 (role이 'household'인 사용자)
    @Query("SELECT r FROM Resident r WHERE r.apartmentName = :apartmentName AND r.buildingNumber = :buildingNumber AND r.unitNumber = :unitNumber AND r.role = 'household'")
    List<Resident> findHouseholdMembers(@Param("apartmentName") String apartmentName, 
                                        @Param("buildingNumber") String buildingNumber, 
                                        @Param("unitNumber") String unitNumber);
    
}

