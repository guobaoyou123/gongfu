package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.InquiryList;
import com.linzhi.gongfu.entity.InquiryRecord;
import com.linzhi.gongfu.entity.InquiryRecordId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InquiryRecordRepository
    extends CrudRepository<InquiryRecord, InquiryRecordId>, QuerydslPredicateExecutor<InquiryRecord> {
    @Query(value="select  max(code)  from inquiry_record  where  inquiry_id=?1 ",
        nativeQuery = true)
    String findMaxCode(String inquiryId);


    @Modifying
    @Query("delete from InquiryRecord as c  where c.inquiryRecordId.inquiryId=?1 ")
    void  deleteProduct(String inquiryId);

    @Modifying
    @Query("delete from InquiryRecord as c  where c.inquiryRecordId.inquiryId=?1 and c.inquiryRecordId.code in ?2")
    void  deleteProducts(String inquiryId, List<Integer> codes);
}
