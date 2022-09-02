package com.linzhi.gongfu.entity;


import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 品牌和二级分类视图表
 */
@Builder
@Setter
@Getter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "v_brand_class")
public class ViewBrand implements Serializable {
    /**
     * 品牌编码
     */
    @Id
    @Column(name = "code", length = 10, nullable = false)
    @NotBlank
    @NotBlank
    private String code;
    /**
     * 品牌名称
     */
    @Column(name = "name", length = 50, nullable = false)
    private String name;
    /**
     * 禁用启用状态
     */
    @Column(length = 1, nullable = false)
    private Availability state;

    /**
     * 排序
     */
    @Column
    private Integer sort;
    /**
     * 二级分类
     */
    @Column(name = "class_2", length = 10, nullable = false)
    private String class2;
}
