package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于转移地址信息
 *
 * @author zgh
 * @create_at 2022-03-23
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TAddress {
    /**
     * 单位id
     */
    private String dcCompId;
    /**
     * 地址编码
     */
    private String code;
    /**
     * 区域编码
     */
    private String areaCode;
    /**
     * 区域名称
     */
    private String areaName;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 是否为本部标志
     */
    private Boolean flag;
    /**
     * 状态（0-禁用 1-启用）
     */
    private String state;
    /**
     * 区域是否禁用
     */
    private  boolean disabled;


}
