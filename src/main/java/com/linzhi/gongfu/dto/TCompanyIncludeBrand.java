package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用于转移公司包括经营、自营、授权品牌
 *
 * @author zgh
 * @create_at 2022-01-27
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCompanyIncludeBrand implements Serializable {
    /**
     * 公司编号
     */
    private String code;

    /**
     * 公司简称
     */
    private String name;

    /**
     * 自营品牌
     */
    private List<TBrand> manageBrands;

    /**
     * 自营品牌
     */
    private List<TBrand> authBrands;

    /**
     * 自营品牌
     */
    private List<TBrand> selfSupportBrands;

    /**
     * 状态
     */
    private String state;
}
