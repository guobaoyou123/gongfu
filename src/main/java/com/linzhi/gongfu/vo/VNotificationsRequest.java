package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端设置消息已读的请求
 */
@Jacksonized
@Data
@NoArgsConstructor
public class VNotificationsRequest {
    /**
     * 消息编码列表
     */
    private List<String> codes;
}
