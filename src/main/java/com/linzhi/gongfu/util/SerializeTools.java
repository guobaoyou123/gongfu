package com.linzhi.gongfu.util;

import com.esotericsoftware.kryo.Kryo;

/**
 * 主要用于提供额外使用的序列化器
 *
 * @author xutao
 * @create_at 2022-01-20
 */
public abstract class SerializeTools {
    /**
     * 提供一个线程安全的Kryo序列化器实例
     */
    public static final ThreadLocal<Kryo> KryoLocal = ThreadLocal.withInitial(() -> {
        var kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
        return kryo;
    });
}
