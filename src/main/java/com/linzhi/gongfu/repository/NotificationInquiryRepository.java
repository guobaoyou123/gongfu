package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.NotificationInquiry;
import com.querydsl.core.types.Predicate;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * 消息通知的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface NotificationInquiryRepository extends CrudRepository<NotificationInquiry, String>, QuerydslPredicateExecutor<NotificationInquiry> {

    /**
     * 根据报价后推送的消息编码查找报价信息
     * @param messageCode 消息编码
     * @return 报价详情
     */
    Optional<NotificationInquiry> findByOfferedMessCode(String messageCode);
}
