package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于转移前端文案词汇
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TWord {
    private String code;
    private String name;
    private String parentName;
    private String type;
    private String key;
    private String word;
}
