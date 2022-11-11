package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Notification;
import com.linzhi.gongfu.entity.NotificationOperator;
import com.linzhi.gongfu.entity.NotificationOperatorId;
import com.linzhi.gongfu.enumeration.Whether;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * 消息通知推送人的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface NotificationOperatorRepository extends CrudRepository<NotificationOperator, NotificationOperatorId>, QuerydslPredicateExecutor<NotificationOperator> {
    /**
     * 更新消息状态
     *
     * @param readed 已读
     * @param codes  消息编码列表
     */
    @Modifying
    @Query(value = "update NotificationOperator as n set n.readed=?1 where n.notificationOperatorId.messageCode in ?2 and n.pushComp=?3 and n.pushOperator=?4")
    void updateNotificationState(Whether readed, List<String> codes,String companyCode,String operator);

    /**
     * 查找信息是否已读
     * @param mess_code 消息编码
     * @param companyCode 单位编码
     * @param operatorCode 操作员编码
     * @return 消息人员是否已读详情
     */
    Optional<NotificationOperator> findByNotificationOperatorId_MessageCodeAndPushCompAndPushOperator(String mess_code,String companyCode,String operatorCode);

    /**
     * 查询该操作员未读的消息有多少条
     * @param companyCode 公司编码
     * @param operator 操作员编码
     * @param readed 是否已读
     * @return 消息条数
     */
    int countNotificationOperatorByPushCompAndAndPushOperatorAndReaded(String companyCode,String operator,Whether readed);
}
