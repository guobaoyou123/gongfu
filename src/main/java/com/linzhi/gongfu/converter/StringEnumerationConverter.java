package com.linzhi.gongfu.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import javax.persistence.AttributeConverter;

/**
 * 抽象对于转换数据库中使用字符串记录枚举量的通用实现
 *
 * @author xutao
 * @create_at 2022-01-14
 */
public abstract class StringEnumerationConverter<T extends Enum<T>> implements AttributeConverter<T, String> {
    private final Class<T> enumerationClazz;
    private final Method stringValueGetter;

    protected StringEnumerationConverter(Class<T> clazz, String methodName) throws NoSuchMethodException {
        this.enumerationClazz = clazz;
        this.stringValueGetter = clazz.getDeclaredMethod(methodName);
    }

    @Override
    public String convertToDatabaseColumn(T t) {
        return Optional.ofNullable(t)
                .map(value -> {
                    try {
                        return this.stringValueGetter.invoke(value);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return null;
                    }
                })
                .map(v -> v.toString())
                .orElse(null);
    }

    @Override
    public T convertToEntityAttribute(String string) {
        return Arrays.stream(this.enumerationClazz.getEnumConstants())
                .filter(cons -> {
                    try {
                        var value = (String) this.stringValueGetter.invoke(cons);
                        return value.equals(string);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }
}
