package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Scene;
import com.linzhi.gongfu.enumeration.CompanyRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface SceneRepository extends CrudRepository<Scene, String>, QuerydslPredicateExecutor<String> {
    /**
     * 场景列表
     *
     * @param operatorCode 操作员编码
     * @param compId       单位编码
     * @return 场景列表
     */
    @Query(value = "select distinct d.* from dc_scene d,dc_operator_scene o\n" +
        "where o.scene_code=d.code and o.operator_code=?1 and o.dc_comp_id=?2 ", nativeQuery = true)
    Set<Scene> findScene(String operatorCode, String compId);

    /**
     * 场景列表
     *
     * @param companyRoles 公司角色
     * @return 场景列表
     */
    Set<Scene> findSceneByRoleIn(List<CompanyRole> companyRoles);
}
