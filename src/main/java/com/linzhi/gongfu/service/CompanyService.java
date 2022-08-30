package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.*;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.mapper.CompTradeMapper;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.mapper.OperatorMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 公司信息及处理业务服务
 *
 * @author xutao
 * @create_at 2022-01-19
 */
@RequiredArgsConstructor
@Service
public class CompanyService {
    private final EnrolledCompanyRepository enrolledCompanyRepository;
    private final CompanyMapper companyMapper;
    private final CompanyRepository companyRepository;
    private final CompTradeRepository compTradeRepository;
    private final CompTradDetailRepository compTradDetailRepository;
    private final CompTradeMapper compTradeMapper;
    private final JPAQueryFactory queryFactory;
    private final BrandMapper brandMapper;
    private final AddressService addressService;
    private final CompTradBrandRepository compTradBrandRepository;
    private final CompVisibleRepository compVisibleRepository;
    private final CompInvitationCodeRepository compInvitationCodeRepository;
    private final CompTradeApplyRepository compTradeApplyRepository;
    private final EnrolledSupplierRepository enrolledSupplierRepository;
    private final OperatorRepository operatorRepository;
    private final OperatorMapper operatorMapper;
    private final OperatorDetailRepository operatorDetailRepository;
    /**
     * 根据给定的主机域名名称，获取对应的公司基本信息
     *
     * @param hostname 主机域名名称
     * @return 公司基本信息
     */
    @Cacheable(value = "Company_Host;1800", unless = "#result == null")
    public Optional<TCompanyBaseInformation> findCompanyInformationByHostname(String hostname) {
        return enrolledCompanyRepository.findBySubdomainName(hostname)
            .map(companyMapper::toBaseInformation);
    }

    /**
     * 根据本单位id,页码，页数，获取供应商信息
     *
     * @param id 本单位id，页码 pageNum,页数 pageSize
     * @return 供应商信息列表
     */
    public Page<VSuppliersPageResponse.VSupplier> pageSuppliers(String id,
                                                                Optional<String> pageNum,
                                                                Optional<String> pageSize
    ) {

        List<CompTrad> compTradList = listSuppliersByCompTradIdCompBuyerAndState(id, Availability.ENABLED);
        List<TCompanyIncludeBrand> tCompanyIncludeBrandList = compTradList.stream()
            .map(compTradeMapper::toSuppliersIncludeBrand)
            .filter(t -> t.getState().equals("1"))
            .collect(Collectors.toList());
        Page<TCompanyIncludeBrand> tCompanyIncludeBrands = PageTools.listConvertToPage(
            tCompanyIncludeBrandList,
            PageRequest.of(
                pageNum.map(PageTools::verificationPageNum).orElse(0),
                pageSize.map(PageTools::verificationPageSize).orElse(10)
            )
        );

        tCompanyIncludeBrands.forEach(compTrad -> {
            //将供应商中的经营品牌与授权品牌和自营品牌对比进行去重
            List<TBrand> selfSupportBrands = compTrad.getSelfSupportBrands().stream()
                .filter(tBrand -> compTrad.getManageBrands().contains(tBrand))
                .collect(Collectors.toList());
            List<TBrand> authBrands = compTrad.getAuthBrands().stream()
                .filter(tBrand -> compTrad.getManageBrands().contains(tBrand))
                .toList();
            List<TBrand> managerBrands = compTrad.getManageBrands().stream()
                .filter(tBrand -> !selfSupportBrands.contains(tBrand))
                .collect(Collectors.toList());

            managerBrands = managerBrands.stream().filter(tBrand -> !authBrands.contains(tBrand))
                .collect(Collectors.toList());
            selfSupportBrands.forEach(dcBrand -> dcBrand.setOwned(true));
            authBrands.forEach(dcBrand -> dcBrand.setVending(true));
            //将供应商中的经营品牌、授权品牌、自营品牌合并在一个集合中
            if (selfSupportBrands.isEmpty())
                compTrad.setSelfSupportBrands(new ArrayList<>());
            else
                compTrad.setSelfSupportBrands(selfSupportBrands);
            if (!authBrands.isEmpty())
                compTrad.getSelfSupportBrands().addAll(authBrands);
            if (!managerBrands.isEmpty())
                compTrad.getSelfSupportBrands().addAll(managerBrands);
            if (compTrad.getSelfSupportBrands().size() > 5)
                compTrad.setSelfSupportBrands(compTrad.getSelfSupportBrands().subList(0, 5));
        });
        return tCompanyIncludeBrands.map(compTradeMapper::toPreloadSuppliersIncludeBrandDTOs);
    }

