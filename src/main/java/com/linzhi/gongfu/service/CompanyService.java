package com.linzhi.gongfu.service;

import java.util.Optional;

import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.repository.EnrolledCompanyRepository;
import com.linzhi.gongfu.util.URLTools;

import org.springframework.cache.annotation.Cacheable;
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

    /**
     * 根据给定的主机域名名称，获取对应的公司基本信息
     *
     * @param hostname 主机域名名称
     * @return 公司基本信息
     */
    @Cacheable("Company_Host")
    public Optional<TCompanyBaseInformation> findCompanyInformationByHostname(String hostname) {
        var subdomainName = URLTools.extractSubdomainName(hostname);
        return enrolledCompanyRepository.findBySubdomainName(subdomainName)
                .map(companyMapper::toBaseInformation);
    }
}
