package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于三级行政区划列表的请求
 *
 * @author zhangguanghua
 * @create_at 2021-12-24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VAreaResponse  extends VBaseResponse {
   private List<Area> areas;
    @Data
    public static class Area {
        /**
         * 系统主键
         */
        private String code ;
        /**
         * 页面显示编码
         */
        private String idcode;
        /**
         * 区域名称
         */
        private String name;
        /**
         * 父级系统主键
         */
        private String parent;
        /**
         * 是否可用
         */
        private Boolean disabled;
        /**
         * 等级
         */
        private long lev;
    }
}
