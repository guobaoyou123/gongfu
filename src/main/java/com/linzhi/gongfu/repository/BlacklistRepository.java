package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.Blacklist;
import com.linzhi.gongfu.entity.BlacklistId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlacklistRepository extends CrudRepository<Blacklist, BlacklistId>, QuerydslPredicateExecutor<Blacklist> {

    /**
     * 查询黑名单列表
     *
     * @param dcCompId 公司编码
     * @return 名单列表
     */

    List<Blacklist> findBlacklistsByBlacklistId_DcCompId(String dcCompId);
}
