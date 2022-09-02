package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于转移合同版本信息
 *
 * @author zgh
 * @create_at 2022-05-27
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TRevision {

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 版本号
     */
    private int revision;
}
