package com.linzhi.gongfu.repository.trade;

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
    /**
     * 根据域名查找入格单位
     *
     * @param subdomainName 域名
     * @return 单位详情
     */
    Optional<EnrolledCompany> findBySubdomainName(String subdomainName);

    /**
     * 根据社会统一代码查找入格单位
     *
     * @param usci 社会统一代码
     * @return 入格单位详情
     */
    Optional<EnrolledCompany> findByUSCI(String usci);

    /**
     * 最大编码
     *
     * @return 编码
     */
    @Query(value = "select  (cast(max(id) as int)+1) from dc_comp ", nativeQuery = true)
    String findMaxCode();

    /**
     * 查找公司详情
     *
     * @param companyCode 单位编码
     * @return 公司详情
     */
    @Cacheable(value = "companyDetail;1800", unless = "#result == null ", key = "#companyCode")
    @NonNull
    Optional<EnrolledCompany> findById(@NonNull String companyCode);

    /**
     * 查找可见的入格单位列表
     *
     * @param visible    是否可见
     * @param enrollment 是否入格
     * @return 入格单位列表
     */
    @Cacheable(value = "EnrolledCompany_List;1800", unless = "#result == null ")
    List<EnrolledCompany> findAllByVisibleAndState(Whether visible, Enrollment enrollment);


}
