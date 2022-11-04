package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.entity.CompBrandOwner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用于转移经营、自营、授权品牌
 *
 * @author zgh
 * @create_at 2022-01-27
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TBrand implements Serializable {

    /**
     * 品牌编号
     */
    private String code;

    /**
     * 品牌简称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 该品牌是否有品牌管理方
     */
    private Boolean haveOwned;

    /**
     * 当前公司是否是品牌管理方
     */
    private Boolean owned;

    /**
     * 当前公司是否正在营销此品牌
     */
    private Boolean vending;

    /**
     * 品牌方
     */
    private List<CompBrandOwner> compBrandOwner;

    /**
     * 优选供应商
     */
    List<TPreferenceSupplier> suppliers;
}
