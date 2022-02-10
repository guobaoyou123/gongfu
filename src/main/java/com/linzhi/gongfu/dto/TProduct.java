package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用于转移产品基本信息
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TProduct implements Serializable {
    /**
     * 产品唯一码
     */
    private String id;
    /**
     * 产品编码
     */
    private String code;
    /**
     * 品牌编码
     */
    private String brandCode;
    /**
     * 品牌名称
     */
    private String brand;
    /**
     * 产品1级分类
     */
    private String class1;
    /**
     * 产品2级分类
     */
    private String class2;
    /**
     * 产品2级分类名称
     */
    private String class2Name;

    /**
     * 产品描述
     */
    private String describe;
    /**
     * 产品驱动方式
     */
    private String drivMode;

    /**
     * 产品主材质
     */
    private String mainMate;
    /**
     * 产品主材质名称
     */
    private String mainMateName;
    /**
     * 产品连接方式
     */
    private String conn1Type;
    /**
     * 产品连接方式
     */
    private String conn2Type;
}
