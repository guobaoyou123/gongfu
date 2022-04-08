package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.InquiryList;
import com.linzhi.gongfu.entity.InquiryRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface InquiryRecordRepository
    extends CrudRepository<InquiryRecord, String>, QuerydslPredicateExecutor<InquiryRecord> {
    @Query(value="select  max(code)  from inquiry_record  where  inquiry_id=?1 ",
        nativeQuery = true)
    String findMaxCode(String inquiryId);
}
