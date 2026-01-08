package com.thiet_thi.project_one.controllers;

import com.thiet_thi.project_one.dtos.ApiResponse;
import com.thiet_thi.project_one.dtos.KiemKeDto;
import com.thiet_thi.project_one.iservices.IKiemKeService;
import com.thiet_thi.project_one.repositorys.KiemKeRepository;
import com.thiet_thi.project_one.responses.KiemKeResponse;
import com.thiet_thi.project_one.models.KiemKe;
import com.thiet_thi.project_one.services.ExcelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/kiem-ke")
@RequiredArgsConstructor
public class KiemKeController {

    private final IKiemKeService kiemKeService;
    private final ExcelService excelService;
    private final KiemKeRepository kiemKeRepository;

    @PostMapping("/session")
    public ResponseEntity<?> createSession(@RequestBody KiemKeDto dto) {
        try {

           // System.out.println("Payload nhận được: " + dto);

            KiemKe kiemKe = kiemKeService.createNewSession(dto);
            return ResponseEntity.ok(kiemKe);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi Server: " + e.getMessage());
        }
    }
    @PostMapping("/submit")
    public ResponseEntity<String> submitChecklist(@RequestBody KiemKeDto dto) {
        try {
            kiemKeService.submitChecklist(dto);
            return ResponseEntity.ok("Hoàn thành kiểm kê và cập nhật tài sản thành công.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi submit checklist: " + e.getMessage());
        }
    }

    @GetMapping("/{maKiemKe}")
    public ResponseEntity<KiemKeResponse> getKiemKeReport(@PathVariable String maKiemKe) {
        try {
            KiemKe kiemKeEntity = kiemKeService.getReportById(maKiemKe);
            KiemKeResponse response = KiemKeResponse.fromKiemKe(kiemKeEntity);


            return ResponseEntity.ok(response);
        } catch (Exception e) {

            return ResponseEntity.status(404).body(null);
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllKiemKeSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String maPhong,
            @RequestParam(required = false) String trangThai
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayKiemKe").descending());

        Page<KiemKeResponse> result = kiemKeService.filterKiemKeSessions(keyword, maPhong, trangThai, pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/export")
    public ApiResponse<byte[]> exportExcel() {
        try {

            List<KiemKe> listEntities = kiemKeRepository.findAll();

            List<KiemKeResponse> listResponses = listEntities.stream()
                    .map(KiemKeResponse::fromKiemKe)
                    .toList();


            byte[] excelBytes = excelService.exportKiemKeToExcel(listResponses);

            return ApiResponse.<byte[]>builder()
                    .result(excelBytes)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi xuất Excel: " + e.getMessage());
        }
    }

    @GetMapping("/{ma}/export-bien-ban")

    public ApiResponse<byte[]> exportBienBan(@PathVariable String ma) {
        try {

            KiemKe kk = kiemKeService.getById(ma);


            KiemKeResponse responseData = KiemKeResponse.fromKiemKe(kk);


            byte[] excelBytes = excelService.exportBienBanKiemKe(responseData);

            return ApiResponse.<byte[]>builder()
                    .result(excelBytes)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi xuất biên bản: " + e.getMessage());
        }
    }
}