package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.VPreferenceSupplierRequest;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 品牌信息及处理业务服务
 *
 * @author zgh
 * @create_at 2022-02-07
 */
@RequiredArgsConstructor
@Service
public class BrandService {
    private final CompAllowedBrandRepository compAllowedBrandRepository;
    private final BrandRepository brandRepository;
    private final CompBrandAuthRepository compBrandAuthRepository;
    private final BrandMapper brandMapper;
    private final DcBrandRepository dcBrandRepository;
    private final ViewBrandRepository viewBrandRepository;
    private final JPAQueryFactory queryFactory;
    private final PreferenceSupplierRepository preferenceSupplierRepository;
    /**
     * 根据本单位id,页码，页数，获取品牌信息
     *
     * @param id 本单位id，页码 pageNum,页数 pageSize
     * @return 品牌信息列表
     */
    public Page<TBrand> pageBrands(String id, Optional<String> pageNum, Optional<String> pageSize) {
        //根据单位id查询全部品牌信息（包括自营、经营、授权等信息）
        List<TBrand> list = listBrandsAll(id);
        //分页
        Pageable pageable = PageRequest.of(
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10)
        );
        return PageTools.listConvertToPage(list, pageable);
    }

    /**
     * 根据单位id查找品牌列表（是否自营，是否授权，是否经营）
     *
     * @param id 单位id
     * @return 品牌列表
     */
    @Cacheable(value = "brands_ID;1800", key = "#id", unless = "#result == null")
    public List<TBrand> listBrandsAll(String id) {
        //查询系统所有品牌
        Iterable<Brand> brandsIterable = brandRepository.findAll();
        //查询经营品牌
        List<CompAllowedBrand> compAllowedBrandSet = compAllowedBrandRepository.findBrandsByCompAllowedBrandIdCompCode(id);
        //查询授权品牌
        List<CompBrandAuth> compBrandAuthSet = compBrandAuthRepository.findCompBrandAuthByCompBrandAuthId_BeAuthComp(id);
        List<TBrand> tBrandSet = StreamSupport.stream(brandsIterable.spliterator(), false)
            .map(brandMapper::toBrand).toList();
        tBrandSet.forEach(brands -> brands.getCompBrandOwner().forEach(compBrandOwner -> {
            brands.setHaveOwned(true);
            if (compBrandOwner.getCompBrandOwnerId().getOwnerCode().equals(id)) {
                brands.setOwned(true);
            } else {
                compBrandAuthSet.forEach(compBrandAuth -> {
                    if (compBrandAuth.getCompBrandAuthId().getBrandCode().equals(compBrandOwner.getCompBrandOwnerId().getBrandCode())) {
                        brands.setVending(true);
                    }
                });
            }
        }));
        //筛选经营品牌
        List<TBrand> allowedBrand = tBrandSet.stream()
            .filter(brands -> {
                AtomicReference<Boolean> flag = new AtomicReference<>(false);
                compAllowedBrandSet.forEach(compAllowedBrand -> {
                    if (compAllowedBrand.getCompAllowedBrandId().getBrandCode().equals(brands.getCode()))
                        flag.set(true);
                });
                return flag.get();
            })
            .collect(Collectors.toList());
        //过滤除经营品牌以外的其他品牌
        List<TBrand> otherBrand = tBrandSet.stream()
            .filter(brands -> {
                AtomicReference<Boolean> flag = new AtomicReference<>(true);
                compAllowedBrandSet.forEach(compAllowedBrand -> {
                    if (compAllowedBrand.getCompAllowedBrandId().getBrandCode().equals(brands.getCode()))
                        flag.set(false);
                });
                return flag.get();
            }).toList();
        allowedBrand.addAll(otherBrand);
        return allowedBrand;
    }

    /**
     * 获取品牌信息
     *
     * @return 供应商信息列表
     */
    @Cacheable(value = "brands;1800", unless = "#result == null")
    public Set<TBrand> listBrands() {
        return StreamSupport.stream(dcBrandRepository.findAll().spliterator(), false)
            .sorted(Comparator.comparingInt(DcBrand::getSort))
            .map(brandMapper::toBrand)
            .collect(Collectors.toSet());
    }

    /**
     * 根据产品二级分类获取品牌列表
     *
     * @param classes 二级分类编码
     * @return 品牌列表
     */
    @Cacheable(value = "brands_class;1800", key = "#classes", unless = "#result == null")
    public Set<TBrand> listBrandsByClass(Optional<String> classes) {
        return viewBrandRepository.findViewBrandByClass2AndStateOrderBySortDescCodeAsc(classes.orElse(null), Availability.ENABLED).stream()
            .map(brandMapper::toViewBrand)
            .collect(Collectors.toSet());

    }

    /**
     * 根据供应商获取品牌列表
     *
     * @param company 供应商编码列表， id 单位id
     * @return 品牌列表
     */
    @Cacheable(value = "brands_company;1800", unless = "#result == null")
    public List<TBrand> listBrandsBySuppliers(List<String> company, String id) {
        QDcBrand qDcBrand = QDcBrand.dcBrand;
        QCompTradeBrand compTradeBrand = QCompTradeBrand.compTradeBrand;
        JPAQuery<DcBrand> query = queryFactory.selectDistinct(qDcBrand).from(compTradeBrand)
            .leftJoin(qDcBrand).on(qDcBrand.code.eq(compTradeBrand.compTradeBrandId.brandCode));
        query.where(compTradeBrand.compTradeBrandId.compBuyer.eq(id));
        if (company.size() > 0)
            query.where(compTradeBrand.compTradeBrandId.compSaler.in(company));
        query.orderBy(qDcBrand.sort.desc());
        List<DcBrand> dcBrands = query.fetch();

        return dcBrands.stream()
            .map(brandMapper::toBrand)
            .toList();

    }

    /**
     * 根据单位编码查找本单位经营品牌列表
     *
     * @param companyCode 本单位编码
     * @return 返回经营品牌列表
     */
    @Cacheable(value = "company_brand_List;1800",key = "#companyCode")
    public List<TBrand> listBrandsByCompanyCode(String companyCode) {
        return compAllowedBrandRepository.findBrandsByCompAllowedBrandIdCompCode(companyCode)
            .stream().map(brandMapper::toCompAllowedBrandDTO)
            .toList();
    }

    /**
     * 设置经营品牌
     * @param brands 品牌编码列表
     * @param companyCode 公司编码
     */
    @Caching(evict = {
        @CacheEvict(value = "company_brand_List;1800",key = "#companyCode"),
        @CacheEvict(value = "brands_ID;1800", key = "#companyCode")
    })
    @Transactional
    public void saveManagementBrands(List<String> brands,String companyCode) throws Exception {
        try{
            //删除原有数据
            compAllowedBrandRepository.deleteAllByCompAllowedBrandId_CompCode(companyCode);
            List<CompAllowedBrand> compAllowedBrands =new ArrayList<>();
            brands.forEach(b-> compAllowedBrands.add(CompAllowedBrand.builder()
                    .compAllowedBrandId(
                        CompAllowedBrandId.builder()
                            .brandCode(b)
                            .compCode(companyCode)
                            .build()
                    )
                .build()));
            compAllowedBrandRepository.saveAll(compAllowedBrands);
        }catch (Exception e){
            throw new  Exception();
        }

    }

    /**
     * 设置优选供应商
     * @param brandCode 品牌编码
     * @param suppliers 优选供应商编码
     * @param companyCode 本单位编码
     */
    @Transactional
    public void savePreferenceSupplier(String brandCode, List<String> suppliers, String companyCode) throws Exception {
        try {
            preferenceSupplierRepository.deleteByPreferenceSupplierId_CompCodeAndPreferenceSupplierId_BrandCode(companyCode,brandCode);
            List<PreferenceSupplier> preferenceSuppliers = new ArrayList<>();
            final int[] i = {1};
            suppliers.forEach(s -> {
                preferenceSuppliers.add(PreferenceSupplier.builder()
                    .preferenceSupplierId(PreferenceSupplierId.builder()
                        .brandCode(brandCode)
                        .compCode(companyCode)
                        .supplierCode(s)
                        .build())
                    .sort(i[0])
                .build());
                i[0]++;
            });
            preferenceSupplierRepository.saveAll(preferenceSuppliers);
        }catch (Exception e){
            throw new  Exception();
        }
    }

    /**
     * 设置优选供应商排序
     * @param brandCode 品牌编码
     * @param suppliers 优选供应商编码
     * @param companyCode 本单位编码
     */
    @CacheEvict(value = "company_brand_List;1800",key = "#companyCode")
    @Transactional
    public void savePreferenceSupplierSort(String brandCode, List<VPreferenceSupplierRequest.VSupplier> suppliers, String companyCode) throws Exception{
        try {
           var supplierCodes =  suppliers.stream().map(VPreferenceSupplierRequest.VSupplier::getCode).toList();
            //移除排序已经变动的供应商
            var preferences = preferenceSupplierRepository.findByPreferenceSupplierId_CompCodeAndPreferenceSupplierId_BrandCodeOrderBySortAsc(companyCode,brandCode)
                .stream()
                .filter(s-> !supplierCodes.contains(s.getPreferenceSupplierId().getSupplierCode())).toList();
            List<PreferenceSupplier> preferenceSuppliers = new ArrayList<>();
            //重新排序
            suppliers=suppliers.stream().sorted(Comparator.comparing(VPreferenceSupplierRequest.VSupplier::getSort)).toList();
            for(int i = 0;i<suppliers.size();i++){
                int sort,preSort=0;
                sort = suppliers.get(i).getSort();
                if(i>0){
                    preSort= suppliers.get(i-1).getSort();
                }

                for (int j=0;j<sort-(preSort+1);j++){
                    preferenceSuppliers.add(preferences.get(j));
                }
                preferences=preferences.stream().filter(s-> !preferenceSuppliers.contains(s)).toList();
                preferenceSuppliers.add(PreferenceSupplier.builder()
                    .preferenceSupplierId(PreferenceSupplierId.builder()
                        .brandCode(brandCode)
                        .compCode(companyCode)
                        .supplierCode(suppliers.get(i).getCode())
                        .build())
                    .sort(suppliers.get(i).getSort())
                    .build());
            }
            final int[] i = {1};
            preferenceSuppliers.forEach(p->{
                p.setSort(i[0]);
                i[0]++;
            });
            //更新排序
            preferenceSupplierRepository.saveAll(preferenceSuppliers);
        }catch (Exception e){
            throw new  Exception();
        }
    }

}
