package com.jang.ykk.login.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jang.ykk.login.model.AdministrationCost;
import com.jang.ykk.login.model.ApproveResponse;
import com.jang.ykk.login.model.OrderCreateForm;
import com.jang.ykk.login.model.ReadyResponse;
import com.jang.ykk.login.service.AdministrationCostService;
import com.jang.ykk.login.service.KakaoPayService;
import com.jang.ykk.login.utils.SessionUtils;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/administration-cost")
public class AdministrationCostController {
   @Autowired
    private AdministrationCostService administrationCostService;
   
   @Autowired
   private final KakaoPayService kakaoPayService;
   
    public AdministrationCostController(AdministrationCostService administrationCostService, KakaoPayService kakaoPayService) {
       this.administrationCostService = administrationCostService;
       this.kakaoPayService = kakaoPayService;
    }

    @GetMapping
    public String getAllAdministrationCosts(Model model) {
        List<AdministrationCost> administrationCosts = administrationCostService.findAll();
        model.addAttribute("administrationCosts", administrationCosts);
        return "administrationcost/list";
    }

    @GetMapping("/register")
    public String registerAdministrationCostForm(Model model) {
        model.addAttribute("administrationCost", new AdministrationCost());
        return "administrationcost/register";
    }

    @PostMapping("/register")
    public String registerAdministrationCost(@ModelAttribute AdministrationCost administrationCost) {
    	if (administrationCost.getPayment() == null) {
            administrationCost.setPayment("N");
         }
        administrationCostService.save(administrationCost);
        return "redirect:/administration-cost";
    }
    
    @GetMapping("/view/{buildingNumber}/{unitNumber}")
    public String findByBuildingNumberAndUnitNumber(
            @PathVariable String buildingNumber,
            @PathVariable String unitNumber,
            HttpSession session,
            Model model) {

        // 세션에서 로그인한 사용자의 동과 호수 정보 가져오기
        String loggedBuildingNumber = (String) session.getAttribute("buildingNumber");
        String loggedUnitNumber = (String) session.getAttribute("unitNumber");

        // 세션에서 가져온 값을 로그로 확인
        System.out.println("Session에서 가져온 Building Number: " + loggedBuildingNumber);
        System.out.println("Session에서 가져온 Unit Number: " + loggedUnitNumber);

        // 로그인되지 않은 상태인 경우 로그인 페이지로 리다이렉트
        if (loggedBuildingNumber == null || loggedUnitNumber == null) {
            return "redirect:/residents/login";
        }

        // 관리비를 동과 호수에 맞게 조회
        List<AdministrationCost> filteredCostList = administrationCostService.findByBuildingNumberAndUnitNumber(loggedBuildingNumber, loggedUnitNumber);

        // 필터링된 관리비 데이터가 없으면 리다이렉트
        if (filteredCostList.isEmpty()) {
            return "redirect:/administration-cost";
        }

        // 필터링된 관리비 데이터를 모델에 추가
        model.addAttribute("administrationCosts", filteredCostList);
        return "administrationcost/view"; // 관리비 조회 페이지로 이동
    }

    
    @PostMapping("/delete/{id}")
    public String deleteAdministrationCost(@PathVariable Long id) {
        administrationCostService.delete(id);
        return "redirect:/administration-cost";
    }
    
    @PostMapping("/update/{id}")
    public String updateAdministrationCost(@PathVariable Long id, @ModelAttribute AdministrationCost updatedAdministrationCost) {
        AdministrationCost administrationCost = administrationCostService.findById(id);
        if (administrationCost != null) {
            administrationCost.setYear(updatedAdministrationCost.getYear());
            administrationCost.setMonth(updatedAdministrationCost.getMonth());
            administrationCost.setMemo(updatedAdministrationCost.getMemo());

            administrationCostService.save(administrationCost);
        }
        return "redirect:/administration-cost";
    }

    @GetMapping("/upload")
    public String uploadForm() {
        return "administrationcost/upload";
    }

    @PostMapping("/upload")
    public String uploadExcelFile(@RequestParam("file") MultipartFile file) {
        administrationCostService.saveFromExcel(file);
        return "redirect:/administration-cost";
    }

    @GetMapping("/view")
    public String viewUploadedData(Model model) {
        List<AdministrationCost> administrationCosts = administrationCostService.findAll();
        model.addAttribute("administrationCosts", administrationCosts);
        return "administrationcost/view";
    }
    
