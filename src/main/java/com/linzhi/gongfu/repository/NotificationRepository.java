package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.BaseProductClassId;
import com.linzhi.gongfu.entity.MainProductClass;
import com.linzhi.gongfu.entity.Notification;
import com.linzhi.gongfu.enumeration.Whether;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository  extends CrudRepository<Notification, String>, QuerydslPredicateExecutor<Notification> {

}
