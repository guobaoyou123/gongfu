package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.dto.TEnrolledSuppliers;
import com.linzhi.gongfu.entity.CompTradId;
import com.linzhi.gongfu.entity.EnrolledSupplier;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EnrolledSupplierRepository
    extends CrudRepository<EnrolledSupplier, CompTradId>, QuerydslPredicateExecutor<EnrolledSupplier> {

    /**
     * 查找本单位内供应商列表
     * @param companyCode 单位编码
     * @return 内供应商列表
     */
    @Cacheable(value = "Enrolled_Supplier_List;1800",key="#companyCode+'-'+#state", unless = "#result == null ")
    @Query(value = "select new  com.linzhi.gongfu.dto.TEnrolledSuppliers(b.code,b.nameInCN  ,b.shortNameInCN  ,c.USCI)  from Company b " +
        "INNER JOIN  EnrolledCompany c on c.id=b.code LEFT JOIN CompTrad t on t.compTradId.compSaler=b.code" +
        " where t.compTradId.compBuyer=?1 and  b.role='1' and t.state=?2")
    List<TEnrolledSuppliers> findEnrolledSupplierList(String companyCode, Availability state);
}
