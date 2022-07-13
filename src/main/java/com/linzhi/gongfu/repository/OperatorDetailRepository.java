package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.OperatorDetail;
import com.linzhi.gongfu.entity.OperatorId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * 公司操作员Repository
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public interface OperatorDetailRepository extends CrudRepository<OperatorDetail, OperatorId>, QuerydslPredicateExecutor<OperatorDetail> {


    @Query(value="select  right(('000'+cast((cast(max(code) as int)+1) as varchar)),3) from comp_operator  where  dc_comp_id=?1 " ,
        nativeQuery = true)
    Optional<String> findMaxCode(String companyCode);

    @Modifying
    @Query(value = "update OperatorDetail o set o.password=?1 where o.identity=?2")
    void  updatePassword(String password,OperatorId operatorId);

}
