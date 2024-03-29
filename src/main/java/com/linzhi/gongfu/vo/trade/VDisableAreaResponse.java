package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于查询本公司停用的区域的预加载请求
 *
 * @author zgh
 * @create_at 2022-02-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VDisableAreaResponse extends VBaseResponse {
    List<Area> areas;

    @Data
    public static class Area {
        /**
         * 系统主键
         */
        private String code;

        /**
         * 页面显示编码
         */
        private String idcode;

        /**
         * 区域名称
         */
        private String name;

        /**
         * 创建时间
         */
        private long createdAt;
    }
}
