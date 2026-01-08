package com.thiet_thi.project_one.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thiet_thi.project_one.models.ChiTietKiemKe;
import com.thiet_thi.project_one.models.KiemKe;
import com.thiet_thi.project_one.models.ThietBi;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KiemKeResponse {

    private String maKiemKe;
    private String maND;
    private String tenNguoiKiemKe;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ngayKiemKe;

    private String tenPhong;
    private String trangThai;
    private String ghiChu;
    private String maPhong;

    private int tongSoLuong;
    private int tonTai;
    private int hong;
    private int mat;
    private String tyLeDat;

    private List<ChiTietKiemKeResponse> chiTiet;


    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ChiTietKiemKeResponse {

        private String maCTKK;
        private String maTB;
        private String tenTB;
        private String tenLoai;
        private String tenPhong;


        private String tinhTrangHeThong;


        private String tinhTrangThucTe;


        private String ketQua;

        private String ghiChu;

        public static ChiTietKiemKeResponse fromChiTietKiemKe(ChiTietKiemKe ct) {
            ThietBi tb = ct.getThietBi();

            String ketQua = xacDinhKetQua(ct.getTinhTrangThucTe());
            return ChiTietKiemKeResponse.builder()
                    .maCTKK(ct.getMaCTKK())
                    .maTB(tb.getMaTB())
                    .tenTB(tb.getTenTB())

                    .tenLoai(tb.getLoaiThietBi() != null ? tb.getLoaiThietBi().getTenLoai() : null)
                    .tenPhong(tb.getPhong() != null ? tb.getPhong().getTenPhong() : null)

                    .tinhTrangHeThong(ct.getTinhTrangHeThong())

                    .tinhTrangThucTe(ct.getTinhTrangThucTe())
                    .ketQua(ketQua)
                    .ghiChu(ct.getGhiChu())
                    .build();
        }
    }


    public static KiemKeResponse fromKiemKe(KiemKe kk) {
        List<ChiTietKiemKeResponse> chiTietList = kk.getChiTiet().stream()
                .map(ChiTietKiemKeResponse::fromChiTietKiemKe)
                .toList();

        long checkedCount = chiTietList.size();
        int tonTai = (int) chiTietList.stream().filter(c -> "tồn tại".equals(c.getKetQua())).count();
        int hong = (int) chiTietList.stream().filter(c -> "hỏng".equals(c.getKetQua())).count();
        int mat = (int) chiTietList.stream().filter(c -> "mất".equals(c.getKetQua())).count();

        int tongSoLuongHienThi;

        if (checkedCount == 0 && kk.getPhong() != null && kk.getPhong().getThietBis() != null) {
            tongSoLuongHienThi = kk.getPhong().getThietBis().size();
        } else {

            tongSoLuongHienThi = (int) checkedCount;
        }

        double tyLe = tongSoLuongHienThi == 0 ? 0 : (double) tonTai / tongSoLuongHienThi * 100;

        return KiemKeResponse.builder()
                .maKiemKe(kk.getMaKiemKe())
                .maND(kk.getNguoiKiemKe().getMaND())
                .tenNguoiKiemKe(kk.getNguoiKiemKe().getTenND())
                .ngayKiemKe(kk.getNgayKiemKe())

                .maPhong(kk.getPhong().getMaPhong())
                .tenPhong(kk.getPhong().getTenPhong())
                .trangThai(kk.getTrangThai())
                .ghiChu(kk.getGhiChu())


                .tongSoLuong(tongSoLuongHienThi)
                .tonTai(tonTai)
                .hong(hong)
                .mat(mat)
                .tyLeDat(String.format("%.1f%%", tyLe))

                .chiTiet(chiTietList)
                .build();
    }


    private static String xacDinhKetQua(String tinhTrang) {
        if (tinhTrang == null || tinhTrang.trim().isEmpty()) return "mất";
        String tt = tinhTrang.toLowerCase();

        if (tt.contains("mất") || tt.contains("không thấy") || tt.contains("thất lạc")) return "mất";
        if (tt.contains("hỏng") || tt.contains("hư") || tt.contains("lỗi")) return "hỏng";

        return "tồn tại";
    }
}