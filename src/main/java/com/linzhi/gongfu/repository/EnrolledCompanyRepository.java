package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.EnrolledCompany;
import com.linzhi.gongfu.enumeration.Enrollment;
import com.linzhi.gongfu.enumeration.Whether;
import lombok.NonNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

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

    @Query(value = "select  (cast(max(id) as int)+1) from dc_comp ", nativeQuery = true)
    String findMaxCode();

    @Cacheable(value = "companyDetail;1800", unless = "#result == null ", key = "#companyCode")
    @NonNull
    Optional<EnrolledCompany> findById(@NonNull String companyCode);

    @Cacheable(value = "EnrolledCompany_List;1800", unless = "#result == null ")
    List<EnrolledCompany> findAllByVisibleAndState(Whether visible, Enrollment enrollment);


}
