package com.thiet_thi.project_one.responses;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardResponse {


    private Long tongThietBi;
    private String tangTruongThietBi;

    private Long dangHoatDong;
    private String tyLeHoatDong;

    private Long canBaoTri;
    private String canBaoTriQuaHan;

    private BigDecimal giaTriTaiSan;
    private String ghiChuGiaTri;

    private List<Map<String, Object>> thietBiTheoDonVi;

    private List<Map<String, Object>> trangThaiThietBi;

    private List<HoatDongGanDay> hoatDongGanDay;

    @Getter @Setter @Builder
    public static class HoatDongGanDay {
        private String loai;
        private String tieuDe;
        private String noiDung;
        private String nguoiThucHien;
        private String thoiGian;
    }
}