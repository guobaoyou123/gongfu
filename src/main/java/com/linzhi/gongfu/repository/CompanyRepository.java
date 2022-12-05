package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Company;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * 公司信息Repository
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public interface CompanyRepository extends CrudRepository<Company, String>, QuerydslPredicateExecutor<Company> {

    /**
     * 外供应查找做大编码
     *
     * @param role     角色
     * @param dcCompId 单位编码
     * @return 最大编码
     */
    @Query(value = " SELECT  cast(max(b.encode) as int)+1 code  FROM    comp_base b,comp_trade t  where b.role=?1 and t.comp_saler=b.code and t.comp_buyer=?2  "
        , nativeQuery = true)
    String findSupplierMaxCode(String role, String dcCompId);

    /**
     * 外客户查找做大编码
     *
     * @param role     角色
     * @param dcCompId 单位编码
     * @return 最大编码
     */
    @Query(value = " SELECT  cast(max(b.encode) as int)+1 code  FROM    comp_base b,comp_trade t  where b.role=?1 and t.comp_buyer=b.code and t.comp_saler=?2  "
        , nativeQuery = true)
    String findCustomerMaxCode(String role, String dcCompId);

    /**
     * 更改单位状态
     *
     * @param availability 状态
     * @param code         单位编码列表
     */
    @Modifying
    @Query("update Company as a set a.state=?1 where a.code in ?2")
    void updateCompanyState(Availability availability, List<String> code);

    /**
     * 根据社会统一信用代码查找公司信息
     *
     * @param ucsi 社会统一信用代码
     * @return 公司信息
     */
    @Query(value = " SELECT  b.*  FROM    comp_base b,dc_comp t  where t.credit_code=? and t.id=b.id  "
        , nativeQuery = true)
    List<Company> findCompanyByUSCI(String ucsi);

    /**
     * 查询公司简称是否有重复的
     *
     * @param shortName   公司简称
     * @param companyCode 公司编码
     * @return 公司数量
     */
    @Query(value = " SELECT count(*)\n" +
        "  FROM comp_base\n" +
        "  where chi_short=?1 and code <>?2  and role='1' "
        , nativeQuery = true)
    int checkRepeat(String shortName, String companyCode);

    /**
     * 根据询价单编码查找公司单位详情
     * @param inquiry 询价编码
     * @return 公司详情
     */
    @Query(value = "select  c.*  from comp_base c " +
            "left join  inquiry_base q on q.created_by_comp = c.code " +
            "where q.id = ?1",nativeQuery = true)
    Optional<Company> getCompany(String inquiry);
}
