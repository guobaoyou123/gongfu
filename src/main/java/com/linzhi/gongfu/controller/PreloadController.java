package com.linzhi.gongfu.controller;

import java.util.Optional;

import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.util.URLTools;
import com.linzhi.gongfu.vo.VPreloadCompanyInfoResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * 用于处理前端应用需要预加载的内容。
 *
 * @author xutao
 * @create_at 2022-01-19
 */
@RequiredArgsConstructor
@RestController
public class PreloadController {
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    /**
     * 通过给定的主机域名名称获取对应的公司基本信息接口
     *
     * @param domain   在请求头信息中包含的主机域名名称
     * @param hostname 在查询串中包含的主机域名名称
     * @return 对应的公司基本信息或者无法找到的提示
     */
    @GetMapping("/host")
    public VPreloadCompanyInfoResponse fetchCompanyInfoByHost(
            @RequestHeader("CompanyDomain") Optional<String> domain, @RequestParam("host") Optional<String> hostname) {
        return hostname.or(() -> domain)
                .map(URLTools::extractSubdomainName)
                .flatMap(companyService::findCompanyInformationByHostname)
                .map(companyMapper::toPreload)
                .orElse(VPreloadCompanyInfoResponse.builder()
                        .code(404)
                        .message("请求的公司信息没有找到。")
                        .companyName("UNKNOWN")
                        .companyShortName("UNKNOWN").build());
    }
}
