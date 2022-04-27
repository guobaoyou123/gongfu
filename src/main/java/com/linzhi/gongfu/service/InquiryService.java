package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.dto.TImportProductTemp;
import com.linzhi.gongfu.dto.TInquiry;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.ImportProductTempMapper;
import com.linzhi.gongfu.mapper.InquiryMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.util.ExcelUtil;
import com.linzhi.gongfu.vo.VModifyInquiryRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final InquiryDetailRepository inquiryDetailRepository;
    private final OperatorRepository operatorRepository;
    private final CompanyRepository companyRepository;
    private final CompTradeRepository compTradeRepository;
    private final InquiryRecordRepository inquiryRecordRepository;
    private final ProductRepository productRepository;
    private final TaxRatesRepository vatRatesRepository;
    private final ImportProductTempRepository importProductTempRepository;
    private final ImportProductTempMapper importProductTempMapper;

    /**
     * 查询未完成的询价单列表
     * @param companyCode 公司编码
     * @param operator 操作员编码
     * @return 返回未完成询价列表
     */
    @Cacheable(value="inquiry_List;1800", key="#companyCode+'_'+#operator")
    public List<TInquiry> inquiryList(String companyCode, String operator){
        try{
            Operator operator1= operatorRepository.findById(OperatorId.builder()
                    .operatorCode(operator)
                    .companyCode(companyCode)
                    .build())
                .orElseThrow(()-> new IOException("请求的操作员找不到"));
            if(operator1.getAdmin().equals(Whether.YES))
                return inquiryRepository.findInquiryListByCreatedByCompAndTypeAndStateOrderByCreatedAtDesc(companyCode, InquiryType.INQUIRY_LIST, InquiryState.UN_FINISHED)
                    .stream()
                    .map(inquiryMapper::toInquiryList)
                    .toList();
            return inquiryRepository.findInquiriesByCreatedByCompAndCreatedByAndTypeAndStateOrderByCreatedAtDesc(companyCode,operator, InquiryType.INQUIRY_LIST, InquiryState.UN_FINISHED)
                .stream()
                .map(inquiryMapper::toInquiryList)
                .toList();
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    /**
     * 询价单详情
     * @param id 询价单主键
     * @return 返回询价单详情
     */
    public Optional<InquiryDetail> inquiryDetail(String id){
        return inquiryDetailRepository.findById(id);
    }

    /**
     * 建立新的空询价单
     * @param companyCode 单位id
     * @param companyName 单位名称
     * @param operator 操作员id
     * @param operatorName 操作员姓名
     * @param supplierCode 供应商名称
     * @return 询价单编码
     */
    @CacheEvict(value="inquiry_List;1800", key="#companyCode+'_'",allEntries=true)
    public String  emptyInquiry(String companyCode,String companyName,String operator,String operatorName,String supplierCode){
        try {
            //查询询价单最大编号
            String maxCode = inquiryDetailRepository.findMaxCode(companyCode, operator);
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
            Optional<Company> supplier = companyRepository.findById(supplierCode);
            String supplierName = null;
            if(supplier.isPresent())
                supplierName=supplier.get().getNameInCN();
            //税模式
            Optional<CompTrad> compTrad = compTradeRepository.findById(
                CompTradId.builder()
                    .compBuyer(companyCode)
                    .compSaler(supplierCode)
                    .build());
            TaxMode taxMode = null;
            if(compTrad.isPresent())
                taxMode= compTrad.get().getTaxModel();
            inquiryDetailRepository.save(
                InquiryDetail.builder()
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
                    .salerCompName(supplierName)
                    .state(InquiryState.UN_FINISHED)
                    .offerMode(taxMode)
                    .build()
            );
            return  inquiryId;
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
    @Transactional
    public Boolean saveInquiryProduct(String id, String productId, BigDecimal price,BigDecimal amount){
        try{
            //查询询价单
            InquiryDetail inquiry = inquiryDetail(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            //查询明细最大顺序号
            String maxCode = inquiryRecordRepository.findMaxCode(id);
            if(maxCode==null)
                maxCode="0";
            //查询产品
            Product product = productRepository.findById(productId).orElseThrow(() -> new IOException("请求的产品不存在"));
            //货物税率
            TaxRates goods= vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.GOODS,Whether.YES,"001")
                .orElseThrow(() -> new IOException("请求的货物税率不存在"));
            //保存产品
            InquiryRecord record = InquiryRecord.builder()
                .inquiryRecordId(
                    InquiryRecordId.builder()
                        .inquiryId(id)
                        .code(Integer.parseInt(maxCode)+1)
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
                .chargeUnit(product.getChargeUnit())
                .stockTime(0)
                .vatRate(goods.getRate())
                .build();
            if(inquiry.getVatProductRate()!=null && inquiry.getVatProductRate().intValue()>0)
                record.setVatRate(inquiry.getVatProductRate());
            if(price!=null){
                if(inquiry.getOfferMode().equals(TaxMode.UNTAXED)){
                    record.setPrice(price);
                }else{
                    record.setPriceVat(price);
                }
            }
            List<InquiryRecord> records = new ArrayList<>();
            records.add(record);
            inquiry.getRecords().add(countRecord(records,inquiry.getOfferMode()).get(0));
            inquiryRecordRepository.save(record);
            inquiryDetailRepository.save(countSum(inquiry));
            //保存明细
            // inquiryRecordRepository.save(countRecord(records,inquiry.getOfferMode()).get(0));
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    /**
     * 删除询价产品
     * @param id 询价单编码
     * @param codes 询价单明细条目号
     * @return 返回成功或者失败
     */
    @Transactional
    public Boolean deleteInquiryProduct(String id,List<Integer> codes){
        try {
            InquiryDetail inquiry = inquiryDetail(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            inquiryRecordRepository.deleteProducts(id,codes);
            return countSum(
                inquiry.getRecords().stream()
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
    @Transactional
    public  Boolean  modifyInquiry(VModifyInquiryRequest vModifyInquiryRequest,String id){
        try{
            InquiryDetail inquiry = inquiryDetail(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            if(StringUtils.isNotBlank(vModifyInquiryRequest.getTaxModel()))
                inquiry.setOfferMode(vModifyInquiryRequest.getTaxModel().equals("0")?TaxMode.UNTAXED:TaxMode.INCLUDED);
            if(vModifyInquiryRequest.getServiceVat()!=null) {
                inquiry.setVatServiceRate(vModifyInquiryRequest.getServiceVat());
                inquiry.getRecords().forEach(
                    record -> {
                        if(record.getType().equals(VatRateType.SERVICE))
                            record.setVatRate(vModifyInquiryRequest.getServiceVat());
                    }
                );
            }
            if(vModifyInquiryRequest.getGoodsVat()!=null) {
                inquiry.setVatProductRate(vModifyInquiryRequest.getGoodsVat());
                inquiry.getRecords().forEach(
                    record -> {
                        if(record.getType().equals(VatRateType.GOODS))
                            record.setVatRate(vModifyInquiryRequest.getGoodsVat());
                    }
                );
            }

            if(vModifyInquiryRequest.getProducts()!=null){
                vModifyInquiryRequest.getProducts().forEach(vProduct -> inquiry.getRecords().forEach(record -> {
                    if(record.getInquiryRecordId().getCode()==vProduct.getCode()){
                        if(vProduct.getAmount()!=null)
                            record.setAmount(vProduct.getAmount());
                        if(vProduct.getVatRate()!=null)
                            record.setVatRate(vProduct.getVatRate());
                        if(vProduct.getPrice()!=null) {
                            if (inquiry.getOfferMode().equals(TaxMode.UNTAXED)) {
                                record.setPrice(vProduct.getPrice());
                            }else {
                                record.setPriceVat(vProduct.getPrice());
                            }
                        }
                    }
                }));
            }
            inquiry.setRecords(countRecord(inquiry.getRecords(),inquiry.getOfferMode()));
            return  countSum(inquiry.getRecords(),id);
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
    @CacheEvict(value="inquiry_List;1800", key="#companyCode+'_'",allEntries=true)
    @Transactional
    public  Boolean deleteInquiry(String id,String companyCode){
        try {
            InquiryDetail inquiry =  inquiryDetail(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            inquiry.setState(InquiryState.CANCELLATION);
            inquiry.setDeletedAt(LocalDateTime.now());
            inquiryDetailRepository.save(inquiry);
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    /**
     * 导出询价单产品模板
     * @param id 询价单主键
     * @return 产品列表
     */
    public List<LinkedHashMap<String,Object>> exportProduct(String id){
        List<LinkedHashMap<String,Object>> list = new ArrayList<>();
        try{
            InquiryDetail inquiry = inquiryDetail(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
            inquiry.getRecords().forEach(record -> {
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
    @Transactional
    public Map<String,Object> importProduct(MultipartFile file,String id,String companyCode,String operator){
        Map<String,Object> resultMap = new HashMap<>();
        try {
            InquiryDetail inquiry = inquiryDetail(id).orElseThrow(() -> new IOException("请求的询价单不存在"));
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
                if(inquiry.getOfferMode().equals(TaxMode.UNTAXED)){
                    if(map.get("未税单价")!=null){
                        String price = map.get("未税单价").toString();
                        importProductTemp.setPrice(price);
                    }

                }else{
                    if(map.get("含税单价")!=null){
                        String price = map.get("含税单价").toString();
                        importProductTemp.setPrice(price);
                    }
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
     * @param id 询价单id
     * @return 返回导入产品列表信息
     */
    public Map findImportProductDetail(String companyCode, String operator, String id) throws IOException {
       Map<String,Object> map = new HashMap<>();
       Inquiry inquiry =  inquiryRepository.findById(id).orElseThrow(()->new IOException("从数据库中查询不到该询价单信息"));
        map.put("inquiryCode",inquiry.getCode());
        List<ImportProductTemp> list=importProductTempRepository.
            findImportProductTempsByImportProductTempId_DcCompIdAndImportProductTempId_OperatorAndImportProductTempId_InquiryId(companyCode,operator,id);
        List<TImportProductTemp> importProductTemps=list.stream()
            .map(importProductTempMapper::toTImportProductTemp)
            .toList();
        importProductTemps.forEach(tImportProductTemp -> {
            //错误数据
            List<String> errorList = new ArrayList<>();
            List<TBrand> tBrands = new ArrayList<>();
            if(tImportProductTemp.getCode()==null){
                errorList.add("产品编码不能为空");
            }else{
                //验证产品编码是否正确
                List<Product> products = productRepository.findProductByCode(tImportProductTemp.getCode());
                if(products.size()==0){
                    errorList.add("产品编码错误或不存在于系统中");
                }else if(products.size()==1){
                    tBrands.add(TBrand.builder()
                        .code(products.get(0).getBrandCode())
                        .name(products.get(0).getBrand())
                        .sort(1)
                        .build());
                    tImportProductTemp.setConfirmedBrand(products.get(0).getBrandCode());
                }else if(products.size()>1 && tImportProductTemp.getProductId()==null){
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
            }
            tImportProductTemp.setMessages(errorList);
        });
        map.put("products",importProductTemps.stream()
            .map(importProductTempMapper::toVProduct)
            .toList());
        return map ;
    }

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
     * 保存导入产品为询价单明细
     * @param id 询价单id
     * @param companyCode 单位id
     * @param operator 操作员编码
     * @return 返回成功或者失败信息
     */
    @Transactional
    public Map<String,Object> modifyImportProduct(String id,String companyCode,String operator,String brandCode,int itemNo){
        Map<String,Object>   resultMap=new HashMap<>();
        try {
            ImportProductTemp temp = importProductTempRepository.findById(
                ImportProductTempId.builder()
                    .inquiryId(id)
                    .itemNo(itemNo)
                    .dcCompId(companyCode)
                    .operator(operator)
                    .build()
            ).orElseThrow(()-> new IOException("数据库中找不到该暂存产品"));

            Product product = productRepository.findProductByCodeAndBrandCode(temp.getCode(),brandCode)
                .orElseThrow(() -> new IOException("数据库中找不到该产品"));
            temp.setProductId(product.getId());
            temp.setBrandCode(product.getBrandCode());
            temp.setBrandName(product.getBrand());
            importProductTempRepository.save(temp);
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
    @Transactional
    public Map<String,Object> saveImportProducts(String id,String companyCode,String operator){
        Map<String,Object> resultMap = new HashMap<>();
        try {
            List<ImportProductTemp> list=importProductTempRepository.
                findImportProductTempsByImportProductTempId_DcCompIdAndImportProductTempId_OperatorAndImportProductTempId_InquiryId(companyCode,operator,id);
            inquiryRecordRepository.deleteProducts(id);
            List<InquiryRecord> inquiryRecords = new ArrayList<>();
            //货物税率
            TaxRates goods= vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.GOODS,Whether.YES,"001")
                .orElseThrow(() -> new IOException("请求的货物税率不存在"));
            //查询询价单
            InquiryDetail inquiry = inquiryDetail(id)
                .orElseThrow(() -> new IOException("请求的询价单不存在"));
            int maxCode =1;
            for (ImportProductTemp importProductTemp : list) {
                InquiryRecord record = InquiryRecord.builder().build();
                //验证产品编码是否正确
                Product product = productRepository.
                    findProductByCodeAndBrandCode(
                        importProductTemp.getCode(),
                        importProductTemp.getBrandCode()
                    )
                    .orElseThrow(() -> new IOException("请求的产品不存在"));
                record.setProductId(product.getId());
                record.setProductCode(product.getCode());
                record.setProductDescription(product.getDescribe());
                record.setChargeUnit(product.getChargeUnit());
                record.setBrand(product.getBrand());
                record.setBrandCode(product.getBrandCode());
                record.setVatRate(inquiry.getVatProductRate() != null ? inquiry.getVatProductRate() : goods.getRate());
                record.setStockTime(0);
                record.setType(VatRateType.GOODS);
                record.setAmount(new BigDecimal(importProductTemp.getAmount()));
                if (importProductTemp.getPrice() != null) {
                    if (inquiry.getOfferMode().equals(TaxMode.UNTAXED)) {
                        record.setPrice(new BigDecimal(importProductTemp.getPrice()));
                    } else {
                        record.setPriceVat(new BigDecimal(importProductTemp.getPrice()));
                    }
                }
                record.setCreatedAt(LocalDateTime.now());
                record.setInquiryRecordId(
                    InquiryRecordId.builder()
                        .inquiryId(id)
                        .code(maxCode)
                        .build()
                );
                record.setVatRate(goods.getRate());
                if (inquiry.getVatProductRate() != null)
                    record.setVatRate(inquiry.getVatProductRate());
                inquiryRecords.add(record);
                maxCode++;

            }

            //删除原有的产品明细
            importProductTempRepository.deleteProduct(id,companyCode,operator);
            inquiryRecords=countRecord(inquiryRecords,inquiry.getOfferMode());
            inquiryRecordRepository.saveAll(inquiryRecords);
            if(countSum(inquiryRecords,id))
                resultMap.put("code",200);
            resultMap.put("message","保存成功");
            return resultMap;

        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("code",500);
            resultMap.put("message","保存失败");
            return resultMap;
        }
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
     * 计算询价单明细
     * @param records 明细列表
     * @param taxMode 税模式
     * @return 明细列表
     */
    public List<InquiryRecord> countRecord(List<InquiryRecord> records ,TaxMode taxMode){
        records.forEach(record -> {
            if(taxMode.equals(TaxMode.UNTAXED)&&record.getPrice()!=null){
                record.setPriceVat(record.getPrice().multiply(new BigDecimal(1).add(record.getVatRate())).setScale(4, RoundingMode.HALF_UP));
            }else if(taxMode.equals(TaxMode.INCLUDED)&&record.getPriceVat()!=null){
                record.setPrice(record.getPriceVat().divide(new BigDecimal(1).add(record.getVatRate()),4, RoundingMode.HALF_UP));
            }

            if(record.getPrice()!=null){
                record.setTotalPrice(record.getPrice().multiply(record.getAmount()).setScale(2, RoundingMode.HALF_UP));
                record.setTotalPriceVat(record.getPriceVat().multiply(record.getAmount()).setScale(2, RoundingMode.HALF_UP));
            }
        });
        return records;
    }

    /**
     * 计算总价
     * @param inquiry 询价单
     * @return 询价单
     */
    public InquiryDetail countSum(InquiryDetail inquiry ){

        //判断是否需要重新计算价格
        List<InquiryRecord> list = inquiry.getRecords()
            .stream()
            .filter(inquiryRecord -> inquiryRecord.getPrice()==null)
            .toList();
        //是 重新计算价格
        BigDecimal totalPrice=new BigDecimal(0);
        BigDecimal  totalPriceVat=new BigDecimal(0);
        if(list.size()==0){
            for (InquiryRecord inquiryRecord:inquiry.getRecords()){
                totalPrice=totalPrice.add(inquiryRecord.getTotalPrice());
                totalPriceVat=totalPriceVat.add(inquiryRecord.getTotalPriceVat());
            }
        }
        BigDecimal vat = totalPriceVat.subtract(totalPrice);
        inquiry.setVat(vat);
        inquiry.setTotalPrice(totalPrice);
        inquiry.setTotalPriceVat(totalPriceVat);
        return inquiry;
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
            BigDecimal totalPrice=new BigDecimal(0);
            BigDecimal  totalPriceVat=new BigDecimal(0);
            if(lists.size()==0){
                for (InquiryRecord inquiryRecord:inquiryRecords){
                    totalPrice=totalPrice.add(inquiryRecord.getTotalPrice());
                    totalPriceVat=totalPriceVat.add(inquiryRecord.getTotalPriceVat());
                }
            }
            BigDecimal vat = totalPriceVat.setScale(2, RoundingMode.HALF_UP).subtract(totalPrice.setScale(2, RoundingMode.HALF_UP));

            inquiryRepository.updateInquiry(totalPrice.setScale(2, RoundingMode.HALF_UP),totalPriceVat.setScale(2, RoundingMode.HALF_UP),vat,id);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }
    public static BigDecimal sumPriceProlde(BigDecimal price, List<InquiryRecord> records){
        if(records==null || records.size()==0)
            return  null;
        records = records.stream().filter(record -> record.getPrice()==null).toList();
        if(records==null || records.size()>0)
            return  null;
        return  price.setScale(2);
    }
}
