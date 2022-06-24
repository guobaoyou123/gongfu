package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.UnfinishedInquiry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UnfinishedInquiryRepository
    extends CrudRepository<UnfinishedInquiry, String>, QuerydslPredicateExecutor<UnfinishedInquiry> {
    @Query(value = "select   b.id as id,b.code as code,b.total_price_vat as totalPriceVat,count(distinct r.product_id ) as counts  from inquiry_base b \n" +
        "left join inquiry_record r on r.inquiry_id=b.id\n" +
        "where b.created_by_comp=?1 and b.created_by =?2 and b.saler_comp=?3 and b.state='0' \n" +
        "group by b.id,b.code ,b.total_price_vat",nativeQuery = true)
    List<UnfinishedInquiry> findUnfinishedInquiry(String companyCode, String operator, String supplierCode);
}
