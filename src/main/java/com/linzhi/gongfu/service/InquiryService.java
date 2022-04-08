package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TInquiry;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.InquiryMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.vo.VInquiryDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 采购询价信息处理及业务服务
 *
 * @author zgh
 * @create_at 2022-02-10
 */
@RequiredArgsConstructor
@Service
public class InquiryService {

    private final InquiryListRepository inquiryListRepository;
    private final InquiryMapper inquiryMapper;
    private final InquiryRepository inquiryRepository;
    private final OperatorRepository operatorRepository;
    private final CompanyRepository companyRepository;
    private final CompTradeRepository compTradeRepository;
    private final InquiryRecordRepository inquiryRecordRepository;
    private final ProductRepository productRepository;
    private final VatRatesRepository vatRatesRepository;
    /**
     * 查询未完成的询价单列表
     * @param companyCode 公司编码
     * @param operator 操作员编码
     * @return 返回未完成询价列表
     */
    @Cacheable(value="inquiry_List;1800", key="#companyCode+'_'+#operator")
    public List<TInquiry> inquiryList(String companyCode, String operator){
        Operator operator1= operatorRepository.findById(OperatorId.builder()
            .operatorCode(operator)
            .companyCode(companyCode)
            .build()).orElseGet(Operator::new);
        if(operator1.getAdmin().equals(Whether.YES))
            return inquiryListRepository.findInquiryListByCreatedByCompAndTypeAndStateOrderByCreatedAtDesc(companyCode, InquiryType.INQUIRY_LIST, InquiryState.UN_FINISHED)
                .stream()
                .map(inquiryMapper::toInquiryList)
                .toList();
        return inquiryListRepository.findInquiriesByCreatedByCompAndCreatedByAndTypeAndStateOrderByCreatedAtDesc(companyCode,operator, InquiryType.INQUIRY_LIST, InquiryState.UN_FINISHED)
            .stream()
            .map(inquiryMapper::toInquiryList)
            .toList();
    }

    /**
     * 询价单详情
     * @param id 询价单主键
     * @return 返回询价单详情
     */
    @Cacheable(value="inquiry_Detail;1800", key="#id")
    public Optional<VInquiryDetailResponse.VInquiry> inquiryDetail(String id){

        return inquiryRepository.findById(id)
            .map(inquiryMapper::toInquiryDetail)
            .map(inquiryMapper::toVInquiryDetail);
    }

