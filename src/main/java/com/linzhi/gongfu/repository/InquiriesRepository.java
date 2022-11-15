package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.Inquiries;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 询价单列表的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface InquiriesRepository
    extends CrudRepository<Inquiries, String>, QuerydslPredicateExecutor<Inquiries> {
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
        "left join sales_contract_base c on c.id = b.sales_contract_id\n" +
        "left join sales_contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from sales_contract_rev  where id = r.id)\n" +
        "left join sales_contract_rev d on d.id = b.contract_id  and d.revision in (select max(revision) from sales_contract_rev  where id = d.id)\n" +

        " where  b.created_by_comp=?1 and b.created_by=?2 and b.type=?3 and b.state=?4 order by b.created_at desc,cast(RIGHT(b.code,3) as int )  desc ",
        nativeQuery = true)
    List<Inquiries> listInquiries(String compId, String operator, String type, String state);

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
        "left join sales_contract_base c on c.id = b.sales_contract_id\n" +
        "left join sales_contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from sales_contract_rev  where id = r.id)\n" +
        "left join sales_contract_rev d on d.id = b.contract_id  and d.revision in (select max(revision) from sales_contract_rev  where id = d.id)\n" +

        " where   b.created_by_comp=?1 and b.type=?2 and b.state=?3 order by b.created_at desc,cast(RIGHT(b.code,3) as int )  desc ",
        nativeQuery = true)
    List<Inquiries> listInquiries(String compId, String type, String state);




}
