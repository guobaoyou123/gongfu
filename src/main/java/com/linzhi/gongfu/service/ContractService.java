package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TContract;
import com.linzhi.gongfu.dto.TContractRecord;
import com.linzhi.gongfu.dto.TRevision;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.ContractMapper;
import com.linzhi.gongfu.mapper.ContractRecordMapper;
import com.linzhi.gongfu.mapper.DeliverRecordMapper;
import com.linzhi.gongfu.mapper.TaxRatesMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.util.DateConverter;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
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
import java.util.concurrent.atomic.AtomicInteger;
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
    private final ImportProductTempRepository importProductTempRepository;
    private final TaxRatesRepository vatRatesRepository;
    private final DeliveryTempRepository deliveryTempRepository;
    private final DeliverRecordMapper deliverRecordMapper;
    private final DeliverBaseRepository deliverBaseRepository;
    private final ContractReceivedRepository contractReceivedRepository;
    private final ContractRecordPreviewRepository contractRecordPreviewRepository;
    /**
     * 查询合同是否重复（产品种类和数量）
     * @param inquiryId 询价单id
     * @return 返回 true 或者 false
     */
    public Optional<String>  findContractProductRepeat(String inquiryId) throws IOException {
            Inquiry inquiry = inquiryService.findInquiry(inquiryId).orElseThrow(()-> new IOException("数据库中找不到该询价单"));
            List<InquiryRecord> records = inquiryRecordRepository.findInquiryRecord(inquiryId);
            String str = createSequenceCode(
                records.stream().map(inquiryRecord-> inquiryRecord.getProductId() + "-" + inquiryRecord.getAmount()).toList(),
                inquiry.getSalerComp(),
                inquiry.getBuyerComp()
            );
            //产品种类和数量相同的合同号
            List<String> contractId = contractRepository.findContractId(inquiry.getCreatedByComp(),str);

           return Optional.of(String.join(",", contractId));
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
    public Boolean saveContract(VGenerateContractRequest generateContractRequest,String companyCode,String operatorName,String operator){
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
                generateContractRequest.getContactNo(),1,operator);
            if(inquiry.getVatProductRate()!=null)
                contractRevision.setVatProductRate(inquiry.getVatProductRate());
            if(inquiry.getVatServiceRate()!=null)
                contractRevision.setVatServiceRate(inquiry.getVatServiceRate());
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
                BigDecimal discountSumVat = new BigDecimal("0");
                BigDecimal discountSum = new BigDecimal("0");
                for (int i  =0;i< inquiry.getRecords().size();i++){
                    InquiryRecord record = inquiry.getRecords().get(i);
                    record.setDiscount(discount);
                    record.setDiscountedPrice(calculateDiscountedPrice(record.getPrice(),discount));
                    record.setTotalDiscountedPrice(calculateDiscountedSubtotal(record.getPrice(),discount,record.getAmount()));
                    record.setDiscountedPriceVat(calculateDiscountedPrice(record.getPriceVat(),discount));
                    if(i==inquiry.getRecords().size()-1){
                        record.setTotalDiscountedPriceVat(inquiry.getConfirmTotalPriceVat().subtract(discountSumVat));
                    }else {
                        record.setTotalDiscountedPriceVat(calculateDiscountedSubtotal(record.getPriceVat(),discount,record.getAmount()));
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
                    .ratio(new BigDecimal("1"))
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

            /*
              获取序列编码
             */
            String str = createSequenceCode(
                recordSort(records),
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
                                                   String salesOrderCode,
                                                   TaxMode offerMode,
                                                   String contractNo,
                                                   int revision,
                                                   String operator
    ){
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
            .confirmedAt(LocalDateTime.now())
            .confirmedBy(operator)
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
    public Boolean changeContractNoRepeated(String contractNo,String companyCode,
                                            String contractId
    ){
        int num;
        if(contractId.equals("")){
             num =  contractRepository.findByOrderCode(companyCode,contractNo);
        }else {
            num =  contractRepository.findByOrderCode(companyCode,contractNo,contractId);
        }

        return num <= 0;
    }

    /**
     * 查看合同列表
     * @param state 合同状态
     * @param supplierCode 供应商编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param companyCode 本单位编码
     * @param operator 操作员编码
     * @param pageable 分页
     * @return 返回合同列表
     */
    public Page<TContract>  findContractPage(String state, String supplierCode,
                                             String startTime, String endTime,
                                             String companyCode, String operator,
                                             Pageable pageable) throws Exception {

        List<TContract> tContractList = findContractList(companyCode,operator,state);
        if(!supplierCode.equals("")){
            tContractList = tContractList.stream().filter(tContract -> tContract.getSalerComp().equals(supplierCode)).toList();
        }
        if(!startTime.equals("")&&!endTime.equals("")){
            LocalDateTime startTimes = LocalDate.parse(startTime, DateType.YYYYMMDD.getType()).atStartOfDay();
            LocalDateTime endTimes = LocalDateTime.parse(endTime+" 23:59:59", DateType.YYYYMMDDHHMMSS.getType());
            tContractList = tContractList.stream().filter(tContract ->
                {
                    LocalDateTime dateTime = LocalDateTime.parse(tContract.getCreatedAt(), DateType.YYYYMMDDHHMMSS.getType());
                    return dateTime.isAfter(startTimes) && dateTime.isBefore(endTimes);
                }
            ).toList();
        }
        return PageTools.listConvertToPage(tContractList,pageable);
    }

    /**
     * 根据状态查询合同列表
     * @param companyCode 本单位编码
     * @param operator 操作员编码
     * @param state 状态
     * @return 返回合同列表
     */
   @Cacheable(value="purchase_contract_List;1800", key="#companyCode+'_'+#operator+'_'+#state")
    public List<TContract> findContractList(String companyCode,String operator,String state) throws Exception{

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

    private List<String> recordSort(List<ContractRecord> records){
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
        return  records.stream().map(contractRecord -> contractRecord.getProductId()+"-"+contractRecord.getAmount()).toList();
    }
    /**
     * 根据合同主键、版本号查询采购合同详情
     * @param id 合同主键
     * @param revision 版本号
     * @return 返回合同详情
     * @throws IOException 异常
     */
    public VPurchaseContractDetailResponse.VContract purchaseContractDetail(String id, int revision) throws IOException {
        TContract contractRevision =getContractRevisionDetail(id, revision)
            .map(contractMapper::toTContractDetail)
            .orElseThrow(()->new IOException("数据库中未查询到该数据"));
        List<TContractRecord> contractRecords;
        if(contractRevision.getState().equals(ContractState.UN_FINISHED.getState()+"")){
            contractRecords = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractId(id).stream()
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
     * 合同版本详情
     * @param id 合同主键
     * @param revision 版本
     * @return 合同详情
     */
    @Cacheable(value = "contract_revision_detail;1800",key = "#id+'-'+#revision")
    public Optional<ContractRevisionDetail> getContractRevisionDetail(String id ,int revision){
        return  contractRevisionDetailRepository.findDetail(revision, id);
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
            ContractRevision contractRevision = createContractRevision(map.get("id"),operatorName,null,taxModel.getTaxModel(),null,1,operator);
            contractDetailRepository.save(contractDetail);
            contractRevisionRepository.save(contractRevision);
            return  Optional.of(map.get("id"));
        } catch (Exception e) {
             e.printStackTrace();
             return Optional.of("");
        }
    }

    /**
     * 生成合同唯一编码和合同编号
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
    public int getUnFinished(String companyCode,String operator,String startTime, String endTime,String supplierCode) throws Exception {

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
        @CacheEvict(value="contract_revision_recordTemp_detail;1800",key = "#id"),
        @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    })
    @Transactional
    public boolean saveProduct(String productId, BigDecimal price, BigDecimal amount, String id, int revision,String companyCode,String operator){
        try {
             ContractRevisionDetail contractRevisionDetail =getContractRevisionDetail(id, revision).orElseThrow(() -> new IOException("请求的产品不存在"));
             List<ContractRecordTemp> contractRecordTemps = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractId(id);
            //查询明细最大顺序号
            String maxCode = contractRecordTempRepository.findMaxCode(id);
            if(maxCode==null)
                maxCode="0";
            //查询产品
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IOException("请求的产品不存在"));
            //货物税率
            TaxRates goods= vatRatesRepository.findByTypeAndDeflagAndUseCountry(
                VatRateType.GOODS,
                Whether.YES,
                "001"
            ).orElseThrow(() -> new IOException("请求的货物税率不存在"));
            ContractRecordTemp contractRecordTemp = createContractRecordTemp(
                id,
                revision,
                Integer.parseInt(maxCode)+1,
                product,
                price,
                contractRevisionDetail.getOfferMode(),
                amount,
                goods.getRate()
            );
            contractRecordTemps.add(contractRecordTemp);
            contractRecordTempRepository.save(contractRecordTemp);
            return  countSum(contractRecordTemps,id,revision,operator);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建合同明细临时记录
     * @param id 合同主键
     * @param revision 版本号
     * @param code 序号
     * @param product 产品
     * @param price 价格
     * @param taxMode 税模式
     * @param amount 数量
     * @param vatRate 税率
     * @return 临时记录实体
     */
    public ContractRecordTemp createContractRecordTemp(String id,int revision,int code,Product product,BigDecimal price,
                                                       TaxMode taxMode,BigDecimal amount,BigDecimal vatRate){
        return ContractRecordTemp.builder()
            .contractRecordTempId(ContractRecordTempId.builder()
                .revision(revision)
                .contractId(id)
                .code(code)
                .build())
            .productId(product.getId())
            .productCode(product.getCode())
            .productId(product.getId())
            .type(VatRateType.GOODS)
            .productCode(product.getCode())
            .brandCode(product.getBrandCode())
            .brand(product.getBrand())
            .productDescription(product.getDescribe())
            .facePrice(product.getFacePrice())
            .chargeUnit(product.getChargeUnit())
            .price(price!=null?taxMode.equals(TaxMode.UNTAXED)?price:calculateUntaxedUnitPrice(price,vatRate):null)
            .priceVat(price!=null?taxMode.equals(TaxMode.INCLUDED)?price:calculateTaxedUnitPrice(price,vatRate):null)
            .amount(amount)
            .myAmount(amount)
            .createdAt(LocalDateTime.now())
            .ratio(new BigDecimal("1"))
            .myChargeUnit(product.getChargeUnit())
            .stockTime(0)
            .vatRate(vatRate)
            .totalPrice(price!=null?taxMode.equals(TaxMode.UNTAXED)?calculateSubtotal(price,amount):calculateSubtotal(calculateUntaxedUnitPrice(price,vatRate),amount):null)
            .totalPriceVat(price!=null?taxMode.equals(TaxMode.INCLUDED)?calculateSubtotal(price,amount):calculateSubtotal(calculateTaxedUnitPrice(price,vatRate),amount):null)
            .build();
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
        @CacheEvict(value="contract_revision_recordTemp_detail;1800",key = "#id"),
        @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    })
    @Transactional
    public Boolean deleteContractProduct(List<Integer> codes,String id,int revision,String companyCode,String operator){
        try{
            List<ContractRecordTemp> contractRecordTemps = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractId(id).stream().filter(contractRecordTemp -> !codes.contains(contractRecordTemp.getContractRecordTempId().getCode()))
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
     * 查询最大版本号
     * @param id 合同主键
     * @return 版本号
     * @throws IOException 异常
     */
    public Integer findMaxRevision(String id) throws IOException {
        return   Integer.parseInt(contractRevisionDetailRepository.findMaxRevision(id).orElseThrow(()->new IOException("不存在该合同")));
    }

    /**
     * 将采购合同的状态设置为未确认的状态，生成新一版合同，生成临时合同明细记录
     * @param id 合同主键
     * @param companyCode 本单位编码
     * @param operator 操作员编码
     * @return  返回版本号
     */
    @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    @Transactional
    public Integer modifyContractState(String id, String companyCode, String operator, Integer revision){
        try{

         Optional<ContractRevisionDetail> contractRevisionDetail = getContractRevisionDetail(id, revision);
          ContractRevision contractRevision =  contractRevisionDetail
              .map(contractMapper::toContractRevision).orElseThrow(()->new IOException("不存在该合同"));
          if(contractRevisionDetail.get().getState().equals(ContractState.UN_FINISHED.getState()+""))
              throw new Exception("该合同已经是未确认的，不可再次修改");
          contractDetailRepository.updateContractState(ContractState.UN_FINISHED,id);
          contractRevision.getContractRevisionId().setRevision(revision+1);
          contractRevision.setCreatedAt(LocalDateTime.now());
          contractRevision.setFingerprint(null);
          contractRevision.setConfirmedAt(null);
          contractRevision.setConfirmedBy(null);
          contractRevision.setModifiedAt(LocalDateTime.now());
          contractRevision.setModifiedBy(operator);
          contractRevisionRepository.save(contractRevision);
          List<ContractRecordTemp> contractRecordTemps= contractRecordRepository.findContractRecordsByContractRecordId_ContractIdAndContractRecordId_Revision(id,revision)
              .stream().map(contractRecordMapper::toContractRecordTemp).toList();
          contractRecordTempRepository.saveAll(contractRecordTemps);
            return revision+1;
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
        @CacheEvict(value="contract_revision_recordTemp_detail;1800",key = "#id"),
        @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    })
    @Transactional
    public  Boolean  modifyPurchaseContract(VModifyInquiryRequest vModifyInquiryRequest, String id, int revision,String companyCode,String operator){
        try{
            ContractRevision contractRevision =contractRevisionRepository.findById(ContractRevisionId.builder()
                    .revision(revision)
                    .id(id)
                .build()).orElseThrow(() -> new IOException("请求的询价单不存在"));
            List<ContractRecordTemp> contractRecordTemps = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractId(id);
            contractRevision.setOfferMode(StringUtils.isNotBlank(vModifyInquiryRequest.getTaxModel())?vModifyInquiryRequest.getTaxModel().equals("0")?TaxMode.UNTAXED:TaxMode.INCLUDED:contractRevision.getOfferMode());
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
                        if(vProduct.getAmount()!=null) {
                            record.setMyAmount(vProduct.getAmount());
                            record.setAmount(record.getAmount() != null ? vProduct.getAmount().multiply(record.getRatio()) : vProduct.getAmount());
                        }
                        record.setVatRate(vProduct.getVatRate()!=null?vProduct.getVatRate():record.getVatRate());
                        record.setPrice(vProduct.getPrice()!=null&&vProduct.getPrice().intValue()<0?
                            record.getPrice():vProduct.getPrice()!=null&&vProduct.getPrice().intValue()>=0?
                            contractRevision.getOfferMode().equals(TaxMode.UNTAXED)?vProduct.getPrice():calculateUntaxedUnitPrice(vProduct.getPrice(),record.getVatRate()):record.getPrice());
                        record.setPriceVat(vProduct.getPrice()!=null&&vProduct.getPrice().intValue()<0?
                            record.getPriceVat():vProduct.getPrice()!=null&&vProduct.getPrice().intValue()>=0?
                            contractRevision.getOfferMode().equals(TaxMode.INCLUDED)?vProduct.getPrice():calculateTaxedUnitPrice(vProduct.getPrice(),record.getVatRate()):record.getPriceVat());
                        record.setTotalPrice(record.getPrice()==null?null:calculateSubtotal(record.getPrice(),record.getMyAmount()));
                        record.setTotalPriceVat(record.getPriceVat()==null?null:calculateSubtotal(record.getPriceVat(),record.getMyAmount()));
                    }
                }));
            }
            contractRecordTempRepository.saveAll(contractRecordTemps);
            return  countSum(contractRecordTemps,id,revision,operator);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 导出产品模板
     * @param id 询价单主键或者合同主键
     * @return 产品列表
     */
    public List<LinkedHashMap<String,Object>> exportProduct(String id,Integer revision){
        List<LinkedHashMap<String,Object>> list = new ArrayList<>();
        try{
            var contract = purchaseContractDetail(id,revision);
            contract.getProducts().forEach(record -> {
                LinkedHashMap<String,Object> m = new LinkedHashMap<>();
                m.put("产品编码",record.getCode());
                if(contract.getOfferMode().equals(TaxMode.UNTAXED.getTaxMode()+"")) {
                    m.put("未税单价", record.getPrice());
                }else{
                    m.put("含税单价", record.getPriceVat());
                }
                m.put("数量", record.getAmount());
                list.add(m);
            });
            if(list.size()==0){
                LinkedHashMap<String,Object> m = new LinkedHashMap<>();
                m.put("产品编码","");
                if(contract.getOfferMode().equals(TaxMode.UNTAXED.getTaxMode()+"")) {
                    m.put("未税单价","");
                }else{
                    m.put("含税单价", "");
                }
                m.put("数量","");
                list.add(m);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 保存导入产品为询价单明细
     * @param id 询价单id
     * @param companyCode 单位id
     * @param operator 操作员编码
     * @return 返回成功或者失败信息
     */
    @Caching(evict = {
        @CacheEvict(value="contract_revision_detail;1800",key = "#id+'-'+#revision"),
        @CacheEvict(value="contract_revision_recordTemp_detail;1800",key = "#id"),
        @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    })
    @Transactional
    public Map<String,Object> saveImportProducts(String id,String companyCode,String operator,int revision){
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("code",500);
        resultMap.put("message","保存失败");
        try {
            List<ImportProductTemp> list=importProductTempRepository.
                findImportProductTempsByImportProductTempId_DcCompIdAndImportProductTempId_OperatorAndImportProductTempId_InquiryId(companyCode,operator,id);
            contractRecordTempRepository.deleteProducts(id);
            List<ContractRecordTemp> contractRecordTemps = new ArrayList<>();
            //货物税率
            TaxRates goods= vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.GOODS,Whether.YES,"001")
                .orElseThrow(() -> new IOException("请求的货物税率不存在"));
            //查询采购合同
            var contract = getContractRevisionDetail(id,1).orElseThrow(() -> new IOException("请求的合同不存在"));
            int maxCode =1;
            for (ImportProductTemp importProductTemp : list) {
                //验证产品编码是否正确
                Product product = productRepository.
                    findProductByCodeAndBrandCode(
                        importProductTemp.getCode(),
                        importProductTemp.getBrandCode()
                    )
                    .orElseThrow(() -> new IOException("请求的产品不存在"));
                ContractRecordTemp contractRecordTemp =   createContractRecordTemp(id,1,maxCode,product,
                    StringUtils.isNotBlank(importProductTemp.getPrice())?new BigDecimal(importProductTemp.getPrice()):null,
                    contract.getOfferMode(),
                    new BigDecimal(importProductTemp.getAmount()),contract.getVatProductRate() != null ? contract.getVatProductRate() : goods.getRate());
                contractRecordTemps.add(contractRecordTemp);
                maxCode++;
            }
            //删除原有的产品明细
            importProductTempRepository.deleteProduct(id,companyCode,operator);
            contractRecordTempRepository.saveAll(contractRecordTemps);
            if(countSum(contractRecordTemps,id,1,operator))
                resultMap.put("code",200);
            resultMap.put("message","保存成功");
        }catch (Exception e){
            e.printStackTrace();
            return resultMap;
        }
        return resultMap;
    }

    /**
     * 查询合同基本信息
     * @param id 合同主键
     * @return 返回合同基本信息
     * @throws IOException 异常
     */
    public ContractDetail getContractDetail(String id) throws IOException {
        return   contractDetailRepository.findById(id).orElseThrow(()->new IOException("请求的合同不存在"));
    }

    /**
     * 撤销该版本合同
     * @param id  合同主键
     * @param revision 版本
     * @param contractDetail 合同基本信息
     */
    @Caching(evict = {@CacheEvict(value="contract_revision_detail;1800",key = "#id+'-'+#revision"),
        @CacheEvict(value="contract_revision_recordTemp_detail;1800",key = "#id"),
        @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    })
    @Transactional
    public void cancelCurrentRevision(String id, int revision, ContractDetail contractDetail, String companyCode) {

            contractDetail.setState(ContractState.FINISHED);
            if(revision==1){
                contractDetail.setState(ContractState.CANCELLATION);
            }else{
                contractRevisionRepository.deleteById(ContractRevisionId.builder()
                    .id(id)
                    .revision(revision)
                    .build());
            }
            contractDetailRepository.save(contractDetail);
            contractRecordTempRepository.deleteProducts(id);
            deliveryTempRepository.deleteDeliverTempsByDeliverTempId_ContracId(id);

    }

    /**
     * 保存退回的临时记录
     * @param list 退回的临时记录列表
     * @param id 合同主键
     * @param revision 版本号
     */
    @Transactional
    public void saveDeliveryTemp(List<VDeliveryTempRequest> list, String id, Integer revision) throws Exception{
        //删除上次的数据
        deliveryTempRepository.deleteDeliverTempsByDeliverTempId_ContracId(id);
        List<ContractRecordTemp> contractRecordTemps = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractId(id);
        Map<String,ContractRecordTemp> map = new HashMap<>();
        contractRecordTemps.forEach(contractRecordTemp -> map.put(contractRecordTemp.getProductId(),contractRecordTemp));
        List<DeliverTemp> deliverTemps = new ArrayList<>();
        AtomicInteger maxCode = new AtomicInteger(1);
        for (VDeliveryTempRequest v : list) {
            ContractRecordTemp temp = map.get(v.getProductId());
                if(v.getReturnAmount().floatValue()>0){
                    if(temp.getProductId().equals(v.getProductId())){
                        DeliverTemp deliverTemp = DeliverTemp.builder()
                            .deliverTempId(
                                DeliverTempId.builder()
                                    .contracId(id)
                                    .code(maxCode.get())
                                    .build()
                            )
                            .type(DeliverType.RECEIVE)
                            .productId(v.getProductId())
                            .productCode(temp.getProductCode())
                            .brandCode(temp.getBrandCode())
                            .brand(temp.getBrand())
                            .customerPCode(temp.getCustomerCustomCode())
                            .localPCode(temp.getCompCustomCode())
                            .productDescription(temp.getProductDescription())
                            .chargeUnit(temp.getChargeUnit())
                            .myChargeUnit(temp.getMyChargeUnit())
                            .ratio(temp.getRatio())
                            .myAmount(v.getReturnAmount())
                            .amount(v.getReturnAmount().multiply(temp.getRatio()))
                            .build();
                        deliverTemps.add(deliverTemp);
                }
               }
            maxCode.getAndIncrement();
        }
        deliveryTempRepository.saveAll(deliverTemps);
    }

    /**
     * 查询合同是否重复（产品种类和数量）
     * @param id 采购合同id
     * @param revision 版本号
     * @return 返回 true 或者 false
     */
    public Optional<String>  findContractProductRepeat(String id, Integer revision) throws Exception {
        ContractRevisionDetail contractRevisionDetail = contractRevisionDetailRepository
            .findDetail(revision,id)
            .orElseThrow(()->new IOException("请求的合同不存在"));

        if(!contractRevisionDetail.getState().equals(ContractState.UN_FINISHED.getState()+""))
            throw new Exception("合同已确认");
        List<ContractRecordTemp> contractRecordTemps = contractRecordTempRepository
            .findContractRecordTempsByContractRecordTempId_ContractId(id);

        String str = createSequenceCode(
            contractRecordTemps.stream()
                .map(record->
                    record.getProductId() + "-" + record.getAmount()
                ).toList(),
            contractRevisionDetail.getSalerComp(),
            contractRevisionDetail.getBuyerComp()
        );
        //产品种类和数量相同的合同号
        List<String> contractId = contractRepository.findContractId(contractRevisionDetail.getCreatedByComp(),str);

        return Optional.of(String.join(",", contractId));
    }

    /**
     * 生成新一版的采购合同
     * @param id 合同主键
     * @param revision 版本
     * @param generateContractRequest 保存内容
     * @param companyCode 公司编码
     * @param operator 操作员编码
     */
    @Caching(evict = {@CacheEvict(value="contract_revision_detail;1800",key = "#id+'-'+#revision"),
        @CacheEvict(value="contract_revision_recordTemp_detail;1800",key = "#id"),
        @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    })
    @Transactional
    public void saveContractRevision(String id, int revision, VGenerateContractRequest generateContractRequest, String companyCode, String operator) {
      try{
          //合同明细
          List<ContractRecordTemp> contractRecordTemps = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractId(id);
          //查询合同详情
          ContractDetail contractDetail = contractDetailRepository.findById(id).orElseThrow(()->new IOException("请求的合同不存在"));
          ContractRevision contractRevision = contractRevisionRepository.findById(ContractRevisionId.builder()
                  .revision(revision)
                  .id(id)
                  .build())
              .orElseThrow(()->new IOException("请求的合同不存在"));
          if(!contractDetail.getState().equals(ContractState.UN_FINISHED)){
              throw new Exception("合同已确认");
          }
          contractRevision.setOrderCode(generateContractRequest.getContactNo());
          contractRevision.setSalerOrderCode(generateContractRequest.getSupplierNo());
          //供应商联系人
          contractRevision.setSalerContactName(generateContractRequest.getSupplierContactName());
          //供应商联系人电话
          contractRevision.setSalerContactPhone(generateContractRequest.getSupplierContactPhone());
          //地址
          if(StringUtils.isNotBlank(generateContractRequest.getAddressCode())){
              //查找地址
              Address address =addressRepository.findById(
                  AddressId.builder()
                      .dcCompId(companyCode)
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
                  companyCode,
                  generateContractRequest.getContactCode()
              ).orElseThrow(()-> new IOException("数据库中找不到该联系人"));
              contractRevision.setConsigneeName(compContacts.getContName());
              contractRevision.setConsigneePhone(compContacts.getContPhone());
          }
          contractRevision.setConfirmTotalPriceVat(generateContractRequest.getSum());
          //判断产品单价是否为空
          List<ContractRecordTemp> list = contractRecordTemps
              .stream()
              .filter(record -> record.getPrice()==null)
              .toList();
          //合同明细
          List<ContractRecord> records = new ArrayList<>();
          //计算折扣，折扣后价格，税额
          if(list.size()==0){
              BigDecimal discount = (contractRevision.getTotalPriceVat().subtract(generateContractRequest.getSum())).divide(contractRevision.getTotalPriceVat(),4, RoundingMode.HALF_UP);
              //计算折扣的含税价格 和未税价格以及小计等
              BigDecimal discountSumVat = new BigDecimal("0");
              BigDecimal discountSum = new BigDecimal("0");
              for (int i  =0;i< contractRecordTemps.size();i++){
                  ContractRecord record =   Optional.of(contractRecordTemps.get(i)).map(contractRecordMapper::toContractRecord).orElse(null);
                  record.getContractRecordId().setCode(i+1);
                  record.setDiscount(discount);
                  record.setDiscountedPrice(calculateDiscountedPrice(record.getPrice(),discount));
                  record.setTotalDiscountedPrice(calculateDiscountedSubtotal(record.getDiscountedPrice(),discount,record.getMyAmount()));
                  record.setDiscountedPriceVat(calculateDiscountedPrice(record.getPriceVat(),discount));
                  if(i==contractRecordTemps.size()-1){
                      record.setTotalDiscountedPriceVat(contractRevision.getConfirmTotalPriceVat().subtract(discountSumVat));
                  }else {
                      record.setTotalDiscountedPriceVat(calculateDiscountedSubtotal(record.getPriceVat(),discount,record.getMyAmount()));
                      discountSumVat = discountSumVat.add(record.getTotalDiscountedPriceVat());
                  }
                  discountSum=discountSum.add(record.getTotalDiscountedPrice());
                  records.add(
                      record
                  );
              }
              contractRevision.setDiscountedTotalPrice(discountSum);
              contractRevision.setVat(contractRevision.getConfirmTotalPriceVat().subtract(discountSum));
              contractRevision.setDiscount(discount);
          }

            /*
              获取序列编码
             */
          String str = createSequenceCode(
              recordSort(records),
              contractDetail.getSalerComp(),
              contractDetail.getBuyerComp()
          );
          contractRecordTempRepository.deleteProducts(id);
          contractRevision.setConfirmedAt(LocalDateTime.now());
          contractRevision.setConfirmedBy(operator);
          contractDetail.setState(ContractState.FINISHED);
          //保存合同
          contractDetailRepository.save(contractDetail);
          contractRevision.setFingerprint(str);
          contractRevisionRepository.save(contractRevision);
          contractRecordRepository.saveAll(records);
          //将退回记录录保存到货运信息表中
          saveDelivery(id, companyCode, operator);
      }catch (Exception e){
          e.printStackTrace();
      }

    }

    /**
     * 将退回的产品生产货运记录
     * @param id 合同id
     * @param companyCode 本单位编码
     * @param operator 操作员编码
     */
    public void saveDelivery(String id, String companyCode, String operator){
        // TODO: 2022/6/1 需要完善货运记录 库存等问题
        List<DeliverRecord> deliverRecords = deliveryTempRepository.findDeliverTempsByDeliverTempId_ContracId(id)
            .stream().map(deliverRecordMapper::toDeliverRecord).toList();
        if(deliverRecords.size()>0){
            UUID uuid = UUID.randomUUID();
            String deliveryId = "HY-"+companyCode+"-"+operator+"-"+uuid.toString().substring(0,8);
            deliverRecords.forEach(deliverRecord -> {
                deliverRecord.getDeliverRecordId().setDeliverCode(deliveryId);
                deliverRecord.setType(DeliverType.RECEIVE);
            });
            DeliverBase deliverBase = DeliverBase.builder()
                .id(deliveryId)
                .contractId(id)
                .type(DeliverType.RECEIVE)
                .createdAt(LocalDateTime.now())
                .createdBy(operator)
                .createdByComp(companyCode)
                .state(DeliverState.PENDING)
                .deliverRecords(deliverRecords)
                .build();
            deliverBaseRepository.save(deliverBase);
        }
    }

    /**
     * 查询采购合同中已开票产品列表
     * @param id 合同主键
     * @return 产品列表
     */
    // TODO: 2022/6/2   需要重新完善
    public List<VInvoicedResponse.VProduct> getInvoicedList(String id ){
        List<VInvoicedResponse.VProduct> products = new ArrayList<>();
        VInvoicedResponse.VProduct product = new VInvoicedResponse.VProduct();
        product.setId("1616490002");
        product.setCode("161546001");
        product.setDescribe("活接 PVDF-HP/FKM SDR21 PN16 d20");
        product.setChargeUnit("个");
        product.setAmount(new BigDecimal("1"));
        product.setInvoiceAmount(new BigDecimal("98.4").setScale(2,RoundingMode.HALF_UP));
        products.add(product);
        VInvoicedResponse.VProduct product1 = new VInvoicedResponse.VProduct();
        product1.setId("150000001");
        product1.setCode("150000001");
        product1.setDescribe("活接 PVDF-HP/FKM SDR21 PN16 d20");
        product1.setChargeUnit("个");
        product1.setAmount(new BigDecimal("4"));
        product1.setInvoiceAmount(new BigDecimal("198.55").setScale(2,RoundingMode.HALF_UP));
        products.add(product1);
        return products;
    }

    /**
     * 查询采购合同中已收货物产品列表
     * @param id 合同主键
     * @return 产品列表
     */
    // TODO: 2022/6/2   需要重新完善
    public List<VReceivedResponse.VProduct> getReceivedList(String id){
       return contractReceivedRepository.findContractReceivedList(id)
           .stream().map(contractMapper::toTContractReceived)
           .map(contractMapper::toVProduct)
           .toList();
    }

    /**
     * 撤销采购合同
     * @param id 合同主键
     * @param companyCode 本单位编码
     * @param operator 操作员编码
     * @throws Exception 异常
     */
    @CacheEvict(value="purchase_contract_List;1800",key = "#companyCode+'-'",allEntries=true)
    @Transactional
    public void cancelPurchaseContract(String id, String companyCode, String operator) throws Exception{
        ContractDetail contractDetail = contractDetailRepository.findById(id)
            .orElseThrow(()->new IOException("请求的合同不存在"));
        int revision = findMaxRevision(id);
        if(contractDetail.getState().equals(ContractState.UN_FINISHED)&& revision>1){
            contractRecordTempRepository.deleteProducts(id);
            contractRevisionRepository.deleteById(ContractRevisionId.builder()
                    .id(id)
                    .revision(revision)
                .build());
        }else if(contractDetail.getState().equals(ContractState.UN_FINISHED)&& revision==1){
            List<ContractRecord> records = new ArrayList<>();
            List<ContractRecordTemp> contractRecordTemps = contractRecordTempRepository.findContractRecordTempsByContractRecordTempId_ContractId(id);
            for (int i  =0;i< contractRecordTemps.size();i++){
                ContractRecord record =   Optional.of(contractRecordTemps.get(i)).map(contractRecordMapper::toContractRecord).orElse(null);
                record.getContractRecordId().setCode(i+1);
                records.add(
                    record
                );
            }
            contractRecordRepository.saveAll(records);
        }else  if(contractDetail.getState().equals(ContractState.CANCELLATION)){
            throw  new Exception("合同已撤销");
        }else{

        }
        contractDetail.setState(ContractState.CANCELLATION);
        saveDelivery(id, companyCode, operator);
    }

    /**
     * 采购合同预览列表
     * @param id 主键
     * @param revision 版本号
     * @return 采购合同预览列表
     */
    public List<VModifyContractPreviewResponse.VProduct> modifyContractPreview(String id,Integer revision){
        return  contractRecordPreviewRepository.findContractRecordPreviewRepositories(id)
            .stream().map(contractRecordMapper::toTContractRecordPreview)
            .map(contractRecordMapper::toVProduct)
            .toList();
    }

    /**
     * 更新合同总价
     * @param contractRecordTemps 合同明细列表
     * @param id 合同主键
     * @return 返回成功或者失败信息
     */
    public  boolean countSum(List<ContractRecordTemp> contractRecordTemps,
                             String id,
                             int revision,
                             String operator){
        try{
            //判断是否需要重新计算价格
            List<ContractRecordTemp> lists = contractRecordTemps
                .stream()
                .filter(contractRecordTemp -> contractRecordTemp.getPrice()==null)
                .toList();
            //是 重新计算价格
            BigDecimal totalPrice=new BigDecimal("0");
            BigDecimal totalPriceVat=new BigDecimal("0");
            BigDecimal vat;
            if(lists.size()==0){
                for (ContractRecordTemp contractRecordTemp:contractRecordTemps){
                    totalPrice=totalPrice.add(contractRecordTemp.getTotalPrice());
                    totalPriceVat=totalPriceVat.add(contractRecordTemp.getTotalPriceVat());
                }
                vat = totalPriceVat.subtract(totalPrice).setScale(2, RoundingMode.HALF_UP);
            }else{
                totalPrice=null;
                totalPriceVat=null;
                vat=null;
            }
            BigDecimal totalPrice1 = totalPrice == null ? null : totalPrice.setScale(2, RoundingMode.HALF_UP);
            contractRevisionDetailRepository.updateContract(
                totalPrice1,
                totalPriceVat==null?null:totalPriceVat.setScale(2, RoundingMode.HALF_UP),
                vat,
                LocalDateTime.now(),
                operator,
                id,
                revision
            );
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 计算未税单价
     * @param price 单价
     * @param vatRate 税率
     * @return 未税单价
     */
    public BigDecimal calculateUntaxedUnitPrice(BigDecimal price,BigDecimal vatRate){
        return price.divide(new BigDecimal("1").add(vatRate),4, RoundingMode.HALF_UP);
    }

    /**
     * 计算含税单价
     * @param price 单价
     * @param vatRate 税率
     * @return 含税单价
     */
    public BigDecimal calculateTaxedUnitPrice(BigDecimal price,BigDecimal vatRate){
        return price.multiply(new BigDecimal("1").add(vatRate))
            .setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 计算小计
     * @param price 单价
     * @param amount 数量
     * @return 小计
     */
    public BigDecimal calculateSubtotal(BigDecimal price,BigDecimal amount){
        return price.multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算折扣后单价
     * @param price  单价
     * @param discount 折扣
     * @return 折扣后单价
     */
    public BigDecimal calculateDiscountedPrice(BigDecimal price,BigDecimal discount){
        return  price.multiply(new BigDecimal("1").subtract(discount)).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 计算折扣后小计
     * @param price 单价
     * @param amount 数量
     * @param discount 折扣
     * @return 折扣后小计
     */
    public BigDecimal calculateDiscountedSubtotal(BigDecimal price,BigDecimal discount,BigDecimal amount){
        return price.multiply(new BigDecimal("1").subtract(discount)).multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }
}
