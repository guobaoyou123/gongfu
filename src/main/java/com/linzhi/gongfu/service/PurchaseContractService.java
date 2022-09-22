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
import com.linzhi.gongfu.util.CalculateUtil;
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
 * 采购合同信息处理及业务服务
 *
 * @author zgh
 * @create_at 2022-04-11
 */
@RequiredArgsConstructor
@Service
public class PurchaseContractService {

    private final PurchaseContractRepository purchaseContractRepository;
    private final InquiryDetailRepository inquiryDetailRepository;
    private final InquiryRecordRepository inquiryRecordRepository;
    private final AddressRepository addressRepository;
    private final CompContactsRepository compContactsRepository;
    private final TaxRatesRepository taxRatesRepository;
    private final TaxRatesMapper taxRatesMapper;
    private final ContractMapper contractMapper;
    private final InquiryService inquiryService;
    private final OperatorRepository operatorRepository;
    private final PurchaseContractDetailRepository purchaseContractDetailRepository;
    private final PurchaseContractRevisionRepository purchaseContractRevisionRepository;
    private final PurchaseContractRevisionDetailRepository purchaseContractRevisionDetailRepository;
    private final PurchaseContractRecordRepository purchaseContractRecordRepository;
    private final PurchaseContractRecordTempRepository purchaseContractRecordTempRepository;
    private final ContractRecordMapper contractRecordMapper;
    private final CompanyRepository companyRepository;
    private final CompTaxModelRepository compTaxModelRepository;
    private final ProductRepository productRepository;
    private final ImportProductTempRepository importProductTempRepository;
    private final TaxRatesRepository vatRatesRepository;
    private final DeliveryTempRepository deliveryTempRepository;
    private final DeliverRecordMapper deliverRecordMapper;
    private final DeliverBaseRepository deliverBaseRepository;
    private final PurchaseContractReceivedRepository contractReceivedRepository;
    private final ContractRecordPreviewRepository contractRecordPreviewRepository;

    /**
     * 查询合同是否重复（产品种类和数量）
     *
     * @param inquiryId 询价单id
     * @return 返回 true 或者 false
     */
    public Optional<String> getContractProductRepeat(String inquiryId) throws IOException {
        //查询询价单详情
        Inquiry inquiry = inquiryService.getInquiry(inquiryId).orElseThrow(() -> new IOException("数据库中找不到该询价单"));
        //询价单名称
        List<InquiryRecord> records = inquiryRecordRepository.findInquiryRecordTwins(inquiryId);
        //形成指纹
        String str = createSequenceCode(
            records.stream().map(inquiryRecord -> {
                {
                    return inquiryRecord.getProductId() + "-"
                        + inquiryRecord.getChargeUnit() + "-"
                        + inquiryRecord.getAmount().setScale(4, RoundingMode.HALF_UP) + "-"
                        + inquiryRecord.getVatRate();
                }
            }).toList(),
            inquiry.getSalerComp(),
            inquiry.getBuyerComp(), inquiry.getOfferMode()
        );
        //产品种类和数量相同的合同号
        List<String> contractId = purchaseContractRepository.findContractId(inquiry.getCreatedByComp(), str);

        return Optional.of(String.join(",", contractId));
    }

