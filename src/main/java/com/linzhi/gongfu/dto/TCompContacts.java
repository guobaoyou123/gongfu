package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于转移地址联系人信息
 *
 * @author zgh
 * @create_at 2022-03-24
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCompContacts {
    /**
     * 单位编码
     */
    private String dcCompId;

    /**
     * 操作员编码
     */
    private String operatorCode;

    /**
     * 地址编码
     */
    private String addrCode;

    /**
     * 联系人编码
     */
    private String code;

    /**
     * 联系人公司名称
     */
    private String contCompName;

    /**
     * 联系人姓名
     */
    private String contName;

    /**
     * 联系人电话
     */
    private String contPhone;

    /**
     * 状态(0,停用;1,启用)
     */
    private String state;

    /**
     * 是否可编辑(0,停用;1,启用)
     */
    private Boolean readOnly;
}
