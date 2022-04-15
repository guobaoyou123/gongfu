package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TTaxRates;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.InquiryState;
import com.linzhi.gongfu.enumeration.InquiryType;
import com.linzhi.gongfu.enumeration.VatRateType;
import com.linzhi.gongfu.mapper.TaxRatesMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.vo.VGenerateContractRequest;
import com.linzhi.gongfu.vo.VTaxRateResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 合同信息处理及业务服务
 *
 * @author zgh
 * @create_at 2022-04-11
 */
@RequiredArgsConstructor
@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final InquiryRepository inquiryRepository;
    private final AddressRepository addressRepository;
    private final CompContactsRepository compContactsRepository;
    private final TaxRatesRepository taxRatesRepository;
    private final TaxRatesMapper taxRatesMapper;

    /**
     * 生成采购合同
     * @param generateContractRequest 参数
     * @return 返回成功或者失败
     */
    @Transactional
    public Boolean saveContract(VGenerateContractRequest generateContractRequest){
        try{
            //查询询价单详情
            Inquiry inquiry = inquiryRepository.findById(generateContractRequest.getInquiryId()).get();
            if(inquiry.getState().equals(InquiryState.FINISHED))
                return false;
            //合同编号
            String id = inquiry.getId().replaceAll("XJ","HT");
            String code = inquiry.getCode().replaceAll("XJ","HT");
            Contract contract = Contract.builder()
                .id(id)
                .code(code)
                .createdByComp(inquiry.getCreatedByComp())
                .type(InquiryType.INQUIRY_LIST)
                .createdBy(inquiry.getCreatedBy())
                .buyerComp(inquiry.getBuyerComp())
                .buyerCompName(inquiry.getBuyerCompName())
                .buyerContactName(inquiry.getBuyerContactName())
                .salerComp(inquiry.getSalerComp())
                .salerCompName(inquiry.getSalerCompName())
                .createdAt(LocalDateTime.now())
                .salesOrderCode(inquiry.getSalesOrderCode())
                .state(InquiryState.UN_FINISHED)
                .offerMode(inquiry.getOfferMode())
                .createdAt(LocalDateTime.now())
                .build();
            if(inquiry.getVatProductRate()!=null)
                contract.setVatProductRate(inquiry.getVatProductRate());
            if(inquiry.getVatServiceRate()!=null)
                contract.setVatServiceRate(inquiry.getVatServiceRate());
            //本单位合同编码
            if(StringUtils.isNotBlank(generateContractRequest.getContactNo()))
                inquiry.setOrderCode(generateContractRequest.getContactNo());
                contract.setOrderCode(generateContractRequest.getContactNo());
            //供应商合同编码
            if(StringUtils.isNotBlank(generateContractRequest.getSupplierNo()))
                inquiry.setSalerOrderCode(generateContractRequest.getSupplierNo());
                contract.setSalerOrderCode(generateContractRequest.getSupplierNo());
                //供应商联系人
            if(StringUtils.isNotBlank(generateContractRequest.getSupplierContactName()))
                inquiry.setSalerContactName(generateContractRequest.getSupplierContactName());
                contract.setSalerContactName(generateContractRequest.getSupplierContactName());
            //供应商联系人电话
            if(StringUtils.isNotBlank(generateContractRequest.getSupplierContactPhone()))
                inquiry.setSalerContactPhone(generateContractRequest.getSupplierContactPhone());
                contract.setSalerContactPhone(generateContractRequest.getSupplierContactPhone());;
            //地址
            if(StringUtils.isNotBlank(generateContractRequest.getAddressCode())){
                //查找地址
                Address address =addressRepository.findById(
                    AddressId.builder()
                        .dcCompId(inquiry.getCreatedByComp())
                        .code(generateContractRequest.getAddressCode())
                        .build()
                ).get();
                inquiry.setAreaCode(address.getAreaCode());
                inquiry.setAreaName(address.getAreaName());
                inquiry.setAddress(address.getAddress());
                contract.setAreaCode(address.getAreaCode());
                contract.setAreaName(address.getAreaName());
                contract.setAddress(address.getAddress());
            }
            //联系人
            if(StringUtils.isNotBlank(generateContractRequest.getAddressCode())){
                //联系人
                CompContacts compContacts =compContactsRepository.findById(
                    CompContactsId.builder()
                        .addrCode(generateContractRequest.getAddressCode())
                        .operatorCode(inquiry.getCreatedBy())
                        .dcCompId(inquiry.getCreatedByComp())
                        .code(generateContractRequest.getContactCode())
                        .build()
                ).get();

                inquiry.setConsigneeName(compContacts.getContName());
                inquiry.setConsigneePhone(compContacts.getContPhone());
                contract.setConsigneeName(compContacts.getContName());
                contract.setConsigneePhone(compContacts.getContPhone());
            }

            inquiry.setConfirmTotalPriceVat(generateContractRequest.getSum());
            contract.setConfirmTotalPriceVat(generateContractRequest.getSum());
            //判断产品单价是否为空
            List<InquiryRecord> list = inquiry.getRecords()
                .stream()
                .filter(inquiryRecord -> inquiryRecord.getPrice()==null)
                .toList();
            //计算折扣，折扣后价格，税额
            if(list.size()==0){
                BigDecimal discount = (inquiry.getTotalPriceVat().subtract(generateContractRequest.getSum())).divide(inquiry.getTotalPriceVat(),4,BigDecimal.ROUND_HALF_UP);
                //计算折扣的含税价格 和未税价格以及小计等
                BigDecimal discountSumVat = new BigDecimal(0);
                BigDecimal discountSum = new BigDecimal(0);
                for (int i  =0;i< inquiry.getRecords().size();i++){
                    InquiryRecord record = inquiry.getRecords().get(i);
                    record.setDiscount(discount);
                    record.setDiscountedPrice(record.getPrice().multiply(new BigDecimal(1).subtract(discount)).setScale(4,BigDecimal.ROUND_HALF_UP));
                    record.setTotalDiscountedPrice(record.getPrice().multiply(new BigDecimal(1).subtract(discount)).multiply(record.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
                    record.setDiscountedPriceVat(record.getPriceVat().multiply(new BigDecimal(1).subtract(discount)).setScale(4,BigDecimal.ROUND_HALF_UP));
                    if(i==inquiry.getRecords().size()-1){
                        record.setTotalDiscountedPriceVat(inquiry.getConfirmTotalPriceVat().subtract(discountSumVat));
                    }else {
                        record.setTotalDiscountedPriceVat(record.getPriceVat().multiply(new BigDecimal(1).subtract(discount)).multiply(record.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        discountSumVat = discountSumVat.add(record.getTotalDiscountedPriceVat());
                    }
                    discountSum=discountSum.add(record.getTotalDiscountedPrice());
                }
                inquiry.setVat(inquiry.getConfirmTotalPriceVat().subtract(discountSum));
                inquiry.setDiscount(discount);
                contract.setDiscount(discount);
                contract.setVat(inquiry.getConfirmTotalPriceVat().subtract(discountSum));
                contract.setTotalPrice(inquiry.getTotalPrice());
                contract.setTotalPriceVat(inquiry.getTotalPriceVat());
            }
            //合同明细
            List<ContractRecord> records = new ArrayList<>();
            for (InquiryRecord inquiryRecord:inquiry.getRecords()) {
                ContractRecord contractRecord= ContractRecord.builder()
                    .contractRecordId(
                        ContractRecordId.builder()
                            .contractId(id)
                            .code(inquiryRecord.getInquiryRecordId().getCode())
                            .build()
                    )
                    .productId(inquiryRecord.getProductId())
                    .productCode(inquiryRecord.getProductCode())
                    .productDescription(inquiryRecord.getProductDescription())
                    .brandCode(inquiryRecord.getBrandCode())
                    .brand(inquiryRecord.getBrand())
                    .amount(inquiryRecord.getAmount())
                    .chargeUnit(inquiryRecord.getChargeUnit())
                    .type(VatRateType.GOODS)
                    .vatRate(inquiryRecord.getVatRate())
                    .stockTime(inquiryRecord.getStockTime())
                    .createdAt(LocalDateTime.now())
                    .build();
                if(inquiryRecord.getPrice()!=null) {
                    contractRecord.setPrice(inquiryRecord.getPrice());
                    contractRecord.setPriceVat(inquiryRecord.getPriceVat());
                    contractRecord.setTotalPrice(inquiryRecord.getTotalPrice());
                    contractRecord.setTotalPriceVat(inquiryRecord.getTotalPriceVat());
                    contractRecord.setDiscount(inquiry.getDiscount());
                    contractRecord.setDiscountedPrice(inquiryRecord.getDiscountedPrice());
                    contractRecord.setDiscountedPriceVat(inquiryRecord.getDiscountedPriceVat());
                    contractRecord.setTotalDiscountedPrice(inquiryRecord.getTotalDiscountedPrice());
                    contractRecord.setTotalDiscountedPriceVat(inquiryRecord.getTotalDiscountedPriceVat());
                }
                records.add(
                    contractRecord
                );
            }
           contract.setRecords(records);
            //保存询价单
           inquiry.setConfirmedAt(LocalDateTime.now());
           inquiry.setState(InquiryState.FINISHED);
           inquiryRepository.save(inquiry);
            //保存合同
           contractRepository.save(contract);

        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
        return  true;
    }

    /**
     * 说率列表
     * @param type 类型
     * @return 返回税率列表信息
     */
    public  List<VTaxRateResponse.VTaxRates> findTaxRates(String type){
        return taxRatesRepository.findTaxRatesByUseCountryAndTypeAndState(
            "001",
            type.equals("1")?VatRateType.GOODS:VatRateType.SERVICE,
            Availability.ENABLED
        ).stream()
            .map(taxRatesMapper::toTTaxRates)
            .map(taxRatesMapper::toVTaxRates)
            .toList();
    }
}