    /**
     * 查询供应的税模式列表
     *
     * @param compBuyer 买方编码
     * @param state     可用状态
     * @return 供应商税模式列表
     */
    @Cacheable(value = "SupplierAndBrand;1800", unless = "#result == null", key = "#compBuyer")
    public List<CompTrad> listSuppliersByCompTradIdCompBuyerAndState(String compBuyer, Availability state) {
        return compTradeRepository.findSuppliersByCompTradId_CompBuyerAndState(compBuyer, state);
    }

    /**
     * 根据品牌查询本单位的供应商
     *
     * @param brands 品牌编码列表
     * @param id     单位id
     * @return 返回供应商列表
     */
    @Cacheable(value = "suppliers_brands;1800", unless = "#result == null")
    public List<TCompanyBaseInformation> listSuppliersByBrands(List<String> brands, String id) {
        QCompany qCompany = QCompany.company;
        QCompTradBrand qCompTradBrand = QCompTradBrand.compTradBrand;

        JPAQuery<Company> query = queryFactory.selectDistinct(qCompany).from(qCompTradBrand).leftJoin(qCompany)
            .on(qCompany.code.eq(qCompTradBrand.compTradBrandId.compSaler));
        if (brands.size() > 0) {
            query.where(qCompTradBrand.compTradBrandId.brandCode.in(brands));
        }
        if (!id.isEmpty()) {
            query.where(qCompTradBrand.compTradBrandId.compBuyer.eq(id));
        }
        query.where(qCompany.state.eq(Availability.ENABLED));
        List<Company> companies = query.orderBy(qCompany.code.desc())
            .fetch();
        return companies.stream()
            .map(companyMapper::toBaseInformation)
            .collect(Collectors.toList());
    }

    /**
     * 查找外供应商列表
     *
     * @param companyCode 单位id
     * @return 返回外供应商列表
     */
    @Cacheable(value = "Foreign_Supplier_List;1800", unless = "#result == null ", key = "#companyCode")
    public List<TCompanyBaseInformation> listForeignSuppliers(String companyCode) {
        QCompany qCompany = QCompany.company;
        QCompTrad qCompTrad = QCompTrad.compTrad;
        QEnrolledCompany qEnrolledCompany = QEnrolledCompany.enrolledCompany;
         JPAQuery<Tuple> query = queryFactory.selectDistinct(qCompany,qEnrolledCompany.USCI).from(qCompany).leftJoin(qCompTrad)
            .on(qCompany.code.eq(qCompTrad.compTradId.compBuyer))
            .leftJoin(qEnrolledCompany).on(qEnrolledCompany.id.eq(qCompany.identityCode));
        query.where
            (qCompTrad.compTradId.compBuyer.eq(companyCode)
                .and(qCompany.role.eq(CompanyRole.EXTERIOR_SUPPLIER.getSign())));
        List<Tuple> companies = query.orderBy(qCompany.code.desc())
            .fetch();
        return companies.stream()
            .map(companyMapper::toForeignCompany)
            .toList();
    }

    /**
     * 查找外供应商详情
     *
     * @param code        供应商编码
     * @param companyCode 单位编码
     * @return 返回详细信息
     */
    @Cacheable(value = "supplierDetail;1800", unless = "#result == null ", key = "#companyCode+'-'+#code")
    public VForeignSupplierResponse.VSupplier getForeignSupplierDetail(String code, String companyCode) throws IOException {

        var trade = compTradeRepository.findById(CompTradId.builder()
            .compSaler(code)
            .compBuyer(companyCode)
            .build()
        ).orElseThrow(() -> new IOException("没用从数据库中查到数据"));
        var brands = trade.getManageBrands().stream()
            .map(brandMapper::toBrand)
            .map(brandMapper::toSupplierBrandPreload)
            .toList();
        VForeignSupplierResponse.VSupplier vSupplier = Optional.of(trade).map(CompTrad::getCompanys)
            .map(companyMapper::toBaseInformation)
            .map(companyMapper::toSupplierDetail).orElseThrow();
        vSupplier.setBrands(brands);
        vSupplier.setTaxMode(String.valueOf(trade.getTaxModel().getTaxMode()));
        return vSupplier;
    }

