package com.jang.ykk.login.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jang.ykk.login.entity.Resident;
import com.jang.ykk.login.repository.ResidentRepository;

@Service
public class ResidentService {

    @Autowired
    private ResidentRepository residentRepository;
   
    // 사용자 저장
    public Resident saveResident(Resident resident) {
        return residentRepository.save(resident);
    }

 // 세대주의 아파트명, 동, 호수, 이름, 승인 코드가 일치하는지 확인
    public boolean verifyHeadOfHouseholdMatch(String apartmentName, String buildingNumber, String unitNumber, String headOfHousehold, String approvalCode) {
        Resident head = residentRepository.findByApartmentNameAndBuildingNumberAndUnitNumberAndNameAndApprovalCodeAndApprovalStatus(
                apartmentName, buildingNumber, unitNumber, headOfHousehold, approvalCode, "Y");
        return head != null;
    }

    // 승인 대기 중인 사용자 목록을 가져오는 메서드
    public List<Resident> findPendingResidents() {
        return residentRepository.findByApprovalStatus("N");
    }

    // 승인 완료된 사용자 목록을 가져오는 메서드
    public List<Resident> findApprovedResidents() {
        return residentRepository.findByApprovalStatus("Y");
    }

    // 사용자 승인 메서드
    public void approveResident(Long residentId) {
        Optional<Resident> residentOptional = residentRepository.findById(residentId);
        if (residentOptional.isPresent()) {
            Resident resident = residentOptional.get();
            resident.setApprovalStatus("Y"); // 승인 상태를 'Y'로 설정
            residentRepository.save(resident); // 승인된 사용자 저장
        }
    }
    
    // 승인 완료된 사용자 삭제 메서드
    public void deleteApprovedResident(Long residentId) {
        Optional<Resident> residentOptional = residentRepository.findById(residentId);
        if (residentOptional.isPresent() && "Y".equals(residentOptional.get().getApprovalStatus())) {
            residentRepository.deleteById(residentId); // 승인 완료된 사용자만 삭제
        }
    }

    // 로그인 메서드
    public Optional<Resident> login(String userid, String password) {
        return residentRepository.findByUseridAndPasswordAndApprovalStatus(userid, password, "Y");
    }
    
    // ID로 사용자 정보 조회
    public Optional<Resident> findById(Long residentId) {
        return residentRepository.findById(residentId);
    }
    
 // 사용자 프로필 업데이트 메서드
    public boolean updateProfile(Long residentId, String nickname, String phone, String password) {
        Optional<Resident> residentOpt = residentRepository.findById(residentId);
        if (residentOpt.isPresent()) {
            Resident resident = residentOpt.get();
            resident.setNickname(nickname);
            resident.setPhoneNumber(phone);
            resident.setPassword(password); // 단순 비밀번호 저장 (암호화 없음)
            residentRepository.save(resident); // 변경사항 저장
            return true; // 업데이트 성공
        }
        return false; // 업데이트 실패 (사용자 찾을 수 없음)
    }
    
 // 세대주를 residentId로 조회하는 메서드
    public Resident findHeadOfHouseholdByResidentId(Long residentId) {
        // residentId에 해당하는 사용자를 조회하고, 해당 사용자의 세대주 정보를 반환
        Optional<Resident> residentOpt = residentRepository.findById(residentId);
        if (residentOpt.isPresent()) {
            Resident resident = residentOpt.get();
            return residentRepository.findHeadOfHousehold(
                resident.getApartmentName(), 
                resident.getBuildingNumber(), 
                resident.getUnitNumber()
            );
        }
        return null; // 세대주가 없을 경우 null 반환
    }
    
 // residentId에 해당하는 세대원의 목록을 조회하는 메서드
    public List<Resident> findHouseholdMembersByResidentId(Long residentId) {
        // residentId에 해당하는 사용자를 조회하고, 해당 사용자의 세대원 목록 반환
        Optional<Resident> residentOpt = residentRepository.findById(residentId);
        if (residentOpt.isPresent()) {
            Resident resident = residentOpt.get();
            return residentRepository.findHouseholdMembers(
                resident.getApartmentName(), 
                resident.getBuildingNumber(), 
                resident.getUnitNumber()
            );
        }
        return List.of(); // 세대원이 없을 경우 빈 리스트 반환
    }
    
}
