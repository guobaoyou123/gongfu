package com.linzhi.gongfu.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于转移优选供应商
 *
 * @author zgh
 * @create_at 2022-11-04
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TPreferenceSupplier  {

    /**
     * 编号
     */
    private String code;
    /**
     * 供应商名称
     */
    private String name;


    /**
     * 排序
     */
    private int sort;
}
