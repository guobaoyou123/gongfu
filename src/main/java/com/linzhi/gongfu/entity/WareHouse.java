package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
public class WareHouse implements Serializable {

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
    @Column(name = "state")
    private Availability state;

    /**
     * 授权操作员
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "code", referencedColumnName = "code", insertable = true, updatable = false),
        @JoinColumn(name = "comp_id", referencedColumnName = "comp_id", insertable = true, updatable = false)
    })
    @NotFound(action=NotFoundAction.IGNORE)
    private List<WareHouseOperator> operatorList;
}
