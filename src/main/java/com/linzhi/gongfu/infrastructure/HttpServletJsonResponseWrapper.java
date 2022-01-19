package com.linzhi.gongfu.infrastructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import org.springframework.http.MediaType;

/**
 * 将原始Http Servlet响应包装成返回Application/Json的响应
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public class HttpServletJsonResponseWrapper {
    private final HttpServletResponse httpServletResponse;
    private final ObjectMapper mapper;

    private HttpServletJsonResponseWrapper(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
        var timeModule = new JavaTimeModule();

        // 日期序列化
        timeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // 日期反序列化
        timeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        timeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        timeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        this.mapper = JsonMapper.builder()
                .addModule(timeModule)
                .build();
    }

    /**
     * 对Http Servlet响应进行包装
     *
     * @param httpServletResponse 原始的Http Servlet响应
     * @return 包装后的Http Servlet响应
     */
    public static HttpServletJsonResponseWrapper wrap(HttpServletResponse httpServletResponse) {
        return new HttpServletJsonResponseWrapper(httpServletResponse);
    }

    /**
     * 将Http状态码和JSON对象写入Http Servlet响应中
     *
     * @param statusCode Http状态码
     * @param object     需要写入的JSON对象
     * @throws IOException 当JSON写入发生错误时抛出
     */
    public void write(int statusCode, Object object) throws IOException {
        httpServletResponse.setStatus(statusCode);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        var writer = httpServletResponse.getWriter();
        writer.write(mapper.writeValueAsString(object));
        writer.flush();
        writer.close();
    }
}
