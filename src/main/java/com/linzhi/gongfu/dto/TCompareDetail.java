package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于转对照表基本信息
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TCompareDetail {

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;
}
