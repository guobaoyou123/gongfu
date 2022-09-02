package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用于转禁用区域基本信息
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TDisableArea {
    /**
     * 系统编码
     */
    private String code;

    /**
     * 国家编码
     */
    private String country;

    /**
     * 页面显示的编码
     */
    private String idcode;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
