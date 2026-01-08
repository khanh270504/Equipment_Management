package com.thiet_thi.project_one.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thiet_thi.project_one.models.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class LoThietBiResponse {

    private String maLo;
    private String tenLo;
    private Integer soLuong;


    private BigDecimal donGia;
    private BigDecimal tongTien;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ngayNhap;

    private String soHoaDon;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ngayHoaDon;

    private Integer trangThai;

    private String ghiChu;

    private String maDeXuat;
    private String tieuDeDeXuat;


    private String maLoai;
    private String tenLoai;

    private String maNhaCungCap;
    private String tenNhaCungCap;

    public static LoThietBiResponse from(LoThietBi lo) {

        ChiTietDeXuatMua ct = lo.getChiTietDeXuatMua();
        DeXuatMua dx = ct != null ? ct.getDeXuatMua() : null;
        LoaiThietBi loai = lo.getLoaiThietBi();
        NhaCungCap ncc = lo.getNhaCungCap();


        BigDecimal total = BigDecimal.ZERO;
        if (lo.getDonGia() != null && lo.getSoLuong() != null) {
            total = lo.getDonGia().multiply(BigDecimal.valueOf(lo.getSoLuong()));
        }

        return LoThietBiResponse.builder()
                .maLo(lo.getMaLo())
                .tenLo(lo.getTenLo())
                .soLuong(lo.getSoLuong())
                .donGia(lo.getDonGia())
                .tongTien(total)
                .ngayNhap(lo.getNgayNhap())
                .soHoaDon(lo.getSoHoaDon())
                .ngayHoaDon(lo.getNgayHoaDon())
                .trangThai(lo.getTrangThai())
                .ghiChu(lo.getGhiChu())

                .maDeXuat(dx != null ? dx.getMaDeXuat() : null)
                .tieuDeDeXuat(dx != null ? dx.getTieuDe() : null)

                .maLoai(loai != null ? loai.getMaLoai() : null)
                .tenLoai(loai != null ? loai.getTenLoai() : null)

                .maNhaCungCap(ncc != null ? ncc.getMaNhaCungCap() : null)
                .tenNhaCungCap(ncc != null ? ncc.getTen() : "N/A")
                .build();
    }
}