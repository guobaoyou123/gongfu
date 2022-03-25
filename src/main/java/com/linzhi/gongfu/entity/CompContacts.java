package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 地址联系人信息实体
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
@Table(name = "comp_contact_person ")
public class CompContacts implements Serializable {

    @EmbeddedId
    private CompContactsId compContactsId;

    /**
     * 联系人公司名称
     */
    @Column(name = "cont_comp_name", length = 50)
    private String contCompName;

    /**
     * 联系人姓名
     */
    @Column(name = "cont_name", length = 20)
    @NotNull
    @NotBlank
    private String contName;

    /**
     * 联系人电话
     */
    @Column(name = "cont_phone", length = 20)
    @NotNull
    @NotBlank
    private String contPhone;

    /**
     *状态(0,停用;1,启用)
     */
    @Column(length = 1)
    private Availability state;

}
