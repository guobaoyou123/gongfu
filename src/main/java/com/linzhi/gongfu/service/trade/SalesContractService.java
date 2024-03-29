package com.linzhi.gongfu.service.trade;

import com.linzhi.gongfu.dto.TContract;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.trade.ContractMapper;
import com.linzhi.gongfu.mapper.trade.ContractRecordMapper;
import com.linzhi.gongfu.mapper.DeliverRecordMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.repository.trade.*;
import com.linzhi.gongfu.util.CalculateUtil;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.trade.*;
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
public class SalesContractService {

    private final SalesContractRepository salesContractRepository;
    private final ContractMapper contractMapper;
    private final OperatorRepository operatorRepository;
    private final SalesContractRecordTempRepository salesContractRecordTempRepository;
    private final SalesContractRecordRepository salesContractRecordRepository;
    private final ContractRecordMapper contractRecordMapper;
    private final SalesContractRevisionRepository salesContractRevisionRepository;
    private final CompanyRepository companyRepository;
    private final CompTaxModelRepository compTaxModelRepository;
    private final SalesContractsRepository salesContractsRepository;
    private final TaxRatesRepository vatRatesRepository;
    private final ProductRepository productRepository;
    private final SalesContractRevisionDetailRepository salesContractRevisionDetailRepository;
    private final AddressRepository addressRepository;
    private final CompContactsRepository compContactsRepository;
    private final DeliverBaseRepository deliverBaseRepository;
    private final DeliveryTempRepository deliveryTempRepository;
    private final DeliverRecordMapper deliverRecordMapper;
    private final ImportProductTempRepository importProductTempRepository;
    private final ContractRecordPreviewRepository contractRecordPreviewRepository;
    private final ContractReceivedRepository contractReceivedRepository;
    private final PurchaseContractBaseRepository purchaseContractBaseRepository;

