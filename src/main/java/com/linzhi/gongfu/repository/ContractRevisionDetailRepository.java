package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.ContractRevisionDetail;
import com.linzhi.gongfu.entity.ContractRevisionId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ContractRevisionDetailRepository
    extends CrudRepository<ContractRevisionDetail, ContractRevisionId>, QuerydslPredicateExecutor<ContractRevisionDetail> {

    /**
     * 合同详情
     *
     * @param revision 版本号
     * @param id       合同主键
     * @return 合同详情
     */
    @Query(value = "select r.*,\n" +
        "b.code as code ,b.sales_contract_id as salesContractId,b.buyer_comp as buyer_comp,b.buyer_comp_name as buyer_comp_name,b.created_by_comp as created_by_comp,b.created_by as created_by,b.saler_comp as saler_comp,b.saler_comp_name as saler_comp_name,b.state as state,\n" +
        "o.name as createdByName,a.code as salesContractCode ,re.order_code as salesOrderCode, \n" +
        "pre.total_price as previousUntaxedTotal,pre.total_price_vat as previousTaxedTotal  \n" +
        " from contract_rev r \n" +
        "left join contract_base b on b.id = r.id\n" +
        "left join comp_operator o on o.dc_comp_id=b.created_by_comp and o.code=b.created_by\n" +
        "left join contract_base a on a.id = b.sales_contract_id\n" +
        "left join contract_rev re on re.id=b.id and re.revision = (select MAX(e.revision) from contract_rev e  where e.id = re.id)\n" +
        "left join contract_rev pre on pre.id=re.id and pre.revision = (re.revision-1) \n" +
        "where r.revision=?1 and r.id=?2", nativeQuery = true)
    Optional<ContractRevisionDetail> getDetail(int revision, String id);

    /**
     * 合同版本号列表
     *
     * @param id 合同主键
     * @return 合同版本号列表
     */
    @Query(value = "select d.contractRevisionId.revision as revision,d.createdAt as createdAt from ContractRevisionDetail d where d.contractRevisionId.id=?1")
    List<Map<String, Object>> listRevision(String id);

    /**
     * 更新合同总价
     *
     * @param totalPrice    总的未税价格
     * @param totalPriceVat 总的含税金额
     * @param vat           税额
     * @param localDateTime 时间
     * @param operator      操作员编码
     * @param id            合同主键
     * @param revision      版本号
     */
    @Modifying
    @Query(value = "update   contract_rev  set total_price=?1 ,total_price_vat=?2,vat=?3 ,modified_at=?4,modified_by=?5 where  id=?6 and revision=?7 ",
        nativeQuery = true)
    void updateContract(BigDecimal totalPrice, BigDecimal totalPriceVat, BigDecimal vat, LocalDateTime localDateTime, String operator, String id, int revision);

    /**
     * 获取最大版本号
     *
     * @param id 合同主键
     * @return 版本号
     */
    @Query(value = "select max(c.contractRevisionId.revision)   from ContractRevisionDetail as c where c.contractRevisionId.id=?1")
    Optional<String> getMaxRevision(String id);
}