    @GetMapping("/receipt/{id}")
    public ResponseEntity<byte[]> generateReceipt(@PathVariable Long id) throws DocumentException, IOException {
        AdministrationCost administrationCost = administrationCostService.findById(id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Load Korean font
        BaseFont baseFont = null;
        try {
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/NotoSansKR-VariableFont_wght.ttf");
            if (fontStream != null) {
                baseFont = BaseFont.createFont("fonts/NotoSansKR-VariableFont_wght.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } else {
                throw new IOException("Font file not found.");
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

        // Title styling
        Font titleFont = new Font(baseFont, 16, Font.BOLD);
        Paragraph title = new Paragraph("관리비 영수증", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(Chunk.NEWLINE); // Spacer

        // Receipt details layout
        Font labelFont = new Font(baseFont, 12, Font.NORMAL);
        Font contentFont = new Font(baseFont, 12, Font.NORMAL);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 3});

        // 동
        table.addCell(new PdfPCell(new Phrase("동:", labelFont)));
        table.addCell(new PdfPCell(new Phrase(administrationCost.getBuildingNumber(), contentFont)));

        // 호
        table.addCell(new PdfPCell(new Phrase("호:", labelFont)));
        table.addCell(new PdfPCell(new Phrase(administrationCost.getUnitNumber(), contentFont)));

        // 부과연도
        table.addCell(new PdfPCell(new Phrase("부과연도:", labelFont)));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(administrationCost.getYear()), contentFont)));

        // 부과월
        table.addCell(new PdfPCell(new Phrase("부과월:", labelFont)));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(administrationCost.getMonth()), contentFont)));

        // 관리비 형태
        table.addCell(new PdfPCell(new Phrase("관리비 형태:", labelFont)));
        table.addCell(new PdfPCell(new Phrase(administrationCost.getType(), contentFont)));

        // 금액
        table.addCell(new PdfPCell(new Phrase("금액:", labelFont)));
        table.addCell(new PdfPCell(new Phrase(administrationCost.getAmount() + " 원", contentFont)));

        document.add(table);
        document.add(Chunk.NEWLINE);

        // Notes
        Paragraph note = new Paragraph("<내역>", labelFont);
        document.add(note);
        document.add(new Paragraph("상기 금액을 위 내역과 같이 관리비로 납입하셨으므로 영수합니다.", contentFont));
        document.add(Chunk.NEWLINE);

     // Footer details (Date, Signature)
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedDate = currentDate.format(dateFormatter);

        Paragraph footer = new Paragraph(formattedDate + "\n\n(인)\n\n아파트 관리사무소", contentFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        
        document.close();

        byte[] pdfBytes = baos.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "receipt_" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }


    
    @PostMapping("/order/pay/ready")
    public @ResponseBody ReadyResponse payReady(@RequestBody OrderCreateForm orderCreateForm) {
        
        String name = orderCreateForm.getName();
        int totalPrice = orderCreateForm.getTotalPrice();
        int payId = orderCreateForm.getPayId();
        
        // TODO 1. 관리비를 넘겨받아서 셋팅하고 결제로 넘김
        
        // 카카오 결제 준비하기
        ReadyResponse readyResponse = kakaoPayService.payReady(name, totalPrice);
        // 세션에 결제 고유번호(tid) 저장
        SessionUtils.addAttribute("tid", readyResponse.getTid());
        SessionUtils.addAttribute("payId", payId);
        
        return readyResponse;
    }
    
    @GetMapping("/order/pay/completed")
    public String payCompleted(@RequestParam("pg_token") String pgToken) {
        String tid = SessionUtils.getStringAttributeValue("tid");
        int payId = SessionUtils.getIntAttributeValue("payId");

        // 카카오 결제 요청하기
        ApproveResponse approveResponse = kakaoPayService.payApprove(tid, pgToken);

        // TODO 1. 결제 완료 후 사용자 관리비 내역 납부여부 'N' -> 'Y' 업데이트
        // 그러려면 뭐가 필요하냐. 1) 해당 결제가 어떤 관리비 내역의 ID 인지 가져오는 부분
        // Update 쿼리를 통해서 관리비 내역을 업데이트 하는 부분
        administrationCostService.updatePaymentWithId(Long.valueOf(payId));
        
        return "redirect:/administration-cost/view/buildingNumber/unitNumber";
    }
}
