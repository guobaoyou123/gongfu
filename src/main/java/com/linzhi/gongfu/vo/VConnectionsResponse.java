package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于连接方式的预加载请求
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VConnectionsResponse extends VBaseResponse {

    List<VConnections> connections;

    @Data
    public static class VConnections {
        /**
         * 驱动编码
         */
        private String code;
        /**
         * 中文名称
         */
        private String name;
    }
}
