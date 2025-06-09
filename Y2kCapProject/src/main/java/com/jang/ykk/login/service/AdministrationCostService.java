package com.jang.ykk.login.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jang.ykk.login.model.AdministrationCost;
import com.jang.ykk.login.repository.AdministrationCostRepository;

@Service
public class AdministrationCostService {
   
   @Autowired
    private AdministrationCostRepository administrationCostRepository;

    public AdministrationCostService(AdministrationCostRepository administrationCostRepository) {
        this.administrationCostRepository = administrationCostRepository;
    }

    public List<AdministrationCost> findAll() {
        return administrationCostRepository.findAll();
    }

    public AdministrationCost findById(Long id) {
       return administrationCostRepository.findById(id).orElseThrow();
    }
    
    public List<AdministrationCost> findAllById(Long id) {
       List<Long> ids = Arrays.asList(id);
        return administrationCostRepository.findAllById(ids);
    }
    
    public List<AdministrationCost> findByBuildingNumberAndUnitNumber(String buildingNumber, String unitNumber) {
       List<AdministrationCost> datas = administrationCostRepository.findByBuildingNumberAndUnitNumber(buildingNumber, unitNumber);

       return datas;
    }

    public List<AdministrationCost> findByBuildingNumber(String buildingNumber) {
       List<AdministrationCost> datas = administrationCostRepository.findByBuildingNumber(buildingNumber);

       return datas;
    }
    public void updatePaymentWithId(Long id) {
       AdministrationCost updateData = administrationCostRepository.findById(id).orElseThrow();
       updateData.setPayment("Y");
       
       administrationCostRepository.saveAndFlush(updateData);
    }
    
    public AdministrationCost save(AdministrationCost administrationCost) {
        return administrationCostRepository.save(administrationCost);
    }

    public void delete(Long id) {
        administrationCostRepository.deleteById(id);
    }
    
    public void saveFromExcel(MultipartFile file) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) continue;
                
                String buildingNumber = getCellValueAsString(row.getCell(1));
                String unitNumber = getCellValueAsString(row.getCell(2));
                String year = getCellValueAsString(row.getCell(3));
                String month = getCellValueAsString(row.getCell(4));
                int amount = row.getCell(5) != null ? (int)row.getCell(5).getNumericCellValue() : 0; // 금액이 null인 경우 0으로 설정
                String type = getCellValueAsString(row.getCell(6));
                String memo = getCellValueAsString(row.getCell(7));

                AdministrationCost cost = new AdministrationCost();
                cost.setBuildingNumber(buildingNumber);
                cost.setUnitNumber(unitNumber);
                cost.setYear(year);
                cost.setMonth(month);
                cost.setAmount(amount);
                cost.setType(type);
                cost.setMemo(memo);

                administrationCostRepository.save(cost);
            }
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    private String getCellValueAsString(XSSFCell cell) {
        if (cell == null) return ""; // 셀이 null인 경우 빈 문자열 반환
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return ""; // 다른 경우도 빈 문자열 반환
    }
    
    public byte[] generateReceipt(Long id) {
        AdministrationCost cost = findById(id); // 영수증을 생성할 관리비 데이터 조회
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("영수증");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 650);
                contentStream.showText("번호: " + cost.getId());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("부과연도: " + cost.getYear());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("부과월: " + cost.getMonth());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("관리비 형태: " + cost.getType());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("금액: " + cost.getAmount());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("메모: " + cost.getMemo());
                contentStream.endText();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("PDF 생성 중 오류 발생", e);
        }
    }
}
