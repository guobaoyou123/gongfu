package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.TemporaryPlan;
import com.linzhi.gongfu.entity.TemporaryPlanId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TemporaryPlanRepository extends CrudRepository<TemporaryPlan, TemporaryPlanId>, QuerydslPredicateExecutor<TemporaryPlan> {
    List<TemporaryPlan> findAllByTemporaryPlanId(TemporaryPlanId temporaryPlanId);
}
