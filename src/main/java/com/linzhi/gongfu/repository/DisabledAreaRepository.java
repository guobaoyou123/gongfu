package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.DisabledArea;
import com.linzhi.gongfu.entity.DisabledAreaId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * 禁用区域的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface DisabledAreaRepository extends CrudRepository<DisabledArea, DisabledAreaId>, QuerydslPredicateExecutor<DisabledArea> {
    /**
     * 禁用区域列表
     *
     * @param dcCompId 本单位编码
     * @return 禁用区域列表
     */
    List<DisabledArea> findAllByDisabledAreaId_DcCompId(String dcCompId);
}
