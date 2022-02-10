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
    private String code;
    private String type;
    private String name;
    private String parentCode;
    private String lev;
    private List<TProductClass> children;
}
