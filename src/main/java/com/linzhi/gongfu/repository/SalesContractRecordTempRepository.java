package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.SalesContractRecordTemp;
import com.linzhi.gongfu.entity.SalesContractRecordTempId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * 销售合同临时明细的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface SalesContractRecordTempRepository
    extends CrudRepository<SalesContractRecordTemp, SalesContractRecordTempId>, QuerydslPredicateExecutor<SalesContractRecordTemp> {
    /**
     * 合同明细临时记录列表
     *
     * @param id 合同主键
     * @return 合同明细临时记录列表
     */
    List<SalesContractRecordTemp> findContractRecordTempsBySalesContractRecordTempId_ContractId(String id);

    /**
     * 最大编码
     *
     * @param id 合同主键
     * @return 编码
     */
    @Query(value = "select  max(code)  from sales_contract_record_temp  where  contract_id=?1 ",
        nativeQuery = true)
    String findMaxCode(String id);

    /**
     * 删除合同明细临时记录
     *
     * @param id 合同主键
     */
    @Modifying
    @Query("delete from SalesContractRecordTemp as c  where c.salesContractRecordTempId.contractId=?1 and c.salesContractRecordTempId.code in ?2")
    void deleteProducts(String id,List<Integer> code);

    /**
     * 删除合同明细中全部的临时记录
     *
     * @param id 合同主键
     */
    @Modifying
    @Query("delete from SalesContractRecordTemp as c  where c.salesContractRecordTempId.contractId=?1")
    void deleteProducts(String id);


}
