package com.linzhi.gongfu.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 三级行政区划信息实体
 *
 * @author zgh
 * @create_at 2022-03-22
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_area")
public class AdministrativeArea implements Serializable {

    /**
     * 主键
     */
    @Id
    @Column(name = "code", length = 12, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String code;

    /**
     *国家名称
    */
    @Column(length = 50)
    @NotNull
    @NotBlank
    private String country;
    /**
     *国家编号
     */
    @Column(length = 50)
    @NotNull
    @NotBlank
    private String number;
    /**
     *区域编号
     */
    @Column(length = 8)
    @NotNull
    @NotBlank
    private String idcode;
    /**
     *区域名称
     */
    @Column(length = 100)
    @NotNull
    @NotBlank
    private String name;
    /**
     *父级id
     */
    @Column(name="parent_code",length = 12)
    private String parentCode;
    /**
     *父级编号
     */
    @Column(name="idparent_code",length = 8)
    private String idparentCode;
    /**
     *等级
     */
    @Column(length = 1)
    private String lev;
}
