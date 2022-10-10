package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.CompanyRole;

import javax.persistence.Converter;

/**
 * 转换公司基本信息中的公司角色枚举类型到数据库VARCHAR类型字段的转换器
 *
 * @author xutao
 * @create_at 2022-01-14
 */
@Converter(autoApply = true)
public class CompanyRoleConverter extends StringEnumerationConverter<CompanyRole> {

    protected CompanyRoleConverter() throws NoSuchMethodException {
        super(CompanyRole.class, "getSign");
    }

}
