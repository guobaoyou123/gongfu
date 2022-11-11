package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.converter.NotificationTypeConverter;
import com.linzhi.gongfu.enumeration.NotificationType;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.NotificationMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.NotificationService;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

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
     *
     * @return 返回消息通知列表
     */
    @GetMapping("/message/notification")
    public VNotificationsResponse listNotifications(@RequestParam("state") Optional<String> state,
                                                    @RequestParam("type") Optional<String> type) throws NoSuchMethodException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var scenes = session.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        var list = notificationService.listNotification(
            session.getSession().getCompanyCode(),
            state.orElseThrow(() -> new NullPointerException("数据为空")).equals("0") ? Whether.NO : Whether.YES,
            session.getSession().getOperatorCode(),
            scenes, new NotificationTypeConverter().convertToEntityAttribute(type.orElse("0").toCharArray()[0])
        );
        return VNotificationsResponse.builder()
            .code(200)
            .message("获取数据成功")
            .list(list.stream()
                .map(notificationMapper::toVNotificationDo)
                .toList()
            )
            .build();
    }

    /**
     * 修改消息通知状态
     *
     * @param notifications 消息通知编码列表
     * @return 返回成功或者失败信息
     */
    @PostMapping("/message/notification/state")
    public VBaseResponse modifyNotificationState(@RequestBody Optional<VNotificationsRequest> notifications) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = notificationService.modifyNotification(
            session.getSession().getCompanyCode(),
            notifications.orElseThrow(() -> new NullPointerException("数据为空")).getCodes(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "操作成功" : "操作失败")
            .build();
    }

    /**
     * 查看消息详情
     * @param code 消息编码
     * @return 消息详情
     */
    @GetMapping("/message/{code}/{type}")
    public VNotificationResponse getNotificationDetail(@PathVariable String code,@PathVariable String type) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var notification = notificationService.getNotification(code,session.getSession().getCompanyCode(),session.getSession().getOperatorCode(), type);
        return VNotificationResponse.builder()
            .code(200)
            .message("获取数据成功")
            .detail(Optional.of(notification).map(
                notificationMapper::toVNotificationDetail).orElse(null))
            .build();
    }

    /**
     * 获取未读的消息数量
     * @return 消息数量
     */
    @GetMapping("/message/count")
    public VPNotificationAmountResponse getMessageAmount(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var count = notificationService.getMessageCount(session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
        return VPNotificationAmountResponse.builder()
            .code(200)
            .message("获取数据成功")
            .amount(count)
            .build();
    }

    /**
     * 锁定消息操作人,判断是否可以报价
     *
     * @return 返回成功或者失败信息
     */
    @GetMapping("/message/{code}/offered")
    public VBaseResponse isOffered(@PathVariable String code) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = notificationService.isOffered(code,session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode());
        return VBaseResponse.builder()
            .code((Integer) map.get("code"))
            .message((String) map.get("message"))
            .build();
    }


}

