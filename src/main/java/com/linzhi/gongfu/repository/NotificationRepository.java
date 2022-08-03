package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.BaseProductClassId;
import com.linzhi.gongfu.entity.MainProductClass;
import com.linzhi.gongfu.entity.Notification;
import com.linzhi.gongfu.enumeration.Whether;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository  extends CrudRepository<Notification, String>, QuerydslPredicateExecutor<Notification> {

    @Modifying
    @Query(value = "update Notification as n set n.readed=?1 where n.code in ?2")
    void updateNotificationState(Whether readed ,List<String> codes);
}
