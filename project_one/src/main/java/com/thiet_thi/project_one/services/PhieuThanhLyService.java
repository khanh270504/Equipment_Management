// src/main/java/com/thiet_thi/project_one/services/impl/PhieuThanhLyService.java
package com.thiet_thi.project_one.services;

import com.thiet_thi.project_one.dtos.ChiTietThanhLyDto;
import com.thiet_thi.project_one.dtos.PhieuThanhLyDto;
import com.thiet_thi.project_one.exceptions.DataNotFoundException;
import com.thiet_thi.project_one.iservices.IThanhLyService;
import com.thiet_thi.project_one.models.*;
import com.thiet_thi.project_one.repositorys.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhieuThanhLyService implements IThanhLyService {

    private final PhieuThanhLyRepository phieuThanhLyRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ThietBiRepository thietBiRepository;
    private final ChiTietPhieuThanhLyRepository chiTietRepository;
    private final LichSuThietBiRepository lichSuThietBiRepository;

    private void ghiLichSuThietBi(ThietBi tb,
                                  String tinhTrangTBCu,
                                  String tinhTrangTBMoi,
                                  String hanhDong,
                                  String ghiChu,
                                  NguoiDung nguoiThucHien) {

        String tenPhong = tb.getPhong() != null ? tb.getPhong().getTenPhong() : null;
        String tenLoai = tb.getLoaiThietBi() != null ? tb.getLoaiThietBi().getTenLoai() : null;

        LichSuThietBi lichSu = LichSuThietBi.builder()
                .maLichSu("LS" + System.currentTimeMillis())
                .thietBi(tb)
                .trangThaiCu(tinhTrangTBCu)
                .trangThaiMoi(tinhTrangTBMoi)

                .phongCu(tenPhong)
                .phongMoi(tenPhong)
                .loaiCu(tenLoai)
                .loaiMoi(tenLoai)

                .hanhDong(hanhDong)
                .ghiChu(ghiChu)
                .ngayThayDoi(LocalDate.now())
                .nguoiThayDoi(nguoiThucHien)
                .build();
        lichSuThietBiRepository.save(lichSu);
    }

    @Override
    @Transactional
    public PhieuThanhLy create(PhieuThanhLyDto dto) throws DataNotFoundException {

        NguoiDung nguoiLap = nguoiDungRepository.findById(dto.getMaNguoiTao())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người lập phiếu: " + dto.getMaNguoiTao()));

        NguoiDung nguoiDuyet = dto.getMaNguoiDuyet() != null ?
                nguoiDungRepository.findById(dto.getMaNguoiDuyet())
                        .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người duyệt: " + dto.getMaNguoiDuyet()))
                : null;


        String maPhieu = generateMaPhieuThanhLy();
        String soPhieu = generateSoPhieu();


        PhieuThanhLy phieu = PhieuThanhLy.builder()
                .maPhieuThanhLy(maPhieu)
                .soPhieu(soPhieu)
                .ngayLap(dto.getNgayLap() != null ? dto.getNgayLap() : LocalDate.now())
                .hinhThuc(dto.getHinhThuc())
                .lyDoThanhLy(dto.getLyDoThanhLy())
                .ghiChu(dto.getGhiChu())
                .trangThai("Chờ duyệt")
                .nguoiLap(nguoiLap)
                .nguoiDuyet(nguoiDuyet)
                .tongGiaTriThuVe(BigDecimal.ZERO)
                .build();

        BigDecimal tongThuVe = BigDecimal.ZERO;

        for (ChiTietThanhLyDto ctDto : dto.getChiTiet()) {
            ThietBi tb = thietBiRepository.findById(ctDto.getMaTB())
                    .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thiết bị: " + ctDto.getMaTB()));

            String tinhTrangTBCu = tb.getTinhTrang();


            ghiLichSuThietBi(
                    tb,
                    tinhTrangTBCu,
                    "Chờ thanh lý",
                    "Tạo phiếu Thanh lý",
                    "Đề xuất thanh lý bởi phiếu " + maPhieu,
                    nguoiLap
            );

            String maCTTL = generateMaChiTiet(phieu.getMaPhieuThanhLy(), phieu.getChiTiet().size() + 1);

            ChiTietPhieuThanhLy chiTiet = ChiTietPhieuThanhLy.builder()
                    .maCTTL(maCTTL)
                    .phieuThanhLy(phieu)
                    .thietBi(tb)
                    .nguyenGia(tb.getGiaTriBanDau())
                    .giaTriConLai(tb.getGiaTriHienTai())
                    .soNamSuDung(ctDto.getSoNamSuDung())
                    .hinhThucThanhLy(ctDto.getHinhThucThanhLy())
                    .lyDoThanhLy(ctDto.getLyDoThanhLy())
                    .giaTriThuVe(ctDto.getGiaTriThuVe() != null ? ctDto.getGiaTriThuVe() : BigDecimal.ZERO)
                    .ngayThanhLy(ctDto.getNgayThanhLy())
                    .ghiChu(ctDto.getGhiChu())
                    .trangThai("Chờ duyệt")
                    .tinhTrangTBCu(tinhTrangTBCu)
                    .build();

            phieu.getChiTiet().add(chiTiet);
            tongThuVe = tongThuVe.add(chiTiet.getGiaTriThuVe());


            tb.setTinhTrang("Chờ thanh lý");
            thietBiRepository.save(tb);
        }

        phieu.setTongGiaTriThuVe(tongThuVe);

        PhieuThanhLy saved = phieuThanhLyRepository.save(phieu);
        return saved;
    }
    private String generateMaPhieuThanhLy() {
        int year = LocalDate.now().getYear();
        long count = phieuThanhLyRepository.count();
        return String.format("TL%d-%04d", year, count); // TL2025-0001
    }

    private String generateSoPhieu() {
        int year = LocalDate.now().getYear();
        long count = phieuThanhLyRepository.count();
        return String.format("TL/%d/%04d", year, count); // TL/2025/003
    }

    private String generateMaChiTiet(String maPhieu, int stt) {
        return maPhieu + "-CT" + String.format("%04d", stt); // TL2025-0003-CT001
    }

    @Override
    public List<PhieuThanhLy> getAll() {
        return phieuThanhLyRepository.findAll();
    }

    @Override
    public PhieuThanhLy getByID(String maTL) throws DataNotFoundException {
        return phieuThanhLyRepository.findById(maTL)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy phiếu thanh lý: " + maTL));
    }

    @Override
    @Transactional
    public void delete(String maPhieu) throws DataNotFoundException {
        PhieuThanhLy phieu = getByID(maPhieu);

        if (!"Chờ duyệt".equals(phieu.getTrangThai())) {
            throw new IllegalStateException("Chỉ được xóa phiếu đang ở trạng thái 'Chờ duyệt'!");
        }

        for (ChiTietPhieuThanhLy ct : phieu.getChiTiet()) {
            ThietBi tb = ct.getThietBi();
            tb.setTinhTrang("Đang sử dụng"); // hoặc trạng thái cũ
            thietBiRepository.save(tb);
        }

        chiTietRepository.deleteAll(phieu.getChiTiet());
        phieuThanhLyRepository.delete(phieu);
    }

    @Override
    @Transactional
    public PhieuThanhLy update(String maPhieu, PhieuThanhLyDto dto) throws DataNotFoundException {
        PhieuThanhLy phieu = getByID(maPhieu);

        if ("Hoàn tất".equals(phieu.getTrangThai()) || "Đã duyệt".equals(phieu.getTrangThai())) {
            throw new IllegalStateException("Không thể sửa phiếu đã duyệt!");
        }

        for (ChiTietPhieuThanhLy ct : phieu.getChiTiet()) {
            ThietBi tb = ct.getThietBi();
            tb.setTinhTrang("Đang sử dụng");
            thietBiRepository.save(tb);
        }

        chiTietRepository.deleteAll(phieu.getChiTiet());
        phieu.getChiTiet().clear();

        phieu.setSoPhieu(dto.getSoPhieu());
        phieu.setNgayLap(dto.getNgayLap());
        phieu.setNgayThanhLy(dto.getNgayThanhLy());
        phieu.setHinhThuc(dto.getHinhThuc());
        phieu.setLyDoThanhLy(dto.getLyDoThanhLy());
        phieu.setGhiChu(dto.getGhiChu());

        BigDecimal tongThuVe = BigDecimal.ZERO;

        for (ChiTietThanhLyDto ctDto : dto.getChiTiet()) {
            ThietBi tb = thietBiRepository.findById(ctDto.getMaTB())
                    .orElseThrow(() -> new DataNotFoundException("Thiết bị không tồn tại: " + ctDto.getMaTB()));

            if ("Đã thanh lý".equals(tb.getTinhTrang())) {
                throw new IllegalStateException("Thiết bị " + tb.getMaTB() + " đã thanh lý ở phiếu khác!");
            }

            ChiTietPhieuThanhLy chiTiet = ChiTietPhieuThanhLy.builder()
                    .maCTTL(ctDto.getMaCTTL())
                    .phieuThanhLy(phieu)
                    .thietBi(tb)
                    .nguyenGia(tb.getGiaTriBanDau())
                    .giaTriConLai(tb.getGiaTriHienTai())
                    .soNamSuDung(ctDto.getSoNamSuDung())
                    .hinhThucThanhLy(ctDto.getHinhThucThanhLy())
                    .lyDoThanhLy(ctDto.getLyDoThanhLy())
                    .giaTriThuVe(ctDto.getGiaTriThuVe() != null ? ctDto.getGiaTriThuVe() : BigDecimal.ZERO)
                    .ngayThanhLy(ctDto.getNgayThanhLy())
                    .ghiChu(ctDto.getGhiChu())
                    .build();

            phieu.getChiTiet().add(chiTiet);
            tongThuVe = tongThuVe.add(chiTiet.getGiaTriThuVe());

            tb.setTinhTrang("Đã thanh lý");
            thietBiRepository.save(tb);
        }

        phieu.setTongGiaTriThuVe(tongThuVe);
        return phieuThanhLyRepository.save(phieu);
    }


    @Override
    @Transactional
    public PhieuThanhLy duyetPhieu(String maPhieu, String maNguoiDuyet) throws DataNotFoundException {
        PhieuThanhLy phieu = getByID(maPhieu);

        if (!"Chờ duyệt".equals(phieu.getTrangThai())) {
            throw new IllegalStateException("Chỉ được duyệt phiếu đang ở trạng thái 'Chờ duyệt'!");
        }

        NguoiDung nguoiDuyet = nguoiDungRepository.findById(maNguoiDuyet)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người duyệt: " + maNguoiDuyet));

        phieu.setTrangThai("Hoàn tất");
        phieu.setNguoiDuyet(nguoiDuyet);
        phieu.setNgayDuyet(LocalDate.now());
        phieu.setNgayThanhLy(LocalDate.now());


        for (ChiTietPhieuThanhLy ct : phieu.getChiTiet()) {
            ThietBi tb = ct.getThietBi();
            String tinhTrangTBCu = tb.getTinhTrang();

            tb.setTinhTrang("Đã thanh lý");
            thietBiRepository.save(tb);
            ct.setTrangThai("Đã duyệt");
            ct.setNguoiDuyet(nguoiDuyet);
            ct.setNgayThanhLy(LocalDate.now());
            chiTietRepository.save(ct);


            ghiLichSuThietBi(
                    tb,
                    tinhTrangTBCu,
                    "Đã thanh lý",
                    "Duyệt Thanh lý",
                    "Hoàn tất thanh lý theo phiếu " + maPhieu,
                    nguoiDuyet
            );
        }

        return phieuThanhLyRepository.save(phieu);
    }

    @Override
    @Transactional
    public PhieuThanhLy tuChoiPhieu(String maPhieu, String maNguoiDuyet, String lyDoTuChoi) throws DataNotFoundException {
        PhieuThanhLy phieu = getByID(maPhieu);

        if (!"Chờ duyệt".equals(phieu.getTrangThai())) {
            throw new IllegalStateException("Chỉ được từ chối phiếu đang ở trạng thái 'Chờ duyệt'!");
        }

        NguoiDung nguoiDuyet = nguoiDungRepository.findById(maNguoiDuyet)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người duyệt: " + maNguoiDuyet));


        phieu.setTrangThai("Từ chối");
        phieu.setNguoiDuyet(nguoiDuyet);
        phieu.setNgayDuyet(LocalDate.now());
        phieu.setGhiChu((phieu.getGhiChu() != null ? phieu.getGhiChu() + "\n" : "")
                + "Từ chối bởi " + nguoiDuyet.getTenND() + ": " + lyDoTuChoi);


        for (ChiTietPhieuThanhLy ct : phieu.getChiTiet()) {
            ThietBi tb = ct.getThietBi();
            String tinhTrangTBCu = tb.getTinhTrang();
            String tinhTrangTBMoi = ct.getTinhTrangTBCu();


            tb.setTinhTrang(tinhTrangTBMoi);
            thietBiRepository.save(tb);
            ct.setTrangThai("Từ chối");
            ct.setNguoiDuyet(nguoiDuyet);
            ct.setNgayThanhLy(LocalDate.now());
            chiTietRepository.save(ct);


            ghiLichSuThietBi(
                    tb,
                    tinhTrangTBCu,
                    tinhTrangTBMoi,
                    "Từ chối Thanh lý",
                    "Từ chối phiếu thanh lý. Khôi phục trạng thái thiết bị.",
                    nguoiDuyet
            );
        }

        return phieuThanhLyRepository.save(phieu);
    }


}