package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.dto.TUnfinishedInquiry;
import com.linzhi.gongfu.entity.Inquiry;
import com.linzhi.gongfu.enumeration.InquiryState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 询价单详情的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface InquiryRepository extends CrudRepository<Inquiry, String>, QuerydslPredicateExecutor<Inquiry> {
    /**
     * 获取最大编码
     *
     * @param dcCompId  单位编码
     * @param createdBy 操作员编码
     * @return 最大编码
     */
    @Query(value = "select  right(('000'+cast((cast(max(right(code,3)) as int)+1) as varchar)),3) from inquiry_base  where  created_by_comp=?1 and created_by=?2\n" +
        "             and DateDiff(dd,created_at,GETDATE())=0 ",
        nativeQuery = true)
    String getMaxCode(String dcCompId, String createdBy);

    /**
     * 更新总金额
     *
     * @param totalPrice         总的未税金额
     * @param totalPriceVat      总的含税金额
     * @param vat                税额
     * @param discountTotalPrice 折扣后金额
     * @param id                 询价单主键
     */
    @Modifying
    @Query(value = "update   Inquiry i  set i.totalPrice=?1 ,i.totalPriceVat=?2,i.vat=?3 ,i.discountedTotalPrice=?4 where  i.id=?5 ")
    void updateInquiry(BigDecimal totalPrice, BigDecimal totalPriceVat, BigDecimal vat, BigDecimal discountTotalPrice, String id);

    /**
     * 更新询价单状态
     *
     * @param deletedAt    删除时间
     * @param inquiryState 状态
     * @param id           询价单主键
     */
    @Modifying
    @Query(value = "update   Inquiries i set i.deletedAt=?1,i.state=?2 where i.id=?3")
    void removeInquiry(LocalDateTime deletedAt, InquiryState inquiryState, String id);

    /**
     * 未完成的询价单列表
     *
     * @param companyCode  单位编码
     * @param operator     操作员编码
     * @param supplierCode 供应商编码
     * @return 未完成询价单主键
     */
    @Query(value = "select new com.linzhi.gongfu.dto.TUnfinishedInquiry(b.id,b.code,b.totalPriceVat,count (distinct r.productId)) from Inquiry b" +
        " left join InquiryRecord  r on r.inquiryRecordId.inquiryId=b.id " +
        " where b.createdByComp=?1 and b.createdBy=?2 and b.salerComp=?3 and b.state=com.linzhi.gongfu.enumeration.InquiryState.UN_FINISHED " +
        " group by  b.id,b.code,b.totalPriceVat")
    List<TUnfinishedInquiry> listUnfinishedInquiries(String companyCode, String operator, String supplierCode);

    @Transactional(readOnly = true)
    Optional<Inquiry> findById(String id);
}
