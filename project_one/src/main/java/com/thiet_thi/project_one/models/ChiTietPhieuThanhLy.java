// 2. CHI_TIET_THANH_LY.java
package com.thiet_thi.project_one.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "chi_tiet_thanh_ly")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true)
public class ChiTietPhieuThanhLy {

    @Id

    @Column(name = "ma_cttl")
    private String maCTTL;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phieu_thanh_ly", nullable = false)
    private PhieuThanhLy phieuThanhLy;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tb", nullable = false)
    private ThietBi thietBi;


    @Column(name = "trang_thai", length = 20)
    private String trangThai;


    @Column(name = "nguyen_gia", precision = 18, scale = 2)
    private BigDecimal nguyenGia;

    @Column(name = "gia_tri_con_lai", precision = 18, scale = 2)
    private BigDecimal giaTriConLai;

    @Column(name = "so_nam_su_dung")
    private Integer soNamSuDung;

    @Column(name = "trang_thai_cu_thiet_bi_cu", length = 50)
    private String tinhTrangTBCu;


    @Column(name = "hinh_thuc_thanh_ly", length = 100, nullable = false)
    private String hinhThucThanhLy;

    @Column(name = "ly_do_thanh_ly", length = 500, nullable = false)
    private String lyDoThanhLy;

    @Column(name = "gia_tri_thu_ve", precision = 18, scale = 2)
    private BigDecimal giaTriThuVe = BigDecimal.ZERO;

    @Column(name = "ngay_thanh_ly")
    private LocalDate ngayThanhLy;

    @Column(name = "ghi_chu", length = 1000)
    private String ghiChu;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ng_duyet")
    private NguoiDung nguoiDuyet;
}