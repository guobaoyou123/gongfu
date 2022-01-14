package com.linzhi.gongfu.proxy;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.linzhi.gongfu.entity.Company;
import com.linzhi.gongfu.enumeration.CompanyRole;

import lombok.Getter;

/**
 * 公司信息的代理对象，用于对公司信息DO进行扩展
 *
 * @author xutao
 * @create_at 2022-01-14
 */
public class CompanyProxy {
    @Getter
    private Company company;

    private CompanyProxy(Company company) {
        this.company = company;
    }

    /**
     * 包装公司信息DO实例，建立功能扩展代理对象
     *
     * @param company 要被扩展的公司信息DO实例
     * @return 公司信息的扩展代理对象
     */
    public static CompanyProxy proxy(Company company) {
        return new CompanyProxy(company);
    }

    /**
     * 将公司信息中的公司角色拆分成公司信息枚举列表
     *
     * @return 公司拥有的角色列表
     */
    public List<CompanyRole> roles() {
        var splitedRoles = company.getRole().split(",");
        return Arrays.stream(splitedRoles)
                .map(CompanyRole::valueBySign)
                .map(Optional::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
