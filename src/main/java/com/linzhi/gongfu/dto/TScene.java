package com.linzhi.gongfu.dto;

import java.io.Serializable;

import com.linzhi.gongfu.enumeration.CompanyRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String code;
    private CompanyRole role;
    private String name;
    private String authorizationSuggestion;
}
