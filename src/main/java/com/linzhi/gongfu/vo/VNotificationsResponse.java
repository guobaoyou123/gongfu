package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于消息通知的请求
 *
 * @author zgh
 * @create_at 2022-08-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VNotificationsResponse extends VBaseResponse {

    /**
     * 当前页
     */
    private int current;

    /**
     * 总条数
     */
    private int total;

    /**
     * 消息列表
     */
    private List<VNotification> list;

    @Data
    public static class VNotification {

        /**
         * 消息通知编码
         */
        private String code;

        /**
         * 类型0-格友申请 1-格友供应商 2-申请采购历史记录
         */
        private String type;

        /**
         * 通知内容
         */
        private String content;

        /**
         * 对应的申请记录主键或者供应商主键
         */
        private String id;
    }
}
