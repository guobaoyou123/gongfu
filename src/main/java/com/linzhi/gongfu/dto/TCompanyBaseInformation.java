package com.linzhi.gongfu.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 * 用于转移公司基本信息
 *
 * @author xutao
 * @create_at 2022-01-19
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCompanyBaseInformation implements Serializable {
    /**
     * 公司编号
     */
    private String code;

    /**
     * 公司全名
     */
    private String name;

    /**
     * 公司简称
     */
    private String shortName;

    /**
     * 公司所使用的二级域名
     */
    private String subdomain;

    /**
     * 公司所具备的应用场景
     */
    @Singular
    private List<String> scenes;
}