    /**
     * 生成采购合同
     *
     * @param generateContractRequest 参数
     * @return 返回成功或者失败
     */
    @Caching(evict = {
        @CacheEvict(value = "inquiry_List;1800", key = "#companyCode+'_'+'*'"),
        @CacheEvict(value = "inquiry_detail;1800", key = "#generateContractRequest.inquiryId"),
        @CacheEvict(value = "inquiry_record_List;1800", key = "#generateContractRequest.inquiryId"),
        @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'_'+'*'")
    })
    @Transactional
    public Boolean saveContract(VPContractRequest generateContractRequest, String companyCode, String operatorName, String operator) {
        try {
            //查询询价单详情
            InquiryDetail inquiry = inquiryDetailRepository.findById(generateContractRequest.getInquiryId()).orElseThrow(() -> new IOException("数据库中找不到该询价单"));
            if (inquiry.getState().equals(InquiryState.FINISHED))
                return false;
            //合同编号
            String id = inquiry.getId().replaceAll("XJ", "HT");
            String code = inquiry.getCode().replaceAll("XJ", "HT");
            //生成合同实体
            PurchaseContractDetail contract = createdContract(
                id,
                code,
                companyCode,
                operator,
                inquiry.getBuyerComp(),
                inquiry.getBuyerCompName(),
                inquiry.getSalerComp(),
                inquiry.getSalerCompName(),
                inquiry.getSalesContractId(),
                InquiryType.INQUIRY_LIST,
                ContractState.FINISHED
            );
            //生成带版本的合同实体
            PurchaseContractRevision contractRevision = createContractRevision(
                id,
                operatorName,
                generateContractRequest.getSupplierNo(),
                inquiry.getOfferMode(),
                generateContractRequest.getContactNo(),
                1,
                operator
            );
            if (inquiry.getVatProductRate() != null)
                contractRevision.setVatProductRate(inquiry.getVatProductRate());
            if (inquiry.getVatServiceRate() != null)
                contractRevision.setVatServiceRate(inquiry.getVatServiceRate());
            //
            contractRevision = modifyContractRevisionDetail(contractRevision, generateContractRequest, companyCode);
            inquiry.setConfirmTotalPriceVat(generateContractRequest.getSum());
            //判断产品单价是否为空
            List<InquiryRecord> list = inquiry.getRecords()
                .stream()
                .filter(inquiryRecord -> inquiryRecord.getPrice() == null)
                .toList();
            //计算折扣，折扣后价格，税额
            if (list.size() == 0) {
                BigDecimal discount = (inquiry.getTotalPriceVat().subtract(generateContractRequest.getSum())).divide(inquiry.getTotalPriceVat(), 4, RoundingMode.HALF_UP);
                //计算折扣的含税价格 和未税价格以及小计等
                BigDecimal discountSumVat = new BigDecimal("0");
                BigDecimal discountSum = new BigDecimal("0");
                for (int i = 0; i < inquiry.getRecords().size(); i++) {
                    InquiryRecord record = inquiry.getRecords().get(i);
                    record.setDiscount(discount);
                    record.setDiscountedPrice(CalculateUtil.calculateDiscountedPrice(record.getPrice(), discount));
                    record.setTotalDiscountedPrice(CalculateUtil.calculateDiscountedSubtotal(record.getPrice(), discount, record.getAmount()));
                    record.setDiscountedPriceVat(CalculateUtil.calculateDiscountedPrice(record.getPriceVat(), discount));
                    if (i == inquiry.getRecords().size() - 1) {
                        record.setTotalDiscountedPriceVat(inquiry.getConfirmTotalPriceVat().subtract(discountSumVat));
                    } else {
                        record.setTotalDiscountedPriceVat(CalculateUtil.calculateDiscountedSubtotal(record.getPriceVat(), discount, record.getAmount()));
                        discountSumVat = discountSumVat.add(record.getTotalDiscountedPriceVat());
                    }
                    discountSum = discountSum.add(record.getTotalDiscountedPrice());
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
            List<PurchaseContractRecord> records = new ArrayList<>();
            for (InquiryRecord inquiryRecord : inquiry.getRecords()) {
                PurchaseContractRecord contractRecord = PurchaseContractRecord.builder().purchaseContractRecordId(
                        PurchaseContractRecordId.builder()
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
                if (inquiryRecord.getPrice() != null) {
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
                inquiry.getBuyerComp(), inquiry.getOfferMode()
            );
            //更新询价单
            inquiry.setConfirmedAt(LocalDateTime.now());
            inquiry.setState(InquiryState.FINISHED);
            inquiry.setContractId(id);
            inquiry.setContractCode(code);
            inquiryDetailRepository.save(inquiry);
            //保存合同
            purchaseContractDetailRepository.save(contract);
            contractRevision.setFingerprint(str);
            purchaseContractRevisionRepository.save(contractRevision);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 创建合同实体
     *
     * @param id            合同唯一码
     * @param code          合同编码
     * @param createdByComp 创建的单位
     * @param createdBy     创建者
     * @param buyerComp     买方单位
     * @param buyerCompName 买方名称
     * @param salerComp     卖方单位
     * @param salerCompName 卖方单位名称
     * @param inquiryType   0-采购合同 1-销售合同
     * @return 合同实体
     */
    public PurchaseContractDetail createdContract(String id, String code, String createdByComp,
                                                  String createdBy, String buyerComp, String buyerCompName,
                                                  String salerComp, String salerCompName,
                                                  String salesContractId, InquiryType inquiryType, ContractState state) {


        return PurchaseContractDetail.builder()
            .id(id)
            .code(code)
            .createdByComp(createdByComp)
            .salesContractId(salesContractId)
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
     *
     * @param id               合同主键
     * @param buyerContactName 买当联系人姓名
     * @param salesOrderCode   销售合同号
     * @param offerMode        税模式
     * @param contractNo       本单位合同号
     * @param revision         版本
     * @return 返回合同版本实体
     */
    public PurchaseContractRevision createContractRevision(String id,
                                                           String buyerContactName,
                                                           String salesOrderCode,
                                                           TaxMode offerMode,
                                                           String contractNo,
                                                           int revision,
                                                           String operator
    ) {
        return PurchaseContractRevision.builder()
            .purchaseContractRevisionId(PurchaseContractRevisionId.builder()
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
     *
     * @param type 类型
     * @return 返回税率列表信息
     */
    public List<VTaxRateResponse.VTaxRates> listTaxRates(String type) {
        return taxRatesRepository.findTaxRatesByUseCountryAndTypeAndState(
                "001",
                type.equals("1") ? VatRateType.GOODS : VatRateType.SERVICE,
                Availability.ENABLED
            ).stream()
            .map(taxRatesMapper::toTTaxRates)
            .map(taxRatesMapper::toVTaxRates)
            .toList();
    }

    /**
     * 判断合同号是否重复
     *
     * @param contractNo  本单位采购合同号
     * @param companyCode 单位id
     * @return 返回是或否
     */
    @Transactional
    public Boolean changeContractNoRepeated(String contractNo, String companyCode,
                                            String contractId
    ) {
        int num;
        if (contractId.equals("")) {
            num = purchaseContractRepository.findByOrderCode(companyCode, contractNo);
        } else {
            num = purchaseContractRepository.findByOrderCode(companyCode, contractNo, contractId);
        }

        return num <= 0;
    }

    /**
     * 查看合同列表
     *
     * @param state        合同状态
     * @param supplierCode 供应商编码
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param companyCode  本单位编码
     * @param operator     操作员编码
     * @param pageable     分页
     * @return 返回合同列表
     */
    public Page<TContract> pageContracts(String state, String supplierCode,
                                         String startTime, String endTime,
                                         String companyCode, String operator,
                                         Pageable pageable) throws Exception {

        List<TContract> tContractList = listContracts(companyCode, operator, state);
        if (!supplierCode.equals("")) {
            tContractList = tContractList.stream().filter(tContract -> tContract.getSalerComp().equals(supplierCode)).toList();
        }
        if (!startTime.equals("") && !endTime.equals("")) {
            LocalDateTime startTimes = LocalDate.parse(startTime, DateType.YYYYMMDD.getType()).atStartOfDay();
            LocalDateTime endTimes = LocalDateTime.parse(endTime + " 23:59:59", DateType.YYYYMMDDHHMMSS.getType());
            tContractList = tContractList.stream().filter(tContract ->
                {
                    LocalDateTime dateTime = LocalDateTime.parse(tContract.getCreatedAt(), DateType.YYYYMMDDHHMMSS.getType());
                    return dateTime.isAfter(startTimes) && dateTime.isBefore(endTimes);
                }
            ).toList();
        }
        return PageTools.listConvertToPage(tContractList, pageable);
    }

    /**
     * 根据状态查询合同列表
     *
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     * @param state       状态
     * @return 返回合同列表
     */
    @Cacheable(value = "purchase_contract_List;1800", key = "#companyCode+'_'+#operator+'_'+#state")
    public List<TContract> listContracts(String companyCode, String operator, String state) throws Exception {

        Operator operator1 = operatorRepository.findById(
                OperatorId.builder()
                    .operatorCode(operator)
                    .companyCode(companyCode)
                    .build()
            )
            .orElseThrow(() -> new IOException("请求的操作员找不到"));
        if (operator1.getAdmin().equals(Whether.YES))
            return purchaseContractRepository.listContracts(companyCode,  state)
                .stream()
                .map(contractMapper::toContractList)
                .toList();
        return purchaseContractRepository.listContracts(companyCode, operator, state)
            .stream()
            .map(contractMapper::toContractList)
            .toList();

    }

    /**
     * 根据明细生成序列码
     *
     * @param records      明细
     * @param supplierCode 供应商编码
     * @param buyerCode    客户编码
     * @param taxMode      税模式
     * @return MD5编码
     */
    private String createSequenceCode(List<String> records, String supplierCode, String buyerCode, TaxMode taxMode) {
        String str = supplierCode + "-" + buyerCode + "-" + taxMode + "-" + String.join("-", records);
        return DigestUtils.md5Hex(str);
    }

    /**
     * 将合同列表进行排序，并以产品编码-我的计价单位-我的数量-含税单价-税率模式进行拼接成字符串
     *
     * @param records 合同明细
     * @return 返回字符串列表
     */
    private List<String> recordSort(List<PurchaseContractRecord> records) {
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

        for(int i =0;i<records.size()-1;i++){
            if(records.get(i).getProductId().equals(records.get(i+1).getProductId())){
                records.get(i).setMyAmount(records.get(i).getMyAmount().add(records.get(i+1).getMyAmount()));
                records.remove(i+1);
            }
        }

        return records.stream().map(contractRecord -> {
                contractRecord.setPriceVat(contractRecord.getPriceVat() == null ? null : contractRecord.getPriceVat().setScale(4, RoundingMode.HALF_UP));
                return contractRecord.getProductId() + "-"
                    + contractRecord.getMyChargeUnit() + "-"
                    + contractRecord.getMyAmount().setScale(4, RoundingMode.HALF_UP) + "-"
                    + contractRecord.getVatRate();
            }

        ).toList();
    }

    /**
     * 根据合同主键、版本号查询采购合同详情
     *
     * @param id       合同主键
     * @param revision 版本号
     * @return 返回合同详情
     * @throws IOException 异常
     */
    public VPContractDetailResponse.VContract getPurchaseContractDetail(String id, int revision) throws IOException {
        TContract contractRevision = getContractRevisionDetail(id, revision)
            .map(contractMapper::toTContractDetail)
            .orElseThrow(() -> new IOException("数据库中未查询到该数据"));
        List<TContractRecord> contractRecords;
        if (contractRevision.getState().equals(ContractState.UN_FINISHED.getState() + "")) {
            contractRecords = purchaseContractRecordTempRepository.findContractRecordTempsByPurchaseContractRecordTempId_ContractId(id).stream()
                .map(contractRecordMapper::toTContractRecord)
                .toList();
        } else {
            contractRecords = purchaseContractRecordRepository.findContractRecordsByPurchaseContractRecordId_ContractIdAndPurchaseContractRecordId_Revision(id, revision).stream()
                .map(contractRecordMapper::toTContractRecord)
                .toList();
        }
        contractRevision.setRecords(contractRecords);
        contractRevision.setRevisions(revisionList(id));
        return Optional.of(contractRevision).map(contractMapper::toContractDetail).orElse(null);
    }

    /**
     * 合同版本详情
     *
     * @param id       合同主键
     * @param revision 版本
     * @return 合同详情
     */
    @Cacheable(value = "contract_revision_detail;1800", key = "#id+'-'+#revision")
    public Optional<PurchaseContractRevisionDetail> getContractRevisionDetail(String id, int revision) {
        return purchaseContractRevisionDetailRepository.getDetail(revision, id);
    }

    /**
     * 查看合同版本号列表
     *
     * @param id 采购合同主键
     * @return 合同版本号列表
     */
    @Cacheable(value = "contract_revisions;1800", key = "#id")
    public List<TRevision> revisionList(String id) {
        List<Map<String, Object>> list = purchaseContractRevisionDetailRepository.listRevision(id);
        List<TRevision> tRevisions = new ArrayList<>();
        list.forEach(map -> tRevisions.add(TRevision.builder()
            .revision((int) map.get("revision"))
            .createdAt(DateConverter.dateFormat((LocalDateTime) map.get("createdAt")))
            .build()));
        return tRevisions;
    }

    /**
     * 新建空的询价单
     *
     * @param supplierCode 供应商编码
     * @param companyCode  本单位编码
     * @param companyName  本单位名称
     * @param operator     操作员编码
     * @param operatorName 操作员姓名
     * @return 返回成功信息
     */
    @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'_'+'*'")
    @Transactional
    public Optional<String> savePurchaseContractEmpty(String supplierCode, String companyCode, String companyName, String operator, String operatorName) {
        try {
            Company supplier = companyRepository.findById(supplierCode).orElseThrow(() -> new IOException("未从数据库中查到供应商信息"));
            String maxCode = purchaseContractDetailRepository.findMaxCode(companyCode, operator).orElse("01");
            Map<String, String> map = getContractCode(maxCode, operator, companyCode, supplierCode);
            PurchaseContractDetail contractDetail = createdContract(map.get("id"),
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
                .build()).orElseThrow(() -> new IOException("从数据库中没有查询到"));
            PurchaseContractRevision contractRevision = createContractRevision(map.get("id"), operatorName, null, taxModel.getTaxModel(), null, 1, operator);
            purchaseContractDetailRepository.save(contractDetail);
            purchaseContractRevisionRepository.save(contractRevision);
            return Optional.of(map.get("id"));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.of("");
        }
    }

    /**
     * 生成合同唯一编码和合同编号
     *
     * @param max          最大编号
     * @param operatorCode 操作员编码
     * @param companyCode  单位唯一编码
     * @param supplierCode 供应商编码
     * @return 生成询价单唯一编码和询价单编号列表
     */
    public Map<String, String> getContractCode(String max, String operatorCode, String companyCode, String supplierCode) {
        Map<String, String> map = new HashMap<>();
        String mCode = ("0000" + max).substring(("0000" + max).length() - 3);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate data = LocalDate.now();
        //uuid
        UUID uuid = UUID.randomUUID();
        String id = "HT-" + companyCode + "-" + operatorCode + "-" + uuid.toString().substring(0, 8);
        String code = "HT-" + operatorCode + "-" + supplierCode + "-" + dtf.format(data) + "-" + mCode;
        map.put("id", id);
        map.put("code", code);
        return map;
    }

    /**
     * 获取未确认的采购合同数量
     *
     * @param companyCode  本单位编码
     * @param operator     操作员编码
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param supplierCode 供应商编码
     * @return 返回确认的采购合同数量
     */
    public int getUnFinished(String companyCode, String operator, String startTime, String endTime, String supplierCode) throws Exception {

        var page = pageContracts(ContractState.UN_FINISHED.getState() + "", supplierCode, startTime, endTime, companyCode, operator, PageRequest.of(
            1,
            10
        ));
        return Integer.parseInt(String.valueOf(page.getTotalElements()));
    }

    /**
     * 添加采购合同产品
     *
     * @param productId 产品id
     * @param price     单价
     * @param amount    数量
     * @param id        合同主键
     * @param revision  版本号
     * @param operator  操作员编码
     * @return 返回成功或者失败信息
     */
    @Caching(evict = {@CacheEvict(value = "contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "contract_revision_recordTemp_detail;1800", key = "#id"),
        @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public boolean saveProduct(String productId, BigDecimal price, BigDecimal amount, String id, int revision, String companyCode, String operator) {
        try {
            PurchaseContractRevisionDetail contractRevisionDetail = getContractRevisionDetail(id, revision).orElseThrow(() -> new IOException("请求的产品不存在"));
            List<PurchaseContractRecordTemp> contractRecordTemps = purchaseContractRecordTempRepository.findContractRecordTempsByPurchaseContractRecordTempId_ContractId(id);
            //查询明细最大顺序号
            String maxCode = purchaseContractRecordTempRepository.findMaxCode(id);
            if (maxCode == null)
                maxCode = "0";
            //查询产品
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IOException("请求的产品不存在"));
            //货物税率
            TaxRates goods = vatRatesRepository.findByTypeAndDeFlagAndUseCountry(
                VatRateType.GOODS,
                Whether.YES,
                "001"
            ).orElseThrow(() -> new IOException("请求的货物税率不存在"));
            PurchaseContractRecordTemp contractRecordTemp = createContractRecordTemp(
                id,
                revision,
                Integer.parseInt(maxCode) + 1,
                product,
                price,
                contractRevisionDetail.getOfferMode(),
                amount,
                goods.getRate()
            );
            contractRecordTemps.add(contractRecordTemp);
            purchaseContractRecordTempRepository.save(contractRecordTemp);
            return countSum(contractRecordTemps, id, revision, operator);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建合同明细临时记录
     *
     * @param id       合同主键
     * @param revision 版本号
     * @param code     序号
     * @param product  产品
     * @param price    价格
     * @param taxMode  税模式
     * @param amount   数量
     * @param vatRate  税率
     * @return 临时记录实体
     */
    public PurchaseContractRecordTemp createContractRecordTemp(String id, int revision, int code, Product product, BigDecimal price,
                                                               TaxMode taxMode, BigDecimal amount, BigDecimal vatRate) {
        return PurchaseContractRecordTemp.builder()
            .purchaseContractRecordTempId(PurchaseContractRecordTempId.builder()
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
            .price(price != null ? taxMode.equals(TaxMode.UNTAXED) ? price : CalculateUtil.calculateUntaxedUnitPrice(price, vatRate) : null)
            .priceVat(price != null ? taxMode.equals(TaxMode.INCLUDED) ? price : CalculateUtil.calculateTaxedUnitPrice(price, vatRate) : null)
            .amount(amount)
            .myAmount(amount)
            .createdAt(LocalDateTime.now())
            .ratio(new BigDecimal("1"))
            .myChargeUnit(product.getChargeUnit())
            .stockTime(0)
            .vatRate(vatRate)
            .totalPrice(price != null ? taxMode.equals(TaxMode.UNTAXED) ? CalculateUtil.calculateSubtotal(price, amount) : CalculateUtil.calculateSubtotal(CalculateUtil.calculateUntaxedUnitPrice(price, vatRate), amount) : null)
            .totalPriceVat(price != null ? taxMode.equals(TaxMode.INCLUDED) ? CalculateUtil.calculateSubtotal(price, amount) : CalculateUtil.calculateSubtotal(CalculateUtil.calculateTaxedUnitPrice(price, vatRate), amount) : null)
            .build();
    }

    /**
     * 删除采购合同产品
     *
     * @param codes    明细序列号
     * @param id       采购合同
     * @param revision 版本号
     * @param operator 操作员
     * @return 返回成功或者失败信息
     */
    @Caching(evict = {@CacheEvict(value = "contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "contract_revision_recordTemp_detail;1800", key = "#id"),
        @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public Boolean removeContractProduct(List<Integer> codes, String id, int revision, String companyCode, String operator) {
        try {
            List<PurchaseContractRecordTemp> contractRecordTemps = purchaseContractRecordTempRepository.findContractRecordTempsByPurchaseContractRecordTempId_ContractId(id).stream().filter(contractRecordTemp -> !codes.contains(contractRecordTemp.getPurchaseContractRecordTempId().getCode()))
                .toList();
            List<PurchaseContractRecordTempId> contractRecordTempIds = new ArrayList<>();
            codes.forEach(s -> contractRecordTempIds.add(PurchaseContractRecordTempId.builder()
                .contractId(id)
                .code(s)
                .revision(revision)
                .build()));
            purchaseContractRecordTempRepository.deleteAllById(contractRecordTempIds);

            return countSum(contractRecordTemps, id, revision, operator);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询最大版本号
     *
     * @param id 合同主键
     * @return 版本号
     * @throws IOException 异常
     */
    public Integer getMaxRevision(String id) throws IOException {
        return Integer.parseInt(purchaseContractRevisionDetailRepository.getMaxRevision(id).orElseThrow(() -> new IOException("不存在该合同")));
    }

    /**
     * 将采购合同的状态设置为未确认的状态，生成新一版合同，生成临时合同明细记录
     *
     * @param id          合同主键
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     * @return 返回版本号
     */
    @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'-'+'*'")
    @Transactional
    public Integer modifyContractState(String id, String companyCode, String operator, Integer revision) {
        try {
            Optional<PurchaseContractRevisionDetail> contractRevisionDetail = getContractRevisionDetail(id, revision);
            PurchaseContractRevision contractRevision = contractRevisionDetail
                .map(contractMapper::toContractRevision).orElseThrow(() -> new IOException("不存在该合同"));
            if (contractRevisionDetail.get().getState().equals(ContractState.UN_FINISHED.getState() + ""))
                throw new Exception("该合同已经是未确认的，不可再次修改");
            purchaseContractDetailRepository.updateContractState(ContractState.UN_FINISHED, id);
            contractRevision.getPurchaseContractRevisionId().setRevision(revision + 1);
            contractRevision.setCreatedAt(LocalDateTime.now());
            contractRevision.setFingerprint(null);
            contractRevision.setConfirmedAt(null);
            contractRevision.setConfirmedBy(null);
            contractRevision.setModifiedAt(LocalDateTime.now());
            contractRevision.setModifiedBy(operator);
            purchaseContractRevisionRepository.save(contractRevision);
            List<PurchaseContractRecordTemp> contractRecordTemps = purchaseContractRecordRepository.findContractRecordsByPurchaseContractRecordId_ContractIdAndPurchaseContractRecordId_Revision(id, revision)
                .stream().map(contractRecordMapper::toContractRecordTemp).toList();
            purchaseContractRecordTempRepository.saveAll(contractRecordTemps);
            return revision + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 修改采购合同
     *
     * @param vModifyInquiryRequest 修改信息
     * @param id                    合同主键
     * @param revision              合同版本
     * @return 返回成功或者失败
     */
    @Caching(evict = {@CacheEvict(value = "contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "contract_revision_recordTemp_detail;1800", key = "#id"),
        @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public Boolean modifyPurchaseContract(VInquiryRequest vModifyInquiryRequest, String id, int revision, String companyCode, String operator) {
        try {
            PurchaseContractRevision contractRevision = purchaseContractRevisionRepository.findById(PurchaseContractRevisionId.builder()
                .revision(revision)
                .id(id)
                .build()).orElseThrow(() -> new IOException("请求的采购合同不存在"));
            List<PurchaseContractRecordTemp> contractRecordTemps = purchaseContractRecordTempRepository.findContractRecordTempsByPurchaseContractRecordTempId_ContractId(id);
            contractRevision.setOfferMode(StringUtils.isNotBlank(vModifyInquiryRequest.getTaxModel()) ? vModifyInquiryRequest.getTaxModel().equals("0") ? TaxMode.UNTAXED : TaxMode.INCLUDED : contractRevision.getOfferMode());
            if (vModifyInquiryRequest.getServiceVat() != null) {
                contractRevision.setVatServiceRate(vModifyInquiryRequest.getServiceVat());
                contractRecordTemps.forEach(
                    record -> {
                        if (record.getType().equals(VatRateType.SERVICE))
                            record.setVatRate(vModifyInquiryRequest.getServiceVat());
                    }
                );
            }
            if (vModifyInquiryRequest.getGoodsVat() != null) {
                contractRevision.setVatProductRate(vModifyInquiryRequest.getGoodsVat());
                contractRecordTemps.forEach(
                    record -> {
                        if (record.getType().equals(VatRateType.GOODS))
                            record.setVatRate(vModifyInquiryRequest.getGoodsVat());
                    }
                );
            }

            if (vModifyInquiryRequest.getProducts() != null) {
                vModifyInquiryRequest.getProducts().forEach(vProduct -> contractRecordTemps.forEach(record -> {
                    if (record.getPurchaseContractRecordTempId().getCode() == vProduct.getCode()) {
                        if (vProduct.getAmount() != null) {
                            record.setMyAmount(vProduct.getAmount().intValue() < 0 ? new BigDecimal("0") : vProduct.getAmount());
                            record.setAmount(vProduct.getAmount().multiply(record.getRatio()));
                        }
                        record.setVatRate(vProduct.getVatRate() != null ? vProduct.getVatRate() : record.getVatRate());
                        record.setPrice(vProduct.getPrice() != null && vProduct.getPrice().intValue() < 0 ?
                            record.getPrice() : vProduct.getPrice() != null && vProduct.getPrice().intValue() >= 0 ?
                            contractRevision.getOfferMode().equals(TaxMode.UNTAXED) ? vProduct.getPrice() : CalculateUtil.calculateUntaxedUnitPrice(vProduct.getPrice(), record.getVatRate()) : record.getPrice());
                        record.setPriceVat(vProduct.getPrice() != null && vProduct.getPrice().intValue() < 0 ?
                            record.getPriceVat() : vProduct.getPrice() != null && vProduct.getPrice().intValue() >= 0 ?
                            contractRevision.getOfferMode().equals(TaxMode.INCLUDED) ? vProduct.getPrice() : CalculateUtil.calculateTaxedUnitPrice(vProduct.getPrice(), record.getVatRate()) : record.getPriceVat());
                        record.setTotalPrice(record.getPrice() == null ? null : CalculateUtil.calculateSubtotal(record.getPrice(), record.getMyAmount()));
                        record.setTotalPriceVat(record.getPriceVat() == null ? null : CalculateUtil.calculateSubtotal(record.getPriceVat(), record.getMyAmount()));
                    }
                }));
            }
            purchaseContractRecordTempRepository.saveAll(contractRecordTemps);
            return countSum(contractRecordTemps, id, revision, operator);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断修改后的合同是否与上一版本相同
     *
     * @param id       合同
     * @param revision 版本号
     * @return 返回是或者否
     */
    public boolean judgeContractRev(String id, Integer revision) {
        PurchaseContractRevisionDetail contractRevision = purchaseContractRevisionDetailRepository.getDetail(revision, id).orElseThrow();
        PurchaseContractRevisionDetail perContractRevision = purchaseContractRevisionDetailRepository.getDetail(revision - 1, id).orElseThrow();
        List<PurchaseContractRecordTemp> contractRecordTemps = purchaseContractRecordTempRepository.
            findContractRecordTempsByPurchaseContractRecordTempId_ContractId(id);
        StringBuilder fingerprint = new StringBuilder(contractRevision.getOfferMode() + "-");
        for (PurchaseContractRecordTemp contractRecordTemp : contractRecordTemps) {

            fingerprint.append(contractRecordTemp.getProductId())
                .append("-")
                .append(contractRecordTemp.getMyChargeUnit())
                .append("-")
                .append(contractRecordTemp.getMyAmount().setScale(4, RoundingMode.HALF_UP))
                .append("-")
                .append(contractRecordTemp.getPriceVat() == null ? null : contractRecordTemp.getPriceVat().setScale(4, RoundingMode.HALF_UP))
                .append("-")
                .append(contractRecordTemp.getVatRate());

        }
        StringBuilder perFingerprint = new StringBuilder(perContractRevision.getOfferMode() + "-");
        for (PurchaseContractRecordTemp contractRecordTemp : contractRecordTemps) {
            if (contractRecordTemp.getPreviousMyAmount() != null) {
                perFingerprint.append(contractRecordTemp.getProductId())
                    .append("-")
                    .append(contractRecordTemp.getPreviousMyChargeUnit())
                    .append("-")
                    .append(contractRecordTemp.getPreviousMyAmount().setScale(4, RoundingMode.HALF_UP))
                    .append("-")
                    .append(contractRecordTemp.getPreviousPriceVat() == null ? null : contractRecordTemp.getPreviousPriceVat().setScale(4, RoundingMode.HALF_UP))
                    .append("-")
                    .append(contractRecordTemp.getPreviousVatRate());
            }


        }
        return fingerprint.toString().equals(perFingerprint.toString());
    }

    /**
     * 导出产品模板
     *
     * @param id       合同主键
     * @param revision 版本
     * @return 产品列表
     */
    public List<LinkedHashMap<String, Object>> exportProductTemplate(String id, Integer revision) {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        try {
            var contract = getPurchaseContractDetail(id, revision);
            contract.getProducts().forEach(record -> {
                LinkedHashMap<String, Object> m = new LinkedHashMap<>();
                m.put("产品代码", record.getCode());
                if (contract.getOfferMode().equals(TaxMode.UNTAXED.getTaxMode() + "")) {
                    m.put("单价（未税）", record.getPrice());
                } else {
                    m.put("单价（含税）", record.getPriceVat());
                }
                m.put("数量", record.getAmount());
                list.add(m);
            });
            if (list.size() == 0) {
                LinkedHashMap<String, Object> m = new LinkedHashMap<>();
                m.put("产品代码", "");
                if (contract.getOfferMode().equals(TaxMode.UNTAXED.getTaxMode() + "")) {
                    m.put("单价（未税）", "");
                } else {
                    m.put("单价（含税）", "");
                }
                m.put("数量", "");
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 导出产品
     *
     * @param id       合同主键
     * @param revision 版本
     * @return 产品列表
     */
    public List<LinkedHashMap<String, Object>> exportProduct(String id, Integer revision) {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        try {
            var contract = getPurchaseContractDetail(id, revision);
            contract.getProducts().forEach(record -> {
                LinkedHashMap<String, Object> m = new LinkedHashMap<>();
                m.put("产品代码", record.getCode());
                m.put("描述", record.getDescribe());
                m.put("数量", record.getAmount());
                if (contract.getOfferMode().equals(TaxMode.UNTAXED.getTaxMode() + "")) {
                    m.put("单价（未税）", record.getPrice());
                } else {
                    m.put("单价（含税）", record.getPriceVat());
                }
                m.put("税率", record.getVatRate().multiply(new BigDecimal("100")).setScale(2) + "%");
                if (contract.getOfferMode().equals(TaxMode.UNTAXED.getTaxMode() + "")) {
                    m.put("小计（未税）", record.getTotalPrice().setScale(2, RoundingMode.HALF_UP));
                } else {
                    m.put("小计（含税）", record.getTotalPriceVat().setScale(2, RoundingMode.HALF_UP));
                }
                list.add(m);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 保存导入产品为合同明细
     *
     * @param id          合同主键id
     * @param companyCode 单位id
     * @param operator    操作员编码
     * @return 返回成功或者失败信息
     */
    @Caching(evict = {
        @CacheEvict(value = "contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "contract_revision_recordTemp_detail;1800", key = "#id"),
        @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public Map<String, Object> saveImportProducts(String id, String companyCode, String operator, int revision) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 500);
        resultMap.put("message", "保存失败");
        try {
            List<ImportProductTemp> list = importProductTempRepository.
                findImportProductTempsByImportProductTempId_DcCompIdAndImportProductTempId_OperatorAndImportProductTempId_InquiryId(companyCode, operator, id);
            purchaseContractRecordTempRepository.deleteProducts(id);
            List<PurchaseContractRecordTemp> contractRecordTemps = new ArrayList<>();
            //货物税率
            TaxRates goods = vatRatesRepository.findByTypeAndDeFlagAndUseCountry(VatRateType.GOODS, Whether.YES, "001")
                .orElseThrow(() -> new IOException("请求的货物税率不存在"));
            //查询采购合同
            var contract = getContractRevisionDetail(id, 1).orElseThrow(() -> new IOException("请求的合同不存在"));
            int maxCode = 1;
            for (ImportProductTemp importProductTemp : list) {
                //验证产品编码是否正确
                Product product = productRepository.
                    findProductByCodeAndBrandCode(
                        importProductTemp.getCode(),
                        importProductTemp.getBrandCode()
                    )
                    .orElseThrow(() -> new IOException("请求的产品不存在"));
                PurchaseContractRecordTemp contractRecordTemp = createContractRecordTemp(id, 1, maxCode, product,
                    StringUtils.isNotBlank(importProductTemp.getPrice()) ? new BigDecimal(importProductTemp.getPrice()) : null,
                    contract.getOfferMode(),
                    new BigDecimal(importProductTemp.getAmount()), contract.getVatProductRate() != null ? contract.getVatProductRate() : goods.getRate());
                contractRecordTemps.add(contractRecordTemp);
                maxCode++;
            }
            //删除原有的产品明细
            importProductTempRepository.deleteProduct(id, companyCode, operator);
            purchaseContractRecordTempRepository.saveAll(contractRecordTemps);
            if (countSum(contractRecordTemps, id, 1, operator))
                resultMap.put("code", 200);
            resultMap.put("message", "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return resultMap;
        }
        return resultMap;
    }

    /**
     * 查询合同基本信息
     *
     * @param id 合同主键
     * @return 返回合同基本信息
     * @throws IOException 异常
     */
    public PurchaseContractDetail getContractDetail(String id) throws IOException {
        return purchaseContractDetailRepository.findById(id).orElseThrow(() -> new IOException("请求的合同不存在"));
    }

    /**
     * 撤销该版本合同
     *
     * @param id             合同主键
     * @param revision       版本
     * @param contractDetail 合同基本信息
     */
    @Caching(evict = {@CacheEvict(value = "contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "contract_revision_recordTemp_detail;1800", key = "#id"),
        @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public void removeCurrentRevision(String id, int revision, PurchaseContractDetail contractDetail, String companyCode) {

        contractDetail.setState(ContractState.FINISHED);
        if (revision == 1) {
            contractDetail.setState(ContractState.CANCELLATION);
        } else {
            purchaseContractRevisionRepository.deleteById(PurchaseContractRevisionId.builder()
                .id(id)
                .revision(revision)
                .build());
        }
        purchaseContractDetailRepository.save(contractDetail);
        purchaseContractRecordTempRepository.deleteProducts(id);
        deliveryTempRepository.deleteDeliverTempsByDeliverTempId_ContractId(id);

    }

    /**
     * 保存退回的临时记录
     *
     * @param list     退回的临时记录列表
     * @param id       合同主键
     * @param revision 版本号
     */
    @Transactional
    public void saveDeliveryTemp(List<VDeliveryTempRequest> list, String id, Integer revision) {
        try {
            //删除上次的数据
            deliveryTempRepository.deleteDeliverTempsByDeliverTempId_ContractId(id);
            List<PurchaseContractRecordTemp> contractRecordTemps = purchaseContractRecordTempRepository.findContractRecordTempsByPurchaseContractRecordTempId_ContractId(id);
            Map<String, PurchaseContractRecordTemp> map = new HashMap<>();
            contractRecordTemps.forEach(contractRecordTemp -> map.put(contractRecordTemp.getProductId(), contractRecordTemp));
            List<DeliverTemp> deliverTemps = new ArrayList<>();
            AtomicInteger maxCode = new AtomicInteger(1);
            for (VDeliveryTempRequest v : list) {
                PurchaseContractRecordTemp temp = map.get(v.getProductId());
                if (v.getReturnAmount().floatValue() > 0) {
                    if (temp.getProductId().equals(v.getProductId())) {
                        DeliverTemp deliverTemp = DeliverTemp.builder()
                            .deliverTempId(
                                DeliverTempId.builder()
                                    .contractId(id)
                                    .code(maxCode.get())
                                    .build()
                            )
                            .type(DeliverType.DELIVER)
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询合同是否重复（产品种类和数量）
     *
     * @param id       采购合同id
     * @param revision 版本号
     * @return 返回 true 或者 false
     */
    public Optional<String> findContractProductRepeat(String id, Integer revision) throws Exception {
        PurchaseContractRevisionDetail contractRevisionDetail = purchaseContractRevisionDetailRepository
            .getDetail(revision, id)
            .orElseThrow(() -> new IOException("请求的合同不存在"));

        if (!contractRevisionDetail.getState().equals(ContractState.UN_FINISHED.getState() + ""))
            throw new Exception("合同已确认");
        List<PurchaseContractRecordTemp> contractRecordTemps = purchaseContractRecordTempRepository
            .findContractRecordTempsTwins(id);

        String str = createSequenceCode(
            contractRecordTemps.stream()
                .map(contractRecord ->
                    contractRecord.getProductId() + "-"
                        + contractRecord.getMyChargeUnit() + "-"
                        + contractRecord.getMyAmount().setScale(4, RoundingMode.HALF_UP) + "-"
                        + contractRecord.getVatRate()
                ).toList(),
            contractRevisionDetail.getSalerComp(),
            contractRevisionDetail.getBuyerComp(),
            contractRevisionDetail.getOfferMode()
        );
        //产品种类和数量相同的合同号
        List<String> contractId = purchaseContractRepository.findContractId(contractRevisionDetail.getCreatedByComp(), str);

        return Optional.of(String.join(",", contractId));
    }

    /**
     * 生成新一版的采购合同
     *
     * @param id                      合同主键
     * @param revision                版本
     * @param generateContractRequest 保存内容
     * @param companyCode             公司编码
     * @param operator                操作员编码
     */
    @Caching(evict = {@CacheEvict(value = "contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "contract_revision_recordTemp_detail;1800", key = "#id"),
        @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public void saveContractRevision(String id, int revision, VPContractRequest generateContractRequest, String companyCode, String operator) {
        try {
            //合同明细
            List<PurchaseContractRecordTemp> contractRecordTemps = purchaseContractRecordTempRepository.findContractRecordTempsByPurchaseContractRecordTempId_ContractId(id);
            //查询合同详情
            PurchaseContractDetail contractDetail = purchaseContractDetailRepository.findById(id).orElseThrow(() -> new IOException("请求的合同不存在"));
            PurchaseContractRevision contractRevision = purchaseContractRevisionRepository.findById(PurchaseContractRevisionId.builder()
                    .revision(revision)
                    .id(id)
                    .build())
                .orElseThrow(() -> new IOException("请求的合同不存在"));
            if (!contractDetail.getState().equals(ContractState.UN_FINISHED)) {
                throw new Exception("合同已确认");
            }
            //修改数据set到版本合同中
            contractRevision = modifyContractRevisionDetail(contractRevision, generateContractRequest, companyCode);
            //判断产品单价是否为空
            List<PurchaseContractRecordTemp> list = contractRecordTemps
                .stream()
                .filter(record -> record.getPrice() == null)
                .toList();
            //合同明细
            List<PurchaseContractRecord> records = new ArrayList<>();
            //计算折扣，折扣后价格，税额
            if (list.size() == 0) {
                BigDecimal discount = (contractRevision.getTotalPriceVat().subtract(generateContractRequest.getSum())).divide(contractRevision.getTotalPriceVat(), 4, RoundingMode.HALF_UP);
                //计算折扣的含税价格 和未税价格以及小计等
                BigDecimal discountSumVat = new BigDecimal("0");
                BigDecimal discountSum = new BigDecimal("0");
                for (int i = 0; i < contractRecordTemps.size(); i++) {
                    PurchaseContractRecord record = Optional.of(contractRecordTemps.get(i)).map(contractRecordMapper::toContractRecord).orElse(null);
                    record.getPurchaseContractRecordId().setCode(i + 1);
                    record.setDiscount(discount);
                    record.setDiscountedPrice(CalculateUtil.calculateDiscountedPrice(record.getPrice(), discount));
                    record.setTotalDiscountedPrice(CalculateUtil.calculateDiscountedSubtotal(record.getDiscountedPrice(), discount, record.getMyAmount()));
                    record.setDiscountedPriceVat(CalculateUtil.calculateDiscountedPrice(record.getPriceVat(), discount));
                    if (i == contractRecordTemps.size() - 1) {
                        record.setTotalDiscountedPriceVat(contractRevision.getConfirmTotalPriceVat().subtract(discountSumVat));
                    } else {
                        record.setTotalDiscountedPriceVat(CalculateUtil.calculateDiscountedSubtotal(record.getPriceVat(), discount, record.getMyAmount()));
                        discountSumVat = discountSumVat.add(record.getTotalDiscountedPriceVat());
                    }
                    discountSum = discountSum.add(record.getTotalDiscountedPrice());
                    records.add(
                        record
                    );
                }
                contractRevision.setDiscountedTotalPrice(discountSum);
                contractRevision.setVat(contractRevision.getConfirmTotalPriceVat().subtract(discountSum));
                contractRevision.setDiscount(discount);
            }
            purchaseContractRecordRepository.saveAll(records);
            /*
              获取指纹
             */
            String str = createSequenceCode(
                recordSort(records),
                contractDetail.getSalerComp(),
                contractDetail.getBuyerComp(),
                contractRevision.getOfferMode()
            );
            purchaseContractRecordTempRepository.deleteProducts(id);
            contractRevision.setConfirmedAt(LocalDateTime.now());
            contractRevision.setConfirmedBy(operator);
            contractDetail.setState(ContractState.FINISHED);
            //保存合同
            purchaseContractDetailRepository.save(contractDetail);
            contractRevision.setFingerprint(str);
            purchaseContractRevisionRepository.save(contractRevision);

            //将退回记录录保存到货运信息表中
            if (generateContractRequest.getDeliveryRecords().size() > 0)
                saveDelivery(id, revision, companyCode, operator, generateContractRequest.getDeliveryRecords());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改合同
     *
     * @param contractRevision        合同
     * @param generateContractRequest 修改数据
     * @param companyCode             本单位编码
     * @return 合同详情
     * @throws IOException 异常
     */
    public PurchaseContractRevision modifyContractRevisionDetail(PurchaseContractRevision contractRevision, VPContractRequest generateContractRequest, String companyCode) throws IOException {
        contractRevision.setOrderCode(generateContractRequest.getContactNo());
        contractRevision.setSalerOrderCode(generateContractRequest.getSupplierNo());
        //供应商联系人
        contractRevision.setSalerContactName(generateContractRequest.getSupplierContactName());
        //供应商联系人电话
        contractRevision.setSalerContactPhone(generateContractRequest.getSupplierContactPhone());
        //地址
        if (StringUtils.isNotBlank(generateContractRequest.getAddressCode())) {
            //查找地址
            Address address = addressRepository.findById(
                AddressId.builder()
                    .dcCompId(companyCode)
                    .code(generateContractRequest.getAddressCode())
                    .build()
            ).orElseThrow(() -> new IOException("数据库中找不到该地址"));
            contractRevision.setDeliveryCode(generateContractRequest.getAddressCode());
            contractRevision.setAreaCode(address.getAreaCode());
            contractRevision.setAreaName(address.getAreaName());
            contractRevision.setAddress(address.getAddress());
        } else {
            contractRevision.setDeliveryCode(null);
            contractRevision.setAreaCode(null);
            contractRevision.setAreaName(null);
            contractRevision.setAddress(null);
        }
        //联系人
        if (StringUtils.isNotBlank(generateContractRequest.getContactCode())) {
            //联系人
            CompContacts compContacts = compContactsRepository.findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_Code(
                generateContractRequest.getAddressCode(),
                companyCode,
                generateContractRequest.getContactCode()
            ).orElseThrow(() -> new IOException("数据库中找不到该联系人"));
            contractRevision.setContactCode(generateContractRequest.getContactCode());
            contractRevision.setConsigneeName(compContacts.getContName());
            contractRevision.setConsigneePhone(compContacts.getContPhone());
        } else {
            contractRevision.setContactCode(null);
            contractRevision.setConsigneeName(null);
            contractRevision.setConsigneePhone(null);
        }
        contractRevision.setConfirmTotalPriceVat(generateContractRequest.getSum());
        return contractRevision;
    }

    /**
     * 将退回的产品生产货运记录
     *
     * @param id          合同id
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     */
    public void saveDelivery(String id, String companyCode, String operator) {
        // TODO: 2022/6/1 需要完善货运记录 库存等问题
        List<DeliverRecord> deliverRecords = deliveryTempRepository.findDeliverTempsByDeliverTempId_ContractId(id)
            .stream().map(deliverRecordMapper::toDeliverRecord).toList();
        if (deliverRecords.size() > 0) {
            UUID uuid = UUID.randomUUID();
            String deliveryId = "HY-" + companyCode + "-" + operator + "-" + uuid.toString().substring(0, 8);
            deliverRecords.forEach(deliverRecord -> {
                deliverRecord.getDeliverRecordId().setDeliverCode(deliveryId);
                deliverRecord.setType(DeliverType.DELIVER);
            });
            DeliverBase deliverBase = DeliverBase.builder()
                .id(deliveryId)
                .contractId(id)
                .type(DeliverType.DELIVER)
                .createdAt(LocalDateTime.now())
                .createdBy(operator)
                .createdByComp(companyCode)
                .state(DeliverState.PENDING)
                .deliverRecords(deliverRecords)
                .build();
            deliverBaseRepository.save(deliverBase);
            deliveryTempRepository.deleteDeliverTempsByDeliverTempId_ContractId(id);
        }
    }

    /**
     * 将退回的产品生产货运记录
     *
     * @param id          合同id
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     * @param list        退回产品列表
     */
    public void saveDelivery(String id, Integer revision, String companyCode, String operator, List<VPContractRequest.DeliveryRecord> list) {
        // TODO: 2022/6/1 需要完善货运记录 库存等问题
        List<PurchaseContractRecord> contractRecords = purchaseContractRecordRepository.findContractRecordsByPurchaseContractRecordId_ContractIdAndPurchaseContractRecordId_Revision(id, revision);
        Map<String, PurchaseContractRecord> map = new HashMap<>();
        contractRecords.forEach(contractRecord -> map.put(contractRecord.getProductId(), contractRecord));
        List<DeliverRecord> deliverRecords = new ArrayList<>();
        AtomicInteger maxCode = new AtomicInteger(1);
        UUID uuid = UUID.randomUUID();
        String deliveryId = "HY-" + companyCode + "-" + operator + "-" + uuid.toString().substring(0, 8);
        DeliverBase deliverBase = DeliverBase.builder()
            .id(deliveryId)
            .contractId(id)
            .type(DeliverType.DELIVER)
            .createdAt(LocalDateTime.now())
            .createdBy(operator)
            .createdByComp(companyCode)
            .state(DeliverState.PENDING)
            .build();
        for (VPContractRequest.DeliveryRecord v : list) {
            PurchaseContractRecord temp = map.get(v.getProductId());
            if (v.getReturnAmount().floatValue() > 0) {
                if (temp.getProductId().equals(v.getProductId())) {
                    DeliverRecord deliverTemp = DeliverRecord.builder()
                        .deliverRecordId(
                            DeliverRecordId.builder()
                                .deliverCode(deliveryId)
                                .code(maxCode.get())
                                .build()
                        )
                        .type(DeliverType.DELIVER)
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
                    deliverRecords.add(deliverTemp);
                }
            }
            maxCode.getAndIncrement();
        }
        deliverBase.setDeliverRecords(deliverRecords);
        deliverBaseRepository.save(deliverBase);
    }

    /**
     * 查询采购合同中已开票产品列表
     *
     * @param id 合同主键
     * @return 产品列表
     */
    // TODO: 2022/6/2   需要重新完善
    public List<VInvoicedResponse.VProduct> getInvoicedList(String id) {
        List<VInvoicedResponse.VProduct> products = new ArrayList<>();
        VInvoicedResponse.VProduct product = new VInvoicedResponse.VProduct();
        product.setId("1616490002");
        product.setCode("161546001");
        product.setDescribe("活接 PVDF-HP/FKM SDR21 PN16 d20");
        product.setChargeUnit("个");
        product.setAmount(new BigDecimal("1"));
        product.setInvoiceAmount(new BigDecimal("98.4").setScale(2, RoundingMode.HALF_UP));
        products.add(product);
        VInvoicedResponse.VProduct product1 = new VInvoicedResponse.VProduct();
        product1.setId("150000001");
        product1.setCode("150000001");
        product1.setDescribe("活接 PVDF-HP/FKM SDR21 PN16 d20");
        product1.setChargeUnit("个");
        product1.setAmount(new BigDecimal("4"));
        product1.setInvoiceAmount(new BigDecimal("198.55").setScale(2, RoundingMode.HALF_UP));
        products.add(product1);
        return products;
    }

    /**
     * 查询采购合同中已收货物产品列表
     *
     * @param id 合同主键
     * @return 产品列表
     */
    // TODO: 2022/6/2   需要重新完善
    public List<VReceivedResponse.VProduct> getReceivedList(String id) {
        return contractReceivedRepository.findContractReceivedList(id)
            .stream().map(contractMapper::toTContractReceived)
            .map(contractMapper::toVProduct)
            .toList();
    }

    /**
     * 撤销采购合同
     *
     * @param id          合同主键
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     */
    @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'-'+'*'")
    @Transactional
    public void removePurchaseContract(String id, String companyCode, String operator) {
        try {
            //查询合同（不包括合同明细）
            PurchaseContractDetail contractDetail = purchaseContractDetailRepository.findById(id)
                .orElseThrow(() -> new IOException("请求的合同不存在"));
            //获取最大版本号
            int revision = getMaxRevision(id);
            //入格合同状态为未完成并且大于1，删除临时合同记录以及版本号为revision的合同
            if (contractDetail.getState().equals(ContractState.UN_FINISHED) && revision > 1) {
                purchaseContractRecordTempRepository.deleteProducts(id);
                purchaseContractRevisionRepository.deleteById(PurchaseContractRevisionId.builder()
                    .id(id)
                    .revision(revision)
                    .build());
            } else if (contractDetail.getState().equals(ContractState.UN_FINISHED) && revision == 1) {
                //入格合同状态为未完成并且等于1，将临时合同记录存入合同记录中
                List<PurchaseContractRecord> records = new ArrayList<>();
                List<PurchaseContractRecordTemp> contractRecordTemps = purchaseContractRecordTempRepository.findContractRecordTempsByPurchaseContractRecordTempId_ContractId(id);
                for (int i = 0; i < contractRecordTemps.size(); i++) {
                    PurchaseContractRecord record = Optional.of(contractRecordTemps.get(i)).map(contractRecordMapper::toContractRecord).orElse(null);
                    record.getPurchaseContractRecordId().setCode(i + 1);
                    records.add(
                        record
                    );
                }
                purchaseContractRecordRepository.saveAll(records);
            } else if (contractDetail.getState().equals(ContractState.CANCELLATION)) {
                throw new Exception("合同已撤销");
            }
            //将合同状态设置为撤销
            contractDetail.setState(ContractState.CANCELLATION);
            //保存退回记录
            saveDelivery(id, companyCode, operator);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 采购合同预览列表
     *
     * @param id       主键
     * @param revision 版本号
     * @return 采购合同预览列表
     */
    public List<VPContractPreviewResponse.VProduct> modifyContractPreview(String id, Integer revision) {
        return contractRecordPreviewRepository.findPurchaseContractRecordPreview(id)
            .stream().map(contractRecordMapper::toTContractRecordPreview)
            .map(contractRecordMapper::toVProduct)
            .toList();
    }

    /**
     * 更新合同总价
     *
     * @param contractRecordTemps 合同明细列表
     * @param id                  合同主键
     * @return 返回成功或者失败信息
     */
    public boolean countSum(List<PurchaseContractRecordTemp> contractRecordTemps,
                            String id,
                            int revision,
                            String operator) {
        try {
            //判断是否需要重新计算价格
            List<PurchaseContractRecordTemp> lists = contractRecordTemps
                .stream()
                .filter(contractRecordTemp -> contractRecordTemp.getPrice() == null)
                .toList();

            BigDecimal totalPrice = new BigDecimal("0");
            BigDecimal totalPriceVat = new BigDecimal("0");
            BigDecimal vat;
            if (lists.size() == 0) {
                //是 重新计算价格
                for (PurchaseContractRecordTemp contractRecordTemp : contractRecordTemps) {
                    totalPrice = totalPrice.add(contractRecordTemp.getTotalPrice());
                    totalPriceVat = totalPriceVat.add(contractRecordTemp.getTotalPriceVat());
                }
                vat = totalPriceVat.subtract(totalPrice).setScale(2, RoundingMode.HALF_UP);
            } else {
                totalPrice = null;
                totalPriceVat = null;
                vat = null;
            }
            BigDecimal totalPrice1 = totalPrice == null ? null : totalPrice.setScale(2, RoundingMode.HALF_UP);
            purchaseContractRevisionDetailRepository.updateContract(
                totalPrice1,
                totalPriceVat == null ? null : totalPriceVat.setScale(2, RoundingMode.HALF_UP),
                vat,
                LocalDateTime.now(),
                operator,
                id,
                revision
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制合同
     *
     * @param contractId  合同id
     * @param revision    版本
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     * @return 返回新建合同主键
     */
    @CacheEvict(value = "purchase_contract_List;1800", key = "#companyCode+'-'+'*'")
    @Transactional
    public String copyContract(String contractId, Integer revision, String companyCode, String operator) {
        try {
            //查询合同详情
            PurchaseContractDetail contractDetail = Optional.of(getContractDetail(contractId)).map(contractMapper::toContractDetail).orElseThrow();
            //版本合同详情
            PurchaseContractRevision contractRevision = purchaseContractRevisionRepository.findById(
                PurchaseContractRevisionId
                    .builder()
                    .id(contractId)
                    .revision(revision)
                    .build()
            ).map(contractMapper::toContractRevision).orElseThrow();
            List<PurchaseContractRecordTemp> contractRecordTemps;
            //合同明细
            if (contractDetail.getState().equals(ContractState.UN_FINISHED)) {
                contractRecordTemps = purchaseContractRecordTempRepository.findContractRecordTempsByPurchaseContractRecordTempId_ContractId(contractId);
            } else {
                contractRecordTemps = purchaseContractRecordRepository.findContractRecordsByPurchaseContractRecordId_ContractIdAndPurchaseContractRecordId_Revision(contractId, revision)
                    .stream().map(contractRecordMapper::toContractRecordTemp).toList();
            }
            //合同编码最大号
            String maxCode = purchaseContractDetailRepository.findMaxCode(companyCode, operator).orElse("01");
            Map<String, String> map = getContractCode(maxCode, operator, companyCode, contractDetail.getSalerComp());
            contractRecordTemps.forEach(contractRecordTemp -> {
                contractRecordTemp.getPurchaseContractRecordTempId().setContractId(map.get("id"));
                contractRecordTemp.getPurchaseContractRecordTempId().setRevision(1);
            });
            contractDetail.setCode(map.get("code"));
            contractDetail.setId(map.get("id"));
            contractDetail.setState(ContractState.UN_FINISHED);
            contractDetail.setCreatedAt(LocalDateTime.now());
            contractRevision.getPurchaseContractRevisionId().setId(map.get("id"));
            contractRevision.setContractRecords(null);
            contractRevision.setCreatedAt(LocalDateTime.now());
            purchaseContractDetailRepository.save(contractDetail);
            purchaseContractRevisionRepository.save(contractRevision);
            purchaseContractRecordTempRepository.saveAll(contractRecordTemps);
            return map.get("id");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }



}
