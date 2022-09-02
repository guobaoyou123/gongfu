package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.Inquiry;
import com.linzhi.gongfu.enumeration.InquiryState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InquiryRepository
    extends CrudRepository<Inquiry, String>, QuerydslPredicateExecutor<Inquiry> {
    /**
     * 询价单列表
     *
     * @param compId   单位编码
     * @param operator 操作员编码
     * @param type     类型
     * @param state    状态
     * @return 询价单列表
     */
    @Query(value = "select b.*,o.name as createdByName ,c.code as salesContractCode,r.order_code as salesOrderCode,d.order_code as orderCode  from   inquiry_base b\n" +
        "left join comp_operator o on b.created_by_comp = o.dc_comp_id and b.created_by = o.code\n" +
        "left join contract_base c on c.id = b.sales_contract_id\n" +
        "left join contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from contract_rev  where id = r.id)\n" +
        "left join contract_rev d on d.id = b.contract_id  and d.revision in (select max(revision) from contract_rev  where id = d.id)\n" +

        " where  b.created_by_comp=?1 and b.created_by=?2 and b.type=?3 and b.state=?4 order by b.created_at desc,cast(RIGHT(b.code,3) as int )  desc ",
        nativeQuery = true)
    List<Inquiry> listInquiries(String compId, String operator, String type, String state);

    /**
     * 询价单列表
     *
     * @param compId 单位编码
     * @param type   类型
     * @param state  状态
     * @return 询价单列表
     */
    @Query(value = "select  b.*,o.name as createdByName ,c.code as salesContractCode,r.order_code as salesOrderCode,d.order_code as orderCode from   inquiry_base b\n" +
        "left join comp_operator o on b.created_by_comp = o.dc_comp_id and b.created_by = o.code\n" +
        "left join contract_base c on c.id = b.sales_contract_id\n" +
        "left join contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from contract_rev  where id = r.id)\n" +
        "left join contract_rev d on d.id = b.contract_id  and d.revision in (select max(revision) from contract_rev  where id = d.id)\n" +

        " where   b.created_by_comp=?1 and b.type=?2 and b.state=?3 order by b.created_at desc,cast(RIGHT(b.code,3) as int )  desc ",
        nativeQuery = true)
    List<Inquiry> listInquiries(String compId, String type, String state);

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
    @Query(value = "update   inquiry_base  set total_price=?1 ,total_price_vat=?2,vat=?3 ,discount_total_price=?4 where  id=?5 ",
        nativeQuery = true)
    void updateInquiry(BigDecimal totalPrice, BigDecimal totalPriceVat, BigDecimal vat, BigDecimal discountTotalPrice, String id);

    /**
     * 询价单详情
     *
     * @param id 询价单主键
     * @return 询价单详情
     */
    @Query(value = "select b.*,o.name as createdByName ,c.code as salesContractCode,r.order_code as salesOrderCode,d.order_code as orderCode from   inquiry_base b\n" +
        "left join comp_operator o on b.created_by_comp = o.dc_comp_id and b.created_by = o.code\n" +
        "left join contract_base c on c.id = b.sales_contract_id\n" +
        "left join contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from contract_rev  where id = r.id)\n" +
        "left join contract_rev d on d.id = b.contract_id  and d.revision in (select max(revision) from contract_rev  where id = d.id)\n" +

        " where   b.id=?1 ",
        nativeQuery = true)
    Optional<Inquiry> findInquiryById(String id);

    /**
     * 更新询价单状态
     *
     * @param deletedAt    删除时间
     * @param inquiryState 状态
     * @param id           询价单主键
     */
    @Modifying
    @Query(value = "update   Inquiry i set i.deletedAt=?1,i.state=?2 where i.id=?3")
    void removeInquiry(LocalDateTime deletedAt, InquiryState inquiryState, String id);


}
