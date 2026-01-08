package com.thiet_thi.project_one.iservices;

import com.thiet_thi.project_one.dtos.KiemKeDto;
import com.thiet_thi.project_one.exceptions.DataNotFoundException;
import com.thiet_thi.project_one.models.KiemKe;
import com.thiet_thi.project_one.responses.KiemKeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IKiemKeService {

    KiemKe createNewSession(KiemKeDto dto) throws DataNotFoundException;

    KiemKe submitChecklist(KiemKeDto dto) throws DataNotFoundException;


    KiemKe getReportById(String maKiemKe) throws DataNotFoundException;

    KiemKe getById(String maKiemKe) throws DataNotFoundException;
    Page<KiemKeResponse> filterKiemKeSessions(String keyword, String maPhong, String trangThai, Pageable pageable);
}