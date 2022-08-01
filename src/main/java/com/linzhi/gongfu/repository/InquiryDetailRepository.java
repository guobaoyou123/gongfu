package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.InquiryDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface InquiryDetailRepository extends CrudRepository<InquiryDetail,String>, QuerydslPredicateExecutor<InquiryDetail> {
    @Query(value="select  right(('000'+cast((cast(max(right(code,3)) as int)+1) as varchar)),3) from inquiry_base  where  created_by_comp=?1 and created_by=?2\n" +
        "             and DateDiff(dd,created_at,GETDATE())=0 ",
        nativeQuery = true)
    String getMaxCode(String dcCompId, String createdBy);




}
