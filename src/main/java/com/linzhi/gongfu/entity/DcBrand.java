package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统品牌表
 */
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dc_brand")
public class DcBrand implements Serializable {

    /**
     * 品牌编号
     */
    @Id
    @Column(name = "code", length = 4, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String code;
    /**
     * 品牌名称
     */
    @Column(name = "name", length = 40)
    @NotNull
    @NotBlank
    private String name;
    /**
     * 品牌简称
     */
    @Column(name = "chi_short", length = 10)
    @NotNull
    @NotBlank
    private String chiShort;

    /**
     * 品牌英文名称
     */
    @Column(name = "eng_name", length = 40)
    @NotNull
    @NotBlank
    private String engName;
    /**
     * 状态
     */
    @Column(name = "state", length = 1)
    @NotNull
    @NotBlank
    private String state;

    /**
     * 排序
     */
    @Column(name = "sort", length = 20)
    private Integer sort;


}
