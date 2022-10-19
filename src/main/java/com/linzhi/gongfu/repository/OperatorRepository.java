package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Operator;
import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 公司操作员Repository
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public interface OperatorRepository extends CrudRepository<Operator, OperatorId>, QuerydslPredicateExecutor<Operator> {

    /**
     * 操作员列表
     *
     * @param state       状态
     * @param companyCode 单位编码
     * @param operator    操作员编码
     * @return 操作员列表
     */
    List<Operator> findOperatorByStateAndIdentity_CompanyCodeAndIdentity_OperatorCodeNot(
        Availability state,
        String companyCode,
        String operator
    );

    /**
     * 根据操作员编码列表，查询操作员列表
     *
     * @param companyCode 单位编码
     * @param operators   操作员编码列表
     * @return 操作员列表
     */
    List<Operator> findOperatorByIdentity_CompanyCodeAndIdentity_OperatorCodeIn(String companyCode, List<String> operators);



}
