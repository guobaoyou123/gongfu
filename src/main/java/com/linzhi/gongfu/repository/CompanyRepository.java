package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Company;

import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 公司信息Repository
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public interface CompanyRepository extends CrudRepository<Company, String>, QuerydslPredicateExecutor<Company> {



    @Query(value = " SELECT  cast(max(b.encode) as int)+1 code  FROM    comp_base b,comp_trade t  where b.role=?1 and t.comp_saler=b.code and t.comp_buyer=?2  "
       ,nativeQuery = true)
   String findMaxCode(String roler,String dcCompId);


    @Modifying
    @Query("update Company as a set a.state=?1 where a.code in ?2")
    void  updateCompanyState(Availability availability, List<String> code);
}