    /**
     * 保存外供应商
     *
     * @param foreignSupplier 供应商信息
     * @param companyCode     单位id
     * @return 返回成功或者是吧消息
     */
    @Caching(evict = {@CacheEvict(value = "suppliers_brands;1800", key = "'*'+#companyCode"),
        @CacheEvict(value = "Foreign_Supplier_List;1800", key = "#companyCode"),
        @CacheEvict(value = "brands_company;1800", key = "'*'+#companyCode"),
        @CacheEvict(value = "SupplierAndBrand;1800", key = "#companyCode"),
        @CacheEvict(value = "supplierDetail;1800", key = "#companyCode+'-'+#code", condition = "#code != null"),
    })
    @Transactional
    public Map<String, Object> saveForeignSupplier(VForeignSupplierRequest foreignSupplier,
                                                   String companyCode,
                                                   String code) {
        Map<String, Object> map = new HashMap<>();
        String maxCode;
        Company company;
        try {
            if (code == null) {
                //判重
                QCompany qCompany = QCompany.company;
                QCompTrad qCompTrad = QCompTrad.compTrad;
                QEnrolledCompany qEnrolledCompany = QEnrolledCompany.enrolledCompany;
                JPAQuery<Company> query = queryFactory.selectDistinct(qCompany)
                    .from(qCompany, qEnrolledCompany)
                    .leftJoin(qCompTrad)
                    .on(qCompany.code.eq(qCompTrad.compTradId.compSaler)
                        .and(qCompTrad.compTradId.compBuyer.eq(companyCode)));
                query.where(qEnrolledCompany.USCI.eq(foreignSupplier.getUsci()));
                query.where(qEnrolledCompany.id.eq(qCompany.identityCode));
                query.where(qCompany.role.eq(CompanyRole.EXTERIOR_SUPPLIER.getSign()));
                List<Company> list = query.fetch();
                if (list.size() > 0) {
                    map.put("code", 201);
                    map.put("message", "供应商已存在于列表中");
                    return map;
                }
                //查询有没有系统唯一码
                Optional<EnrolledCompany> enrolledCompany = enrolledCompanyRepository
                    .findByUSCI(foreignSupplier.getUsci());
                //判断系统单位表是否为空
                if (enrolledCompany.isEmpty()) {
                    //空的话获取系统单位表的最大编码，生成新的单位
                    String maxId = enrolledCompanyRepository.findMaxCode();
                    if (maxId == null)
                        maxId = "1002";
                    EnrolledCompany enrolledCompany1 = EnrolledCompany.builder()
                        .id(maxId)
                        .nameInCN(foreignSupplier.getCompanyName())
                        .USCI(foreignSupplier.getUsci())
                        .contactName(foreignSupplier.getContactName())
                        .contactPhone(foreignSupplier.getContactPhone())
                        .state(Enrollment.NOT_ENROLLED)
                        .build();
                    enrolledCompanyRepository.save(enrolledCompany1);
                    enrolledCompany = Optional.of(enrolledCompany1);
                }
                maxCode = companyRepository.findMaxCode(CompanyRole.EXTERIOR_SUPPLIER.getSign(), companyCode);
                if (maxCode == null)
                    maxCode = "101";
                code = companyCode + maxCode;
                company = Company.builder()
                    .code(code)
                    .encode(maxCode)
                    // .USCI(foreignSupplier.getUsci())
                    .role(CompanyRole.EXTERIOR_SUPPLIER.getSign())
                    .nameInCN(foreignSupplier.getCompanyName())
                    .identityCode(enrolledCompany.get().getId())
                    .state(Availability.ENABLED)
                    .build();
            } else {
                company = companyRepository.findById(code).orElseThrow(() -> new IOException("从数据库搜索不到该供应商"));
                compTradBrandRepository.deleteCompTradBrand(companyCode, code);
            }
            if (foreignSupplier.getAreaCode() != null && !foreignSupplier.getAreaCode().equals("")) {
                company.setAreaCode(foreignSupplier.getAreaCode());
                company.setAreaName(addressService.findByCode("", foreignSupplier.getAreaCode()));
            } else {
                company.setAreaCode(null);
                company.setAreaName(null);
            }
            company.setAddress(foreignSupplier.getAddress());
            company.setShortNameInCN(foreignSupplier.getCompanyShortName());
            company.setContactName(foreignSupplier.getContactName());
            company.setContactPhone(foreignSupplier.getContactPhone());
            company.setEmail(foreignSupplier.getEmail());
            company.setPhone(foreignSupplier.getPhone());
            companyRepository.save(company);
            //保存价税模式
            compTradeRepository.save(
                CompTrad.builder()
                    .compTradId(
                        CompTradId.builder()
                            .compBuyer(companyCode)
                            .compSaler(code)
                            .build()
                    )
                    .taxModel(foreignSupplier.getTaxMode().equals("0") ? TaxMode.UNTAXED : TaxMode.INCLUDED)
                    .state(Availability.ENABLED)
                    .build()
            );
            List<CompTradBrand> compTradBrands = new ArrayList<>();
            String finalCode = code;
            foreignSupplier.getBrands().forEach(s -> compTradBrands.add(
                    CompTradBrand.builder()
                        .compTradBrandId(
                            CompTradBrandId.builder()
                                .brandCode(s)
                                .compBuyer(companyCode)
                                .compSaler(finalCode)
                                .build()
                        )
                        .sort(0)
                        .build()
                )
            );
            //保存经营品牌
            compTradBrandRepository.saveAll(compTradBrands);
            map.put("code", 200);
            map.put("message", "保存成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 500);
            map.put("message", "保存失败");
            return map;
        }
    }

    /**
     * 修改供应商状态
     *
     * @param code  供应商编码
     * @param state 状态
     * @param type  类型1-外供供应商 2-内供应商
     * @return 返回成功或者失败
     */
    @Caching(evict = {@CacheEvict(value = "suppliers_brands;1800", key = "'*'+#companyCode"),
        @CacheEvict(value = "brands_company;1800", key = "'*'+#companyCode"),
        @CacheEvict(value = "SupplierAndBrand;1800", key = "#companyCode"),
        @CacheEvict(value = "Foreign_Supplier_List;1800", key = "#companyCode", condition = "#type=='1'"),
        @CacheEvict(value = "Enrolled_Supplier_List;1800", key = "#companyCode")
    })
    @Transactional
    public Boolean modifySupplierState(List<String> code, Availability state, String companyCode, String type) {
        try {
            if (type.equals("1")) {
                companyRepository.updateCompanyState(state, code);
            }
            compTradeRepository.updateCompTradeState(state, companyCode, code);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证社会统一信息代码
     *
     * @param usci        社会统一信用代码
     * @param companyCode 公司编码
     * @return 返回公司信息
     */
    public Map<String, Object> supplierVerification(String usci, String companyCode) {
        Map<String, Object> map = new HashMap<>();
        List<Company> list = companyRepository.findCompanyByUSCI(usci);
        if (list.size() > 0) {
            //判断用户是否为外供
            List<String> outSuppliers = list.stream()
                .filter(company -> company.getRole().equals(CompanyRole.EXTERIOR_SUPPLIER.getSign()))
                .map(Company::getCode)
                .toList();
            if (outSuppliers.size() > 0) {
                List<CompTrad> compTradList = compTradeRepository
                    .findCompTradsByCompTradId_CompBuyerAndCompTradId_CompSalerIn(companyCode, outSuppliers);
                if (compTradList.size() > 0) {
                    map.put("code", 201);
                    map.put("companyName", "UNKNOWN");
                    map.put("message", "该供应商已存在于外供应商列表，不可重新添加");
                    return map;
                }
            }
            map.put("code", 200);
            map.put("companyName", list.stream()
                .map(companyMapper::toBaseInformation)
                .toList()
                .get(0).getName()
            );
            map.put("message", "该社会统一信用代码正确");
        } else {
            //调取第三方验证接口
            map.put("code", 404);
            map.put("companyName", "UNKNOWN");
            map.put("message", "该社会统一信用代码不正确");
        }
        return map;
    }

    /**
     * 查找公司详情
     *
     * @param companyCode 单位编码
     * @return 返回详细信息
     */

    public VCompanyResponse.VCompany getCompany(String companyCode) throws Exception {
        return enrolledCompanyRepository.findById(companyCode).map(companyMapper::toCompDetail)
            .map(companyMapper::toCompanyDetail).orElseThrow(() -> new IOException("未找到公司信息"));
    }


    /**
     * 判重
     *
     * @param companyCode 公司编码
     * @param name        公司简称
     * @return 是否重复
     */
    public boolean shortNameRepeat(String companyCode, String name) {
        int count = companyRepository.checkRepeat(name, companyCode);
        return count >= 1;
    }

    /**
     * 保存本公司详情
     *
     * @param companyRequest 供应商信息
     * @param companyCode    单位id
     * @return 返回成功或者失败消息
     */
    @Caching(
        evict = {
            @CacheEvict(value = "Company_Host;1800", key = "#result"),
            @CacheEvict(value = "companyDetail;1800", key = "#companyCode")
        }
    )
    @Transactional
    public String saveCompanyDetail(VCompanyRequest companyRequest, String companyCode) {
        try {
            EnrolledCompany company = enrolledCompanyRepository.findById(companyCode)
                .orElseThrow(() -> new IOException("未找到公司信息"));
            company.getDetails().setShortNameInCN(companyRequest.getCompanyShortName());
            company.getDetails().setContactName(companyRequest.getContactName());
            company.getDetails().setContactPhone(companyRequest.getContactPhone());
            company.getDetails().setAreaCode(companyRequest.getAreaCode());
            if (companyRequest.getAreaCode() != null) {
                company.getDetails().setAreaName(addressService.findByCode("", companyRequest.getAreaCode()));
            } else {
                company.getDetails().setAreaName(null);
            }
            company.getDetails().setAddress(companyRequest.getAddress());
            company.setIntroduction(companyRequest.getIntroduction());
            enrolledCompanyRepository.save(company);
            companyRepository.save(company.getDetails());
            return company.getSubdomainName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 设置可见
     *
     * @param companyCode    公司编码
     * @param visibleContent 可见内容
     * @return 返回操作是否成功信息
     */
    @CacheEvict(value = "companyDetail;1800", key = "#companyCode")
    @Transactional
    public Boolean setVisible(String companyCode, VCompanyVisibleRequest visibleContent) {
        try {
            EnrolledCompany company = enrolledCompanyRepository.findById(companyCode).
                orElseThrow(() -> new IOException("没有从数据库中找到该公司公司信息"));
            company.setVisible(visibleContent.getVisible() ? Whether.YES : Whether.NO);
            Optional<CompVisible> compVisible = compVisibleRepository.findById(companyCode);
            if (visibleContent.getContent() == null && compVisible.isPresent()) {
                compVisibleRepository.deleteById(companyCode);
            } else if (visibleContent.getContent() != null) {
                CompVisible compVisible1 = compVisible.orElse(new CompVisible(companyCode, null));
                compVisible1.setVisibleContent(visibleContent.getContent());
                compVisibleRepository.save(compVisible1);
            }
            enrolledCompanyRepository.save(company);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 查询入格单位列表分页
     *
     * @param name     公司名称
     * @param pageable 分页
     * @return 入格单位信息列表
     */
    public Page<VEnrolledCompanyPageResponse.VCompany> pageEnrolledCompanies(String name, Pageable pageable,
                                                                             String companyCode
    ) {
        var list = enrolledCompanyRepository.
            findAllByVisibleAndState(Whether.YES, Enrollment.ENROLLED)
            .stream().filter(enrolledCompany -> {
                if (enrolledCompany.getDetails() != null && enrolledCompany.getDetails().getShortNameInCN() != null) {
                    return enrolledCompany.getDetails().getNameInCN().contains(name);
                } else {
                    return false;
                }
            })
            .filter(enrolledCompany -> !enrolledCompany.getId().equals(companyCode))
            .map(companyMapper::toCompDetail)
            .map(companyMapper::toEnrolledCompany)
            .toList();
        return PageTools.listConvertToPage(list, pageable);
    }

    /**
     * 根据邀请码查找入格单位编码
     *
     * @param invitationCode 邀请码
     * @return 入格单位编码
     */
    public Optional<String> getECompanyCode(String invitationCode) {
        Optional<CompInvitationCode> compInvitationCode = compInvitationCodeRepository.
            findCompInvitationCodeByCompInvitationCodeId_InvitationCode(invitationCode);
        if (compInvitationCode.isPresent()) {
            //判断是否超时
            long m = Duration.between(LocalDateTime.now(), compInvitationCode.get().getCreatedAt()).toMinutes();
            if (m > 30)
                return Optional.empty();
        } else {
            return Optional.empty();
        }
        return Optional.of(compInvitationCode.get().getCompInvitationCodeId().getDcCompId());
    }

    /**
     * 格友公司可见详情
     *
     * @param enrolledCode 格友编码
     * @param companyCode  公司编码
     * @return 格友公司可见详情
     */
    public Optional<VEnrolledCompanyResponse.VCompany> getEnrolledCompany(String enrolledCode,
                                                                          String companyCode
    )
        throws IOException {
        var company = getEnrolledCompanyDetail(enrolledCode)
            .orElseThrow(() -> new IOException("未从数据库找到"));
        //是否为供应商
        var compTrad1 = compTradeRepository.findById(CompTradId.builder()
            .compSaler(enrolledCode)
            .compBuyer(companyCode)
            .build()
        );
        if (compTrad1.isPresent())
            company.setIsSupplier(true);
        //是否为客户
        var compTrad2 = compTradeRepository.findById(CompTradId.builder()
            .compSaler(companyCode)
            .compBuyer(enrolledCode)
            .build()
        );
        if (compTrad2.isPresent())
            company.setIsCustomer(true);
        //是否有申请记录
        var compTradeApply = compTradeApplyRepository.
            findByCreatedCompByAndHandledCompByAndStateAndType(
                companyCode,
                enrolledCode,
                TradeApply.APPLYING, "1"
            );
        if (compTradeApply.isPresent())
            company.setState("1");
        return Optional.of(company);

    }

    /**
     * 查找入格单位可见公司详情信息
     *
     * @param enrolledCode 公司编码
     * @return 公司可见详情
     */
    public Optional<VEnrolledCompanyResponse.VCompany> getEnrolledCompanyDetail(String enrolledCode) {
        return enrolledCompanyRepository.findById(enrolledCode).map(companyMapper::toEnrolledCompanyDetail)
            .map(companyMapper::toEnrolledCompanyDetail);
    }


    /**
     * 生成邀请码
     *
     * @param companyCode 公司编码
     * @return 返回邀请码
     */
    @Transactional
    public String getInvitationCode(String companyCode) {
        try {
            //查询是否有申请码
            Optional<CompInvitationCode> compInvitationCode = compInvitationCodeRepository.
                findCompInvitationCodeByCompInvitationCodeId_DcCompId(companyCode);
            //如何不为空，将原来的邀请码删除
            compInvitationCode.ifPresent(invitationCode -> compInvitationCodeRepository.deleteById(invitationCode.getCompInvitationCodeId()));
            //邀请码的格式（单位编码——uuid随机编码）
            String InvitationCode = companyCode + "-" + UUID.randomUUID().toString().substring(0, 8);
            //保存数据
            compInvitationCodeRepository.save(
                CompInvitationCode.builder()
                    .compInvitationCodeId(
                        CompInvitationCodeId.builder()
                            .invitationCode(InvitationCode)
                            .dcCompId(companyCode)
                            .build())
                    .createdAt(LocalDateTime.now())
                    .build()
            );
            return InvitationCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查找本单位内供应商或者内客户列表
     *
     * @param name        供应商公司名称
     * @param pageNum     页数
     * @param pageSize    每页展示几条
     * @param companyCode 本单位编码
     * @param type        类型 1-内供应商 2-内客户
     * @return 内供应商列表
     */
    public Page<TEnrolledTradeCompanies> pageEnrolledTradeCompanies(String name, int pageNum, int pageSize, String companyCode, String type) {
        List<TEnrolledTradeCompanies> list;
        if (type.equals("1")) {
            list = enrolledSupplierRepository.findEnrolledSupplierList(companyCode);
        } else {
            list = enrolledSupplierRepository.findEnrolledCustomerList(companyCode);
        }
        return PageTools.listConvertToPage(
            list.stream().filter(tEnrolledSuppliers -> tEnrolledSuppliers.getNameInCN().contains(name))
                .toList(),
            PageRequest.of(pageNum, pageSize)
        );
    }

    /**
     * 入格供应商查询
     *
     * @param code        入格供应商编码
     * @param companyCode 本单位编码
     * @return 入格供应商查询
     */
    @Cacheable(value = "Enrolled_Supplier_detail;1800", key = "#companyCode+'-'+#code", unless = "#result == null ")
    public Optional<TEnrolledTradeCompany> enrolledSupplier(String code, String companyCode) throws IOException {
        EnrolledTrade enrolledSupplier = enrolledSupplierRepository.findById(CompTradId.builder()
            .compSaler(code)
            .compBuyer(companyCode)
            .build()).orElseThrow(() -> new IOException("未从数据库找到"));
        Optional<TEnrolledTradeCompany> tEnrolledSupplier = Optional.of(enrolledSupplier).map(companyMapper::toTEnrolledSupplierDetail);
        if (enrolledSupplier.getBuyerBelongTo() != null) {
            //查询本单位负责人
            List<TOperatorInfo> operatorList = operatorRepository.findOperatorByIdentity_CompanyCodeAndIdentity_OperatorCodeIn(
                    companyCode,
                    Arrays.asList(enrolledSupplier.getBuyerBelongTo().split(","))
                ).stream().map(operatorMapper::toDTO)
                .toList();
            tEnrolledSupplier.get().setOperators(operatorList);
        }
        return tEnrolledSupplier;
    }

    /**
     * 授权操作员
     *
     * @param compSaler 卖方编码
     * @param compBuyer 买方编码
     * @param operators 操作员编码（以逗号隔开）
     */
    @Transactional
    public void authorizedOperator(String compSaler, String compBuyer, String operators, String type) {
        try {
            if (type.equals("1")) {
                compTradeRepository.updateCompTradeBuyer(
                    operators.equals("") ? null : operators,
                    CompTradId.builder()
                        .compBuyer(compBuyer)
                        .compSaler(compSaler)
                        .build()
                );
            } else {
                compTradeRepository.updateCompTradeSaler(
                    operators.equals("") ? null : operators,
                    CompTradId.builder()
                        .compBuyer(compBuyer)
                        .compSaler(compSaler)
                        .build()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 入格客户查询
     *
     * @param code        入格客户编码
     * @param companyCode 本单位编码
     * @return 入格客户查询
     */
    @Cacheable(value = "Enrolled_Customer_detail;1800", key = "#companyCode+'-'+#code", unless = "#result == null ")
    public Optional<TEnrolledTradeCompany> enrolledCustomer(String code, String companyCode) throws IOException {
        EnrolledTrade enrolledSupplier = enrolledSupplierRepository.findById(CompTradId.builder()
            .compSaler(companyCode)
            .compBuyer(code)
            .build()).orElseThrow(() -> new IOException("未从数据库找到"));
        Optional<TEnrolledTradeCompany> tEnrolledCustomer = Optional.of(enrolledSupplier).map(companyMapper::toTEnrolledCustomerDetail);
        if (enrolledSupplier.getSalerBelongTo() != null) {
            //查询本单位负责人
            List<TOperatorInfo> operatorList = operatorRepository.findOperatorByIdentity_CompanyCodeAndIdentity_OperatorCodeIn(
                    companyCode,
                    Arrays.asList(enrolledSupplier.getSalerBelongTo().split(","))
                ).stream().map(operatorMapper::toDTO)
                .toList();
            tEnrolledCustomer.get().setOperators(operatorList);
        }
        return tEnrolledCustomer;
    }

    /**
     * 修改交易品牌
     *
     * @param compSaler 卖方编码
     * @param compBuyer 买方编码
     * @param brands    品牌列表
     */
    @Caching(evict = {
        @CacheEvict(value = "Enrolled_Customer_detail;1800", key = "#compSaler+'-'+#compBuyer"),
        @CacheEvict(value = "Enrolled_Supplier_detail;1800", key = "#compBuyer+'-'+#compSaler"),
        @CacheEvict(value = "suppliers_brands;1800", key = "'*'+#compBuyer"),
        @CacheEvict(value = "brands_company;1800", key = "'*'+#compBuyer"),
        @CacheEvict(value = "SupplierAndBrand;1800", key = "#compBuyer"),
    })
    @Transactional
    public void modifyTradeBrands(String compSaler, String compBuyer, List<String> brands) {
        try {
            compTradBrandRepository.deleteCompTradBrand(compBuyer, compSaler);
            List<CompTradBrand> compTradBrands = new ArrayList<>();
            brands.forEach(s -> compTradBrands.add(CompTradBrand.builder()
                .compTradBrandId(CompTradBrandId.builder()
                    .brandCode(s)
                    .compSaler(compSaler)
                    .compBuyer(compBuyer)
                    .build())
                .build()));
            if (compTradBrands.size() > 0)
                compTradBrandRepository.saveAll(compTradBrands);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改交易报价模式
     *
     * @param compSaler 卖方编码
     * @param compBuyer 买方编码
     * @param taxModel  报价模式
     */
    @Caching(evict = {
        @CacheEvict(value = "Enrolled_Customer_detail;1800", key = "#compSaler+'-'+#compBuyer"),
        @CacheEvict(value = "Enrolled_Supplier_detail;1800", key = "#compBuyer+'-'+#compSaler")
    })
    @Transactional
    public String modifyTradeTaxModel(String compSaler, String compBuyer, String taxModel) {
        try {
            CompTradDetail compTradDetail = compTradDetailRepository.findById(CompTradId
                .builder()
                .compSaler(compSaler)
                .compBuyer(compBuyer)
                .build()).orElseThrow(() -> new IOException("没有从数据库中找到该数据"));
            compTradeRepository.updateTaxModel(
                taxModel.equals("0") ? TaxMode.UNTAXED : TaxMode.INCLUDED,
                CompTradId.builder()
                    .compBuyer(compBuyer)
                    .compSaler(compSaler)
                    .build());
            return compTradDetail.getBuyerBelongTo() == null ? "000" : compTradDetail.getBuyerBelongTo();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询外客户列表 分页
     * @param companyCode 本单位编码
     * @param operator 操作员编码
     * @param name 公司名称
     * @param pageSize 每页展示几条
     * @param pageNum 页码
     * @return 外客户列表
     * @throws IOException 异常
     */
    public Page<TCompanyBaseInformation> pageForeignCustomers(String companyCode, String operator, String name, Integer pageNum, Integer pageSize,String state) throws IOException {
        OperatorDetail operatorDetail = operatorDetailRepository.findById(OperatorId.builder()
                .operatorCode(operator)
                .companyCode(companyCode)
            .build()).orElseThrow(()->new IOException("没有从数据库中找打该数据"));
        return PageTools.listConvertToPage(listForeignCustomers(companyCode,operator,operatorDetail.getAdmin(),name,state),PageRequest.of(pageNum,pageSize));
    }
    /**
     * 查找外供应商列表
     *
     * @param companyCode 单位id
     * @param operator 操作员编码
     * @param name 公司名称
     * @return 返回外客户列表
     */
    @Cacheable(value = "Foreign_Customer_List;1800", unless = "#result == null ", key = "#companyCode+'-'+#operator+'-'+#state+'-'+#name")
    public List<TCompanyBaseInformation> listForeignCustomers(String companyCode,String operator,Whether isAdmin,String name,String state) {
        QCompany qCompany = QCompany.company;
        QCompTrad qCompTrad = QCompTrad.compTrad;
        QEnrolledCompany qEnrolledCompany = QEnrolledCompany.enrolledCompany;
        JPAQuery<Tuple> query = queryFactory.selectDistinct(qCompany,qEnrolledCompany.USCI).from(qCompany).leftJoin(qCompTrad)
            .on(qCompany.code.eq(qCompTrad.compTradId.compBuyer))
            .leftJoin(qEnrolledCompany).on(qEnrolledCompany.id.eq(qCompany.identityCode));
        query.where
            (qCompTrad.compTradId.compSaler.eq(companyCode)
                .and(qCompany.role.eq(CompanyRole.EXTERIOR_CUSTOMER.getSign())));
        if(isAdmin.equals(Whether.NO))
            query.where(qCompTrad.salerBelongTo.contains(operator));
        if(!name.equals(""))
            query.where(qCompany.nameInCN.like("%"+name+"%"));
        query.where(qCompany.state.eq(state.equals("1")?Availability.ENABLED:Availability.DISABLED));
        List<Tuple> companies = query.orderBy(qCompany.code.desc())
            .fetch();
        return companies.stream()
            .map(companyMapper::toForeignCompany)
            .toList();
    }
}
