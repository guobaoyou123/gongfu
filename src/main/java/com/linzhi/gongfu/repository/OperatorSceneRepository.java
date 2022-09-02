package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.OperatorScene;
import com.linzhi.gongfu.entity.OperatorSceneId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OperatorSceneRepository extends CrudRepository<OperatorScene, OperatorSceneId>, QuerydslPredicateExecutor<OperatorScene> {

    /**
     * 删除操作员场景
     *
     * @param compId    单位编码
     * @param operators 操作员编码列表
     */
    void deleteByOperatorSceneId_DcCompIdAndOperatorSceneId_OperatorCodeIn(String compId, List<String> operators);
}
