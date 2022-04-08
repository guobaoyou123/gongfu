package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.Inquiry;
import com.linzhi.gongfu.entity.InquiryList;
import com.linzhi.gongfu.enumeration.InquiryState;
import com.linzhi.gongfu.enumeration.InquiryType;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InquiryListRepository
    extends CrudRepository<InquiryList, String>, QuerydslPredicateExecutor<InquiryList> {
    List<InquiryList> findInquiriesByCreatedByCompAndCreatedByAndTypeAndStateOrderByCreatedAtDesc(String compId, String operator, InquiryType type, InquiryState state);

    List<InquiryList> findInquiryListByCreatedByCompAndTypeAndStateOrderByCreatedAtDesc(String compId,InquiryType type, InquiryState state);
}
