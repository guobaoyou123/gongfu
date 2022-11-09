package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Notification;
import com.linzhi.gongfu.entity.NotificationOperator;
import com.linzhi.gongfu.enumeration.Whether;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 消息通知推送人的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface NotificationOperatorRepository extends CrudRepository<NotificationOperator, String>, QuerydslPredicateExecutor<NotificationOperator> {
    /**
     * 更新消息状态
     *
     * @param readed 已读
     * @param codes  消息编码列表
     */
    @Modifying
    @Query(value = "update NotificationOperator as n set n.readed=?1 where n.notificationOperatorId.messageCode in ?2 and n.pushComp=?3 and n.pushOperator=?4")
    void updateNotificationState(Whether readed, List<String> codes,String companyCode,String operator);

}
