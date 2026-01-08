package com.thiet_thi.project_one.repositorys;

import com.thiet_thi.project_one.dtos.LoTBStatDto;
import com.thiet_thi.project_one.models.LoThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoThietBiRepository extends JpaRepository<LoThietBi, String> {

    @Query("SELECT COALESCE(SUM(l.soLuong), 0) FROM LoThietBi l WHERE l.chiTietDeXuatMua.maCTDX = :maCTDX")
    Integer sumSoLuongDaNhap(@Param("maCTDX") String maCTDX);


    List<LoThietBi> findByChiTietDeXuatMua_DeXuatMua_MaDeXuat(String maDeXuat);


    List<LoThietBi> findByChiTietDeXuatMua_MaCTDX(String maCTDX);


    List<LoThietBi> findByTrangThai(Integer trangThai);


    List<LoThietBi> findAllByOrderByNgayNhapDesc();

    @Query("SELECT new com.thiet_thi.project_one.dtos.LoTBStatDto(" +
            "COUNT(l), " +
            "COALESCE(SUM(l.soLuong), 0), " +
            "COALESCE(SUM(l.donGia * l.soLuong), 0)) " +
            "FROM LoThietBi l")
    LoTBStatDto getStatistics();
}