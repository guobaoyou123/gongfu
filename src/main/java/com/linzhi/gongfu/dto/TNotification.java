package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.enumeration.NotificationType;
import com.linzhi.gongfu.enumeration.Whether;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用于转移从数据库获取的消息通知
 *
 * @author zgh
 * @create_at 2022-08-02
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TNotification {
    /**
     * 消息主键
     */
    private String code;

    /**
     * 消息类型（0-格友申请 1-格友供应商 2-申请采购历史记录）
     */
    private NotificationType type;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 相关表的主键
     */
    private String id;

    /**
     * 创建单位
     */
    private String createdCompBy;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 已读时间
     */
    private String readedAt;

    /**
     * 推送单位
     */
    private String pushComp;

    /**
     * 推送场景
     */
    private String pushScene;

    /**
     * 推送人
     */
    private String pushOperator;

    /**
     * 是否已读
     */
    private Whether readed;

    /**
     * 税模式
     */
    private String taxModel;

    /**
     * 是否已经完成报价
     */
    private boolean offered;

    /**
     * 产品列表
     */
    List<TInquiryRecord> products;
}
