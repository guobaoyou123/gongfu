package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.DisabledArea;
import com.linzhi.gongfu.entity.DisabledAreaId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DisabledAreaRepository extends CrudRepository<DisabledArea, DisabledAreaId>, QuerydslPredicateExecutor<DisabledArea> {
    /**
     * 禁用区域列表
     *
     * @param dcCompId 本单位编码
     * @return 禁用区域列表
     */
    List<DisabledArea> findAllByDisabledAreaId_DcCompId(String dcCompId);
}