    @CacheEvict(value="inquiry_List;1800", key="#companyCode+'_'+#operator")
    public String  emptyInquiry(String companyCode,String companyName,String operator,String operatorName,String supplierCode){
        try {
            //查询询价单最大编号
            String maxCode = inquiryRepository.findMaxCode(companyCode, operator);
            if(maxCode ==null)
                maxCode ="01";
            String mCode = ("0000"+maxCode).substring(("0000"+maxCode).length()-3);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate data=LocalDate.now();
            //uuid
            UUID uuid = UUID.randomUUID();
            String inquiryId = "XJ-"+companyCode+"-0"+operator+"-"+uuid.toString().substring(0,8);
            String inquiryCode ="XJ-"+operator+"-"+companyCode+"-"+dtf.format(data)+"-"+mCode;
            //查询供应商信息
            Company supplier = companyRepository.findById(supplierCode).get();
            //税模式
            CompTrad compTrad = compTradeRepository.findById(
                CompTradId.builder()
                    .compBuyer(companyCode)
                    .compSaler(supplierCode)
                .build()).get();
            inquiryRepository.save(
                Inquiry.builder()
                    .id(inquiryId)
                    .code(inquiryCode)
                    .type(InquiryType.INQUIRY_LIST)
                    .createdByComp(companyCode)
                    .createdBy(operator)
                    .createdAt(LocalDateTime.now())
                    .buyerComp(companyCode)
                    .buyerCompName(companyName)
                    .buyerContactName(operatorName)
                    .salerComp(supplierCode)
                    .salerCompName(supplier.getNameInCN())
                    .state(InquiryState.UN_FINISHED)
                    .offerMode(compTrad.getTaxModel())
                    .build()
            );
            return  inquiryId;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    @CacheEvict(value="inquiry_Detail;1800", key="#id")
    @Transactional
    public Boolean saveInquiryProduct(String id, String productId, BigDecimal price,BigDecimal amount){
       try{
           //查询询价单
           Inquiry inquiry = inquiryRepository.findById(id).get();
           //查询明细最大顺序号
           String maxCode = inquiryRecordRepository.findMaxCode(id);
           if(maxCode==null)
               maxCode="0";
           //查询产品
           Product product = productRepository.findById(productId).get();
           //货物税率
           VatRates goods= vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.GOODS,Whether.YES,"001").get();
           //保存产品
            InquiryRecord record = InquiryRecord.builder()
                .inquiryRecordId(
                    InquiryRecordId.builder()
                        .inquiryId(id)
                        .code(Integer.valueOf(maxCode)+1)
                        .build()
                )
                .createdAt(LocalDateTime.now())
                .amount(amount)
                .productId(productId)
                .type(VatRateType.GOODS)
                .productCode(product.getCode())
                .brandCode(product.getBrandCode())
                .brand(product.getBrand())
                .productDescription(product.getDescribe())
                .charge_unit(product.getChargeUnit())
                .stockTime(0)
                .vatRate(goods.getRate())
                .build();
            if(price!=null){
                if(inquiry.getOfferMode().equals(TaxMode.UNTAXED)){
                    record.setPrice(price);
                    record.setPriceVat(price.multiply(goods.getRate().add(new BigDecimal(1))).setScale(4,BigDecimal.ROUND_HALF_UP));
                }else{
                    record.setPriceVat(price);
                    record.setPrice(price.divide(goods.getRate().add(new BigDecimal(1)),4,BigDecimal.ROUND_HALF_UP));
                }
                record.setTotalPrice(record.getPrice().multiply(amount).setScale(4,BigDecimal.ROUND_HALF_UP));
                record.setTotalPriceVat(record.getPriceVat().multiply(amount).setScale(4,BigDecimal.ROUND_HALF_UP));
            }
            //保存明细
            inquiryRecordRepository.save(record);
            //判断是否需要重新计算价格
            List<InquiryRecord> list = inquiry.getRecords()
                .stream()
                .filter(inquiryRecord -> inquiryRecord.getPrice()==null)
                .toList();

            if(list.size()==0){
                //是 重新计算价格
                BigDecimal totalPrice=new BigDecimal(0);
                BigDecimal  totalPriceVat=new BigDecimal(0);

                if(inquiry.getOfferMode().equals(TaxMode.UNTAXED)){

                    for (InquiryRecord inquiryRecord:inquiry.getRecords()){
                        totalPrice=totalPrice.add(inquiryRecord.getPrice().multiply(inquiryRecord.getAmount()));
                    }
                    totalPrice=totalPrice.add(price.multiply(amount));
                    totalPriceVat  = totalPrice.multiply(goods.getRate().add(new BigDecimal(1))).setScale(4,BigDecimal.ROUND_HALF_UP);

                }else{
                    for (InquiryRecord inquiryRecord:inquiry.getRecords()){
                        totalPriceVat=totalPriceVat.add(inquiryRecord.getPriceVat().multiply(inquiryRecord.getAmount()));
                    }
                    totalPriceVat=totalPriceVat.add(price.multiply(amount));
                    totalPrice  = totalPriceVat.divide(goods.getRate().add(new BigDecimal(1)),4,BigDecimal.ROUND_HALF_UP);
                }
                BigDecimal vat = totalPriceVat.subtract(totalPrice);
                inquiryRepository.updateInquiry(totalPrice,totalPriceVat,vat,id);
            }
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }

    }
}
