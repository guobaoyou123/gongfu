package com.linzhi.gongfu.mapper;


import com.linzhi.gongfu.dto.TContract;
import com.linzhi.gongfu.dto.TInquiry;
import com.linzhi.gongfu.entity.Contract;
import com.linzhi.gongfu.entity.Inquiry;
import com.linzhi.gongfu.vo.VContractPageResponse;
import com.linzhi.gongfu.vo.VInquiryPageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换合同相关信息
 *
 * @author xutao
 * @create_at 2022-05-24
 */
@Mapper(componentModel = "spring")
public interface ContractMapper {


    /**
     * 转换询价单列表
     * @param contract 采购合同基本信息
     * @return 采购合同基本信息
     */
    @Mapping(target = "createdAt",expression ="java(com.linzhi.gongfu.util.DateConverter.dateFormat(contract.getCreatedAt()))" )
    @Mapping(target = "type",expression = "java(String.valueOf(contract.getType().getType()))")
    @Mapping(target = "state",expression = "java(String.valueOf(contract.getState().getState()))")
    TContract toContractList(Contract contract);

    /**
     * 转换采购合同列表
     * @param tContract 采购合同基本信息
     * @return 采购合同基本信息
     */
    @Mapping(target = "supplierName",source = "salerCompName")
    @Mapping(target = "ownerCode",source = "createdBy")
    @Mapping(target = "ownerName",source = "createdByName")
    @Mapping(target = "salesContractId",source = "salesContractId")
    @Mapping(target = "salesContractCode",source = "salesContractCode")
    @Mapping(target = "salesContractNo",source = "salesOrderCode")
    @Mapping(target = "paired",constant = "false")
    VContractPageResponse.VContract toContractPage(TContract tContract);
}
