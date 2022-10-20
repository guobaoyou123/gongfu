package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.PurchaseContractBase;
import com.linzhi.gongfu.enumeration.ContractState;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * 采购合同基础信息的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface PurchaseContractBaseRepository
    extends CrudRepository<PurchaseContractBase, String>, QuerydslPredicateExecutor<PurchaseContractBase> {

    /**
     * 查找做大编码
     *
     * @param dcCompId  本单位编码
     * @param createdBy 操作员编码
     * @return 编码
     */
    @Query(value = "select  right(('000'+cast((cast(max(right(code,3)) as int)+1) as varchar)),3) from purchase_contract_base  where  created_by_comp=?1 and created_by=?2\n" +
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
    @Query(value = "update PurchaseContractBase c set c.state=?1,c.pairedCode=null where c.id=?2")
    void updateContractState(ContractState state, String id);

    /**
     * 查找与之相同的未配对的采购合同配对码
     * @param fingerprint 指纹
     * @return 配对码
     */
    @Query(value = "select top 1 paired_code   from purchase_contract_base b\n" +
        "left join purchase_contract_rev br on br.id = b.id and br.revision = (select max(revision) from purchase_contract_rev r where r.id = b.id)\n" +
        "where br.fingerprint = ?1 and b.paired_code not in (select paired_code from sales_contract_base where state = '1' and paired_code<>null)",nativeQuery = true)
    Optional<String> findPairedCode(String fingerprint);

    /**
     * 查找与之相同的未配对的采购合同配对码
     * @param salesContractId 销售合同主键
     * @param revision 版本号
     * @return 配对码
     */
    @Query(value = "select top 1 paired_code   from purchase_contract_base b\n" +
        "left join purchase_contract_rev br on br.id = b.id and br.revision = (select max(revision) from purchase_contract_rev r where r.id = b.id)\n" +
        "where br.fingerprint = (select fingerprint  from sales_contract_rev where id = ?1 and revision =?2) and b.paired_code not in (select paired_code from sales_contract_base where state = '1' and paired_code<>null)",nativeQuery = true)
    Optional<String> findPairedCode(String salesContractId,int revision);
}
