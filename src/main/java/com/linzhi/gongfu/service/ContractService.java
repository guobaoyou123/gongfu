package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TContract;
import com.linzhi.gongfu.dto.TContractRecord;
import com.linzhi.gongfu.dto.TRevision;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.ContractMapper;
import com.linzhi.gongfu.mapper.ContractRecordMapper;
import com.linzhi.gongfu.mapper.TaxRatesMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.util.DateConverter;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.VGenerateContractRequest;
import com.linzhi.gongfu.vo.VModifyInquiryRequest;
import com.linzhi.gongfu.vo.VPurchaseContractDetailResponse;
import com.linzhi.gongfu.vo.VTaxRateResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private final InquiryDetailRepository inquiryDetailRepository;
    private final  InquiryRecordRepository inquiryRecordRepository;
    private final AddressRepository addressRepository;
    private final CompContactsRepository compContactsRepository;
    private final TaxRatesRepository taxRatesRepository;
    private final TaxRatesMapper taxRatesMapper;
    private final ContractMapper contractMapper;
    private final InquiryService inquiryService;
    private final OperatorRepository operatorRepository;
    private final  ContractDetailRepository contractDetailRepository;
    private final ContractRevisionRepository contractRevisionRepository;
    private final  ContractRevisionDetailRepository contractRevisionDetailRepository;
    private final  ContractRecordRepository contractRecordRepository;
    private final ContractRecordTempRepository contractRecordTempRepository;
    private final ContractRecordMapper contractRecordMapper;
    private final CompanyRepository companyRepository;
    private final CompTaxModelRepository compTaxModelRepository;
    private final ProductRepository productRepository;

    private final TaxRatesRepository vatRatesRepository;
    /**
     * 查询合同是否重复（产品种类和数量）
     * @param inquiryId 询价单id
     * @return 返回 true 或者 false
     */
    public String  findContractProductRepeat(String inquiryId) throws IOException {
            Inquiry inquiry = inquiryService.findInquiry(inquiryId).orElseThrow(()-> new IOException("数据库中找不到该询价单"));
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
        @CacheEvict(value="inquiry_record_List;1800", key="#generateContractRequest.inquiryId"),
        @CacheEvict(value="purchase_contract_List;1800", key="#companyCode+'_'",allEntries=true)
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
            ContractDetail contract = createdContract(
                id,
                code,
                inquiry.getCreatedByComp(),
                inquiry.getCreatedBy(),
                inquiry.getBuyerComp(),
                inquiry.getBuyerCompName(),
                inquiry.getSalerComp(),
                inquiry.getSalerCompName(),
                inquiry.getSalesContractId(),
                InquiryType.INQUIRY_LIST,
                ContractState.FINISHED
            );
            ContractRevision contractRevision =  createContractRevision(id,
                operatorName,
                generateContractRequest.getSupplierNo(),
                inquiry.getOfferMode(),
                generateContractRequest.getContactNo(),1);
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
                    .facePrice(inquiryRecord.getFacePrice())
                    .amount(inquiryRecord.getAmount())
                    .myAmount(inquiryRecord.getAmount())
                    .myChargeUnit(inquiryRecord.getChargeUnit())
                    .ratio(new BigDecimal(1))
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
            //保存合同
            contractDetailRepository.save(contract);
            contractRevision.setFingerprint(str);
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
    public ContractDetail createdContract(String id, String code, String createdByComp,
                                    String createdBy, String buyerComp, String buyerCompName,
                                   String salerComp, String salerCompName,
                                    String salesContractId,InquiryType inquiryType,ContractState  state){



        return  ContractDetail.builder()
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
            .state(state)
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * 生成合同版本实体
     * @param id 合同主键
     * @param buyerContactName 买当联系人姓名
     * @param salesOrderCode 销售合同号
     * @param offerMode 税模式
     * @param contractNo 本单位合同号
     * @param revision 版本
     * @return 返回合同版本实体
     */
    public ContractRevision createContractRevision(String id,
                                                   String buyerContactName,
                                                   String salesOrderCode, TaxMode offerMode,String contractNo,int revision){
        return ContractRevision.builder()
            .contractRevisionId(ContractRevisionId.builder()
                .revision(revision)
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

    public Page<TContract>  findContractPage(String state, String supplierCode,
                                             String startTime, String endTime,
                                             String companyCode, String operator, Pageable pageable){

        List<TContract> tContractList = findContractList(companyCode,operator,state);
        if(StringUtils.isNotBlank(supplierCode)){
            tContractList = tContractList.stream().filter(tContract -> tContract.getSalerComp().equals(supplierCode)).toList();
        }
        if(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime)){
            DateTimeFormatter dateTimeFormatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatters = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTimes = LocalDate.parse(startTime, dateTimeFormatterDay).atStartOfDay();
            LocalDateTime endTimes = LocalDateTime.parse(endTime+" 23:59:59", dateTimeFormatters);
            tContractList = tContractList.stream().filter(tContract ->
                {
                    LocalDateTime dateTime = LocalDateTime.parse(tContract.getCreatedAt(), dateTimeFormatters);
                    return dateTime.isAfter(startTimes) && dateTime.isBefore(endTimes);
                }
            ).toList();
        }
        return PageTools.listConvertToPage(tContractList,pageable);
    }

   @Cacheable(value="purchase_contract_List;1800", key="#companyCode+'_'+#operator+'_'+#state")
    public List<TContract> findContractList(String companyCode,String operator,String state){
        try{
            Operator operator1= operatorRepository.findById(
                    OperatorId.builder()
                        .operatorCode(operator)
                        .companyCode(companyCode)
                        .build()
                )
                .orElseThrow(()-> new IOException("请求的操作员找不到"));
            if(operator1.getAdmin().equals(Whether.YES))
                return contractRepository.findContractList(companyCode, InquiryType.INQUIRY_LIST.getType()+"", state)
                    .stream()
                    .map(contractMapper::toContractList)
                    .toList();
            return contractRepository.findContractList(companyCode,operator, InquiryType.INQUIRY_LIST.getType()+"", state)
                .stream()
                .map(contractMapper::toContractList)
                .toList();
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
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

    /**
     * 根据合同主键、版本号查询采购合同详情
     * @param id 合同主键
     * @param revision 版本号
     * @return 返回合同详情
     * @throws IOException 异常
     */
    public VPurchaseContractDetailResponse.VContract purchaseContractDetail(String id, int revision) throws IOException {
        TContract contractRevision =contractRevisionDetailRepository.findDetail(revision, id)
            .map(contractMapper::toTContractDetail)
            .orElseThrow(()->new IOException("数据库中未查询到该数据"));
        List<TContractRecord> contractRecords;
        if(contractRevision.getState().equals(ContractState.UN_FINISHED.getState()+"")){
            contractRecords = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractIdAndContractRecordTempId_Revision(id,revision).stream()
                .map(contractRecordMapper::toTContractRecord)
                .toList();
        }else{
            contractRecords = contractRecordRepository.findContractRecordsByContractRecordId_ContractIdAndContractRecordId_Revision(id,revision).stream()
                .map(contractRecordMapper::toTContractRecord)
                .toList();
        }
        contractRevision.setRecords(contractRecords);
        contractRevision.setRevisions(revisionList(id));
        return  Optional.of(contractRevision).map(contractMapper::toContractDetail).orElse(null);
    }

    /**
     * 查看合同版本号列表
     * @param id 采购合同主键
     * @return 合同版本号列表
     */
    @Cacheable(value = "contract_revisions;1800",key="#id")
    public List<TRevision> revisionList(String id){
        List<Map<String,Object>> list= contractRevisionDetailRepository.findRevisionList(id);
        List<TRevision> tRevisions = new ArrayList<>();
        list.forEach(map -> tRevisions.add(TRevision.builder()
                .revision((int)map.get("revision"))
                .createdAt(DateConverter.dateFormat((LocalDateTime) map.get("createdAt")))
            .build()));
        return  tRevisions;
    }




    /**
     * 新建空的询价单
     * @param supplierCode 供应商编码
     * @param companyCode 本单位编码
     * @param companyName 本单位名称
     * @param operator 操作员编码
     * @param operatorName 操作员姓名
     * @return 返回成功信息
     */
    @CacheEvict(value="purchase_contract_List;1800", key="#companyCode+'_'",allEntries=true)
    @Transactional
    public Optional<String> savePurchaseContractEmpty(String supplierCode,String companyCode,String companyName,String operator,String operatorName) {
        try {
            Company supplier = companyRepository.findById(supplierCode).orElseThrow(()->new IOException("未从数据库中查到供应商信息"));
            String maxCode = contractDetailRepository.findMaxCode(companyCode,operator).orElse("01");
            Map<String,String> map = getContractCode(maxCode,operator,companyCode,supplierCode);
            ContractDetail contractDetail = createdContract(map.get("id"),
                map.get("code"),
                companyCode,
                operator,
                companyCode,
                companyName,
                supplierCode,
                supplier.getNameInCN(),
                null,
                InquiryType.INQUIRY_LIST,
                ContractState.UN_FINISHED);
            CompTaxModel taxModel = compTaxModelRepository.findById(CompTradId.builder()
                    .compSaler(supplierCode)
                    .compBuyer(companyCode)
                .build()).orElseThrow(()->new IOException("从数据库中没有查询到"));
            ContractRevision contractRevision = createContractRevision(map.get("id"),operatorName,null,taxModel.getTaxModel(),null,1);
            contractDetailRepository.save(contractDetail);
            contractRevisionRepository.save(contractRevision);
            return  Optional.of(map.get("id"));
        } catch (Exception e) {
             e.printStackTrace();
             return Optional.of("");
        }

    }

    /**
     * 生成询价单唯一编码和询价单编号
     * @param max 最大编号
     * @param operatorCode 操作员编码
     * @param companyCode 单位唯一编码
     * @param supplierCode 供应商编码
     * @return 生成询价单唯一编码和询价单编号列表
     */
    public Map<String,String> getContractCode(String max,String operatorCode,String companyCode,String supplierCode){
        Map<String,String> map = new HashMap<>();
        String mCode = ("0000"+max).substring(("0000"+max).length()-3);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate data=LocalDate.now();
        //uuid
        UUID uuid = UUID.randomUUID();
        String id = "HT-"+companyCode+"-"+operatorCode+"-"+uuid.toString().substring(0,8);
        String code ="HT-"+operatorCode+"-"+supplierCode+"-"+dtf.format(data)+"-"+mCode;
        map.put("id",id);
        map.put("code",code);
        return  map;
    }

    /**
     * 获取未确认的采购合同数量
     * @param companyCode 本单位编码
     * @param operator 操作员编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param supplierCode 供应商编码
     * @return 返回确认的采购合同数量
     */
    public int getUnFinished(String companyCode,String operator,String startTime, String endTime,String supplierCode){

        var page =findContractPage(ContractState.UN_FINISHED.getState()+"",supplierCode,startTime,endTime,companyCode,operator, PageRequest.of(
           1,
           10
        ));
        return Integer.parseInt(String.valueOf(page.getTotalElements()));
    }

    /**
     * 添加采购合同产品
     * @param productId 产品id
     * @param price 单价
     * @param amount 数量
     * @param id 合同主键
     * @param revision 版本号
     * @param operator 操作员编码
     * @return 返回成功或者失败信息
     */
    @Caching(evict = {@CacheEvict(value="contract_revision_detail;1800",key = "#id+'-'+#revision"),
        @CacheEvict(value="contract_revision_recordTemp_detail;1800",key = "#id+'-'+#revision"),
        @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    })
    @Transactional
    public boolean saveProduct(String productId, BigDecimal price, BigDecimal amount, String id, Integer revision,String companyCode,String operator){
        try {
             ContractRevisionDetail contractRevisionDetail =contractRevisionDetailRepository.findDetail(revision, id).orElseThrow(() -> new IOException("请求的产品不存在"));
             List<ContractRecordTemp> contractRecordTemps = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractIdAndContractRecordTempId_Revision(id,
                 revision);
            //查询明细最大顺序号
            String maxCode = contractRecordTempRepository.findMaxCode(id);
            if(maxCode==null)
                maxCode="0";
            //查询产品
            Product product = productRepository.findById(productId).orElseThrow(() -> new IOException("请求的产品不存在"));
            //货物税率
            TaxRates goods= vatRatesRepository.findByTypeAndDeflagAndUseCountry(
                VatRateType.GOODS,
                Whether.YES,
                "001"
            ).orElseThrow(() -> new IOException("请求的货物税率不存在"));
            ContractRecordTemp contractRecordTemp = ContractRecordTemp.builder()
                .contractRecordTempId(ContractRecordTempId.builder()
                    .revision(revision)
                    .contractId(id)
                    .code(Integer.parseInt(maxCode)+1)
                    .build())
                .productId(productId)
                .productCode(product.getCode())
                .productId(product.getId())
                .type(VatRateType.GOODS)
                .productCode(product.getCode())
                .brandCode(product.getBrandCode())
                .brand(product.getBrand())
                .productDescription(product.getDescribe())
                .facePrice(product.getFacePrice())
                .chargeUnit(product.getChargeUnit())
                .price(price!=null?contractRevisionDetail.getOfferMode().equals(TaxMode.UNTAXED)?price:price.divide(new BigDecimal(1).add(goods.getRate()),4, RoundingMode.HALF_UP):null)
                .priceVat(price!=null?contractRevisionDetail.getOfferMode().equals(TaxMode.INCLUDED)?price:price.multiply(new BigDecimal(1).add(goods.getRate())).setScale(4, RoundingMode.HALF_UP):null)
                .amount(amount)
                .myAmount(amount)
                .createdAt(LocalDateTime.now())
                .ratio(new BigDecimal(1))
                .myChargeUnit(product.getChargeUnit())
                .stockTime(0)
                .vatRate(goods.getRate())
                .totalPrice(price!=null?contractRevisionDetail.getOfferMode().equals(TaxMode.UNTAXED)?price.multiply(amount).setScale(2, RoundingMode.HALF_UP):price.divide(new BigDecimal(1).add(goods.getRate()),4, RoundingMode.HALF_UP).multiply(amount).setScale(2, RoundingMode.HALF_UP):null)
                .totalPriceVat(price!=null?contractRevisionDetail.getOfferMode().equals(TaxMode.INCLUDED)?price.multiply(amount).setScale(2, RoundingMode.HALF_UP):price.multiply(new BigDecimal(1).add(goods.getRate())).setScale(4, RoundingMode.HALF_UP).multiply(amount).setScale(2, RoundingMode.HALF_UP):null)
                .build();
            contractRecordTemps.add(contractRecordTemp);
            contractRecordTempRepository.save(contractRecordTemp);
            return  countSum(contractRecordTemps,id,revision,operator);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除采购合同产品
     * @param codes 明细序列号
     * @param id 采购合同
     * @param revision 版本号
     * @param operator 操作员
     * @return 返回成功或者失败信息
     */
    @Caching(evict = {@CacheEvict(value="contract_revision_detail;1800",key = "#id+'-'+#revision"),
        @CacheEvict(value="contract_revision_recordTemp_detail;1800",key = "#id+'-'+#revision"),
        @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    })
    @Transactional
    public Boolean deleteContractProduct(List<Integer> codes,String id,int revision,String companyCode,String operator){
        try{
            List<ContractRecordTemp> contractRecordTemps = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractIdAndContractRecordTempId_Revision(id,
                revision ).stream().filter(contractRecordTemp -> !codes.contains(contractRecordTemp.getContractRecordTempId().getCode()))
                .toList();
            List<ContractRecordTempId> contractRecordTempIds = new ArrayList<>();
            codes.forEach(s -> contractRecordTempIds.add(ContractRecordTempId.builder()
                    .contractId(id)
                    .code(s)
                    .revision(revision)
                .build()));
            contractRecordTempRepository.deleteAllById(contractRecordTempIds);

            return countSum(contractRecordTemps,id,revision,operator);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将采购合同的状态设置为未确认的状态，生成新一版合同，生成临时合同明细记录
     * @param id 合同主键
     * @param companyCode 本单位编码
     * @param operator 操作员编码
     * @return  返回版本号
     */
    @Caching(evict = {@CacheEvict(value="contract_revision_detail;1800",key = "#id+'-'+#revision"),
        @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    })
    @Transactional
    public Integer modifyContractState(String id,String companyCode,String operator){
        try{
          String maxRevision =   contractRevisionDetailRepository.findMaxRevision(id).orElseThrow(()->new IOException("不存在该合同"));
         Optional<ContractRevisionDetail> contractRevisionDetail = contractRevisionDetailRepository.findDetail(Integer.parseInt(maxRevision),id);
          ContractRevision contractRevision =  contractRevisionDetail
              .map(contractMapper::toContractRevision).orElseThrow(()->new IOException("不存在该合同"));
          if(contractRevisionDetail.get().getState().equals(ContractState.UN_FINISHED.getState()+""))
              throw new Exception("该合同已经是未确认的，不可再次修改");
          contractDetailRepository.updateContractState(ContractState.UN_FINISHED,id);
          contractRevision.getContractRevisionId().setRevision(Integer.parseInt(maxRevision)+1);
          contractRevision.setCreatedAt(LocalDateTime.now());
          contractRevision.setFingerprint(null);
          contractRevision.setConfirmedAt(null);
          contractRevision.setConfirmedBy(null);
          contractRevision.setModifiedAt(LocalDateTime.now());
          contractRevision.setModifiedBy(operator);
          contractRevisionRepository.save(contractRevision);
          List<ContractRecordTemp> contractRecordTemps= contractRecordRepository.findContractRecordsByContractRecordId_ContractIdAndContractRecordId_Revision(id,Integer.parseInt(maxRevision))
              .stream().map(contractRecordMapper::toContractRecordTemp).toList();
          contractRecordTempRepository.saveAll(contractRecordTemps);
            return Integer.parseInt(maxRevision)+1;
        }catch (Exception e){
            e.printStackTrace();
            return  0;
        }
    }

    /**
     * 修改采购合同
     * @param vModifyInquiryRequest 修改信息
     * @param id 合同主键
     * @param revision 合同版本
     * @return 返回成功或者失败
     */
    @Caching(evict = {@CacheEvict(value="contract_revision_detail;1800",key = "#id+'-'+#revision"),
        @CacheEvict(value="contract_revision_recordTemp_detail;1800",key = "#id+'-'+#revision"),
        @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    })
    @Transactional
    public  Boolean  modifyPurchaseCotract(VModifyInquiryRequest vModifyInquiryRequest, String id, Integer revision,String companyCode,String operator){
        try{
            ContractRevisionDetail contractRevisionDetail = contractRevisionDetailRepository.findDetail(revision,id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            ContractRevision contractRevision =contractRevisionRepository.findById(ContractRevisionId.builder()
                    .revision(revision)
                    .id(id)
                .build()).orElseThrow(() -> new IOException("请求的询价单不存在"));
            List<ContractRecordTemp> contractRecordTemps = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractIdAndContractRecordTempId_Revision(id,revision);

            if(StringUtils.isNotBlank(vModifyInquiryRequest.getTaxModel()))
                contractRevision.setOfferMode(vModifyInquiryRequest.getTaxModel().equals("0")?TaxMode.UNTAXED:TaxMode.INCLUDED);
            if(vModifyInquiryRequest.getServiceVat()!=null) {
                contractRevision.setVatServiceRate(vModifyInquiryRequest.getServiceVat());
                contractRecordTemps.forEach(
                    record -> {
                        if(record.getType().equals(VatRateType.SERVICE))
                            record.setVatRate(vModifyInquiryRequest.getServiceVat());
                    }
                );
            }
            if(vModifyInquiryRequest.getGoodsVat()!=null) {
                contractRevision.setVatProductRate(vModifyInquiryRequest.getGoodsVat());
                contractRecordTemps.forEach(
                    record -> {
                        if(record.getType().equals(VatRateType.GOODS))
                            record.setVatRate(vModifyInquiryRequest.getGoodsVat());
                    }
                );
            }

            if(vModifyInquiryRequest.getProducts()!=null){
                vModifyInquiryRequest.getProducts().forEach(vProduct -> contractRecordTemps.forEach(record -> {
                    if(record.getContractRecordTempId().getCode()==vProduct.getCode()){
                        if(vProduct.getAmount()!=null)
                            record.setMyAmount(vProduct.getAmount());
                            record.setAmount(vProduct.getAmount().multiply(record.getRatio()));
                        if(vProduct.getVatRate()!=null)
                            record.setVatRate(vProduct.getVatRate());
                        if(vProduct.getPrice()!=null&&vProduct.getPrice().intValue()<0) {
                            record.setPrice(null);
                            record.setPriceVat(null);
                            record.setTotalPrice(null);
                            record.setTotalPriceVat(null);
                        }else if(vProduct.getPrice()!=null&&vProduct.getPrice().intValue()>=0) {
                            if (contractRevision.getOfferMode().equals(TaxMode.UNTAXED)) {
                                record.setPrice(vProduct.getPrice());
                            }else {
                                record.setPriceVat(vProduct.getPrice());
                            }

                        }
                    }
                }));
            }
            List<ContractRecordTemp> contractRecordTempList =countRecord(contractRecordTemps,contractRevision.getOfferMode());

            contractRecordTempRepository.saveAll(contractRecordTemps);
            return  countSum(contractRecordTempList,id,revision,operator);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 更新合同总价
     * @param contractRecordTemps 合同明细列表
     * @param id 合同主键
     * @return 返回成功或者失败信息
     */
    public  boolean countSum(List<ContractRecordTemp> contractRecordTemps,String id,int revision,String operator){
        try{
            //判断是否需要重新计算价格
            List<ContractRecordTemp> lists = contractRecordTemps
                .stream()
                .filter(contractRecordTemp -> contractRecordTemp.getPrice()==null)
                .toList();
            //是 重新计算价格
            BigDecimal totalPrice=new BigDecimal(0);
            BigDecimal  totalPriceVat=new BigDecimal(0);
            BigDecimal vat;
            if(lists.size()==0){
                for (ContractRecordTemp contractRecordTemp:contractRecordTemps){
                    totalPrice=totalPrice.add(contractRecordTemp.getTotalPrice());
                    totalPriceVat=totalPriceVat.add(contractRecordTemp.getTotalPriceVat());
                }
                vat = totalPriceVat.setScale(2, RoundingMode.HALF_UP).subtract(totalPrice.setScale(2, RoundingMode.HALF_UP));

            }else{
                totalPrice=null;
                totalPriceVat=null;
                vat=null;
            }

            BigDecimal totalPrice1 = totalPrice == null ? null : totalPrice.setScale(2, RoundingMode.HALF_UP);
            contractRevisionDetailRepository.updateContract(totalPrice1,totalPriceVat==null?null:totalPriceVat.setScale(2, RoundingMode.HALF_UP),vat,totalPrice1,LocalDateTime.now(),operator,id,revision);
             return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    /**
     * 计算合同明细
     * @param contractRecordTemps 明细列表
     * @param taxMode 税模式
     * @return 明细列表
     */
    public List<ContractRecordTemp> countRecord(List<ContractRecordTemp> contractRecordTemps ,TaxMode taxMode){
        contractRecordTemps.forEach(record -> {
            if(taxMode.equals(TaxMode.UNTAXED)&&record.getPrice()!=null){
                record.setPriceVat(record.getPrice().multiply(new BigDecimal(1).add(record.getVatRate())).setScale(4, RoundingMode.HALF_UP));
            }else if(taxMode.equals(TaxMode.INCLUDED)&&record.getPriceVat()!=null){
                record.setPrice(record.getPriceVat().divide(new BigDecimal(1).add(record.getVatRate()),4, RoundingMode.HALF_UP));
            }
            if(record.getPrice()!=null){
                record.setTotalPrice(record.getPrice().multiply(record.getMyAmount()).setScale(2, RoundingMode.HALF_UP));
                record.setTotalPriceVat(record.getPriceVat().multiply(record.getMyAmount()).setScale(2, RoundingMode.HALF_UP));
            }
        });
        return contractRecordTemps;
    }
}
