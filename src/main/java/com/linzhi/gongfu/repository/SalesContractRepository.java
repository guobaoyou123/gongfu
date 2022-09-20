package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.ContractState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


import java.util.List;
import java.util.Optional;
/**
 * 销售合同基础信息的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface SalesContractRepository
    extends CrudRepository<SalesContractBase, String>, QuerydslPredicateExecutor<SalesContractBase> {

    /**
     * 查找做大编码
     *
     * @param dcCompId  本单位编码
     * @param createdBy 操作员编码
     * @return 编码
     */
    @Query(value = "select  right(('000'+cast((cast(max(right(code,3)) as int)+1) as varchar)),3) from sales_contract_base  where  created_by_comp=?1 and created_by=?2\n" +
        "             and DateDiff(dd,created_at,GETDATE())=0 ",
        nativeQuery = true)
    Optional<String> findMaxCode(String dcCompId, String createdBy);


    /**
     * 更改合同状态
     *
     * @param state 状态
     * @param id    合同主键
     */
    @Modifying
    @Query(value = "update SalesContractBase c set c.state=?1 where c.id=?2")
    void updateContractState(ContractState state, String id);

    /**
     * 根据指纹查找合同编码列表
     *
     * @param dcCompId     本单位编码
     * @param sequenceCode 指纹
     * @return 合同编码列表
     */
    @Query(value = "select  c.id from sales_contract_base c ,sales_contract_rev  r  where  c.created_by_comp =?1   and c.id = r.id   and r.fingerprint =?2 and r.revision=(select max(revision) from sales_contract_rev v where v.id = r.id) ", nativeQuery = true)
    List<String> findContractId(String dcCompId, String sequenceCode);
}
