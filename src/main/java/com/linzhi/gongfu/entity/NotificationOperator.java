package com.linzhi.gongfu.entity;


import com.linzhi.gongfu.enumeration.Whether;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息通知实体类
 *
 * @author zhangguanghua
 * @create_at 2022-11-09
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comp_message_operator")
public class NotificationOperator {

    @EmbeddedId
    private NotificationOperatorId notificationOperatorId;

    /**
     * 已读时间
     */
    @Column(name = "readed_at", length = 50)
    private LocalDateTime readedAt;

    /**
     * 推送单位
     */
    @Column(name = "push_comp", length = 50)
    private String pushComp;

    /**
     * 推送场景
     */
    @Column(name = "push_scene", length = 50)
    private String pushScene;

    /**
     * 推送人
     */
    @Column(name = "push_operator", length = 50)
    private String pushOperator;

    /**
     * 是否已读
     */
    @Column(name = "readed")
    private Whether readed;

}
