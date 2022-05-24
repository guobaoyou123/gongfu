package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.enumeration.InquiryState;
import com.linzhi.gongfu.enumeration.InquiryType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用于转移合同信息
 *
 * @author zgh
 * @create_at 2022-05-24
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TContract {

    /**
     * 合同唯一id
     */
    private String id ;
    /**
     * 合同编号
     */

    private String code;

    private String orderCode;
    /**
     * 类型（0-采购合同 1-销售合同）
     */
    private InquiryType type;

    /*
     * 对应销售合同记录系统主键
     */
    private String salesContractId;
    /**
     * 所属单位编码
     */
    private String createdByComp;
    /**
     * 所属操作员编码
     */
    private String createdBy;
    /**
     * 客户公司编码
     */
    private String buyerComp;
    /**
     * 客户名称
     */
    private String buyerCompName;
    /**
     * 供应商公司编号
     */

    private String salerComp;
    /**
     * 供应商名称
     */
    private String salerCompName;
    /**
     * 状态（0-未形成合同 1-以生成合同 2-撤销合同）
     */
    @Column
    private InquiryState state;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;


}
