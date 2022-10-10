package com.linzhi.gongfu.converter;

import javax.persistence.AttributeConverter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * 抽象对于转换数据库中使用字符记录枚举量的通用实现
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public abstract class CharacterEnumerationConverter<T extends Enum<T>> implements AttributeConverter<T, Character> {
    private final Class<T> enumerationClazz;
    private final Method charValueGetter;

    protected CharacterEnumerationConverter(Class<T> clazz, String methodName) throws NoSuchMethodException {
        this.enumerationClazz = clazz;
        this.charValueGetter = clazz.getDeclaredMethod(methodName);
    }

    @Override
    public Character convertToDatabaseColumn(T t) {
        return Optional.ofNullable(t)
            .map(value -> {
                try {
                    return this.charValueGetter.invoke(value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    return null;
                }
            })
            .map(Character.class::cast)
            .orElse(null);
    }

    @Override
    public T convertToEntityAttribute(Character character) {
        return Arrays.stream(this.enumerationClazz.getEnumConstants())
            .filter(cons -> {
                try {
                    var value = (Character) this.charValueGetter.invoke(cons);
                    return value.equals(character);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    return false;
                }
            })
            .findFirst()
            .orElse(null);
    }
}
