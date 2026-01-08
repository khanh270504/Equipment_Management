package com.thiet_thi.project_one.controllers;

import com.thiet_thi.project_one.dtos.LoTBStatDto;
import com.thiet_thi.project_one.dtos.LoThietBiDto;
import com.thiet_thi.project_one.iservices.ILoThietBiService;
import com.thiet_thi.project_one.models.LoThietBi;
import com.thiet_thi.project_one.dtos.ApiResponse; // 👇 Dùng class này để gói dữ liệu
import com.thiet_thi.project_one.repositorys.LoThietBiRepository;
import com.thiet_thi.project_one.responses.LoThietBiResponse;
import com.thiet_thi.project_one.services.ExcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lo_thiet_bi")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LoThietBiController {


    private final ILoThietBiService loThietBiService;
    private final LoThietBiRepository loThietBiRepository;
    private final ExcelService excelService;


    @PostMapping
    public ApiResponse<LoThietBiResponse> create(@Valid @RequestBody LoThietBiDto dto) {

        LoThietBi lo = loThietBiService.create(dto);


        return ApiResponse.<LoThietBiResponse>builder()
                .result(LoThietBiResponse.from(lo))
                .build();
    }


    @GetMapping
    public ApiResponse<List<LoThietBiResponse>> getAll() {
        List<LoThietBiResponse> list = loThietBiService.getAll().stream()
                .map(LoThietBiResponse::from)
                .toList();

        return ApiResponse.<List<LoThietBiResponse>>builder()
                .result(list)
                .build();
    }


    @GetMapping("/{ma}")
    public ApiResponse<LoThietBiResponse> getByMa(@PathVariable String ma) {
        return ApiResponse.<LoThietBiResponse>builder()
                .result(LoThietBiResponse.from(loThietBiService.getByMa(ma)))
                .build();
    }


    @PostMapping("/nhap-kho/{maDeXuat}")
    public ApiResponse<List<LoThietBiResponse>> nhapKhoTuDeXuat(@PathVariable String maDeXuat) {
        List<LoThietBiResponse> list = loThietBiService.nhapKhoTuDeXuat(maDeXuat).stream()
                .map(LoThietBiResponse::from)
                .toList();

        return ApiResponse.<List<LoThietBiResponse>>builder()
                .result(list)
                .build();
    }

    @GetMapping("/stats")
    public ApiResponse<LoTBStatDto> getStats() {

        return ApiResponse.<LoTBStatDto>builder()
                 .result(loThietBiService.getStatistics())
                .build();
    }
    @GetMapping("/export")
    public ApiResponse<byte[]> exportExcel() {
        try {

            List<LoThietBi> listData = loThietBiRepository.findAll();
            byte[] excelBytes = excelService.exportLoThietBiToExcel(listData);

            return ApiResponse.<byte[]>builder()
                    .result(excelBytes)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi xuất file Excel");
        }
    }

}