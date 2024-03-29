package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用于转申请采购表基本信息
 *
 * @author zgh
 * @create_at 2022-07-20
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TCompTradeApply {
    /**
     * 申请记录编码
     */
    private String code;

    /**
     * 类型（1-申请采购 ）
     */
    private String type;

    /**
     * 创建单位
     */
    private String createdCompBy;

    /**
     * 创建单位公司名称
     */
    private String createdCompanyName;

    /**
     * 创建单位公司简称
     */
    private String createdCompanyShortName;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 处理单位
     */
    private String handledCompanyCode;

    /**
     * 处理单位公司名称
     */
    private String handledCompanyName;

    /**
     * 处理单位公司简称
     */
    private String handledCompanyShortName;

    /**
     * 处理人
     */
    private String handledBy;

    /**
     * 拒绝原因
     */
    private String refuseRemark;

    /**
     * 处理时间
     */
    private String handledAt;

    /**
     * 状态 0-申请中 1-同意 2-拒绝 3-始终拒绝
     */
    private String state;

    /**
     * 申请或者被申请公司编码
     */
    private String companyCode;

    /**
     * 申请或者被申请公司名称
     */
    private String companyName;

    /**
     * 申请或者被申请公司简称
     */
    private String companyShortName;

    /**
     * 申请或者被申请公司社会统一信用代码
     */
    private String usci;

    /**
     * 申请或者被申请公司联系人姓名
     */
    private String contactName;

    /**
     * 申请或者被申请公司联系人电话
     */
    private String contactPhone;

    /**
     * 申请或者被申请公司区域编码
     */
    private String areaCode;

    /**
     * 申请或者被申请公司区域名称
     */
    private String areaName;

    /**
     * 申请或者被申请公司详细地址
     */
    private String address;

    /**
     * 申请或者被申请公司 公司简介
     */
    private String introduction;

    /**
     * 本单位编码
     */
    private String dcCompId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 可见品牌
     */
    private List<TBrand> brands;

    /**
     * 税模式
     */
    private String taxModel;

    /**
     * 操作员列表
     */
    private List<TOperatorInfo> operators;

}
