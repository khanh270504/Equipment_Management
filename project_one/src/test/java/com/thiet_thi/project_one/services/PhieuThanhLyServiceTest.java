package com.thiet_thi.project_one.services;


import com.thiet_thi.project_one.dtos.ChiTietThanhLyDto;
import com.thiet_thi.project_one.dtos.PhieuThanhLyDto;
import com.thiet_thi.project_one.exceptions.DataNotFoundException;
import com.thiet_thi.project_one.models.*;
import com.thiet_thi.project_one.repositorys.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PhieuThanhLyServiceTest {
    @Mock
    private PhieuThanhLyRepository phieuThanhLyRepository;
    @Mock
    private NguoiDungRepository nguoiDungRepository;
    @Mock
    private ThietBiRepository thietBiRepository;
    @Mock
    private ChiTietPhieuThanhLyRepository chiTietPhieuThanhLyRepository;
    @Mock
    private LichSuThietBiRepository lichSuThietBiRepository;

    @InjectMocks
    private PhieuThanhLyService phieuThanhLyService;

    private PhieuThanhLyDto dto;
    private NguoiDung mockUser;
    private ThietBi mockTB;

    @BeforeEach
    void setUp() {
        mockUser = NguoiDung.builder()
                .maND("USER001")
                .tenND("Nguyen Van A")
                .build();
        mockTB = ThietBi.builder()
                .maTB("TB001")
                .tenTB("Hadda")
                .tinhTrang("Hong")
                .giaTriBanDau(new BigDecimal("100"))
                .giaTriHienTai(new BigDecimal("20"))
                .build();
        ChiTietThanhLyDto ctDto = new ChiTietThanhLyDto();
        ctDto.setMaTB("TB001");
        ctDto.setGiaTriThuVe(new BigDecimal("20"));

        dto = new PhieuThanhLyDto();
        dto.setMaNguoiTao("USER001");
        dto.setHinhThuc("Bán thanh lý");
        dto.setLyDoThanhLy("Hư hỏng nặng");
        dto.setChiTiet(List.of(ctDto));
    }

    @Test
    void create_Success() throws DataNotFoundException {
        when(nguoiDungRepository.findById("USER001")).thenReturn(Optional.of(mockUser));
        when(thietBiRepository.findById("TB001")).thenReturn(Optional.of(mockTB));
        when(phieuThanhLyRepository.save(any(PhieuThanhLy.class))).thenAnswer(i -> i.getArguments()[0]);

        PhieuThanhLy result = phieuThanhLyService.create(dto);
        assertNotNull(result);
        assertEquals("Chờ duyệt", result.getTrangThai());
        assertEquals(new BigDecimal("20"), result.getTongGiaTriThuVe());
        assertEquals("Chờ thanh lý", mockTB.getTinhTrang());

        verify(lichSuThietBiRepository, atLeastOnce()).save(any(LichSuThietBi.class));
        verify(phieuThanhLyRepository, times(1)).save(any(PhieuThanhLy.class));
    }
    @Test
    void create_Fail_UserNotFound() {
        when(nguoiDungRepository.findById("USER001")).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> phieuThanhLyService.create(dto));
        verify(phieuThanhLyRepository, never()).save(any());
    }

    @Test
    void create_Fail_ThietBiNotFound() {
        when(nguoiDungRepository.findById("USER001")).thenReturn(Optional.of(mockUser));
        when(thietBiRepository.findById("TB001")).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> phieuThanhLyService.create(dto));
    }
}
