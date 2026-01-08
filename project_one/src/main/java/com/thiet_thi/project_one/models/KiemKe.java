package com.thiet_thi.project_one.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <-- Import quan trọng
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "kiem_ke")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KiemKe {

    @Id
    @Column(name = "ma_kiem_ke")
    private String maKiemKe;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nd", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "matKhau", "vaiTro", "donVi", "lichSuThietBis", "kiemKes"})
    private NguoiDung nguoiKiemKe;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "thietBis", "cacPhienKiemKe", "donVi"})
    private Phong phong;

    @Column(name = "ngay_kiem_ke", nullable = false)
    private LocalDate ngayKiemKe;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai", length = 50, nullable = false)
    private String trangThai;

    @Column(name = "ghi_chu", length = 200)
    private String ghiChu;

    @OneToMany(mappedBy = "kiemKe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private Set<ChiTietKiemKe> chiTiet = new HashSet<>();
}