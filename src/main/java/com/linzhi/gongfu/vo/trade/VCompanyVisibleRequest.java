package com.linzhi.gongfu.vo.trade;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于接前端公司设置可见信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VCompanyVisibleRequest {

    /**
     * 是否对可以可见
     */
    private Boolean visible;

    /**
     * 可见内容
     */
    private String content;
}
