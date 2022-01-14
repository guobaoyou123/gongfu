package com.linzhi.gongfu.converter;

import javax.persistence.Converter;

import com.linzhi.gongfu.enumeration.Enrollment;

/**
 * 转换是否入格状态枚举类型到数据库的char型字段值
 *
 * @author xutao
 * @create_at 2021-12-23
 */
@Converter(autoApply = true)
public class EnrollmentConverter extends CharacterEnumerationConverter<Enrollment> {
    public EnrollmentConverter() throws NoSuchMethodException {
        super(Enrollment.class, "getFlag");
    }
}
