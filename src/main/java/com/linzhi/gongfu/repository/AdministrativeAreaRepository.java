package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.AdministrativeArea;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


public interface AdministrativeAreaRepository extends CrudRepository<AdministrativeArea,String>, QuerydslPredicateExecutor<AdministrativeArea> {


}
