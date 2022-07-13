package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.OperatorScene;
import com.linzhi.gongfu.entity.OperatorSceneId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface OperatorSceneRepository extends CrudRepository<OperatorScene, OperatorSceneId>, QuerydslPredicateExecutor<OperatorScene> {



    void deleteByOperatorSceneId_DcCompIdAndOperatorSceneId_OperatorCode(String compId,String operatorCode);
}
