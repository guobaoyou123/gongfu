package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端询修改导入产品信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VImportProductStockTempRequest {
    /**
     * 仓库编码
     */
    private String code;

    /**
     * 类型
     */
    private String type;

    /**
     * 产品列表
     */
    List<VProduct> products;

    @Data
    public static class VProduct{
        /**
         * 品牌编码
         */
        private String brandCode;

        /**
         * 条目号
         */
        private int itemNo;
    }


}
