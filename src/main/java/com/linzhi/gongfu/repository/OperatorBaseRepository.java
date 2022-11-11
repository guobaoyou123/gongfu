package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.Operator;
import com.linzhi.gongfu.entity.OperatorBase;
import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * 公司操作员基础信息的Repository
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public interface OperatorBaseRepository extends CrudRepository<OperatorBase, OperatorId>, QuerydslPredicateExecutor<OperatorBase> {

    /**
     * 最大编码
     *
     * @param companyCode 单位编码
     * @return 编码
     */
    @Query(value = "select  right(('000'+cast((cast(max(code) as int)+1) as varchar)),3) from comp_operator  where  dc_comp_id=?1 ",
        nativeQuery = true)
    Optional<String> findMaxCode(String companyCode);

    /**
     * 重置密码
     *
     * @param password   密码
     * @param changed    是否更改
     * @param operatorId 操作员主键
     */
    @Modifying
    @Query(value = "update OperatorBase o set o.password=?1 ,o.changed=?2 where o.identity=?3")
    void updatePassword(String password, Whether changed, OperatorId operatorId);

    /**
     * 操作员详情
     *
     * @param state       状态
     * @param companyCode 单位编码
     * @param operator    操作员编码
     * @return 操作员详情
     */
    List<OperatorBase> findOperatorByStateAndIdentity_CompanyCodeAndIdentity_OperatorCodeNot(
        Availability state,
        String companyCode,
        String operator
    );

    /**
     * 操作员列表
     *
     * @param state       状态
     * @param companyCode 单位编码
     * @return 操作员列表
     */
    List<OperatorBase> findOperatorByIdentity_CompanyCodeAndState(String companyCode, Availability state);

    @Query(value = "select o.* from comp_operator o\n" +
        "left join dc_operator_scene s on s.dc_comp_id=o.dc_comp_id and s.operator_code = o.code\n" +
        "left join dc_scene_menu m on s.scene_code =m.scene_code\n" +
        "left join sys_menu me on me.code = m.menu_code\n" +
        "where me.name=?1 and o.dc_comp_id=?2",nativeQuery = true)
    List<OperatorBase> findByScene(String menuName , String compCode);
}
