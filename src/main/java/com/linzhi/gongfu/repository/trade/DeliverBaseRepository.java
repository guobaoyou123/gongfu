package com.linzhi.gongfu.repository.trade;

import com.linzhi.gongfu.entity.DeliverBase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface DeliverBaseRepository extends CrudRepository<DeliverBase, String>, QuerydslPredicateExecutor<DeliverBase> {

    @Query(value = "select  right(('000'+cast((cast(max(right(code,2)) as int)+1) as varchar)),2) from deliver_base  where  created_by_comp=?1 and created_by=?2\n" +
        "             and DateDiff(dd,created_at,GETDATE())=0 ",
        nativeQuery = true)
    String findMaxCode(String company, String operator);
}
