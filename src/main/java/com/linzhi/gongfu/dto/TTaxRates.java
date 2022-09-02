package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用于转移税率信息场景
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TTaxRates implements Serializable {

    /**
     * 主键
     */
    private String id;

    /**
     * 使用国家
     */
    private String useCountry;

    /**
     * 类型(1货物，2服务)
     */
    private String type;

    /**
     * 税率编号
     */
    private String code;

    /**
     * 税率
     */
    private BigDecimal rate;

    /**
     * 创建时间
     */
    private long createdAt;

    /**
     * 启用（1是，0否)
     */
    private String state;

    /**
     * 默认（1-是 0-否）
     */
    private String deflag;
}
