package com.thiet_thi.project_one.services;

import com.thiet_thi.project_one.dtos.ChiTietDeXuatMuaDto;
import com.thiet_thi.project_one.dtos.DeXuatMuaDto;
import com.thiet_thi.project_one.exceptions.DataNotFoundException;
import com.thiet_thi.project_one.iservices.IDeXuatMuaService;
import com.thiet_thi.project_one.models.*;
import com.thiet_thi.project_one.repositorys.*;
import com.thiet_thi.project_one.responses.DeXuatMuaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeXuatMuaService implements IDeXuatMuaService {

    private final DeXuatMuaRepository deXuatMuaRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final LoaiThietBiRepository loaiThietBiRepository;
    private final PhongRepository phongRepository;

    @Override
    public DeXuatMuaResponse create(DeXuatMuaDto dto) throws DataNotFoundException {
        NguoiDung nguoiTao = nguoiDungRepository.findById(dto.getMaND())
                .orElseThrow(() -> new DataNotFoundException("Người dùng không tồn tại"));
        Phong phongDuKien = null;
        if (dto.getMaPhong() != null && !dto.getMaPhong().isEmpty()) {
            phongDuKien = phongRepository.findById(dto.getMaPhong())
                    .orElseThrow(() -> new DataNotFoundException("Phòng không tồn tại"));
        }
        DeXuatMua deXuat = DeXuatMua.builder()
                .maDeXuat(dto.getMaDeXuat() != null ? dto.getMaDeXuat() : "DX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .tieuDe(dto.getTieuDe())
                .noiDung(dto.getNoiDung())
                .ngayTao(LocalDate.now())
                .trangThai("Chờ duyệt") // Mặc định
                .nguoiTao(nguoiTao)
                .phong(phongDuKien)
                .build();

        if (dto.getChiTiet() != null) {
            for (ChiTietDeXuatMuaDto ctDto : dto.getChiTiet()) {
                LoaiThietBi loai = loaiThietBiRepository.findById(ctDto.getMaLoai())
                        .orElseThrow(() -> new DataNotFoundException("Loại thiết bị không tồn tại: " + ctDto.getMaLoai()));

                ChiTietDeXuatMua ctEntity = ChiTietDeXuatMua.builder()
                        .maCTDX("CT-" + UUID.randomUUID().toString().substring(0, 8))
                        .deXuatMua(deXuat) // Link ngược lại cha
                        .loaiThietBi(loai)
                        .soLuong(ctDto.getSoLuong())
                        .donGia(ctDto.getDonGia())
                        .ghiChu(ctDto.getGhiChu())
                        .build();

                deXuat.getChiTietDeXuat().add(ctEntity);
            }
        }

        DeXuatMua saved = deXuatMuaRepository.save(deXuat);
        return DeXuatMuaResponse.from(saved);
    }

    @Override
    public List<DeXuatMuaResponse> getAll() {
        return deXuatMuaRepository.findAll().stream()
                .map(DeXuatMuaResponse::from)
                .toList();
    }

    @Override
    public DeXuatMuaResponse getById(String id) throws DataNotFoundException {
        return deXuatMuaRepository.findById(id)
                .map(DeXuatMuaResponse::from)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đề xuất"));
    }

    @Override
    public List<DeXuatMuaResponse> getMyProposals(String maNguoiDung) {

        return null;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public DeXuatMuaResponse approve(String maDeXuat, String maNguoiDuyet) throws DataNotFoundException {


        DeXuatMua deXuat = deXuatMuaRepository.findById(maDeXuat)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đề xuất: " + maDeXuat));


        NguoiDung approver = nguoiDungRepository.findById(maNguoiDuyet)
                .orElseThrow(() -> new DataNotFoundException("Người duyệt không hợp lệ: " + maNguoiDuyet));


        deXuat.setTrangThai("Đã duyệt");
        deXuat.setNguoiDuyet(approver);
        deXuat.setNgayDuyet(LocalDate.now());

        DeXuatMua saved = deXuatMuaRepository.save(deXuat);


        return DeXuatMuaResponse.from(saved);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public DeXuatMuaResponse reject(String maDeXuat, String maNguoiDuyet, String lyDo) throws DataNotFoundException {


        DeXuatMua deXuat = deXuatMuaRepository.findById(maDeXuat)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đề xuất: " + maDeXuat));


        NguoiDung approver = nguoiDungRepository.findById(maNguoiDuyet)
                .orElseThrow(() -> new DataNotFoundException("Người duyệt không hợp lệ: " + maNguoiDuyet));


        deXuat.setTrangThai("Từ chối");
        deXuat.setNguoiDuyet(approver);
        deXuat.setLyDo(lyDo);
        deXuat.setNgayDuyet(LocalDate.now());

        DeXuatMua saved = deXuatMuaRepository.save(deXuat);

        return DeXuatMuaResponse.from(saved);
    }


    @Override
    public Page<DeXuatMuaResponse> getAllPage(Pageable pageable) {
        Page<DeXuatMua> dxPage = deXuatMuaRepository.findAll(pageable);
        return dxPage.map(DeXuatMuaResponse::from);
    }

    @Override
    public Page<DeXuatMuaResponse> searchAndFilter(
            String search,
            String trangThai,
            String tieuDe,
            Pageable pageable) {

        Page<DeXuatMua> dxPage = deXuatMuaRepository.findByCriteria(
                search,
                trangThai,
                tieuDe,
                pageable
        );

        return dxPage.map(DeXuatMuaResponse::from);
    }
}