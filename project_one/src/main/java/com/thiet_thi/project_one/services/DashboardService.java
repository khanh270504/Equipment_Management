
package com.thiet_thi.project_one.services;

import com.thiet_thi.project_one.models.*;
import com.thiet_thi.project_one.repositorys.*;
import com.thiet_thi.project_one.responses.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ThietBiRepository thietBiRepo;
    private final DonViRepository donViRepo;
    private final LoThietBiRepository loThietBiRepo;
    private final KiemKeRepository kiemKeRepo;
    private final PhieuThanhLyRepository thanhLyRepo;
    private final DeXuatMuaRepository deXuatMuaRepo;

    public DashboardResponse getDashboardData() {
        List<ThietBi> all = thietBiRepo.findAll();
        long tong = all.size();

        long dangSuDung = all.stream()
                .filter(tb -> "Đang sử dụng".equalsIgnoreCase(tb.getTinhTrang()))
                .count();

        long canBaoTri = all.stream()
                .filter(tb -> tb.getTinhTrang() != null &&
                        (tb.getTinhTrang().toLowerCase().contains("bảo trì") ||
                                tb.getTinhTrang().toLowerCase().contains("hỏng") ||
                                tb.getTinhTrang().toLowerCase().contains("hư")))
                .count();

        BigDecimal giaTriConLai = all.stream()
                .filter(tb -> tb.getTinhTrang() == null || !"Đã thanh lý".equals(tb.getTinhTrang()))
                .map(tb -> {
                    if (tb.getGiaTriHienTai() != null) {
                        return tb.getGiaTriHienTai();
                    } else if (tb.getGiaTriBanDau() != null) {
                        return tb.getGiaTriBanDau();
                    } else {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> theoDonVi = donViRepo.findAll().stream()
                .map(dv -> Map.<String, Object>of(
                        "donVi", dv.getTenDonVi(),
                        "soLuong", thietBiRepo.countByPhong_DonVi_MaDonVi(dv.getMaDonVi())

                ))
                .collect(Collectors.toList());

        Map<String, Long> mapTrangThai = all.stream()
                .collect(Collectors.groupingBy(
                        tb -> tb.getTinhTrang() != null ? tb.getTinhTrang() : "Chưa xác định",
                        Collectors.counting()
                ));

        List<Map<String, Object>> trangThaiList = mapTrangThai.entrySet().stream()
                .map(e -> {
                    long count = e.getValue();
                    double tyLe = tong > 0 ? (double) count / tong * 100 : 0;
                    return Map.<String, Object>of(
                            "trangThai", e.getKey(),
                            "soLuong", count,
                            "tyLe", Math.round(tyLe)
                    );
                })
                .collect(Collectors.toList());

        List<DashboardResponse.HoatDongGanDay> activities = new ArrayList<>();

        return DashboardResponse.builder()
                .tongThietBi(tong)
                .tangTruongThietBi(tong > 0 ? "+12.5% tháng trước" : "Chưa có dữ liệu")
                .dangHoatDong(dangSuDung)
                .tyLeHoatDong(tong > 0 ? Math.round((double) dangSuDung / tong * 100) + "% tổng số" : "0%")
                .canBaoTri(canBaoTri)
                .canBaoTriQuaHan("0 quá hạn")
                .giaTriTaiSan(giaTriConLai)
                .ghiChuGiaTri("Sau khấu hao")
                .thietBiTheoDonVi(theoDonVi)
                .trangThaiThietBi(trangThaiList)
                .hoatDongGanDay(activities.stream().limit(5).toList())
                .build();
    }
}