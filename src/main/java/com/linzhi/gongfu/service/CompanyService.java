package com.linzhi.gongfu.service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.dto.TCompanyIncludeBrand;
import com.linzhi.gongfu.entity.CompTrad;
import com.linzhi.gongfu.entity.CompTradId;
import com.linzhi.gongfu.mapper.CompTradeMapper;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.repository.CompTradeRepository;
import com.linzhi.gongfu.repository.EnrolledCompanyRepository;

import com.linzhi.gongfu.vo.VSuppliersIncludeBrandsResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

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
    private final CompTradeRepository compTradeRepository;
    private final CompTradeMapper compTradeMapper;
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
    /*@Cacheable(value = "SuppliersPage;1800", unless = "#result == null")*/
    public Page<VSuppliersIncludeBrandsResponse.VSupplier> CompanyIncludeBrandbyId(String id, Optional<Integer> pageNum,Optional<Integer> pageSize) {
        Page<CompTrad> compTradPage =compTradeRepository.findSuppliersByCompTradIdCompBuyer(id, PageRequest.of(pageNum.orElse(1)-1,pageSize.orElse(10)));
        Page<TCompanyIncludeBrand> tCompanyIncludeBrands =compTradPage.map(compTradeMapper::toSuppliersIncludeBrand);
        tCompanyIncludeBrands.forEach(compTrad ->  {
            //将供应商中的经营品牌与授权品牌和自营品牌对比进行去重
            List<TBrand>   selfSupportBrands =  compTrad.getSelfSupportBrands().stream().filter(tBrand -> compTrad.getManageBrands().contains(tBrand)).collect(Collectors.toList());
            List<TBrand>      authBrands     =  compTrad.getAuthBrands().stream().filter(tBrand -> compTrad.getManageBrands().contains(tBrand)).collect(Collectors.toList());
            List<TBrand>    managerBrands    =  compTrad.getManageBrands().stream().filter(tBrand -> !selfSupportBrands.contains(tBrand))
                                                    .collect(Collectors.toList());
                             managerBrands   =  managerBrands.stream().filter(tBrand -> !authBrands.contains(tBrand))
                                                   .collect(Collectors.toList());
            selfSupportBrands.forEach(dcBrand -> dcBrand.setOwned(true));
            authBrands.forEach(dcBrand -> dcBrand.setVending(true));
             //将供应商中的经营品牌、授权品牌、自营品牌合并在一个集合中
            if(selfSupportBrands.isEmpty())
                compTrad.setSelfSupportBrands(new ArrayList<>());
            else
                compTrad.setSelfSupportBrands(selfSupportBrands);
            if(!authBrands.isEmpty())
                compTrad.getSelfSupportBrands().addAll(authBrands);

            if(!managerBrands.isEmpty())
                compTrad.getSelfSupportBrands().addAll(managerBrands);
            if(compTrad.getSelfSupportBrands().size()>5)
                compTrad.setSelfSupportBrands(compTrad.getSelfSupportBrands().subList(0,5));
        });

        return   tCompanyIncludeBrands .map(compTradeMapper::toPreloadSuppliersIncludeBrandDTOs);
    }
}
