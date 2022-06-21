package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.DeliverState;
import com.linzhi.gongfu.enumeration.DeliverType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deliver_base")
public class DeliverBase implements Serializable {

    /**
     * 货运记录主键
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String id;
    /**
     * 采购合同主键
     */
    @Column(name = "contract_id", length = 50)
    private String contractId;

    /**
     * 类型（ 1-收货 2-发货）
     */
    @Column(name = "type", length = 1)
    private DeliverType type;

    /**
     * 发货单位
     */
    @Column(name = "deliver_comp", length = 40)
    private String deliverComp;

    /**
     * 发货区域
     */
    @Column(name = "deliver_area_code", length = 20)
    private String deliverAreaCode;

    /**
     * 发货区域名称
     */
    @Column(name = "deliver_area_name", length = 40)
    private String deliverAreaName;

    /**
     * 发货详情地址
     */
    @Column(name = "deliver_address", length = 100)
    private String deliverAddress;

    /**
     * 发货联系公司名称
     */
    @Column(name = "deliver_contact_company", length = 40)
    private String deliverContactCompany;

    /**
     * 联系人姓名
     */
    @Column(name = "deliver_contact_name", length = 40)
    private String deliverContactName;

    /**
     * 联系人电话
     */
    @Column(name = "deliver_contact_phone", length = 40)
    private String deliverContactPhone;

    /**
     * 名义发货地址区域编码
     */
    @Column(name = "actual_deliver_area_code", length = 50)
    private String actualDeliverAreaCode;

    /**
     * 名义发货地址区域名称
     */
    @Column(name = "actual_deliver_area_name", length = 50)
    private String actualDeliverAreaName;

    /**
     * 名义发货地址
     */
    @Column(name = "actual_deliver_address", length = 150)
    private String actualDeliverAddress;

    /**
     * 名义发货公司
     */
    @Column(name = "actual_deliver_company", length = 100)
    private String actualDeliverCompany;

    /**
     * 名义发货联系人姓名
     */
    @Column(name = "actual_deliver_name", length = 40)
    private String actualDeliverName;

    /**
     * 名义发货联系人电话
     */
    @Column(name = "actual_deliver_phone", length = 20)
    private String actualDeliverPhone;

    /**
     * 收货单位
     */
    @Column(name = "receive_comp", length = 40)
    private String receiveComp;

    /**
     * 收货区域编码
     */
    @Column(name = "receive_area_code", length = 20)
    private String receiveAreaCode;

    /**
     * 收货区域名称
     */
    @Column(name = "receive_area_name", length = 40)
    private String receiveAreaName;

    /**
     * 收货详情地址
     */
    @Column(name = "receive_address", length = 100)
    private String receiveAddress;

    /**
     * 收货公司名称
     */
    @Column(name = "receive_contact_company", length = 40)
    private String receiveContactCompany;

    /**
     *收货人姓名
     */
    @Column(name = "receive_contact_name", length = 40)
    private String receiveContactName;

    /**
     *收货人电话
     */
    @Column(name = "receive_contact_phone", length = 40)
    private String receiveContactPhone;

    /**
     *创建单位
     */
    @Column(name = "created_by_comp", length = 40)
    private String createdByComp;

    /**
     *创建者
     */
    @Column(name = "created_by", length = 20)
    private String createdBy;

    /**
     *创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     *确认时间
     */
    @Column(name = "confirm_at")
    private LocalDateTime confirmdAt;

    /**
     *确认人
     */
    @Column(name = "confirm_by", length = 40)
    private String confirmBy;

    /**
     *拣货人
     */
    @Column(name = "picked_by", length = 40)
    private String pickedBy;

    /**
     *复核人
     */
    @Column(name = "checked_by", length = 40)
    private String checkedBy;

    /**
     * 0-待处理 1-已处理
     */
    @Column
    private DeliverState state;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "deliver_code",referencedColumnName = "id", insertable = true, updatable = true)
    private List<DeliverRecord> deliverRecords;
}
