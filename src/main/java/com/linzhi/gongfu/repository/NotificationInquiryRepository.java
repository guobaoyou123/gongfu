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


    Optional<NotificationInquiry> findByOfferedMessCode(String messageCode);
}
