package com.linzhi.gongfu.service;

import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.ContractMapper;
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
import java.util.Map;
import java.util.Optional;
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
    private final ContractRevisionRepository contractRevisionRepository;
    private final ContractMapper contractMapper;
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
            List<String> contractId = contractRepository.findContractId(inquiry.getCreatedByComp(),str);

           return String.join(",", contractId);
    }

    /**
     * 生成采购合同
     * @param generateContractRequest 参数
     * @return 返回成功或者失败
     */
    @Caching(evict = {
        @CacheEvict(value="inquiry_List;1800", key="#companyCode+'_'",allEntries=true),
        @CacheEvict(value="inquiry_detail;1800",key = "#generateContractRequest.inquiryId"),
        @CacheEvict(value="inquiry_record_List;1800", key="#generateContractRequest.inquiryId")
    })
    @Transactional
    public Boolean saveContract(VGenerateContractRequest generateContractRequest,String companyCode,String operatorName){
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
                inquiry.getSalerComp(),
                inquiry.getSalerCompName(),
                inquiry.getSalesContractId(),
                InquiryType.INQUIRY_LIST
            );
            ContractRevision contractRevision =  createContractRevision(id,
                operatorName,
                generateContractRequest.getSupplierNo(),
                inquiry.getOfferMode(),
                generateContractRequest.getContactNo());
            if(inquiry.getVatProductRate()!=null)
                contractRevision.setVatProductRate(inquiry.getVatProductRate());
            if(inquiry.getVatServiceRate()!=null)
                contractRevision.setVatServiceRate(inquiry.getVatServiceRate());
            //本单位合同编码
           // contractRevision.setOrderCode(generateContractRequest.getContactNo());
           // inquiry.setOrderCode(generateContractRequest.getContactNo());
            //供应商合同编码
           // inquiry.setSalerOrderCode(generateContractRequest.getSupplierNo());
                //contract.setSalerOrderCode(generateContractRequest.getSupplierNo());
            //供应商联系人
            contractRevision.setSalerContactName(generateContractRequest.getSupplierContactName());
            //供应商联系人电话
            contractRevision.setSalerContactPhone(generateContractRequest.getSupplierContactPhone());
            //地址
            if(StringUtils.isNotBlank(generateContractRequest.getAddressCode())){
                //查找地址
                Address address =addressRepository.findById(
                    AddressId.builder()
                        .dcCompId(inquiry.getCreatedByComp())
                        .code(generateContractRequest.getAddressCode())
                        .build()
                ).orElseThrow(()-> new IOException("数据库中找不到该地址"));
                /*inquiry.setAreaCode(address.getAreaCode());
                inquiry.setAreaName(address.getAreaName());
                inquiry.setAddress(address.getAddress());*/
                contractRevision.setAreaCode(address.getAreaCode());
                contractRevision.setAreaName(address.getAreaName());
                contractRevision.setAddress(address.getAddress());
            }
            //联系人
            if(StringUtils.isNotBlank(generateContractRequest.getContactCode())){
                //联系人
                CompContacts compContacts =compContactsRepository.findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_Code(
                    generateContractRequest.getAddressCode(),
                    inquiry.getCreatedByComp(),
                    generateContractRequest.getContactCode()
                ).orElseThrow(()-> new IOException("数据库中找不到该联系人"));
                /*inquiry.setConsigneeName(compContacts.getContName());
                inquiry.setConsigneePhone(compContacts.getContPhone());*/
                contractRevision.setConsigneeName(compContacts.getContName());
                contractRevision.setConsigneePhone(compContacts.getContPhone());
            }
            inquiry.setConfirmTotalPriceVat(generateContractRequest.getSum());
            contractRevision.setConfirmTotalPriceVat(generateContractRequest.getSum());
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
                contractRevision.setDiscount(discount);
                contractRevision.setVat(inquiry.getConfirmTotalPriceVat().subtract(discountSum));
                contractRevision.setTotalPrice(inquiry.getTotalPrice());
                contractRevision.setTotalPriceVat(inquiry.getTotalPriceVat());
                contractRevision.setDiscountedTotalPrice(discountSum);
            }
            //合同明细
            List<ContractRecord> records = new ArrayList<>();
            for (InquiryRecord inquiryRecord:inquiry.getRecords()) {
                ContractRecord contractRecord= ContractRecord.builder()
                    .contractRecordId(
                        ContractRecordId.builder()
                            .revision(1)
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
            contractRevision.setContractRecords(records);
            //contractRevision.setRecords(records);
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
           inquiry.setContractCode(code);
           inquiryDetailRepository.save(inquiry);
            contractRevision.setFingerprint(str);
            //保存合同
           contractRepository.save(contract);
            contractRevisionRepository.save(contractRevision);
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
     * @param salerComp 卖方单位
     * @param salerCompName 卖方单位名称
     * @param inquiryType 0-采购合同 1-销售合同
     * @return 合同实体
     */
    public Contract createdContract(String id, String code, String createdByComp,
                                    String createdBy, String buyerComp, String buyerCompName,
                                   String salerComp, String salerCompName,
                                    String salesContractId,InquiryType inquiryType){



        return  Contract.builder()
            .id(id)
            .code(code)
            .createdByComp(createdByComp)
            .salesContractId(salesContractId)
            .type(inquiryType)
            .createdBy(createdBy)
            .buyerComp(buyerComp)
            .buyerCompName(buyerCompName)
            .salerComp(salerComp)
            .salerCompName(salerCompName)
            .createdAt(LocalDateTime.now())
            .state(InquiryState.FINISHED)
            .createdAt(LocalDateTime.now())
            .build();
    }


    public ContractRevision createContractRevision(String id,
                                                   String buyerContactName,
                                                   String salesOrderCode, TaxMode offerMode,String contractNo){
        return ContractRevision.builder()
            .contractRevisionId(ContractRevisionId.builder()
                .revision(1)
                .id(id)
                .build())
            .orderCode(contractNo)
            .salerOrderCode(salesOrderCode)
            .buyerContactName(buyerContactName)
            .createdAt(LocalDateTime.now())
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
