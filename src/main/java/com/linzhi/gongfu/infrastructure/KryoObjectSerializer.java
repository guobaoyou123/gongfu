package com.linzhi.gongfu.infrastructure;

import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.linzhi.gongfu.util.SerializeTools;

import org.springframework.data.redis.serializer.RedisElementReader;
import org.springframework.data.redis.serializer.RedisElementWriter;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.stereotype.Component;

/**
 * 定义使用Kryo进行序列化的Redis Cache序列化器
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Component
public class KryoObjectSerializer implements SerializationPair<Object> {

    @Override
    public RedisElementReader<Object> getReader() {
        return new RedisElementReader<Object>() {
            @Override
            public Object read(ByteBuffer buffer) {
                Kryo kryo = SerializeTools.KryoLocal.get();
                return kryo.readClassAndObject(new Input(new ByteBufferInputStream(buffer)));
            }
        };
    }

    @Override
    public RedisElementWriter<Object> getWriter() {
        return new RedisElementWriter<Object>() {
            @Override
            public ByteBuffer write(Object element) {
                Kryo kryo = SerializeTools.KryoLocal.get();
                Output output = new Output(1024, 1024 * 100);
                kryo.writeClassAndObject(output, element);
                return ByteBuffer.wrap(output.getBuffer());
            }
        };
    }

}
