package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Inquiry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface InquiryRepository extends CrudRepository<Inquiry,String>, QuerydslPredicateExecutor<Inquiry> {
    @Query(value="select  right(('000'+cast((cast(max(right(id,2)) as int)+1) as varchar)),2) from inquiry_base  where  comp_buyer=?1 and buyer_created_by=?2\n" +
        "             and DateDiff(dd,created_at,GETDATE())=0 ",
        nativeQuery = true)
    String findMaxCode(String dcCompId, String createdBy);

}
