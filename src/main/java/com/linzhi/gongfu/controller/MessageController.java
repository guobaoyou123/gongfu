package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.NotificationMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.NotificationService;
import com.linzhi.gongfu.vo.VBaseResponse;
import com.linzhi.gongfu.vo.VNotificationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于消息类通知等
 *
 * @author zgh
 * @create_at 2022-08-02
 */
@RestController
@RequiredArgsConstructor
public class MessageController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    /**
     * 消息通知
     * @return 返回消息通知列表
     */
    @GetMapping("/message/notification")
    public VNotificationsResponse listNotifications(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var scenes = session.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        var list= notificationService.listNotification(session.getSession().getCompanyCode(), Whether.NO,session.getSession().getOperatorCode(),scenes);
        return VNotificationsResponse.builder()
            .code(200)
            .message("获取数据成功")
            .list(list.stream()
                    .map(notificationMapper::toVNotificationDo)
                        .toList()
            )
            .build();
    }


}

