package com.thiet_thi.project_one.repositorys;

import com.thiet_thi.project_one.models.DeXuatMua;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeXuatMuaRepository extends JpaRepository<DeXuatMua, String> {

    @Query("SELECT dx FROM DeXuatMua dx WHERE " +

            "(:search IS NULL OR lower(dx.maDeXuat) LIKE lower(concat('%', :search, '%')) OR " +
            "lower(dx.tieuDe) LIKE lower(concat('%', :search, '%')) OR " +
            "lower(dx.noiDung) LIKE lower(concat('%', :search, '%'))) AND " +
            "(:trangThai IS NULL OR dx.trangThai = :trangThai) AND " +
            "(:maNguoiTao IS NULL OR dx.nguoiTao.maND = :maNguoiTao)")
    Page<DeXuatMua> findByCriteria(
            @Param("search") String search,
            @Param("trangThai") String trangThai,
            @Param("maNguoiTao") String maNguoiTao,
            Pageable pageable);
}

