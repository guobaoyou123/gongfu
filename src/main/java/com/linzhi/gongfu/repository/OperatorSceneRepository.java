package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.OperatorScene;
import com.linzhi.gongfu.entity.OperatorSceneId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OperatorSceneRepository extends CrudRepository<OperatorScene, OperatorSceneId>, QuerydslPredicateExecutor<OperatorScene> {


    void deleteByOperatorSceneId_DcCompIdAndOperatorSceneId_OperatorCode(String compId, String operatorCode);

    void deleteByOperatorSceneId_DcCompIdAndOperatorSceneId_OperatorCodeIn(String compId, List<String> operators);
}
