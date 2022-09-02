package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.enumeration.CompanyRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用于转移入格企业可供功能场景
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TScene implements Serializable {

    /**
     * 编码
     */
    private String code;

    /**
     * 角色
     */
    private CompanyRole role;

    /**
     * 名称
     */
    private String name;

    /**
     * 授权建议
     */
    private String authorizationSuggestion;
}
