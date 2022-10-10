package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.SalesContractRevisionDetail;
import com.linzhi.gongfu.entity.SalesContractRevisionId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 销售合同详细内容的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface SalesContractRevisionDetailRepository
    extends CrudRepository<SalesContractRevisionDetail, SalesContractRevisionId>, QuerydslPredicateExecutor<SalesContractRevisionDetail> {

    /**
     * 根据合同编码和版本号查找合同详情
     *
     * @param salesContractRevisionId 合同主键
     * @return 合同详情
     */
    @Transactional(readOnly = true)
    Optional<SalesContractRevisionDetail> getSalesContractRevisionDetailBySalesContractRevisionId(SalesContractRevisionId salesContractRevisionId);
}