    /**
     * 查看合同列表
     *
     * @param state        合同状态
     * @param customerCode 客户编码
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param companyCode  本单位编码
     * @param operator     操作员编码
     * @param pageable     分页
     * @return 返回合同列表
     */
    public Page<TContract> pageContracts(String state, String customerCode,
                                         String startTime, String endTime,
                                         String companyCode, String operator,
                                         Pageable pageable) throws Exception {

        List<TContract> tContractList = listContracts(companyCode, operator, state);
        if (!customerCode.equals("")) {
            tContractList = tContractList.stream().filter(tContract -> tContract.getBuyerComp().equals(customerCode)).toList();
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
    @Cacheable(value = "sales_contract_List;1800", key = "#companyCode+'-'+#operator+'-'+#state")
    public List<TContract> listContracts(String companyCode, String operator, String state) throws Exception {

        Operator operator1 = operatorRepository.findById(
            OperatorId.builder()
                .operatorCode(operator)
                .companyCode(companyCode)
                .build()
        ).orElseThrow(() -> new IOException("请求的操作员找不到"));
        if (operator1.getAdmin().equals(Whether.YES))
            return salesContractsRepository.listContracts(companyCode, state)
                .stream()
                .map(contractMapper::toContractList)
                .toList();
        return salesContractsRepository.listContracts(companyCode, operator, state)
            .stream()
            .map(contractMapper::toContractList)
            .toList();

    }

    /**
     * 根据合同主键、版本号查询销售合同详情
     *
     * @param id       合同主键
     * @param revision 版本号
     * @return 返回合同详情
     */
    public VPContractDetailResponse.VContract getSalesContractDetail(String id, int revision) {
        SalesContractRevisionDetail salesContractRevisionDetail = getSalesContractRevisionDetail(id, revision);
        List<SalesContractRecordTemp> salesContractRecordTemps = salesContractRevisionDetail.getContractRecordTemps();
        List<SalesContractRecord> salesContractRecords = salesContractRevisionDetail.getContractRecords();
        Optional<TContract> contractRevision = Optional.of(salesContractRevisionDetail)
            .map(contractMapper::toTContractDetail);
        if (salesContractRecordTemps != null && salesContractRecordTemps.size() > 0) {
            contractRevision.get().setRecords(salesContractRecordTemps.stream()
                .map(contractRecordMapper::toTContractRecord)
                .toList());
        }

        if (salesContractRecords != null && salesContractRecords.size() > 0) {
            contractRevision.get().setRecords(salesContractRecords.stream()
                .map(contractRecordMapper::toTContractRecord)
                .toList());
        }
        if (revision > 1) {
            SalesContractRevision perSalesContractRevision = salesContractRevisionDetail.getSalesContractRevisions().stream().filter(
                s -> s.getSalesContractRevisionId().getRevision() == (revision - 1)
            ).toList().get(0);
            contractRevision.get().setPreviousTaxedTotal(perSalesContractRevision.getTotalPriceVat());
            contractRevision.get().setPreviousUntaxedTotal(perSalesContractRevision.getTotalPrice());
        }
        return contractRevision.map(contractMapper::toContractDetail).orElse(null);
    }

    /**
     * 判断修改后的合同是否与上一版本相同
     *
     * @param id       合同
     * @param revision 版本号
     * @return 返回是或者否
     */
    public boolean judgeContractRev(String id, Integer revision) {
        //合同详情
        SalesContractRevisionDetail salesContractRevisionDetail = getSalesContractRevisionDetail(id, revision);
        //获取版本号为revision 的版本基础详情
        SalesContractRevision contractRevision = salesContractRevisionDetail.getSalesContractRevisions().stream()
            .filter(s -> s.getSalesContractRevisionId().getRevision().intValue() == revision.intValue())
            .toList().get(0);
        SalesContractRevision perContractRevision = salesContractRevisionDetail.getSalesContractRevisions().stream()
            .filter(s -> s.getSalesContractRevisionId().getRevision() == (revision - 1))
            .toList().get(0);
        List<SalesContractRecordTemp> contractRecordTemps = salesContractRevisionDetail.getContractRecordTemps();
        StringBuilder fingerprint = new StringBuilder(contractRevision.getOfferMode() + "-");
        for (SalesContractRecordTemp contractRecordTemp : contractRecordTemps) {

            fingerprint.append(contractRecordTemp.getProductId())
                .append("-")
                .append(contractRecordTemp.getChargeUnit())
                .append("-")
                .append(contractRecordTemp.getAmount().setScale(4, RoundingMode.HALF_UP))
                .append("-")
                .append(contractRecordTemp.getPriceVat() == null ? null : contractRecordTemp.getPriceVat().setScale(4, RoundingMode.HALF_UP))
                .append("-")
                .append(contractRecordTemp.getVatRate());

        }
        StringBuilder perFingerprint = new StringBuilder(perContractRevision.getOfferMode() + "-");
        for (SalesContractRecordTemp contractRecordTemp : contractRecordTemps) {
            if (contractRecordTemp.getPreviousAmount() != null) {
                perFingerprint.append(contractRecordTemp.getProductId())
                    .append("-")
                    .append(contractRecordTemp.getPreviousChargeUnit())
                    .append("-")
                    .append(contractRecordTemp.getPreviousAmount().setScale(4, RoundingMode.HALF_UP))
                    .append("-")
                    .append(contractRecordTemp.getPreviousPriceVat() == null ? null : contractRecordTemp.getPreviousPriceVat().setScale(4, RoundingMode.HALF_UP))
                    .append("-")
                    .append(contractRecordTemp.getPreviousVatRate());
            }
        }
        return fingerprint.toString().equals(perFingerprint.toString());
    }

    /**
     * 新建空的询价单
     *
     * @param customerCode 客户编码
     * @param companyCode  本单位编码
     * @param companyName  本单位名称
     * @param operator     操作员编码
     * @param operatorName 操作员姓名
     * @return 返回成功信息
     */
    @CacheEvict(value = "sales_contract_List;1800", key = "#companyCode+'-'+'*'")
    @Transactional
    public Optional<String> saveSalesContractEmpty(String customerCode, String companyCode, String companyName, String operator, String operatorName) {
        try {
            Company customer = companyRepository.findById(customerCode).orElseThrow(() -> new IOException("未从数据库中查到客户信息"));
            String maxCode = salesContractRepository.findMaxCode(companyCode, operator).orElse("01");
            Map<String, String> map = getContractCode(maxCode, operator, companyCode, customerCode);
            SalesContractBase contractDetail = createdContract(map.get("id"),
                map.get("code"),
                companyCode,
                operator,
                customerCode,
                customer.getNameInCN(),
                companyCode,
                companyName,
                ContractState.UN_FINISHED);
            CompTaxModel taxModel = compTaxModelRepository.findById(CompTradeId.builder()
                .compSaler(companyCode)
                .compBuyer(customerCode)
                .build()).orElseThrow(() -> new IOException("从数据库中没有查询到"));
            SalesContractRevision contractRevision = createContractRevision(map.get("id"), operatorName, taxModel.getTaxModel(), null, 1, operator);
            salesContractRepository.save(contractDetail);
            salesContractRevisionRepository.save(contractRevision);
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
     * @param customerCode 客户编码
     * @return 生成合同唯一编码和合同编号列表
     */
    public Map<String, String> getContractCode(String max, String operatorCode, String companyCode, String customerCode) {
        Map<String, String> map = new HashMap<>();
        String mCode = ("0000" + max).substring(("0000" + max).length() - 3);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate data = LocalDate.now();
        //uuid
        UUID uuid = UUID.randomUUID();
        String id = "HT-" + companyCode + "-" + operatorCode + "-" + uuid.toString().substring(0, 8);
        String code = "HT-" + operatorCode + "-" + customerCode + "-" + dtf.format(data) + "-" + mCode;
        map.put("id", id);
        map.put("code", code);
        return map;
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
     * @return 合同实体
     */
    public SalesContractBase createdContract(String id, String code, String createdByComp,
                                             String createdBy, String buyerComp, String buyerCompName,
                                             String salerComp, String salerCompName,
                                             ContractState state) {


        return SalesContractBase.builder()
            .id(id)
            .code(code)
            .createdByComp(createdByComp)
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
     * @param salerContactName 买当联系人姓名
     * @param offerMode        税模式
     * @param contractNo       本单位合同号
     * @param revision         版本
     * @return 返回合同版本实体
     */
    public SalesContractRevision createContractRevision(String id,
                                                        String salerContactName,
                                                        TaxMode offerMode,
                                                        String contractNo,
                                                        int revision,
                                                        String operator
    ) {
        return SalesContractRevision.builder()
            .salesContractRevisionId(SalesContractRevisionId.builder()
                .revision(revision)
                .id(id)
                .build())
            .orderCode(contractNo)
            .salerContactName(salerContactName)
            .createdAt(LocalDateTime.now())
            .offerMode(offerMode)
            .createdAt(LocalDateTime.now())
            .confirmedAt(LocalDateTime.now())
            .confirmedBy(operator)
            .build();
    }

    /**
     * 添加销售合同产品
     *
     * @param productId 产品id
     * @param price     单价
     * @param amount    数量
     * @param id        合同主键
     * @param revision  版本号
     * @param operator  操作员编码
     * @return 返回成功或者失败信息
     */
    @Caching(evict = {@CacheEvict(value = "sales_contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "sales_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public boolean saveProduct(String productId, BigDecimal price, BigDecimal amount, String id, int revision, String companyCode, String operator) {
        try {
            //查询合同版本详情
            SalesContractRevision contractRevision = salesContractRevisionRepository.findById(SalesContractRevisionId.builder()
                .id(id)
                .revision(revision)
                .build()).orElseThrow(() -> new IOException("数据库中未查询到该数据"));
            List<SalesContractRecordTemp> recordTemps = getSalesContractRevisionDetail(id,revision).getContractRecordTemps();
            //查询明细最大顺序号
            String maxCode = salesContractRecordTempRepository.findMaxCode(id);
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
            //形成合同明细实体
            SalesContractRecordTemp contractRecordTemp = createContractRecordTemp(
                id,
                revision,
                Integer.parseInt(maxCode) + 1,
                product,
                price,
                contractRevision.getOfferMode(),
                amount,
                goods.getRate(), 0
            );
            //保存合同明
            salesContractRecordTempRepository.save(contractRecordTemp);
            recordTemps.add(contractRecordTemp);
            var flag =  countSum(recordTemps,contractRevision,operator);
            if(!flag)
                throw new Exception("更新总价失败");
            return true;
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
    public SalesContractRecordTemp createContractRecordTemp(String id, int revision, int code, Product product, BigDecimal price,
                                                            TaxMode taxMode, BigDecimal amount, BigDecimal vatRate, Integer productCode) {
        return SalesContractRecordTemp.builder()
            .salesContractRecordTempId(SalesContractRecordTempId.builder()
                .revision(revision)
                .contractId(id)
                .code(code)
                .build())
            .parentCode(productCode)
            .productId(product.getId())
            .productCode(product.getCode())
            .compCustomCode(product.getCode())
            .customerCustomCode(product.getCode())
            .productId(product.getId())
            .type(VatRateType.GOODS)
            .productCode(product.getCode())
            .brandCode(product.getBrandCode())
            .brand(product.getBrand())
            .productDescription(product.getDescribe())
            .facePrice(product.getFacePrice())
            .sysChargeUnit(product.getChargeUnit())
            .chargeUnit(product.getChargeUnit())
            .price(price != null ? taxMode.equals(TaxMode.UNTAXED) ? price : CalculateUtil.calculateUntaxedUnitPrice(price, vatRate) : null)
            .priceVat(price != null ? taxMode.equals(TaxMode.INCLUDED) ? price : CalculateUtil.calculateTaxedUnitPrice(price, vatRate) : null)
            .sysAmount(amount)
            .amount(amount)
            .createdAt(LocalDateTime.now())
            .ratio(new BigDecimal("1"))
            .vatRate(vatRate)
            .specification(product.getSpecification())
            .totalPrice(price != null ? taxMode.equals(TaxMode.UNTAXED) ? CalculateUtil.calculateSubtotal(price, amount) : CalculateUtil.calculateSubtotal(CalculateUtil.calculateUntaxedUnitPrice(price, vatRate), amount) : null)
            .totalPriceVat(price != null ? taxMode.equals(TaxMode.INCLUDED) ? CalculateUtil.calculateSubtotal(price, amount) : CalculateUtil.calculateSubtotal(CalculateUtil.calculateTaxedUnitPrice(price, vatRate), amount) : null)
            .build();
    }

    /**
     * 删除销售合同产品
     *
     * @param codes    明细序列号
     * @param id       销售合同
     * @param revision 版本号
     * @param operator 操作员
     */
    @Caching(evict = {@CacheEvict(value = "sales_contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "sales_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public void removeContractProduct(List<Integer> codes, String id, int revision, String companyCode, String operator) {
        try {
            salesContractRecordTempRepository.deleteProducts(id, codes);
            SalesContractRevision salesContractRevision = salesContractRevisionRepository.findById(
                SalesContractRevisionId.builder()
                    .id(id)
                    .revision(revision)
                    .build()
            ).orElseThrow(() -> new IOException("数据库中未查询到该数据"));
            //查询合同明细列表
            List<SalesContractRecordTemp> list = salesContractRecordTempRepository.findContractRecordTempsBySalesContractRecordTempId_ContractId(id).stream()
                .filter(s -> !codes.contains(s.getSalesContractRecordTempId().getCode()))
                .toList();
            //计算总价
            var flag =   countSum(list, salesContractRevision, operator);
            if(!flag)
                throw new Exception("更新总价失败");
        } catch (Exception e) {
            e.printStackTrace();
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
        return Integer.parseInt(salesContractRevisionRepository.getMaxRevision(id).orElseThrow(() -> new IOException("不存在该合同")));
    }

    /**
     * 将销售合同的状态设置为未确认的状态，生成新一版合同，生成临时合同明细记录
     *
     * @param id          合同主键
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     * @return 返回版本号
     */
    @Caching(
        evict = {
            @CacheEvict(value = "sales_contract_List;1800", key = "#companyCode+'-'+'*'"),
            @CacheEvict(value = "sales_contract_revision_detail;1800", key = "#id+'-'+'*'")
        }
    )
    @Transactional
    public Integer modifyContractState(String id, String companyCode, String operator, Integer revision) {
        try {
            SalesContractRevisionDetail contractRevisionDetail = getSalesContractRevisionDetail(id, revision);
            List<SalesContractRecord> salesContractRecords = contractRevisionDetail.getContractRecords();
            if (salesContractRecords != null && salesContractRecords.size() > 0) {
                List<SalesContractRecordTemp> contractRecordTemps = salesContractRecords
                    .stream().map(contractRecordMapper::toContractRecordTemp).toList();
                salesContractRecordTempRepository.saveAll(contractRecordTemps);
            }
            SalesContractRevision contractRevision = Optional.of(contractRevisionDetail)
                .map(contractMapper::toContractRevision)
                .orElseThrow(() -> new IOException("不存在该合同"));
            if (contractRevisionDetail.getSalesContractBase().getState().equals(ContractState.UN_FINISHED))
                throw new Exception("该合同已经是未确认的，不可再次修改");

            contractRevision.getSalesContractRevisionId().setRevision(revision + 1);
            contractRevision.setCreatedAt(LocalDateTime.now());
            contractRevision.setFingerprint(null);
            contractRevision.setConfirmedAt(null);
            contractRevision.setConfirmedBy(null);
            contractRevision.setModifiedAt(LocalDateTime.now());
            contractRevision.setModifiedBy(operator);
            salesContractRevisionRepository.save(contractRevision);
            salesContractRepository.updateContractState(ContractState.UN_FINISHED, id);
            return revision + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
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
            num = salesContractRevisionRepository.findByOrderCode(companyCode, contractNo);
        } else {
            num = salesContractRevisionRepository.findByOrderCode(companyCode, contractNo, contractId);
        }

        return num <= 0;
    }

    /**
     * 导出产品模板
     *
     * @param id       合同主键
     * @param revision 版本
     * @return 产品列表
     */
    public Map<String, Object> exportProductTemplate(String id, Integer revision) {
        Map<String, Object> map = new HashMap<>();
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        try {
            var contract = getSalesContractDetail(id, revision);
            if (contract.getProducts() != null && contract.getProducts().size() > 0) {
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
            } else {
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
            map.put("list", list);
            map.put("code", contract.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 导出产品
     *
     * @param id       合同主键
     * @param revision 版本
     * @return 产品列表
     */
    public Map<String, Object> exportProduct(String id, Integer revision, String type) {
        Map<String, Object> map = new HashMap<>();
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        try {
            var contract = getSalesContractDetail(id, revision);
            contract.getProducts().forEach(record -> {
                LinkedHashMap<String, Object> m = new LinkedHashMap<>();
                if (type.equals("0")) {
                    m.put("产品代码", record.getCustomerPCode());
                } else {
                    m.put("产品代码", record.getCode());
                }
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
            map.put("list", list);
            map.put("code", contract.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
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
    @CacheEvict(value = "sales_contract_List;1800", key = "#companyCode+'-'+'*'")
    @Transactional
    public String copyContract(String contractId, Integer revision, String companyCode, String operator) {
        try {
            //查询合同详情
            SalesContractRevisionDetail salesContractRevisionDetail = getSalesContractRevisionDetail(contractId, revision);
            var contractRecordTempList = salesContractRevisionDetail.getContractRecordTemps();
            var contractRecords = salesContractRevisionDetail.getContractRecords();
            //合同版本详情
            SalesContractRevision salesContractRevision = Optional.of(salesContractRevisionDetail)
                .map(contractMapper::toContractRevision)
                .orElseThrow(() -> new IOException("未找到数据"));
            salesContractRevision.getSalesContractRevisionId().setRevision(1);
            //合同基础表
            SalesContractBase salesContractBase = Optional.of(salesContractRevisionDetail.getSalesContractBase())
                .map(contractMapper::toContractBase)
                .orElseThrow();
            List<SalesContractRecordTemp> contractRecordTemps;
            //合同明细
            if (salesContractRevisionDetail.getSalesContractBase().getState().equals(ContractState.UN_FINISHED)) {
                contractRecordTemps = contractRecordTempList;
            } else {
                contractRecordTemps = contractRecords
                    .stream().map(contractRecordMapper::toContractRecordTemp).toList();
            }
            //合同编码最大号
            String maxCode = salesContractRepository.findMaxCode(companyCode, operator).orElse("01");
            Map<String, String> map = getContractCode(maxCode, operator, companyCode, salesContractBase.getBuyerComp());
            contractRecordTemps.forEach(contractRecordTemp -> {
                contractRecordTemp.getSalesContractRecordTempId().setContractId(map.get("id"));
                contractRecordTemp.getSalesContractRecordTempId().setRevision(1);
            });
            salesContractBase.setCode(map.get("code"));
            salesContractBase.setId(map.get("id"));
            salesContractBase.setState(ContractState.UN_FINISHED);
            salesContractBase.setCreatedAt(LocalDateTime.now());
            salesContractRevision.getSalesContractRevisionId().setId(map.get("id"));
            salesContractBase.setCreatedAt(LocalDateTime.now());
            salesContractRepository.save(salesContractBase);
            salesContractRevisionRepository.save(salesContractRevision);
            salesContractRecordTempRepository.saveAll(contractRecordTemps);
            return map.get("id");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取未确认的销售合同数量
     *
     * @param companyCode  本单位编码
     * @param operator     操作员编码
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param customerCode 客户编码
     * @return 返回确认的采购合同数量
     */
    public int getUnFinished(String companyCode, String operator, String startTime, String endTime, String customerCode) throws Exception {

        var page = pageContracts(ContractState.UN_FINISHED.getState() + "", customerCode, startTime, endTime, companyCode, operator, PageRequest.of(
            1,
            10
        ));
        return Integer.parseInt(String.valueOf(page.getTotalElements()));
    }

    /**
     * 修改销售合同
     *
     * @param vModifyInquiryRequest 修改信息
     * @param id                    合同主键
     * @param revision              合同版本
     */
    @Caching(evict = {@CacheEvict(value = "sales_contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "sales_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public void modifySalesContract(VInquiryRequest vModifyInquiryRequest, String id, int revision, String companyCode, String operator) {
        try {
            //查询合同详情
            SalesContractRevisionDetail salesContractRevisionDetail = getSalesContractRevisionDetail(id, revision);
            //合同临时明细列表
            List<SalesContractRecordTemp> contractRecordTemps = salesContractRevisionDetail.getContractRecordTemps();
            //合同基础信息
            SalesContractRevision contractRevision = salesContractRevisionDetail.getSalesContractRevisions().stream()
                .filter(s -> s.getSalesContractRevisionId().getRevision() == revision)
                .toList().get(0);
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
                    if (record.getSalesContractRecordTempId().getCode() == vProduct.getCode()) {
                        //保存计价单位
                        if (vProduct.getChargeUnit() != null) {
                            record.setChargeUnit(vProduct.getChargeUnit());
                            record.setRatio(vProduct.getRatio());
                        }
                        //数量
                        if (vProduct.getAmount() != null) {
                            record.setAmount(vProduct.getAmount().intValue() < 0 ? new BigDecimal("0") : vProduct.getAmount());
                            record.setSysAmount(vProduct.getAmount().multiply(vProduct.getRatio() == null ? record.getRatio() : vProduct.getRatio()));
                        }
                        //税率
                        record.setVatRate(vProduct.getVatRate() != null ? vProduct.getVatRate() : record.getVatRate());
                        //价格
                        record.setPrice(vProduct.getPrice() != null && vProduct.getPrice().intValue() < 0 ?
                            record.getPrice() : vProduct.getPrice() != null && vProduct.getPrice().intValue() >= 0 ?
                            contractRevision.getOfferMode().equals(TaxMode.UNTAXED) ? vProduct.getPrice() : CalculateUtil.calculateUntaxedUnitPrice(vProduct.getPrice(), record.getVatRate()) : record.getPrice());
                        record.setPriceVat(vProduct.getPrice() != null && vProduct.getPrice().intValue() < 0 ?
                            record.getPriceVat() : vProduct.getPrice() != null && vProduct.getPrice().intValue() >= 0 ?
                            contractRevision.getOfferMode().equals(TaxMode.INCLUDED) ? vProduct.getPrice() : CalculateUtil.calculateTaxedUnitPrice(vProduct.getPrice(), record.getVatRate()) : record.getPriceVat());
                        //总价
                        record.setTotalPrice(record.getPrice() == null ? null : CalculateUtil.calculateSubtotal(record.getPrice(), record.getAmount()));
                        record.setTotalPriceVat(record.getPriceVat() == null ? null : CalculateUtil.calculateSubtotal(record.getPriceVat(), record.getAmount()));
                    }
                }));
            }
            salesContractRecordTempRepository.saveAll(contractRecordTemps);
            //判断产品列表中的产品是否都有价格
            List<SalesContractRecordTemp> list1 = contractRecordTemps.stream()
                .filter(s -> s.getTotalPrice() == null)
                .toList();
            //list的长度为0，表示产品都有价格，需要计算总金额
            if (list1.size() == 0) {
                BigDecimal totalPrice = new BigDecimal("0");
                BigDecimal totalPriceVat = new BigDecimal("0");
                for (SalesContractRecordTemp t : contractRecordTemps) {
                    totalPrice = totalPrice.add(t.getTotalPrice());
                    totalPriceVat = totalPriceVat.add(t.getTotalPriceVat());
                }
                BigDecimal vat = totalPriceVat.subtract(totalPrice).setScale(2, RoundingMode.HALF_UP);
                contractRevision.setTotalPrice(totalPrice);
                contractRevision.setTotalPriceVat(totalPriceVat);
                contractRevision.setVat(vat);
                contractRevision.setModifiedBy(operator);
                contractRevision.setModifiedAt(LocalDateTime.now());
                salesContractRevisionRepository.save(contractRevision);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 销售合同详情
     *
     * @param id       合同主键
     * @param revision 版本号
     * @return 合同详情
     */

    @Cacheable(value = "sales_contract_revision_detail;1800", key = "#id+'-'+#revision")
    public SalesContractRevisionDetail getSalesContractRevisionDetail(String id, int revision) {
        return salesContractRevisionDetailRepository.getSalesContractRevisionDetailBySalesContractRevisionId(
            SalesContractRevisionId.builder()
                .id(id)
                .revision(revision)
                .build()
        ).orElse(null);
    }

    /**
     * 查找税模式和合同编号
     *
     * @param id       合同主键
     * @param revision 版本号
     * @return 返回税模式和合同编号
     */
    public Map<String, Object> findTaxModelAndEnCode(String id, int revision) {
        Map<String, Object> map = new HashMap<>();
        var contract = getSalesContractRevisionDetail(id, revision);
        map.put("taxMode", contract.getOfferMode());
        map.put("encode", contract.getSalesContractBase().getCode());
        return map;
    }


    /**
     * 查询合同是否重复（产品种类和数量）
     *
     * @param id       销售合同id
     * @param revision 版本号
     * @return 返回 true 或者 false
     */
    public Optional<String> findContractProductRepeat(String id, Integer revision) throws Exception {
        SalesContractRevisionDetail salesContractRevisionDetail = getSalesContractRevisionDetail(id, revision);
        if (!salesContractRevisionDetail.getSalesContractBase().getState().equals(ContractState.UN_FINISHED))
            throw new Exception("合同已确认");
        List<SalesContractRecordTemp> contractRecordTemps = salesContractRevisionDetail.getContractRecordTemps();
        //进行排序
        contractRecordTemps.sort((o1, o2) -> {
            if (o1.getProductId().compareTo(o2.getProductId()) == 0) {
                if (o1.getAmount().doubleValue() < o2.getAmount().doubleValue()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return o1.getProductId().compareTo(o2.getProductId());
        });

        // 将相同的产品系统数量进行累加
        for (int i = 0; i < contractRecordTemps.size() - 1; i++) {
            if (contractRecordTemps.get(i).getProductId().equals(contractRecordTemps.get(i + 1).getProductId())) {
                contractRecordTemps.get(i).setSysAmount(contractRecordTemps.get(i).getSysAmount().add(contractRecordTemps.get(i + 1).getSysAmount()));
                contractRecordTemps.remove(i + 1);
                i = i - 1;
            }
        }
        String str = createSequenceCode(
            contractRecordTemps.stream()
                .map(contractRecord ->
                    contractRecord.getProductId() + "-"
                        + contractRecord.getAmount().setScale(4, RoundingMode.HALF_UP)
                ).toList(),
            salesContractRevisionDetail.getSalesContractBase().getSalerComp(),
            salesContractRevisionDetail.getSalesContractBase().getBuyerComp(),
            salesContractRevisionDetail.getOfferMode()
        );
        //产品种类和数量相同的合同号
        List<String> contractId = salesContractRepository.findContractId(salesContractRevisionDetail.getSalesContractBase().getCreatedByComp(), str);

        return Optional.of(String.join(",", contractId));
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
     * 确认销售合同
     *
     * @param id                      合同主键
     * @param revision                版本
     * @param generateContractRequest 保存内容
     * @param companyCode             公司编码
     * @param operator                操作员编码
     */
    @Caching(evict = {@CacheEvict(value = "sales_contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "sales_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public void saveContractRevision(String id, int revision, VPContractRequest generateContractRequest, String companyCode, String operator) {
        try {
            SalesContractRevisionDetail salesContractRevisionDetail = getSalesContractRevisionDetail(id, revision);
            //合同明细
            List<SalesContractRecordTemp> contractRecordTemps = salesContractRevisionDetail.getContractRecordTemps();
            //查询合同详情
            SalesContractBase contractBase = salesContractRevisionDetail.getSalesContractBase();
            SalesContractRevision contractRevision = salesContractRevisionDetail.getSalesContractRevisions().stream()
                .filter(s -> s.getSalesContractRevisionId().getRevision() == revision).toList().get(0);
            if (!contractBase.getState().equals(ContractState.UN_FINISHED)) {
                throw new Exception("合同已确认");
            }
            //修改数据set到版本合同中
            contractRevision = modifyContractRevisionDetail(contractRevision, generateContractRequest, companyCode);

            //合同明细
            List<SalesContractRecord> records = new ArrayList<>();
            //计算折扣，折扣后价格，税额
            if (contractRevision.getTotalPrice() != null) {
                BigDecimal discount = (contractRevision.getTotalPriceVat().subtract(generateContractRequest.getSum())).divide(contractRevision.getTotalPriceVat(), 4, RoundingMode.HALF_UP);
                //计算折扣的含税价格 和未税价格以及小计等
                BigDecimal discountSumVat = new BigDecimal("0");
                BigDecimal discountSum = new BigDecimal("0");
                for (int i = 0; i < contractRecordTemps.size(); i++) {
                    SalesContractRecord record = Optional.of(contractRecordTemps.get(i)).map(contractRecordMapper::toSalesContractRecord).orElse(null);
                    record.getSalesContractRecordId().setCode(i + 1);
                    record.setDiscount(discount);
                    record.setDiscountedPrice(CalculateUtil.calculateDiscountedPrice(record.getPrice(), discount));
                    record.setTotalDiscountedPrice(CalculateUtil.calculateDiscountedSubtotal(record.getDiscountedPrice(), discount, record.getAmount()));
                    record.setDiscountedPriceVat(CalculateUtil.calculateDiscountedPrice(record.getPriceVat(), discount));
                    if (i == contractRecordTemps.size() - 1) {
                        record.setTotalDiscountedPriceVat(contractRevision.getConfirmTotalPriceVat().subtract(discountSumVat));
                    } else {
                        record.setTotalDiscountedPriceVat(CalculateUtil.calculateDiscountedSubtotal(record.getPriceVat(), discount, record.getAmount()));
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
            salesContractRecordRepository.saveAll(records);
            /*
              获取指纹
             */
            String str = createSequenceCode(
                recordSort(records),
                contractBase.getSalerComp(),
                contractBase.getBuyerComp(),
                contractRevision.getOfferMode()
            );
            salesContractRecordTempRepository.deleteProducts(id);
            contractRevision.setConfirmedAt(LocalDateTime.now());
            contractRevision.setConfirmedBy(operator);
            contractRevision.setFingerprint(str);
            salesContractRevisionRepository.save(contractRevision);

            //将收回记录保存到货运信息表中
            if (generateContractRequest.getReceivedRecords() != null && generateContractRequest.getReceivedRecords().size() > 0)
                saveDelivery(id, revision, companyCode, operator, generateContractRequest.getReceivedRecords());

            contractBase.setState(ContractState.FINISHED);

            contractBase.setPairedCode(findPairedCode(str, companyCode, 0, null));
            //保存合同
            salesContractRepository.save(contractBase);
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
    public SalesContractRevision modifyContractRevisionDetail(SalesContractRevision contractRevision, VPContractRequest generateContractRequest, String companyCode) throws IOException {
        contractRevision.setOrderCode(generateContractRequest.getContactNo());
        contractRevision.setBuyerOrderCode(generateContractRequest.getCustomerNo());
        //客户联系人
        contractRevision.setBuyerContactName(generateContractRequest.getCustomerContactName());
        //客户联系人电话
        contractRevision.setBuyerContactPhone(generateContractRequest.getCustomerContactPhone());
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
     * 将合同列表进行排序，并以产品编码-系统数量进行拼接成字符串
     *
     * @param records 合同明细
     * @return 返回字符串列表
     */
    private List<String> recordSort(List<SalesContractRecord> records) {
        //去重，将相同的产品进行累加
        Map<String, SalesContractRecord> map = new HashMap<>();
        for (SalesContractRecord record : records) {
            if (map.get(record.getProductId()) != null) {
                SalesContractRecord p = map.get(record.getProductId());
                p.setAmount(record.getAmount().add(p.getAmount()));
            } else {
                map.put(record.getProductId(), record);
            }
        }

        List<SalesContractRecord> recordList = new ArrayList<>();

        for (String key : map.keySet()) {

            recordList.add(map.get(key));

        }
        //进行排序
        recordList.sort((o1, o2) -> {
            if (o1.getProductId().compareTo(o2.getProductId()) == 0) {
                if (o1.getAmount().doubleValue() < o2.getAmount().doubleValue()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return o1.getProductId().compareTo(o2.getProductId());
        });
        return recordList.stream().map(contractRecord -> contractRecord.getProductId() + "-"
            + contractRecord.getAmount().setScale(4, RoundingMode.HALF_UP)

        ).toList();
    }

    /**
     * 将退回的产品生产货运记录
     *
     * @param id          合同id
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     * @param list        退回产品列表
     */
    public void saveDelivery(String id, Integer revision, String companyCode, String operator, List<VPContractRequest.VReceivedRecord> list) {
        // TODO: 2022/6/1 需要完善货运记录 库存等问题
        SalesContractRevisionDetail contractRevisionDetail = getSalesContractRevisionDetail(id, revision);
        List<SalesContractRecord> contractRecords = contractRevisionDetail.getContractRecords();
        Map<String, SalesContractRecord> map = new HashMap<>();
        contractRecords.forEach(contractRecord -> map.put(contractRecord.getProductId(), contractRecord));
        List<DeliverRecord> deliverRecords = new ArrayList<>();
        AtomicInteger maxCode = new AtomicInteger(1);
        UUID uuid = UUID.randomUUID();
        String deliveryId = "HY-" + companyCode + "-" + operator + "-" + uuid.toString().substring(0, 8);
        DeliverBase deliverBase = DeliverBase.builder()
            .id(deliveryId)
            .contractId(id)
            .type(DeliverType.RECEIVE)
            .createdAt(LocalDateTime.now())
            .createdBy(operator)
            .createdByComp(companyCode)
            .state(DeliverState.PENDING)
            .build();
        for (VPContractRequest.VReceivedRecord v : list) {
            SalesContractRecord temp = map.get(v.getProductId());
            if (v.getReceivedAmount().floatValue() > 0) {
                if (temp.getProductId().equals(v.getProductId())) {
                    DeliverRecord deliverTemp = DeliverRecord.builder()
                        .deliverRecordId(
                            DeliverRecordId.builder()
                                .deliverCode(deliveryId)
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
                        .sysChargeUnit(temp.getSysChargeUnit())
                        .chargeUnit(temp.getChargeUnit())
                        .ratio(temp.getRatio())
                        .amount(v.getReceivedAmount())
                        .sysAmount(v.getReceivedAmount().multiply(temp.getRatio()))
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
     * 查询合同基本信息
     *
     * @param id 合同主键
     * @return 返回合同基本信息
     * @throws IOException 异常
     */
    public SalesContractBase getContractDetail(String id) throws IOException {
        return salesContractRepository.findById(id).orElseThrow(() -> new IOException("请求的合同不存在"));
    }

    /**
     * 撤销该版本合同
     *
     * @param id             合同主键
     * @param revision       版本
     * @param contractDetail 合同基本信息
     */
    @Caching(evict = {
        @CacheEvict(value = "sales_contract_revision_detail;1800", key = "#id+'-'+'*'"),
        @CacheEvict(value = "sales_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public void removeCurrentRevision(String id, int revision, SalesContractBase contractDetail, String companyCode) {

        contractDetail.setState(ContractState.FINISHED);
        if (revision == 1) {
            contractDetail.setState(ContractState.CANCELLATION);
        } else {
            salesContractRevisionRepository.deleteById(SalesContractRevisionId.builder()
                .id(id)
                .revision(revision)
                .build());
            contractDetail.setPairedCode(findPairedCode(null, companyCode, revision - 1, id));
        }
        salesContractRepository.save(contractDetail);
        salesContractRecordTempRepository.deleteProducts(id);
        deliveryTempRepository.deleteDeliverTempsByDeliverTempId_ContractId(id);
    }

    /**
     * 撤销销售合同
     *
     * @param id          合同主键
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     */
    @Caching(
        evict = {
            @CacheEvict(value = "sales_contract_revision_detail;1800", key = "#id+'-'+'*'"),
            @CacheEvict(value = "sales_contract_List;1800", key = "#companyCode+'-'+'*'")
        }
    )
    @Transactional
    public void removeSalesContract(String id, String companyCode, String operator) {
        try {
            //查询基础信息
            SalesContractBase salesContractBase = salesContractRepository.findById(id)
                .orElseThrow(() -> new IOException("请求的合同不存在"));
            //获取最大版本号
            int revision = getMaxRevision(id);
            //入格合同状态为未完成并且大于1，删除临时合同记录以及版本号为revision的合同
            if (salesContractBase.getState().equals(ContractState.UN_FINISHED) && revision > 1) {
                salesContractRecordTempRepository.deleteProducts(id);
                salesContractRevisionRepository.deleteById(SalesContractRevisionId.builder()
                    .id(id)
                    .revision(revision)
                    .build());
            } else if (salesContractBase.getState().equals(ContractState.UN_FINISHED) && revision == 1) {
                //入格合同状态为未完成并且等于1，将临时合同记录存入合同记录中
                List<SalesContractRecord> records = new ArrayList<>();
                List<SalesContractRecordTemp> contractRecordTemps = salesContractRecordTempRepository
                    .findContractRecordTempsBySalesContractRecordTempId_ContractId(id);
                for (int i = 0; i < contractRecordTemps.size(); i++) {
                    SalesContractRecord record = Optional.of(contractRecordTemps.get(i))
                        .map(contractRecordMapper::toSalesContractRecord)
                        .orElse(null);
                    record.getSalesContractRecordId().setCode(i + 1);
                    records.add(
                        record
                    );
                }
                salesContractRecordRepository.saveAll(records);
            } else if (salesContractBase.getState().equals(ContractState.CANCELLATION)) {
                throw new Exception("合同已撤销");
            }
            //将合同状态设置为撤销
            salesContractBase.setState(ContractState.CANCELLATION);
            salesContractRepository.save(salesContractBase);
            //保存退回记录
            saveDelivery(id, companyCode, operator);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 保存导入产品为合同明细
     *
     * @param id          合同主键id
     * @param companyCode 单位id
     * @param operator    操作员编码
     * @return 返回成功或者失败信息
     */
    @Caching(evict = {
        @CacheEvict(value = "sales_contract_revision_detail;1800", key = "#id+'-'+#revision"),
        @CacheEvict(value = "sales_contract_List;1800", key = "#companyCode+'-'+'*'")
    })
    @Transactional
    public Map<String, Object> saveImportProducts(String id, String companyCode, String operator, int revision) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 500);
        resultMap.put("message", "保存失败");
        try {
            List<ImportProductTemp> list = importProductTempRepository.
                findImportProductTempsByImportProductTempId_DcCompIdAndImportProductTempId_OperatorAndImportProductTempId_InquiryId(companyCode, operator, id);
            salesContractRecordTempRepository.deleteProducts(id);
            List<SalesContractRecordTemp> contractRecordTemps = new ArrayList<>();
            //货物税率
            TaxRates goods = vatRatesRepository.findByTypeAndDeFlagAndUseCountry(VatRateType.GOODS, Whether.YES, "001")
                .orElseThrow(() -> new IOException("请求的货物税率不存在"));
            //查询采购合同
            var contract = getSalesContractRevisionDetail(id, 1);
            int maxCode = 1;
            for (ImportProductTemp importProductTemp : list) {
                //验证产品编码是否正确
                Product product = productRepository.
                    findProductByCodeAndBrandCode(
                        importProductTemp.getCode(),
                        importProductTemp.getBrandCode()
                    )
                    .orElseThrow(() -> new IOException("请求的产品不存在"));
                SalesContractRecordTemp contractRecordTemp = createContractRecordTemp(id, 1, maxCode, product,
                    StringUtils.isNotBlank(importProductTemp.getPrice()) ? new BigDecimal(importProductTemp.getPrice()) : null,
                    (contract.getOfferMode().getTaxMode() + "").equals("1") ? TaxMode.INCLUDED : TaxMode.UNTAXED,
                    new BigDecimal(importProductTemp.getAmount()), contract.getVatProductRate() != null ? contract.getVatProductRate() : goods.getRate(), 0);
                contractRecordTemps.add(contractRecordTemp);
                maxCode++;
            }
            //删除原有的产品明细
            importProductTempRepository.deleteProduct(id, companyCode, operator);
            salesContractRecordTempRepository.saveAll(contractRecordTemps);
            //计算总价
            var flag =    countSum(contractRecordTemps, contract.getSalesContractRevisions().stream().filter(s -> s.getSalesContractRevisionId().getRevision() == revision).toList().get(0), operator);
            if(!flag)
                throw new Exception("更新总价失败");
            resultMap.put("code", 200);
            resultMap.put("message", "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return resultMap;
        }
        return resultMap;
    }

    /**
     * 计算总价
     *
     * @param list                  销售明细列表
     * @param salesContractRevision 销售合同版本详情
     * @param operator              操作员编码
     */
    public boolean countSum(List<SalesContractRecordTemp> list, SalesContractRevision salesContractRevision, String operator) {
        //判断产品列表中的产品是否都有价格
        List<SalesContractRecordTemp> list1 = list.stream()
            .filter(s -> s.getTotalPrice() == null)
            .toList();
        //list的长度为0，表示产品都有价格，需要计算总金额
        if (list1.size() == 0) {
            try {
                BigDecimal totalPrice = new BigDecimal("0");
                BigDecimal totalPriceVat = new BigDecimal("0");
                for (SalesContractRecordTemp t : list) {
                    totalPrice = totalPrice.add(t.getTotalPrice());
                    totalPriceVat = totalPriceVat.add(t.getTotalPriceVat());
                }
                BigDecimal vat = totalPriceVat.subtract(totalPrice).setScale(2, RoundingMode.HALF_UP);
                salesContractRevision.setTotalPrice(totalPrice);
                salesContractRevision.setTotalPriceVat(totalPriceVat);
                salesContractRevision.setVat(vat);
                salesContractRevision.setModifiedBy(operator);
                salesContractRevision.setModifiedAt(LocalDateTime.now());
                salesContractRevisionRepository.save(salesContractRevision);
            }catch (Exception e){
                return false;
            }
        }
        return  true;
    }

    /**
     * 销售合同预览列表
     *
     * @param id       主键
     * @param revision 版本号
     * @return 采购合同预览列表
     */
    public List<VPContractPreviewResponse.VProduct> modifyContractPreview(String id, Integer revision) {
        return contractRecordPreviewRepository.findSalesContractRecordPreview(id)
            .stream().map(contractRecordMapper::toTContractRecordPreview)
            .map(contractRecordMapper::toVProduct)
            .toList();
    }

    /**
     * 保存收回的临时记录
     *
     * @param list     收回的临时记录列表
     * @param id       合同主键
     * @param revision 版本号
     */
    @Transactional
    public void saveDeliveryTemp(List<VDeliveryTempRequest> list, String id, Integer revision) {
        try {
            //删除上次的数据
            deliveryTempRepository.deleteDeliverTempsByDeliverTempId_ContractId(id);
            List<SalesContractRecordTemp> contractRecordTemps = salesContractRecordTempRepository.findContractRecordTempsBySalesContractRecordTempId_ContractId(id);
            Map<String, SalesContractRecordTemp> map = new HashMap<>();
            contractRecordTemps.forEach(contractRecordTemp -> map.put(contractRecordTemp.getProductId(), contractRecordTemp));
            List<DeliverTemp> deliverTemps = new ArrayList<>();
            AtomicInteger maxCode = new AtomicInteger(1);
            for (VDeliveryTempRequest v : list) {
                SalesContractRecordTemp temp = map.get(v.getProductId());
                if (v.getReturnAmount().floatValue() > 0) {
                    if (temp.getProductId().equals(v.getProductId())) {
                        DeliverTemp deliverTemp = DeliverTemp.builder()
                            .deliverTempId(
                                DeliverTempId.builder()
                                    .contractId(id)
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
                            .sysChargeUnit(temp.getSysChargeUnit())
                            .ratio(temp.getRatio())
                            .amount(v.getRetrieveAmount())
                            .sysAmount(v.getRetrieveAmount().multiply(temp.getRatio()))
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
     * 查询采购合同中已收货物产品列表
     *
     * @param id 合同主键
     * @return 产品列表
     */
    // TODO: 2022/6/2   需要重新完善
    public List<VDeliveredResponse.VProduct> getReceivedList(String id) {
        return contractReceivedRepository.findContractDeliveredList(id)
            .stream().map(contractMapper::toTContractDelivered)
            .map(contractMapper::toVDeliveredProduct)
            .toList();
    }

    /**
     * 生成合同配对码
     *
     * @param fingerprint     指纹
     * @param companyCode     单位编码
     * @param revision        版本号
     * @param salesContractId 销售合同编码
     * @return 配对码
     */
    public String findPairedCode(String fingerprint, String companyCode, int revision, String salesContractId) {
        String pairedCode;
        if (fingerprint == null) {
            pairedCode = purchaseContractBaseRepository.findPairedCode(salesContractId, revision)
                .orElse("");
        } else {
            //查找与之相同的客户的采购合同
            pairedCode = purchaseContractBaseRepository.findPairedCode(fingerprint)
                .orElse("");
        }
        if (pairedCode.equals("")) {
            //生成自己的配对码
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMddhhssmm");
            pairedCode = companyCode + UUID.randomUUID().toString().substring(0, 8) + dtf.format(LocalDateTime.now());
        }
        return pairedCode;
    }
}
