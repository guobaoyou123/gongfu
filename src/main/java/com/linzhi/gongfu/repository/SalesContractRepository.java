package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.SalesContractBase;
import com.linzhi.gongfu.enumeration.ContractState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;
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
    @Query(value = "update SalesContractBase c set c.state=?1,c.pairedCode=null where c.id=?2")
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

    /**
     * 查找与之相同的未配对的销售合同配对码
     * @param fingerprint 指纹
     * @return 配对码
     */
    @Query(value = "select top 1 paired_code   from sales_contract_base b\n" +
        "left join sales_contract_rev br on br.id = b.id and br.revision = (select max(revision) from sales_contract_rev r where r.id = b.id)\n" +
        "where br.fingerprint = ?1 and b.paired_code not in (select paired_code from purchase_contract_base where state = '1'  and paired_code<>null)",nativeQuery = true)
    Optional<String> findPairedCode(String fingerprint);

    /**
     * 查找与之相同的未配对的销售合同配对码
     * @param contractId 采购合同主键
     * @param revision 版本号
     * @return 配对码
     */
    @Query(value = "select top 1 paired_code   from sales_contract_base b\n" +
        "left join sales_contract_rev br on br.id = b.id and br.revision = (select max(revision) from sales_contract_rev r where r.id = b.id)\n" +
        "where br.fingerprint = (select fingerprint  from purchase_contract_rev where id = ?1 and revision =?2) and b.paired_code not in (select paired_code from purchase_contract_rev where state = '1' and paired_code<>null)",nativeQuery = true)
    Optional<String> findPairedCode(String contractId,int revision);

    /**
     * 查找销售销售合同单位合同号，系统编码
     * @param id
     * @return 销售销售合同单位合同号，系统编码
     */
    @Query(value = "select b.code ,br.order_code from sales_contract_base b " +
        "left join sales_contract_rev br on br.id = b.id and br.revision = (select max(revision) from sales_contract_rev r where r.id = b.id)" +
        "where b.id = ?1",nativeQuery = true)
    Map<String,Object> findSalesCode(String id);
}
