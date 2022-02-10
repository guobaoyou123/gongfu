package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sys_product")
public class Product implements Serializable {
    /**
     * 产品唯一码
     */
    @Id
    @Column(length = 64,nullable = false)
    @NotBlank
    @NotNull
    private String id;
    /**
     * 产品编码
     */
    @Column(length = 20,nullable = false)
    @NotBlank
    @NotNull
    private String code;
    /**
     * 品牌编码
     */
    @Column(name = "brand_code",length = 10,nullable = false)
    @NotBlank
    @NotNull
    private String brandCode;
    /**
     * 品牌名称
     */
    @Column(length = 50,nullable = false)
    private String brand;
    /**
     * 产品1级分类
     */
    @Column(name = "class_1",length = 10,nullable = false)
    private String class1;
    /**
     * 产品2级分类
     */
    @Column(name = "class_2",length = 10,nullable = false)
    private String class2;
    /**
     * 产品2级分类名称
     */
    @Column(name = "class_2_name",length = 50,nullable = false)
    private String class2Name;

    /**
     * 产品描述
     */
    @Column(length = 100,nullable = false)
    private String describe;
    /**
     * 产品驱动方式
     */
    @Column(name = "driv_mode",length = 20,nullable = false)
    private String drivMode;

    /**
     * 产品主材质
     */
    @Column(name = "main_mate",length = 10,nullable = false)
    private String mainMate;
    /**
     * 产品主材质名称
     */
    @Column(name = "main_mate_name",length = 10,nullable = false)
    private String mainMateName;
    /**
     * 产品连接方式
     */
    @Column(name = "conn1_type",length = 20,nullable = false)
    private String conn1Type;
    /**
     * 产品连接方式
     */
    @Column(name = "conn2_type",length = 20,nullable = false)
    private String conn2Type;

}
