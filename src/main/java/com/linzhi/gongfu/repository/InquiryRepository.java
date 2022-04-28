package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.Inquiry;
import com.linzhi.gongfu.enumeration.InquiryState;
import com.linzhi.gongfu.enumeration.InquiryType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;

public interface InquiryRepository
    extends CrudRepository<Inquiry, String>, QuerydslPredicateExecutor<Inquiry> {
    @Query(value="select * from   inquiry_base  where  created_by_comp=?1 and created_by=?2 and type=?3 and state=?4 order by created_at desc,cast(RIGHT(code,3) as int )  desc ",
        nativeQuery = true)
    List<Inquiry> findInquiryList(String compId, String operator, String  type, String  state);
    @Query(value="select * from   inquiry_base  where  created_by_comp=?1 and type=?2 and state=?3 order by created_at desc,cast(RIGHT(code,3) as int )  desc ",
        nativeQuery = true)
    List<Inquiry> findInquiryList(String compId, String type, String  state);
    @Modifying
    @Query(value="update   inquiry_base  set total_price=?1 ,total_price_vat=?2,vat=?3 where  id=?4 ",
        nativeQuery = true)
    void  updateInquiry(BigDecimal totalPrice, BigDecimal totalPriceVat, BigDecimal vat, String id);
}
