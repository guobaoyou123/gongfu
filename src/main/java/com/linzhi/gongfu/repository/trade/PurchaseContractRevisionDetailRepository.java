package com.linzhi.gongfu.repository.trade;


import com.linzhi.gongfu.entity.PurchaseContractRevisionDetail;
import com.linzhi.gongfu.entity.PurchaseContractRevisionId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 采购合同详细信息的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface PurchaseContractRevisionDetailRepository
    extends CrudRepository<PurchaseContractRevisionDetail, PurchaseContractRevisionId>, QuerydslPredicateExecutor<PurchaseContractRevisionDetail> {

    /**
     * 根据合同编码和版本号查找合同详情
     *
     * @param id 合同主键
     * @return 合同详情
     */
    @Transactional(readOnly = true)
    Optional<PurchaseContractRevisionDetail> getPurchaseContractRevisionDetailByPurchaseContractRevisionId(PurchaseContractRevisionId id);


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
    @Query(value = "update   purchase_contract_rev  set total_price=?1 ,total_price_vat=?2,vat=?3 ,modified_at=?4,modified_by=?5 where  id=?6 and revision=?7 ",
        nativeQuery = true)
    void updateContract(BigDecimal totalPrice, BigDecimal totalPriceVat, BigDecimal vat, LocalDateTime localDateTime, String operator, String id, int revision);

    /**
     * 获取最大版本号
     *
     * @param id 合同主键
     * @return 版本号
     */
    @Query(value = "select max(c.purchaseContractRevisionId.revision)   from PurchaseContractRevisionDetail as c where c.purchaseContractRevisionId.id=?1")
    Optional<String> getMaxRevision(String id);
}
