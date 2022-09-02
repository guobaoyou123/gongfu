package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于转移三级行政区划
 *
 * @author zgh
 * @create_at 2022-03-22
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TArea {

    /**
     * 编码
     */
    private String code;

    /**
     * 国家名称
     */
    private String country;

    /**
     * 国家编号
     */
    private String number;

    /**
     * 区域编号
     */
    private String idcode;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 父级id
     */
    private String parentCode;

    /**
     * 父级编号
     */
    private String idparentCode;

    /**
     * 等级
     */
    private long lev;

    /**
     * 是否禁用
     */
    private Boolean disabled;
}
