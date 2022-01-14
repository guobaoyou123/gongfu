package com.linzhi.gongfu.enumeration;

import java.util.Arrays;
import java.util.Optional;

/**
 * 平台所记录的公司的角色
 *
 * @author xutao
 * @created_at 2022-01-14
 */
public enum CompanyRole {
    PLATFORM("0"),
    SUPPLIER("1"),
    BRAND_GOVERNOR("2"),
    CLOUD_WAREHOUSE("3"),
    PREFABRICATION_CENTER("4"),
    FIELD_SERVICE("5"),
    EXTERIOR_SUPPLIER("6"),
    EXTERIOR_CUSTOMER("7");

    private final String sign;

    private CompanyRole(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    public static Optional<CompanyRole> valueBySign(String sign) {
        return Arrays.stream(CompanyRole.values())
                .filter(role -> role.getSign().equals(sign))
                .findFirst();
    }
}
