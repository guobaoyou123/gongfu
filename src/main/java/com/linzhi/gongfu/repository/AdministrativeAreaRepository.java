package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.AdministrativeArea;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AdministrativeAreaRepository extends CrudRepository<AdministrativeArea,String>, QuerydslPredicateExecutor<AdministrativeArea> {


}
