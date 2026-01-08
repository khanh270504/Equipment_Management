package com.thiet_thi.project_one.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KiemKeDto {



    @JsonProperty("ma_kiem_ke")
    private String maKiemKe;

    @JsonProperty("ma_phong")
    private String maPhong;

    @JsonProperty("ma_nguoi_kiem_ke")
    private String maNguoiKiemKe;

    @JsonProperty("ghi_chu")
    private String ghiChu;
    @JsonProperty("ngay_kiem_ke")
    private LocalDate ngayKiemKe;

    @JsonProperty("chi_tiet")
    private List<ChiTietKiemKeDto> chiTiet = new ArrayList<>();

}