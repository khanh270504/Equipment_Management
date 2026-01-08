package com.thiet_thi.project_one.controllers;

import com.thiet_thi.project_one.dtos.ApiResponse;
import com.thiet_thi.project_one.dtos.DeXuatMuaDto;
import com.thiet_thi.project_one.exceptions.DataNotFoundException;
import com.thiet_thi.project_one.iservices.IDeXuatMuaService;
import com.thiet_thi.project_one.models.DeXuatMua;
import com.thiet_thi.project_one.repositorys.DeXuatMuaRepository;
import com.thiet_thi.project_one.responses.DeXuatMuaResponse;
import com.thiet_thi.project_one.services.ExcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/de_xuat_mua")
@RequiredArgsConstructor
public class DeXuatMuaCotroller {
    private final IDeXuatMuaService deXuatMuaService;
    private final ExcelService excelService;
    private final DeXuatMuaRepository deXuatMuaRepository;

    @PostMapping
    public ResponseEntity<DeXuatMuaResponse> create(@Valid @RequestBody DeXuatMuaDto dto)
            throws DataNotFoundException {

        DeXuatMuaResponse response = deXuatMuaService.create(dto);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/list")
    public ResponseEntity<List<DeXuatMuaResponse>> getAll() {
        return ResponseEntity.ok(deXuatMuaService.getAll());
    }


    @GetMapping("/{ma}")
    public ResponseEntity<DeXuatMuaResponse> getByMa(@PathVariable String ma)
            throws DataNotFoundException {

        return ResponseEntity.ok(deXuatMuaService.getById(ma));
    }


    @PatchMapping("/{ma}/duyet")
    public ResponseEntity<DeXuatMuaResponse> duyet(
            @PathVariable String ma,
            @RequestParam String maNguoiDuyet) throws DataNotFoundException {
        DeXuatMuaResponse response = deXuatMuaService.approve(ma, maNguoiDuyet);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{ma}/tu_choi")
    public ResponseEntity<DeXuatMuaResponse> tuChoi(
            @PathVariable String ma,
            @RequestParam String maNguoiDuyet,
            @RequestParam String lyDo) throws DataNotFoundException {
        DeXuatMuaResponse response = deXuatMuaService.reject(ma, maNguoiDuyet, lyDo);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ApiResponse<Page<DeXuatMuaResponse>> getAllDeXuat(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,


            @RequestParam(required = false) String search,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String nguoiTao) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DeXuatMuaResponse> deXuatPage;


        if (search != null || trangThai != null || nguoiTao != null) {
            deXuatPage = deXuatMuaService.searchAndFilter(
                    search, trangThai, nguoiTao, pageable
            );
        } else {
            deXuatPage = deXuatMuaService.getAllPage(pageable);
        }
        return ApiResponse.<Page<DeXuatMuaResponse>>builder()
                .result(deXuatPage)
                .build();
    }
    @GetMapping("/export")
    public ApiResponse<byte[]> exportExcel() {
        try {
            List<DeXuatMua> listEntities = deXuatMuaRepository.findAll();

            List<DeXuatMuaResponse> listResponses = listEntities.stream()
                    .map(DeXuatMuaResponse::from)
                    .toList();
            byte[] excelBytes = excelService.exportDeXuatMuaToExcel(listResponses);

            return ApiResponse.<byte[]>builder()
                    .result(excelBytes)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi xuất Excel: " + e.getMessage());
        }
    }
}