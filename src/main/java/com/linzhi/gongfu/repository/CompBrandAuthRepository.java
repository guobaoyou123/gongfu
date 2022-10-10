package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompBrandAuth;
import com.linzhi.gongfu.entity.CompBrandAuthId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 公司授权品牌的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */
public interface CompBrandAuthRepository extends CrudRepository<CompBrandAuth, CompBrandAuthId>, QuerydslPredicateExecutor<CompBrandAuth> {
    /**
     * 查找授权品牌列表
     *
     * @param beAuthComp 被授权单位编码
     * @return 授权品牌列表
     */
    List<CompBrandAuth> findCompBrandAuthByCompBrandAuthId_BeAuthComp(String beAuthComp);
}
