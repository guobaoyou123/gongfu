package com.linzhi.gongfu.entity;


import com.linzhi.gongfu.enumeration.NotificationType;
import com.linzhi.gongfu.enumeration.Whether;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息通知实体类
 *
 * @author zhangguanghua
 * @create_at 2022-07-20
 */
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comp_message")
public class Notification {

    @Id
    @Column(length = 50, nullable = false)
    @NotNull
    @NotBlank
    @NonNull
    private String code;

    /**
     * 消息类型（0-格友申请 1-格友供应商 2-申请采购历史记录）
     */
    @Column(length = 1, nullable = false)
    private NotificationType type;

    /**
     * 消息内容
     */
    @Column(length = 100, nullable = false)
    private String message;

    /**
     * 相关表的主键
     */
    @Column(length = 50, nullable = false)
    private String id;

    /**
     * 创建单位
     */
    @Column(name = "created_comp_by", length = 40, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String createdCompBy;

    /**
     * 创建人
     */
    @Column(name = "created_by", length = 40)
    private String createdBy;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    /**
     * 推送单位
     */
    @Column(name = "push_comp", length = 50)
    private String pushComp;

    /**
     * 推送场景
     */
    @Column(name = "operated_by", length = 50)
    private String operatedBy;

    @OneToMany
    @JoinColumn(name = "message_code", referencedColumnName = "code",insertable = true,updatable = true)
    List<NotificationOperator> operatorList;
}
