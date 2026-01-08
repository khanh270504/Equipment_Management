package com.thiet_thi.project_one.services;

import com.thiet_thi.project_one.dtos.KiemKeDto;
import com.thiet_thi.project_one.dtos.ChiTietKiemKeDto;
import com.thiet_thi.project_one.exceptions.DataNotFoundException;
import com.thiet_thi.project_one.iservices.IKiemKeService;
import com.thiet_thi.project_one.models.*;
import com.thiet_thi.project_one.repositorys.*;
import com.thiet_thi.project_one.responses.KiemKeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KiemKeService implements IKiemKeService {

    private final KiemKeRepository kiemKeRepository;
    private final ChiTietKiemKeRepository chiTietKiemKeRepository;
    private final ThietBiRepository thietBiRepository;
    private final LichSuThietBiRepository lichSuThietBiRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final PhongRepository phongRepository;

    public NguoiDung getCurrentUser() throws DataNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Người dùng chưa đăng nhập!");
        }
        String maND = null;
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            Map<String, Object> attributes = jwtToken.getTokenAttributes();
            maND = (String) attributes.get("maND");
        }
        if (maND == null) throw new DataNotFoundException("Token không hợp lệ.");

        return nguoiDungRepository.findById(maND)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy user."));
    }

    @Transactional
    public void createHistoryLog(ThietBi tb, String hanhDong, String ghiChu,
                                 String ttCu, String ttMoi, NguoiDung nguoiThucHien) {
        LichSuThietBi ls = LichSuThietBi.builder()
                .maLichSu("LS" + UUID.randomUUID())
                .thietBi(tb)
                .hanhDong(hanhDong)
                .ghiChu(ghiChu)
                .phongCu(tb.getPhong() != null ? tb.getPhong().getTenPhong() : null)
                .phongMoi(tb.getPhong() != null ? tb.getPhong().getTenPhong() : null)
                .trangThaiCu(ttCu)
                .trangThaiMoi(ttMoi)
                .ngayThayDoi(LocalDate.now())
                .nguoiThayDoi(nguoiThucHien)
                .build();
        lichSuThietBiRepository.save(ls);
    }

    @Override
    @Transactional
    public KiemKe createNewSession(KiemKeDto dto) throws DataNotFoundException {
        NguoiDung nguoiTao = nguoiDungRepository.findById(dto.getMaNguoiKiemKe())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người kiểm kê: " + dto.getMaNguoiKiemKe()));

        Phong phong = phongRepository.findById(dto.getMaPhong())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy phòng: " + dto.getMaPhong()));

        String maKiemKe = "KK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        KiemKe kiemKe = KiemKe.builder()
                .maKiemKe(maKiemKe)
                .nguoiKiemKe(nguoiTao)
                .phong(phong)
                .ngayKiemKe(dto.getNgayKiemKe() != null ? dto.getNgayKiemKe() : LocalDate.now())
                .trangThai("Mới tạo")
                .ghiChu(dto.getGhiChu())
                .build();

        KiemKe savedKiemKe = kiemKeRepository.save(kiemKe);

        if (savedKiemKe.getPhong() != null) savedKiemKe.getPhong().getTenPhong();
        if (savedKiemKe.getNguoiKiemKe() != null) savedKiemKe.getNguoiKiemKe().getTenND();

        return savedKiemKe;
    }

    @Override
    @Transactional
    public KiemKe submitChecklist(KiemKeDto dto) throws DataNotFoundException {
        KiemKe kiemKe = kiemKeRepository.findById(dto.getMaKiemKe())
                .orElseThrow(() -> new DataNotFoundException("Phiếu không tồn tại"));

        NguoiDung nguoiThucHien = nguoiDungRepository.findById(dto.getMaNguoiKiemKe())
                .orElseThrow(() -> new DataNotFoundException("Người thực hiện không tồn tại"));

        chiTietKiemKeRepository.deleteAll(kiemKe.getChiTiet());
        kiemKe.getChiTiet().clear();

        for (ChiTietKiemKeDto ctDto : dto.getChiTiet()) {
            ThietBi tb = thietBiRepository.findById(ctDto.getMaTB())
                    .orElseThrow(() -> new DataNotFoundException("Thiết bị k tồn tại: " + ctDto.getMaTB()));

            String trangThaiCu = tb.getTinhTrang();
            String trangThaiMoi = trangThaiCu;
            String ketQuaKiemKe = ctDto.getTinhTrangThucTe();


            if ("Hỏng".equalsIgnoreCase(ketQuaKiemKe)) {
                trangThaiMoi = "Hỏng hóc";
            } else if ("Mất".equalsIgnoreCase(ketQuaKiemKe)) {
                trangThaiMoi = "Chờ thanh lý";
            } else if ("Tốt".equalsIgnoreCase(ketQuaKiemKe)) {

                if ("Hỏng hóc".equals(trangThaiCu) || "Chờ thanh lý".equals(trangThaiCu) || "Bảo trì".equals(trangThaiCu)) {
                    trangThaiMoi = "Đang sử dụng";
                }
            }


            if (!trangThaiCu.equals(trangThaiMoi)) {
                tb.setTinhTrang(trangThaiMoi);
                thietBiRepository.save(tb);
                createHistoryLog(tb, "Kiểm kê",
                        String.format("Kết quả: %s. Ghi chú: %s", ketQuaKiemKe, ctDto.getGhiChu()),
                        trangThaiCu, trangThaiMoi, nguoiThucHien);
            }


            ChiTietKiemKe chiTiet = ChiTietKiemKe.builder()
                    .maCTKK(UUID.randomUUID().toString())
                    .kiemKe(kiemKe)
                    .thietBi(tb)
                    .tinhTrangHeThong(trangThaiCu)
                    .tinhTrangThucTe(ketQuaKiemKe)
                    .ghiChu(ctDto.getGhiChu())
                    .build();

            kiemKe.getChiTiet().add(chiTiet);
        }


        long tongThietBiTrongPhong = thietBiRepository.countByPhong(kiemKe.getPhong());


        int soLuongDaKiem = kiemKe.getChiTiet().size();


        if (soLuongDaKiem >= tongThietBiTrongPhong && tongThietBiTrongPhong > 0) {

            kiemKe.setTrangThai("Hoàn thành");
            kiemKe.setNgayKetThuc(LocalDate.now());
        } else {

            kiemKe.setTrangThai("Đang kiểm kê");
            kiemKe.setNgayKetThuc(null);
        }

        return kiemKeRepository.save(kiemKe);
    }

    @Override
    @Transactional(readOnly = true)
    public KiemKe getReportById(String maKiemKe) throws DataNotFoundException {
        KiemKe kiemKe = kiemKeRepository.findById(maKiemKe)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy phiếu"));

        Hibernate.initialize(kiemKe.getChiTiet());
        if(kiemKe.getPhong() != null) kiemKe.getPhong().getTenPhong();
        if(kiemKe.getNguoiKiemKe() != null) kiemKe.getNguoiKiemKe().getTenND();

        return kiemKe;
    }

    @Override
    public KiemKe getById(String maKiemKe) throws DataNotFoundException {
        return getReportById(maKiemKe);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<KiemKeResponse> filterKiemKeSessions(String keyword, String maPhong, String trangThai, Pageable pageable) {

        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanMaPhong = (maPhong != null && !maPhong.trim().isEmpty()) ? maPhong.trim() : null;
        String cleanTrangThai = (trangThai != null && !trangThai.trim().isEmpty()) ? trangThai.trim() : null;

        Page<KiemKe> pageResult = kiemKeRepository.findSessionsWithFilter(cleanKeyword, cleanMaPhong, cleanTrangThai, pageable);

        return pageResult.map(KiemKeResponse::fromKiemKe);
    }
}