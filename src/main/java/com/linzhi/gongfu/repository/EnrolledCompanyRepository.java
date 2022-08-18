package com.linzhi.gongfu.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.linzhi.gongfu.entity.EnrolledCompany;

import com.linzhi.gongfu.enumeration.Enrollment;
import com.linzhi.gongfu.enumeration.Whether;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 入格公司信息Repository
 *
 * @author xutao
 * @create_at 2022-01-14
 */
public interface EnrolledCompanyRepository
        extends CrudRepository<EnrolledCompany, String>, QuerydslPredicateExecutor<EnrolledCompany> {
    Optional<EnrolledCompany> findBySubdomainName(String subdomainName);

    Optional<EnrolledCompany> findByUSCI(String usci);

    @Query(value="select  (cast(max(id) as int)+1) from dc_comp " , nativeQuery = true)
    String findMaxCode();

    @Cacheable(value = "companyDetail;1800", unless = "#result == null ",key = "#companyCode")
    Optional<EnrolledCompany> findById(String companyCode);

    @Cacheable(value = "EnrolledCompany_List;1800", unless = "#result == null ")
    List<EnrolledCompany> findAllByVisibleAndState(Whether visible, Enrollment enrollment);

    /**
     * 查找本单位内供应商列表
     * @param companyCode 单位编码
     * @return 内供应商列表
     */
    @Cacheable(value = "Enrolled_Supplier_List;1800",key="#companyCode+'-'+#state", unless = "#result == null ")
    @Query(nativeQuery = true,value = "select  c.credit_code, b.code,b.chi_name,b.chi_short from comp_base b\n" +
        "left join comp_trade t on  t.comp_saler = b.code\n" +
        "left join dc_comp c on c.id = b.code\n" +
        "where t.comp_buyer=?1 and b.role='1' and t.state =?2")
    List<Map<String,String>> findEnrolledSupplierList(String companyCode, String state);
}
