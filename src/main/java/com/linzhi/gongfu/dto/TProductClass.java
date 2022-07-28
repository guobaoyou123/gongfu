package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用于转移产品分类基本信息
 *
 * @author zgh
 * @create_at 2022-02-08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TProductClass {

    /**
     * 编码
     */
    private String code;
    /**
     * 类型
     */
    private String type;
    /**
     * 名称
     */
    private String name;
    /**
     * 父级编码
     */
    private String parentCode;
    /**
     * 等级
     */
    private String lev;
    /**
     * 子分类列表
     */
    private List<TProductClass> children;
}
