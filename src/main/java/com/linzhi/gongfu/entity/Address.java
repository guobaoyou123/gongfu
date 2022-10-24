package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 地址信息实体
 *
 * @author zgh
 * @create_at 2022-03-22
 */
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_address")
public class Address implements Serializable {
    /**
     * 主键
     */
    @EmbeddedId
    private AddressId addressId;

    /**
     * 区域编码
     */
    @Column(name = "area_code", length = 8)
    private String areaCode;

    /**
     * 区域名称
     */
    @Column(name = "area_name", length = 8)
    private String areaName;

    /**
     * 详细地址
     */
    @Column(length = 150)
    private String address;

    /**
     * 是否为本部地址（本部标志（1-代表是公司地址 0-不是公司地址））
     */
    @Column(length = 1)
    private Whether flag;

    /**
     * 状态(0,停用;1,启用)
     */
    @Column(length = 1)
    private Availability state;

    /**
     * 创建者
     */
    @Column(name = "created_by", length = 10)
    private String createdBy;
}
