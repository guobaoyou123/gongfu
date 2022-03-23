package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.DisabledArea;
import com.linzhi.gongfu.entity.DisabledAreaId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface DisabledAreaRepository extends CrudRepository<DisabledArea, DisabledAreaId>, QuerydslPredicateExecutor<DisabledArea> {
    List<DisabledArea> findAllByDisabledAreaId_DcCompId(String dcCompId);


}
