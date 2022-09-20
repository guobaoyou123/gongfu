package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.InquiryRecord;
import com.linzhi.gongfu.entity.InquiryRecordId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * 询价单的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface InquiryRecordRepository
    extends CrudRepository<InquiryRecord, InquiryRecordId>, QuerydslPredicateExecutor<InquiryRecord> {
    /**
     * 最大编码
     *
     * @param inquiryId 询价单主键
     * @return 编码
     */
    @Query(value = "select  max(code)  from inquiry_record  where  inquiry_id=?1 ",
        nativeQuery = true)
    String findMaxCode(String inquiryId);

    /**
     * 删除询价单明细
     *
     * @param inquiryId 询价单主键
     */
    @Modifying
    @Query("delete from InquiryRecord as c  where c.inquiryRecordId.inquiryId=?1 ")
    void deleteProduct(String inquiryId);

    /**
     * 根据明细编码删除询价单明细
     *
     * @param inquiryId 询价单主键
     * @param codes     明细编码列表
     */
    @Modifying
    @Query("delete from InquiryRecord as c  where c.inquiryRecordId.inquiryId=?1 and c.inquiryRecordId.code in ?2")
    void removeProducts(String inquiryId, List<Integer> codes);

    /**
     * 根据询价单编码删除所有的询价单明细
     *
     * @param inquiryId 询价单主键
     */
    @Modifying
    @Query("delete from InquiryRecord as c  where c.inquiryRecordId.inquiryId=?1")
    void deleteProducts(String inquiryId);

    /**
     * 查找询价单明细
     *
     * @param inquiryId 询价单主键
     * @return 询价单明细列表
     */
    @Query(value = "select  *  from inquiry_record  where  inquiry_id=?1  order by product_id ,quantity  ",
        nativeQuery = true)
    List<InquiryRecord> findInquiryRecord(String inquiryId);

    /**
     * 查找询价单孪生明细
     *
     * @param inquiryId 询价单主键
     * @return 询价单明细列表
     */
    @Query(value = "select *  from \n" +
        "(select  product_id,count(quantity) as quantity ,max(charge_unit) as charge_unit,max(vat_rate) as vat_rate   from inquiry_record  where  inquiry_id=?1   group by product_id ) \n" +
        "as d \n" +
        "  order by product_id ",
        nativeQuery = true)
    List<InquiryRecord> findInquiryRecordTwins(String inquiryId);
}
