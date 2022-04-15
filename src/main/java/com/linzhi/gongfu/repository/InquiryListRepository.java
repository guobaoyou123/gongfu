package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.Inquiry;
import com.linzhi.gongfu.entity.InquiryList;
import com.linzhi.gongfu.enumeration.InquiryState;
import com.linzhi.gongfu.enumeration.InquiryType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;

public interface InquiryListRepository
    extends CrudRepository<InquiryList, String>, QuerydslPredicateExecutor<InquiryList> {
    List<InquiryList> findInquiriesByCreatedByCompAndCreatedByAndTypeAndStateOrderByCreatedAtDesc(String compId, String operator, InquiryType type, InquiryState state);

    List<InquiryList> findInquiryListByCreatedByCompAndTypeAndStateOrderByCreatedAtDesc(String compId,InquiryType type, InquiryState state);
    @Modifying
    @Query(value="update   inquiry_base  set total_price=?1 ,total_price_vat=?2,vat=?3 where  id=?4 ",
        nativeQuery = true)
    void  updateInquiry(BigDecimal totalPrice, BigDecimal totalPriceVat, BigDecimal vat, String id);
}
