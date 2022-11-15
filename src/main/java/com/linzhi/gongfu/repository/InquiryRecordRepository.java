package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.InquiryRecord;
import com.linzhi.gongfu.entity.InquiryRecordId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
     * 查找询价单孪生明细
     *
     * @param inquiryId 询价单主键
     * @return 询价单明细列表
     */
    @Query(value = "select d.*  from \n" +
        "(select  product_id,count(quantity) as quantity  " +
        " from inquiry_record  where  inquiry_id = ?1   group by product_id ) \n" +
        "as d \n" +
        "  order by d.product_id ",
        nativeQuery = true)
    List<Map<String,Object>> findInquiryRecordTwins(String inquiryId);


}
