package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

/**
 * 用于响应前端对于导入产品详情展示请求
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VImportProductTempResponse extends VBaseResponse{
    private boolean passed;
    private List<VProduct> products;

    @Data
    public  static  class VProduct{
        /**
         * 行号
         */
        private Integer itemNo ;
        /**
         * 产品id
         */
        private String productId;
        /**
         * 产品编码
         */
        private String code;
        /**
         * 品牌编码
         */
        private String brandCode;
        /**
         * 品牌名称
         */
        private String brandName;
        /**
         * 价格
         */
        private String price;
        /*
         *数量
         */
        private String amount;
        /**
         * 错误信息
         */
        private List<Map<String,Object>> errors;
    }
}
