package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.dto.TImportProductTemp;
import com.linzhi.gongfu.dto.TInquiry;
import com.linzhi.gongfu.dto.TInquiryRecord;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.ImportProductTempMapper;
import com.linzhi.gongfu.mapper.InquiryMapper;
import com.linzhi.gongfu.mapper.InquiryRecordMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.util.ExcelUtil;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.VImportProductTempRequest;
import com.linzhi.gongfu.vo.VImportProductTempResponse;
import com.linzhi.gongfu.vo.VModifyInquiryRequest;
import com.linzhi.gongfu.vo.VUnfinishedInquiryListResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * 采购询价信息处理及业务服务
 *
 * @author zgh
 * @create_at 2022-02-10
 */
@RequiredArgsConstructor
@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMapper inquiryMapper;
    private final InquiryRecordMapper inquiryRecordMapper;
    private final InquiryDetailRepository inquiryDetailRepository;
    private final OperatorRepository operatorRepository;
    private final CompanyRepository companyRepository;
    private final CompTradeRepository compTradeRepository;
    private final InquiryRecordRepository inquiryRecordRepository;
    private final ProductRepository productRepository;
    private final TaxRatesRepository vatRatesRepository;
    private final ImportProductTempRepository importProductTempRepository;
    private final ImportProductTempMapper importProductTempMapper;
    private final PurchasePlanProductSupplierRepository purchasePlanProductSupplierRepository;
    private final ContractRepository contractRepository;
    private final PurchasePlanProductRepository purchasePlanProductRepository;
    private final PurchasePlanRepository purchasePlanRepository;
    private final UnfinishedInquiryRepository unfinishedInquiryRepository;
  //  private final ContractService contractService;
    private final CompTaxModelRepository compTaxModelRepository;
    private final CompanyService companyService;

    /**
     * 保存询价单
     * @param planCode 采购计划
     * @param companyCode 单位id
     * @param compName 单位名称
     * @param operatorCode 操作员编码
     * @return 返回成功信息
     */
    @CacheEvict(value="inquiry_List;1800", key="#companyCode+'_'",allEntries=true)
    @Transactional
    public Map<String,Object> savePurchaseInquiry(String planCode, String companyCode,String compName, String operatorCode){
        Map<String,Object> resultMap = new HashMap<>();
        try{
            Map<String,List<InquiryRecord>> supplierInquiryRecordMap = new HashMap<>();
            List<String> suppliers = new ArrayList<>();
            List<InquiryDetail> inquiries = new ArrayList<>();
            //查找采购计划
            Optional<PurchasePlan> purchasePlan = purchasePlanRepository.findById(
                PurchasePlanId.builder()
                    .dcCompId(companyCode)
                    .planCode(planCode)
                    .build()
            );
            if(purchasePlan.isEmpty()) {
                resultMap.put("code", 404);
                resultMap.put("message", "找不到该数据");
                return resultMap;
            }
            //查出货物税率
            TaxRates goods= vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.GOODS,Whether.YES,"001").orElseThrow(()-> new IOException("数据库中找不到该税率"));
            //查出服务税率
            // Optional<TaxRates> service=vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.SERVICE,Whether.YES,"001");
            //查出对应的销售合同
            ContractList salesContract = null;
            if(purchasePlan.get().getSalesId()!=null){
                 salesContract = contractRepository.findById(purchasePlan.get().getSalesId()).orElseThrow(()-> new IOException("数据库中找不到该销售合同"));
            }
            //查询每个供应商税模式对本单位设置的税模式
            List<CompTrad>compTades=compTradeRepository.findSuppliersByCompTradId_CompBuyerAndState(companyCode, Trade.TRANSACTION);
            Map<String,CompTrad> compTradMap = new HashMap<>();
            compTades.forEach(compTrad -> compTradMap.put(compTrad.getCompTradId().getCompSaler(),compTrad));
            //查出向每个供应商询价商品且询价数量>0的有哪些
            purchasePlan.get().getProduct().forEach(purchasePlanProduct -> purchasePlanProduct.getSalers().forEach(supplier -> {
                if(supplier.getDemand().intValue()>0) {
                    InquiryRecord record = getInquiryRecord(
                        Product.builder()
                        .id(purchasePlanProduct.getPurchasePlanProductId().getProductId())
                        .code(purchasePlanProduct.getProductCode())
                        .brand(purchasePlanProduct.getBrand())
                        .brandCode(purchasePlanProduct.getBrandCode())
                        .describe(purchasePlanProduct.getDescribe())
                        .chargeUnit(purchasePlanProduct.getChargeUnit())
                        .facePrice(purchasePlanProduct.getFacePrice())
                            .build(),
                        null,null,    supplier.getDemand(),goods.getRate(),null,compTradMap.get(supplier.getPurchasePlanProductSupplierId().getSalerCode()).getTaxModel()
                    );
                    List<InquiryRecord> list = supplierInquiryRecordMap.get(supplier.getPurchasePlanProductSupplierId().getSalerCode());
                    if (list==null) {
                        list = new ArrayList<>();
                        suppliers.add(supplier.getPurchasePlanProductSupplierId().getSalerCode());
                    }
                    list.add(record);
                    supplierInquiryRecordMap.put(supplier.getPurchasePlanProductSupplierId().getSalerCode(),list);
                }
            }));

            //查询询价单最大编号
            String maxCode = inquiryDetailRepository.findMaxCode(companyCode, operatorCode);
            if(maxCode ==null)
                maxCode ="01";
            AtomicInteger max = new AtomicInteger(Integer.parseInt(maxCode));
            //对每个供应商生成询价单
            ContractList finalSalesContract = salesContract;
            companyRepository.findAllById(suppliers).forEach(company -> {

                    List<String> inquiryCodes = getInquiryCode(max.get()+"",operatorCode,companyCode,company.getRole().equals(CompanyRole.EXTERIOR_SUPPLIER.getSign())?company.getEncode():company.getCode());
                    List<InquiryRecord> records = supplierInquiryRecordMap.get(company.getCode());
                    AtomicInteger code = new AtomicInteger();
                    records.forEach(inquiryRecord -> {
                        code.getAndIncrement();
                        inquiryRecord.setInquiryRecordId(InquiryRecordId.builder().code(code.get()).inquiryId(inquiryCodes.get(0)).build());
                        inquiryRecord.setCreatedAt(LocalDateTime.now());
                    });
                    inquiries.add(
                        createInquiryDetail(inquiryCodes.get(0),
                            inquiryCodes.get(1),
                            companyCode,
                            operatorCode,
                            compName,
                            company.getCode(),
                            company.getNameInCN(),
                            compTradMap.get(company.getCode())==null? TaxMode.UNTAXED :compTradMap.get(company.getCode()).getTaxModel(),
                            finalSalesContract !=null? finalSalesContract.getId():null,
                            records
                        )
                    );
                    max.getAndIncrement();

            });
            //保存询价单
            inquiryDetailRepository.saveAll(inquiries);
            //删除计划
            purchasePlanProductSupplierRepository.deleteSupplier(companyCode,planCode);
            purchasePlanProductRepository.deleteProduct(companyCode, planCode);
            purchasePlanRepository.deletePurchasePlan(
                PurchasePlanId.builder()
                .dcCompId(companyCode)
                .planCode(planCode)
                .build()
            );
            resultMap.put("code",200);
            resultMap.put("message","生成询价单成功");
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("code",500);
            resultMap.put("message","生成询价单失败");
            return resultMap;
        }
    }

    /**
     * 查询询价单列表
     * @param companyCode 公司编码
     * @param operator 操作员编码
     * @param state   状态
     * @return 返回询价列表
     */
    @Cacheable(value="inquiry_List;1800", key="#companyCode+'_'+#operator+'_'+#state")
    public List<TInquiry> inquiryList(String companyCode, String operator,String state){
        try{
            Operator operator1= operatorRepository.findById(
                OperatorId.builder()
                    .operatorCode(operator)
                    .companyCode(companyCode)
                    .build()
                )
                .orElseThrow(()-> new IOException("请求的操作员找不到"));
            if(operator1.getAdmin().equals(Whether.YES))
                return inquiryRepository.findInquiryList(companyCode, InquiryType.INQUIRY_LIST.getType()+"", state)
                    .stream()
                    .map(inquiryMapper::toInquiryList)
                    .toList();
            return inquiryRepository.findInquiryList(companyCode,operator, InquiryType.INQUIRY_LIST.getType()+"", state)
                .stream()
                .map(inquiryMapper::toInquiryList)
                .toList();
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    /**
     * 根据供应商编码查询未完成的询价单列表
     * @param companyCode 客户编码
     * @param operator 操作员
     * @param supplierCode 供应商编码
     * @return 返回未完成的询价单列表
     */
    public List<VUnfinishedInquiryListResponse.VInquiry> unfinishedInquiry(String companyCode,String operator,String supplierCode){

        return  unfinishedInquiryRepository.findUnfinishedInquiry(companyCode,operator,supplierCode).stream()
            .map(inquiryMapper::toUnfinishedTinquiry)
            .map(inquiryMapper::toUnfinishedInquiry).toList();
    }

    /**
     * 查询历史询价单列表
     * @param companyCode 单位id
     * @param operator 操作员编码
     * @param supplierCode 供应商编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param state 状态
     * @param pageable 页码
     * @return 返回询价单信息列表
     */
    public Page<TInquiry> inquiryHistoryPage(String companyCode, String operator,String supplierCode,
                                             String startTime,String endTime,String state,Pageable pageable)  {
        List<TInquiry> tInquiryList = inquiryList(companyCode,operator,state);
        if(StringUtils.isNotBlank(supplierCode)){
            tInquiryList = tInquiryList.stream().filter(tInquiry -> tInquiry.getSalerComp().equals(supplierCode)).toList();
        }
        if(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime)){
            DateTimeFormatter dateTimeFormatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatters = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTimes = LocalDate.parse(startTime, dateTimeFormatterDay).atStartOfDay();
            LocalDateTime endTimes = LocalDateTime.parse(endTime+" 23:59:59", dateTimeFormatters);
            tInquiryList = tInquiryList.stream().filter(tInquiry ->
            {
                LocalDateTime dateTime = LocalDateTime.parse(tInquiry.getCreatedAt(), dateTimeFormatters);
                return dateTime.isAfter(startTimes) && dateTime.isBefore(endTimes);
            }
            ).toList();
        }
        return PageTools.listConvertToPage(tInquiryList,pageable);
    }


    /**
     * 询价单详情
     * @param id 询价单主键
     * @return 返回询价单详情
     */
    public TInquiry inquiryDetail(String id) {
      TInquiry tInquiry= findInquiry(id).map(inquiryMapper::toInquiryDetail).get();

        List<InquiryRecord>  records  = findInquiryRecords(id);
        List<TInquiryRecord>  tRecords = findInquiryRecords(id).stream().map(inquiryRecordMapper::toTInquiryRecordDo).toList();
        tInquiry.setRecords(tRecords);
        tInquiry.setVat(judgeInquiryMoney(tInquiry.getVat(),records));
        tInquiry.setTotalPrice(judgeInquiryMoney(tInquiry.getTotalPrice(),records));
        tInquiry.setTotalPriceVat(judgeInquiryMoney(tInquiry.getTotalPriceVat(),records));
        return  tInquiry;
    }

    /**
     * 查询询价单
     * @param id 询价单编码
     * @return 询价单信息
     */
    @Cacheable(value="inquiry_detail;1800",key = "#id")
    public Optional<Inquiry> findInquiry(String id ){
        return  inquiryRepository.findInquiryById(id);
    }

    /**
     * 查询询价单明细
     * @param id 询价单id
     * @return 询价单明细列表
     */
    @Cacheable(value="inquiry_record_List;1800",key = "#id")
    public List<InquiryRecord> findInquiryRecords(String id){return  inquiryRecordRepository.findInquiryRecord(id);}

    /**
     * 建立新的空询价单
     * @param companyCode 单位id
     * @param companyName 单位名称
     * @param operator 操作员id
     * @param supplierCode 供应商名称
     * @return 询价单编码
     */
    @CacheEvict(value="inquiry_List;1800", key="#companyCode+'_'",allEntries=true)
    public String  emptyInquiry(String companyCode,String companyName,String operator,String supplierCode){
        try {
            //查询询价单最大编号
            String maxCode = inquiryDetailRepository.findMaxCode(companyCode, operator);
            if(maxCode ==null)
                maxCode ="01";
            //查询供应商信息
            Company supplier = companyRepository.findById(supplierCode).orElseThrow(()->new IOException("数据库中未查询到该供应商"));
            String supplierName = supplier.getNameInCN();
            List<String> inquiryCodes = getInquiryCode(maxCode,operator,companyCode,supplier.getRole().equals(CompanyRole.EXTERIOR_SUPPLIER.getSign())?supplier.getEncode():supplierCode);
            //税模式
            CompTaxModel taxModel = compTaxModelRepository.findById(CompTradId.builder()
                .compSaler(supplierCode)
                .compBuyer(companyCode)
                .build()).orElseThrow(()->new IOException("从数据库中没有查询到"));
            TaxMode taxMode = taxModel.getTaxModel();
            inquiryDetailRepository.save(
                createInquiryDetail(inquiryCodes.get(0),
                    inquiryCodes.get(1),
                    companyCode,
                    operator,
                    companyName,

                    supplierCode,
                    supplierName,
                    taxMode,null,null
                )
            );
            return  inquiryCodes.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 保存产品
     * @param id 询价单编码
     * @param productId 产品编码
     * @param price 价格
     * @param amount 数量
     * @return 返回成功或者失败信息
     */
    @Caching(evict = {@CacheEvict(value="inquiry_detail;1800",key = "#id"),
        @CacheEvict(value="inquiry_record_List;1800", key="#id")
    })
    @Transactional
    public Boolean saveInquiryProduct(String id, String productId, BigDecimal price,BigDecimal amount){
        try{
            //查询询价单
            //InquiryDetail inquiry = inquiryDetail(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            Inquiry inquiry = findInquiry(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            List<InquiryRecord> inquiryRecordList = findInquiryRecords(id);
            //查询明细最大顺序号
            String maxCode = inquiryRecordRepository.findMaxCode(id);
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
            //保存产品inquiry = {InquiryDetail@17473}
            InquiryRecord record = getInquiryRecord(product,id,maxCode,amount,goods.getRate(),price,inquiry.getOfferMode());


            inquiryRecordRepository.save(record);
            inquiryRecordList.add(record);
            return  countSum(inquiryRecordList,id);
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    /**
     * 创建询价单实体
     * @param id 主键
     * @param   code 编码
     * @param companyCode 单位id
     * @param operator 操作员id
     * @param companyName 单位名称
     * @param supplierCode 供应商code
     * @param supplierName 供应商名称
     * @param taxMode 税模式
     * @param records 询价单明细列表
     * @return 询价单实体
     */
    public InquiryDetail createInquiryDetail(String id,String code,String companyCode,
                                             String operator ,String companyName,
                                             String supplierCode,String supplierName ,TaxMode taxMode,
                                             String salesContractId,List<InquiryRecord> records){
        return  InquiryDetail.builder()
            .id(id)
            .code(code)
            .type(InquiryType.INQUIRY_LIST)
            .createdByComp(companyCode)
            .createdBy(operator)
            .createdAt(LocalDateTime.now())
            .buyerComp(companyCode)
            .buyerCompName(companyName)
            .salerComp(supplierCode)
            .salerCompName(supplierName)
            .salesContractId(salesContractId)
            .state(InquiryState.UN_FINISHED)
            .offerMode(taxMode)
            .records(records)
            .build();
    }

    /**
     * 创建询价单明细实体
     * @param product 产品信息
     * @param id 询价单主键
     * @param maxCode 最大code
     * @param amount 数量
     * @param taxRates 税率
     * @return 询价单明细实体
     */
    public  InquiryRecord getInquiryRecord(Product product,String id,String maxCode,BigDecimal amount,BigDecimal taxRates,BigDecimal price,TaxMode taxMode){
        return InquiryRecord.builder()
            .inquiryRecordId(
                InquiryRecordId.builder()
                    .inquiryId(id)
                    .code(maxCode!=null?Integer.parseInt(maxCode)+1:null)
                    .build()
            )
            .createdAt(LocalDateTime.now())
            .amount(amount)
            .productId(product.getId())
            .type(VatRateType.GOODS)
            .productCode(product.getCode())
            .brandCode(product.getBrandCode())
            .brand(product.getBrand())
            .productDescription(product.getDescribe())
            .facePrice(product.getFacePrice())
            .chargeUnit(product.getChargeUnit())
            .stockTime(0)
            .vatRate(taxRates)
            .price(price!=null?taxMode.equals(TaxMode.UNTAXED)?price:calculateUntaxedUnitPrice(price,taxRates):null)
            .priceVat(price!=null?taxMode.equals(TaxMode.INCLUDED)?price:calculateTaxedUnitPrice(price,taxRates):null)
            .totalPrice(price!=null?taxMode.equals(TaxMode.UNTAXED)?calculateSubtotal(price,amount):calculateSubtotal(calculateUntaxedUnitPrice(price,taxRates),amount):null)
            .totalPriceVat(price!=null?taxMode.equals(TaxMode.INCLUDED)?calculateSubtotal(price,amount):calculateSubtotal(calculateTaxedUnitPrice(price,taxRates),amount):null)
            .build();
    }

    /**
     * 删除询价产品
     * @param id 询价单编码
     * @param codes 询价单明细条目号
     * @return 返回成功或者失败
     */
    @Caching(evict = {@CacheEvict(value="inquiry_detail;1800",key = "#id"),
        @CacheEvict(value="inquiry_record_List;1800", key="#id")
    })
    @Transactional
    public Boolean deleteInquiryProduct(String id,List<Integer> codes){
        try {
            List<InquiryRecord> records = findInquiryRecords(id);
            inquiryRecordRepository.deleteProducts(id,codes);
            return countSum(
                records.stream()
                    .filter(
                        record ->
                            !codes.contains(record.getInquiryRecordId().getCode())
                    ).toList(),
                id
            );
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改询价单
     * @param vModifyInquiryRequest 修改信息
     * @param id 询价单主键
     * @return 返回成功或者失败
     */
    @Caching(evict = {@CacheEvict(value="inquiry_detail;1800",key = "#id"),
        @CacheEvict(value="inquiry_record_List;1800", key="#id")
    })
    @Transactional
    public  Boolean  modifyInquiry(VModifyInquiryRequest vModifyInquiryRequest,String id){
        try{
            Inquiry inquiry = findInquiry(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            List<InquiryRecord> records = findInquiryRecords(id);
            if(StringUtils.isNotBlank(vModifyInquiryRequest.getTaxModel()))
                inquiry.setOfferMode(vModifyInquiryRequest.getTaxModel().equals("0")?TaxMode.UNTAXED:TaxMode.INCLUDED);
            if(vModifyInquiryRequest.getServiceVat()!=null) {
                inquiry.setVatServiceRate(vModifyInquiryRequest.getServiceVat());
                records.forEach(
                    record -> {
                        if(record.getType().equals(VatRateType.SERVICE))
                            record.setVatRate(vModifyInquiryRequest.getServiceVat());
                    }
                );
            }
            if(vModifyInquiryRequest.getGoodsVat()!=null) {
                inquiry.setVatProductRate(vModifyInquiryRequest.getGoodsVat());
                records.forEach(
                    record -> {
                        if(record.getType().equals(VatRateType.GOODS))
                            record.setVatRate(vModifyInquiryRequest.getGoodsVat());
                    }
                );
            }

            if(vModifyInquiryRequest.getProducts()!=null){
                vModifyInquiryRequest.getProducts().forEach(vProduct -> records.forEach(record -> {
                    if(record.getInquiryRecordId().getCode()==vProduct.getCode()){
                        if(vProduct.getAmount()!=null)
                            record.setAmount(vProduct.getAmount());
                        if(vProduct.getVatRate()!=null)
                            record.setVatRate(vProduct.getVatRate());
                        if(vProduct.getPrice()!=null&&vProduct.getPrice().intValue()<0) {
                                record.setPrice(null);
                                record.setPriceVat(null);
                                record.setTotalPrice(null);
                                record.setTotalPriceVat(null);
                        }else if(vProduct.getPrice()!=null&&vProduct.getPrice().intValue()>=0) {
                            record.setPrice(inquiry.getOfferMode().equals(TaxMode.UNTAXED)?vProduct.getPrice():calculateUntaxedUnitPrice(vProduct.getPrice(),record.getVatRate()));
                            record.setPriceVat( inquiry.getOfferMode().equals(TaxMode.INCLUDED)?vProduct.getPrice():calculateTaxedUnitPrice(vProduct.getPrice(),record.getVatRate()));
                            record.setTotalPrice(calculateSubtotal(record.getPrice(),record.getAmount()));
                            record.setTotalPriceVat(calculateSubtotal(record.getPriceVat(),record.getAmount()));
                        }
                    }
                }));
            }
            inquiryRecordRepository.saveAll(records);
            return  countSum(records,id);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 撤销询价单
     * @param id 询价单主键
     * @return 成功或者失败信息
     */

    @Caching(evict = {@CacheEvict(value="inquiry_detail;1800",key = "#id"),
        @CacheEvict(value="inquiry_record_List;1800", key="#id"),
        @CacheEvict(value="inquiry_List;1800", key="#companyCode+'_'",allEntries=true)
    })
    @Transactional
    public  Boolean deleteInquiry(String id,String companyCode){
        try {
            inquiryRepository.cancleInquiry(LocalDateTime.now(),InquiryState.CANCELLATION,id);
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    /**
     * 导出产品模板
     * @param id 询价单主键或者合同主键
     * @return 产品列表
     */
    public List<LinkedHashMap<String,Object>> exportProduct(String id){
        List<LinkedHashMap<String,Object>> list = new ArrayList<>();
        try{
            Inquiry inquiry =findInquiry(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            List<InquiryRecord> records = findInquiryRecords(id);
            records.forEach(record -> {
                LinkedHashMap<String,Object> m = new LinkedHashMap<>();
                m.put("产品编码",record.getProductCode());
                if(inquiry.getOfferMode().equals(TaxMode.UNTAXED)) {
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
                if(inquiry.getOfferMode().equals(TaxMode.UNTAXED)) {
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
     * 导入产品
     * @param file 导入文件
     * @param id 询价单id
     * @param companyCode 公司编码
     * @param operator 操作员编码
     * @return 返回成功或者失败信息
     */
   // @CacheEvict(value="inquiry_record_List;1800", key="#id")
    @Transactional
    public Map<String,Object> importProduct(MultipartFile file,String id,String companyCode,String operator,TaxMode taxMode){
        Map<String,Object> resultMap = new HashMap<>();
        try {
           // Inquiry inquiry = findInquiry(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            List<Map<String, Object>> list =  ExcelUtil.excelToList(file);
            List<ImportProductTemp> importProductTemps = new ArrayList<>();
            for (int i =0;i<list.size();i++){
                Map<String, Object> map = list.get(i);
                ImportProductTemp importProductTemp = ImportProductTemp.builder().build();
                importProductTemp.setImportProductTempId(
                    ImportProductTempId.builder()
                        .dcCompId(companyCode)
                        .operator(operator)
                        .inquiryId(id)
                        .itemNo(i+2)
                        .build()
                );
                if(map.get("产品编码")!=null){
                    String code = map.get("产品编码").toString();
                    importProductTemp.setCode(code);
                    //验证产品编码是否正确
                    List<Product> products = productRepository.findProductByCode(code);
                    if(products.size()==1){
                        importProductTemp.setProductId(products.get(0).getId());
                        importProductTemp.setBrandCode(products.get(0).getBrandCode());
                        importProductTemp.setBrandName(products.get(0).getBrand());
                    }
                }
                if(map.get("数量")!=null){
                    String amount = map.get("数量").toString();
                    importProductTemp.setAmount(amount);
                }

                if(map.get("未税单价")!=null){
                        String price = map.get("未税单价").toString();
                        importProductTemp.setPrice(price);
                        importProductTemp.setFlag(TaxMode.UNTAXED);
                }else if(map.get("含税单价")!=null){
                        String price = map.get("含税单价").toString();
                        importProductTemp.setPrice(price);
                    importProductTemp.setFlag(TaxMode.INCLUDED);
                }else{
                    importProductTemp.setFlag(taxMode);
                }

                importProductTemps.add(importProductTemp);
            }
            importProductTempRepository.saveAll(importProductTemps);
            resultMap.put("code",200);
            resultMap.put("message","导入产品成功！");
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("code",500);
            return resultMap;
        }
    }

    /**
     * 查询导入产品列表
     * @param companyCode 单位id
     * @param operator 操作员编码
     * @param id 询价单id或者合同主键
     * @param code 询价单编码或者合同编码
     * @return 返回导入产品列表信息
     */
    public Map<String,Object> findImportProductDetail(String companyCode, String operator, String id,String code,TaxMode taxMode) throws IOException {
       Map<String,Object> map = new HashMap<>();
       //Inquiry inquiry =  findInquiry(id).orElseThrow(()->new IOException("从数据库中查询不到该询价单信息"));
        map.put("inquiryCode",code);
        map.put("contractCode",code);
        List<ImportProductTemp> list=importProductTempRepository.
            findImportProductTempsByImportProductTempId_DcCompIdAndImportProductTempId_OperatorAndImportProductTempId_InquiryId(companyCode,operator,id);
        List<TImportProductTemp> importProductTemps=list.stream()
            .map(importProductTempMapper::toTImportProductTemp)
            .toList();
        importProductTemps.forEach(tImportProductTemp -> {
            //错误数据
            List<String> errorList = new ArrayList<>();
            List<TBrand> tBrands = new ArrayList<>();
            if(tImportProductTemp.getProductId()==null&&tImportProductTemp.getCode()==null){
                errorList.add("产品编码不能为空");
            }else if(tImportProductTemp.getProductId()==null&&tImportProductTemp.getCode()!=null){
                //验证产品编码是否正确
                List<Product> products = productRepository.findProductByCode(tImportProductTemp.getCode());
                if(products.size()==0){
                    errorList.add("产品编码错误或不存在于系统中");
                }else{
                    errorList.add("该产品编码在系统中存在多个，请选择品牌");
                    AtomicInteger i= new AtomicInteger();
                    products.forEach(product -> {
                        i.getAndIncrement();
                        tBrands.add(TBrand.builder()
                            .code(product.getBrandCode())
                            .name(product.getBrand())
                            .sort(i.get())
                            .build());
                    });
                }
            }else{
                tBrands.add(TBrand.builder()
                    .code(tImportProductTemp.getConfirmedBrand())
                    .name(tImportProductTemp.getConfirmedBrandName())
                    .sort(1)
                    .build());
            }
            tImportProductTemp.setBrand(tBrands);
            if(tImportProductTemp.getAmount()==null){
                errorList.add("数量不能为空");
            }else{
                //验证 数量是否为数字
                if(isNumeric(tImportProductTemp.getAmount())) {
                    errorList.add("数量应为数字");
                }
            }

            if(tImportProductTemp.getPrice()!=null){
                //验证 数量是否为数字
                if(isNumeric(tImportProductTemp.getPrice())) {
                    errorList.add("单价应为数字");
                }
                if(!taxMode.equals(tImportProductTemp.getFlag())){
                    String offerMode=taxMode.equals(TaxMode.UNTAXED)?"未税单价":"含税单价";
                    errorList.add("单价应为"+offerMode);
                }
            }
            tImportProductTemp.setMessages(errorList);
        });
        map.put("products",importProductTemps.stream()
            .map(importProductTempMapper::toVProduct)
            .toList());
        return map ;
    }

    /**
     * 删除暂存产品
     * @param id 询价单id
     * @param companyCode 公司编码
     * @param operator 操作员编码
     * @return 返回true或者false
     */
    @Transactional
    public Boolean deleteImportProducts(String id,String companyCode,String operator){
        try{
            importProductTempRepository.deleteProduct(id,companyCode,operator);
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    /**
     * 修改暂存导入产品
     * @param id 询价单id或者采购合同主键
     * @param companyCode 单位id
     * @param operator 操作员编码
     * @return 返回成功或者失败信息
     */
    @Transactional
    public Map<String,Object> modifyImportProduct(String id, String companyCode, String operator, List<VImportProductTempRequest> vImportProductTempRequests){
        Map<String,Object>   resultMap=new HashMap<>();
        List<ImportProductTemp> list = new ArrayList<>();
        try {
            for (VImportProductTempRequest vImport : vImportProductTempRequests) {
                ImportProductTemp temp =importProductTempRepository.findById(
                    ImportProductTempId.builder()
                        .inquiryId(id)
                        .itemNo(vImport.getItemNo())
                        .dcCompId(companyCode)
                        .operator(operator)
                        .build()
                ).orElseThrow(() -> new IOException("数据库中找不到该暂存产品"));
                Product product = productRepository.findProductByCodeAndBrandCode(temp.getCode(), vImport.getBrandCode())
                    .orElseThrow(() -> new IOException("数据库中找不到该产品"));
                temp.setProductId(product.getId());
                temp.setBrandCode(product.getBrandCode());
                temp.setBrandName(product.getBrand());
                list.add(temp);
            }
            importProductTempRepository.saveAll(list);
            resultMap.put("code",200);
            resultMap.put("message","修改成功");

            return resultMap;

        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("code",500);
            resultMap.put("message","保存失败");
            return resultMap;
        }
    }

    /**
     * 保存导入产品为询价单明细
     * @param id 询价单id
     * @param companyCode 单位id
     * @param operator 操作员编码
     * @return 返回成功或者失败信息
     */
    @Caching(evict = {@CacheEvict(value="inquiry_detail;1800",key = "#id"),
        @CacheEvict(value="inquiry_record_List;1800", key="#id")
    })
    @Transactional
    public Map<String,Object> saveImportProducts(String id,String companyCode,String operator){
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("code",500);
        resultMap.put("message","保存失败");
        try {
            List<ImportProductTemp> list=importProductTempRepository.
                findImportProductTempsByImportProductTempId_DcCompIdAndImportProductTempId_OperatorAndImportProductTempId_InquiryId(companyCode,operator,id);
            inquiryRecordRepository.deleteProducts(id);
            List<InquiryRecord> inquiryRecords = new ArrayList<>();
            //货物税率
            TaxRates goods= vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.GOODS,Whether.YES,"001")
                .orElseThrow(() -> new IOException("请求的货物税率不存在"));
            //查询询价单
            Inquiry inquiry =findInquiry(id)  .orElseThrow(() -> new IOException("请求的询价单不存在"));
            int maxCode =0;
            for (ImportProductTemp importProductTemp : list) {
                //验证产品编码是否正确
                Product product = productRepository.
                    findProductByCodeAndBrandCode(
                        importProductTemp.getCode(),
                        importProductTemp.getBrandCode()
                    )
                    .orElseThrow(() -> new IOException("请求的产品不存在"));
                InquiryRecord record = getInquiryRecord(
                    product,
                    id,
                    maxCode+"" ,
                    new BigDecimal(importProductTemp.getAmount()),
                    inquiry.getVatProductRate() != null ? inquiry.getVatProductRate() : goods.getRate(),
                    StringUtils.isNotBlank(importProductTemp.getPrice())?new BigDecimal(importProductTemp.getPrice()):null,inquiry.getOfferMode()
                );
                inquiryRecords.add(record);
                maxCode++;
            }
            //删除原有的产品明细
            importProductTempRepository.deleteProduct(id,companyCode,operator);
          //  inquiryRecords=countRecord(inquiryRecords,inquiry.getOfferMode());
            inquiryRecordRepository.saveAll(inquiryRecords);
            if(countSum(inquiryRecords,id))
                resultMap.put("code",200);
                resultMap.put("message","保存成功");
        }catch (Exception e){
            e.printStackTrace();
            return resultMap;
        }
        return resultMap;
    }

    /**
     * 判断 字符串是否为数字
     * @param str 字符串
     * @return 返回是或者否
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        if(str.indexOf(".")>0){//判断是否有小数点
            if(str.indexOf(".")==str.lastIndexOf(".") && str.split("\\.").length==2){ //判断是否只有一个小数点
                return !pattern.matcher(str.replace(".", "")).matches();
            }else {
                return true;
            }
        }else {
            return !pattern.matcher(str).matches();
        }
    }



    /**
     * 更新询价单总价
     * @param inquiryRecords 询价单明细列表
     * @param id 询价单主键
     * @return 返回成功或者失败信息
     */
    public  boolean countSum(List<InquiryRecord> inquiryRecords,String id){
        try{
            //判断是否需要重新计算价格
            List<InquiryRecord> lists = inquiryRecords
                .stream()
                .filter(inquiryRecord -> inquiryRecord.getPrice()==null)
                .toList();
            //是 重新计算价格
            BigDecimal totalPrice=new BigDecimal("0");
            BigDecimal  totalPriceVat=new BigDecimal("0");
            BigDecimal vat;
            if(lists.size()==0){
                for (InquiryRecord inquiryRecord:inquiryRecords){
                    totalPrice=totalPrice.add(inquiryRecord.getTotalPrice());
                    totalPriceVat=totalPriceVat.add(inquiryRecord.getTotalPriceVat());
                }
                vat = totalPriceVat.setScale(2, RoundingMode.HALF_UP).subtract(totalPrice.setScale(2, RoundingMode.HALF_UP));

            }else{
                totalPrice=null;
                totalPriceVat=null;
                vat=null;
            }

            BigDecimal totalPrice1 = totalPrice == null ? null : totalPrice.setScale(2, RoundingMode.HALF_UP);
            inquiryRepository.updateInquiry(totalPrice1,totalPriceVat==null?null:totalPriceVat.setScale(2, RoundingMode.HALF_UP),vat,totalPrice1,id);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    /**
     * 判断询价单中的金额 是否为null
     * @param price 金额（税额、总价）
     * @param records 询价单明细列表
     * @return 金额
     */
    public static BigDecimal judgeInquiryMoney(BigDecimal price, List<InquiryRecord> records){
        if(records==null || records.size()==0)
            return  null;
        records = records.stream().filter(record -> record.getPrice()==null).toList();
        if(records==null || records.size()>0)
            return  null;
        return  price.setScale(2);
    }

    /**
     * 生成询价单唯一编码和询价单编号
     * @param max 最大编号
     * @param operatorCode 操作员编码
     * @param companyCode 单位唯一编码
     * @param supplierCode 供应商编码
     * @return 生成询价单唯一编码和询价单编号列表
     */
    public List<String> getInquiryCode(String max,String operatorCode,String companyCode,String supplierCode){
        List<String> list = new ArrayList<>();
        String mCode = ("0000"+max).substring(("0000"+max).length()-3);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate data=LocalDate.now();
        //uuid
        UUID uuid = UUID.randomUUID();
        String inquiryId = "XJ-"+companyCode+"-"+operatorCode+"-"+uuid.toString().substring(0,8);
        String inquiryCode ="XJ-"+operatorCode+"-"+supplierCode+"-"+dtf.format(data)+"-"+mCode;
        list.add(inquiryId);
        list.add(inquiryCode);
        return  list;
    }

    /**
     * 查询暂存产品详情
     * @param id 合同主键或者询价单主键
     * @param companyCode 本单位编码
     * @param operator 操作员编码
     * @param code 合同编码或者询价单编码
     * @param taxMode 税模式
     * @return 返回暂存产品列表
     * @throws IOException
     */
    public VImportProductTempResponse getvImportProductTempResponse(String id, String companyCode, String operator, String code, TaxMode taxMode) throws IOException {
        var map = findImportProductDetail(companyCode,
            operator,
            id,code,taxMode);
        var list =(List<VImportProductTempResponse.VProduct>) map.get("products");
        return VImportProductTempResponse.builder()
            .code(200)
            .message("产品导入临时表成功")
            .confirmable(list.stream().filter(vProduct -> vProduct.getMessages().size() > 0 || vProduct.getConfirmedBrand()==null).toList().size()==0)
            .products(list)
            .contractCode((String)map.get("contractCode"))
            .inquiryCode((String)map.get("inquiryCode"))
            .build();
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
        return price.multiply(new BigDecimal("1").add(vatRate)).setScale(4, RoundingMode.HALF_UP);
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
}
