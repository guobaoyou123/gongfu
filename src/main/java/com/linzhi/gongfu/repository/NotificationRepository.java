package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.BaseProductClassId;
import com.linzhi.gongfu.entity.MainProductClass;
import com.linzhi.gongfu.entity.Notification;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository  extends CrudRepository<Notification, String>, QuerydslPredicateExecutor<Notification> {

}
