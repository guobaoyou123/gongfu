package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.vo.VImportProductTempResponse;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 用于转移导入产品信息
 *
 * @author zgh
 * @create_at 2022-04-14
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TImportProductTemp {
    /**
     * 单位id
     */
    private String dcCompId ;
    /**
     * 操作员id
     */
    private String operator ;
    /**
     * 序号
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
     * 品牌列表
     */
    private List<TBrand> brand;
    /**
     * 价格
     */
    private String price;
    /*
     *数量
     */
    private String amount;
    /**
     * 已被确认的品牌编码
     */
    private String confirmedBrand;
    /**
     * 错误信息
     */
    private List<String> messages;
}
