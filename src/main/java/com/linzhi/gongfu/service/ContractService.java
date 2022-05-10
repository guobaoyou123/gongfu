package com.linzhi.gongfu.service;

import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.TaxRatesMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.vo.VGenerateContractRequest;
import com.linzhi.gongfu.vo.VTaxRateResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final  InquiryRepository inquiryRepository;
    private final InquiryDetailRepository inquiryDetailRepository;
    private final  InquiryRecordRepository inquiryRecordRepository;
    private final AddressRepository addressRepository;
    private final CompContactsRepository compContactsRepository;
    private final TaxRatesRepository taxRatesRepository;
    private final TaxRatesMapper taxRatesMapper;

    /**
     * 查询合同是否重复（产品种类和数量）
     * @param inquiryId 询价单id
     * @return 返回 true 或者 false
     */
    public String  findContractProductRepeat(String inquiryId) throws IOException {
            Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(()-> new IOException("数据库中找不到该询价单"));
            List<InquiryRecord> records = inquiryRecordRepository.findInquiryRecord(inquiryId);
            String str = createSequenceCode(
                records.stream().map(inquiryRecord-> inquiryRecord.getProductId() + "-" + inquiryRecord.getAmount()).toList(),
                inquiry.getSalerComp(),
                inquiry.getBuyerComp()
            );
            //产品种类和数量相同的合同号
            List<String> contractId = contractRepository.findContractId(inquiry.getCreatedByComp(),inquiry.getCreatedBy(),str);

           return contractId.stream().collect(Collectors.joining(","));
    }

    /**
     * 生成采购合同
     * @param generateContractRequest 参数
     * @return 返回成功或者失败
     */
    @Caching(evict = {
        @CacheEvict(value="inquiry_List;1800", key="#companyCode+'_'",allEntries=true),
        @CacheEvict(value="inquiry_history_page;1800", key="#companyCode+'_'",allEntries=true)
    })
    @Transactional
    public Boolean saveContract(VGenerateContractRequest generateContractRequest,String companyCode){
        try{
            //查询询价单详情
            InquiryDetail inquiry = inquiryDetailRepository.findById(generateContractRequest.getInquiryId()).orElseThrow(()-> new IOException("数据库中找不到该询价单"));
            if(inquiry.getState().equals(InquiryState.FINISHED))
                return false;
            //合同编号
            String id = inquiry.getId().replaceAll("XJ","HT");
            String code = inquiry.getCode().replaceAll("XJ","HT");
            Contract contract = createdContract(id,
                code,
                inquiry.getCreatedByComp(),
                inquiry.getCreatedBy(),
                inquiry.getBuyerComp(),
                inquiry.getBuyerCompName(),
                inquiry.getBuyerContactName(),
                inquiry.getSalerComp(),
                inquiry.getSalerCompName(),
                inquiry.getSalesOrderCode(),
                inquiry.getOfferMode(),
                generateContractRequest.getContactNo(),
                InquiryType.INQUIRY_LIST
            );
            if(inquiry.getVatProductRate()!=null)
                contract.setVatProductRate(inquiry.getVatProductRate());
            if(inquiry.getVatServiceRate()!=null)
                contract.setVatServiceRate(inquiry.getVatServiceRate());
            //本单位合同编码
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
                contract.setSalerContactPhone(generateContractRequest.getSupplierContactPhone());
            //地址
            if(StringUtils.isNotBlank(generateContractRequest.getAddressCode())){
                //查找地址
                Address address =addressRepository.findById(
                    AddressId.builder()
                        .dcCompId(inquiry.getCreatedByComp())
                        .code(generateContractRequest.getAddressCode())
                        .build()
                ).orElseThrow(()-> new IOException("数据库中找不到该地址"));
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
                CompContacts compContacts =compContactsRepository.findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_Code(
                    generateContractRequest.getAddressCode(),
                    inquiry.getCreatedByComp(),
                    generateContractRequest.getContactCode()
                ).orElseThrow(()-> new IOException("数据库中找不到该联系人"));
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
                BigDecimal discount = (inquiry.getTotalPriceVat().subtract(generateContractRequest.getSum())).divide(inquiry.getTotalPriceVat(),4, RoundingMode.HALF_UP);
                //计算折扣的含税价格 和未税价格以及小计等
                BigDecimal discountSumVat = new BigDecimal(0);
                BigDecimal discountSum = new BigDecimal(0);
                for (int i  =0;i< inquiry.getRecords().size();i++){
                    InquiryRecord record = inquiry.getRecords().get(i);
                    record.setDiscount(discount);
                    record.setDiscountedPrice(record.getPrice().multiply(new BigDecimal(1).subtract(discount)).setScale(4, RoundingMode.HALF_UP));
                    record.setTotalDiscountedPrice(record.getPrice().multiply(new BigDecimal(1).subtract(discount)).multiply(record.getAmount()).setScale(2, RoundingMode.HALF_UP));
                    record.setDiscountedPriceVat(record.getPriceVat().multiply(new BigDecimal(1).subtract(discount)).setScale(4, RoundingMode.HALF_UP));
                    if(i==inquiry.getRecords().size()-1){
                        record.setTotalDiscountedPriceVat(inquiry.getConfirmTotalPriceVat().subtract(discountSumVat));
                    }else {
                        record.setTotalDiscountedPriceVat(record.getPriceVat().multiply(new BigDecimal(1).subtract(discount)).multiply(record.getAmount()).setScale(2, RoundingMode.HALF_UP));
                        discountSumVat = discountSumVat.add(record.getTotalDiscountedPriceVat());
                    }
                    discountSum=discountSum.add(record.getTotalDiscountedPrice());
                }
                inquiry.setDiscountedTotalPrice(discountSum);
                inquiry.setVat(inquiry.getConfirmTotalPriceVat().subtract(discountSum));
                inquiry.setDiscount(discount);
                contract.setDiscount(discount);
                contract.setVat(inquiry.getConfirmTotalPriceVat().subtract(discountSum));
                contract.setTotalPrice(inquiry.getTotalPrice());
                contract.setTotalPriceVat(inquiry.getTotalPriceVat());
                contract.setDiscountedTotalPrice(discountSum);
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
            /*
              将明细按照产品id，数量进行重新排序
             */
            records.sort((o1, o2) -> {
                if (o1.getProductId().compareTo(o2.getProductId()) == 0) {
                    if (o1.getAmount().doubleValue() < o2.getAmount().doubleValue()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                return o1.getProductId().compareTo(o2.getProductId());
            });
            /*
              获取序列编码
             */
            String str = createSequenceCode(
                records.stream().map(contractRecord -> contractRecord.getProductId()+"-"+contractRecord.getAmount()).toList(),
                inquiry.getSalerComp(),
                inquiry.getBuyerComp()
            );
            //更新询价单
           inquiry.setConfirmedAt(LocalDateTime.now());
           inquiry.setState(InquiryState.FINISHED);
           inquiry.setContractId(id);
           inquiryDetailRepository.save(inquiry);
           contract.setSequenceCode(str);
            //保存合同
           contractRepository.save(contract);
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
        return  true;
    }

    /**
     * 创建合同实体
     * @param id 合同唯一码
     * @param code 合同编码
     * @param createdByComp 创建的单位
     * @param createdBy 创建者
     * @param buyerComp 买方单位
     * @param buyerCompName 买方名称
     * @param buyerContactName 买方联系人名称
     * @param salerComp 卖方单位
     * @param salerCompName 卖方单位名称
     * @param salesOrderCode 销售合同号
     * @param offerMode 税模式
     * @param contractNo 单位合同号
     * @param inquiryType 0-采购合同 1-销售合同
     * @return 合同实体
     */
    public Contract createdContract(String id, String code, String createdByComp,
                                    String createdBy, String buyerComp, String buyerCompName,
                                    String buyerContactName, String salerComp, String salerCompName,
                                    String salesOrderCode, TaxMode offerMode,String contractNo,InquiryType inquiryType){
        return  Contract.builder()
            .id(id)
            .code(code)
            .createdByComp(createdByComp)
            .type(inquiryType)
            .orderCode(contractNo)
            .createdBy(createdBy)
            .buyerComp(buyerComp)
            .buyerCompName(buyerCompName)
            .buyerContactName(buyerContactName)
            .salerComp(salerComp)
            .salerCompName(salerCompName)
            .createdAt(LocalDateTime.now())
            .salesOrderCode(salesOrderCode)
            .state(InquiryState.FINISHED)
            .offerMode(offerMode)
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * 税率列表
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

    /**
     * 判断合同号是否重复
     * @param contractNo 本单位采购合同号
     * @param companyCode 单位id
     * @return 返回是或否
     */
    @Transactional
    public Boolean changeContractNoRepeated(String contractNo,String companyCode){
            int num =  contractRepository.findByOrderCode(companyCode,contractNo);
        return num <= 0;
    }

    /**
     * 根据明细生成序列码
     * @param records 明细
     * @param supplierCode 供应商编码
     * @param buyerCode 客户编码
     * @return MD5编码
     */
    private String createSequenceCode(List<String> records,String supplierCode ,String buyerCode){
        String str = supplierCode + "-" + buyerCode +"-"+ String.join("-", records);
        return  DigestUtils.md5Hex(str);
    }
}
