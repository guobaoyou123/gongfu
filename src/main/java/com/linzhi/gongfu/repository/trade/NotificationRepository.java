package com.linzhi.gongfu.repository.trade;

import com.linzhi.gongfu.entity.Notification;
import com.linzhi.gongfu.enumeration.NotificationType;
import com.linzhi.gongfu.enumeration.Whether;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * 消息通知的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface NotificationRepository extends CrudRepository<Notification, String>, QuerydslPredicateExecutor<Notification> {

    /**
     * 根据询价单查询已经呼叫了几次
     * @param id 询价单id
     * @param createdBy 创建者
     * @param CreatedCompBy 创建单位
     * @return
     */
    Optional<Integer> countNotificationByIdAndCreatedByAndCreatedCompByAndType(String id, String createdBy, String CreatedCompBy, NotificationType type);

}
