package com.linzhi.gongfu.repository.trade;


import com.linzhi.gongfu.entity.SalesContractRevision;
import com.linzhi.gongfu.entity.SalesContractRevisionId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Map;
import java.util.Optional;

/**
 * 销售合同版本的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface SalesContractRevisionRepository
    extends CrudRepository<SalesContractRevision, SalesContractRevisionId>, QuerydslPredicateExecutor<SalesContractRevision> {


    /**
     * 获取最大版本号
     *
     * @param id 合同主键
     * @return 版本号
     */
    @Query(value = "select max(c.salesContractRevisionId.revision)   from SalesContractRevision as c where c.salesContractRevisionId.id=?1")
    Optional<String> getMaxRevision(String id);

    /**
     * 查找有相同单位合同号的合同数量
     *
     * @param dcCompId  本单位编码
     * @param orderCode 单位合同号
     * @return 合同数量
     */
    @Query(value = "select  count(distinct c.id) " +
        "from sales_contract_base c ,sales_contract_rev  r  " +
        "where  c.created_by_comp=?1 and c.id = r.id and r.order_code=?2  " +
        "  and r.revision = (select max(revision) from contract_rev re where re.id=r.id) " +
        "  and  c.state ='1' ",
        nativeQuery = true)
    int findByOrderCode(String dcCompId, String orderCode);

    /**
     * 查找有相同单位合同号的合同数量
     *
     * @param dcCompId   本单位编码
     * @param orderCode  单位合同号
     * @param contractId 合同主键
     * @return
     */
    @Query(value = "select  count(distinct c.id) from sales_contract_base c ,sales_contract_rev  r " +
        " where  c.created_by_comp=?1 and c.id = r.id and r.order_code=?2  " +
        "  and r.revision = (select max(revision) from sales_contract_rev re where re.id=r.id) " +
        "  and  c.state ='1' " +
        " and c.id <> ?3",
        nativeQuery = true)
    int findByOrderCode(String dcCompId, String orderCode, String contractId);

    /**
     * 查找合同版本详情
     * @param id 系统合同主键
     * @return 合同版本详情
     */
    @Query(value = "select  s.orderCode from SalesContractRevision s " +
        "where s.salesContractRevisionId.id=?1 and s.salesContractRevisionId.revision=(select max(sa.salesContractRevisionId.revision) from SalesContractRevision sa where sa.salesContractRevisionId.id=s.salesContractRevisionId.id )")
   Optional<SalesContractRevision> getSalesContractRevision(String id);
}
