package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Inquiry;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.InquiryType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface InquiryRepository extends CrudRepository<Inquiry,String>, QuerydslPredicateExecutor<Inquiry> {
    @Query(value="select  right(('000'+cast((cast(max(right(code,3)) as int)+1) as varchar)),3) from inquiry_base  where  created_by_comp=?1 and created_by=?2\n" +
        "             and DateDiff(dd,created_at,GETDATE())=0 ",
        nativeQuery = true)
    String findMaxCode(String dcCompId, String createdBy);

    @Modifying
    @Query("update Inquiry as a set a.totalPrice=?1 ,a.totalPriceVat=?2,a.vat=?3 where a.id = ?4")
    void  updateInquiry(BigDecimal totalPrice, BigDecimal totalPriceVat,BigDecimal vat,String id);

}
