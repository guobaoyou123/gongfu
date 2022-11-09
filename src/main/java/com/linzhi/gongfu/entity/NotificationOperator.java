package com.linzhi.gongfu.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.linzhi.gongfu.enumeration.NotificationType;
import com.linzhi.gongfu.enumeration.Whether;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comp_message_operator")
public class NotificationOperator {

    @Id
    @Column(length = 50, nullable = false)
    @NotNull
    @NotBlank
    @NonNull
    private String code;

    /**
     * 已读时间
     */
    @Column(name = "readed_at", length = 50)
    private String readedAt;

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

    /**
     * 关联消息基本信息
     */
    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonBackReference
    private Notification notification;
}
