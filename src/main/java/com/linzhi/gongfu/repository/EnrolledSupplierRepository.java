package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.dto.TEnrolledTradeCompanies;
import com.linzhi.gongfu.entity.CompTradId;
import com.linzhi.gongfu.entity.EnrolledTrade;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EnrolledSupplierRepository
    extends CrudRepository<EnrolledTrade, CompTradId>, QuerydslPredicateExecutor<EnrolledTrade> {

    /**
     * 查找本单位内供应商列表
     *
     * @param companyCode 单位编码
     * @return 内供应商列表
     */
    @Cacheable(value = "Enrolled_Supplier_List;1800", key = "#companyCode", unless = "#result == null ")
    @Query(value = "select new  com.linzhi.gongfu.dto.TEnrolledTradeCompanies(b.code,b.nameInCN  ,b.shortNameInCN  ,c.USCI)  from Company b " +
        "INNER JOIN  EnrolledCompany c on c.id=b.code LEFT JOIN CompTrad t on t.compTradId.compSaler=b.code" +
        " where t.compTradId.compBuyer=?1 and  b.role='1' ")
    List<TEnrolledTradeCompanies> findEnrolledSupplierList(String companyCode);

    /**
     * 查找本单位内客户列表
     *
     * @param companyCode 单位编码
     * @return 内客户列表
     */
    @Cacheable(value = "Enrolled_Customer_List;1800", key = "#companyCode", unless = "#result == null ")
    @Query(value = "select new  com.linzhi.gongfu.dto.TEnrolledTradeCompanies(b.code,b.nameInCN  ,b.shortNameInCN  ,c.USCI)  from Company b " +
        "INNER JOIN  EnrolledCompany c on c.id=b.code LEFT JOIN CompTrad t on t.compTradId.compBuyer=b.code" +
        " where t.compTradId.compSaler=?1 and  b.role='1' ")
    List<TEnrolledTradeCompanies> findEnrolledCustomerList(String companyCode);
}
