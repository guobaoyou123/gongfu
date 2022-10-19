package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;

import javax.persistence.Column;
import java.util.List;


/**
 * 客户或者供应商列表信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TCompanyList {

    /**
     * 系统编码
     */
    private String code;

    /**
     * 前端显示编码
     */
    private String enCode;

    /**
     * 公司简称
     */
    private String shortName;

    /**
     * 状态
     */
    private String state;

    /**
     * 卖方所属操作员
     */
    private String salerBelongTo;

    /**
     * 买方所属操作员
     */
    private String buyerBelongTo;
    /**
     * 可见品牌
     */
    private List<TBrand> brands;

    /**
     *授权操作员
     */
    private List<TOperatorInfo> operators;
}
