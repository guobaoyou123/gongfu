package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库房表
 *
 * @author zhangguanghua
 * @create_at 2022-12-08
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_warehouse")
public class WareHouse {

    /**
     * 库房编号
     */
    @Id
    @Column(name = "code", length = 20, nullable = false)
    @NonNull
    private String code;

    /**
     * 单位编码
     */
    @Column(name = "comp_id", length = 12, nullable = false)
    @NonNull
    private String compId;

    /**
     * 库房简称
     */
    @Column(name = "name", length = 10, nullable = false)
    private String name;

    /**
     * 面积（默认 ㎡）
     */
    @Column(name = "acreage")
    private BigDecimal acreage;

    /**
     * 区域编码
     */
    @Column(name = "area_code", length = 20)
    private String areaCode;

    /**
     * 区域名称
     */
    @Column(name = "area_name", length = 20)
    private String areaName;

    /**
     * 详细地址
     */
    @Column(name = "address", length = 100)
    private String address;

    /**
     * 创建时间
     */
    @Column(name = "created_at", columnDefinition = "DATE")
    private LocalDateTime createdAt;

    /**
     * 状态
     */
    @Column
    private Availability sate;
}
