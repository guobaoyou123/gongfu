package com.linzhi.gongfu.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 将原始Http Servlet响应包装成返回Application/Json的响应
 * @author xutao
 * @create_at 2021-12-23
 */
public class HttpServletJsonResponseWrapper {
    private final HttpServletResponse httpServletResponse;
    private final ObjectMapper mapper;

    private HttpServletJsonResponseWrapper(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
        this.mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    }

    /**
     * 对Http Servlet响应进行包装
     * @param httpServletResponse 原始的Http Servlet响应
     * @return 包装后的Http Servlet响应
     */
    public static HttpServletJsonResponseWrapper wrap(HttpServletResponse httpServletResponse) {
        return new HttpServletJsonResponseWrapper(httpServletResponse);
    }

    /**
     * 将Http状态码和JSON对象写入Http Servlet响应中
     * @param statusCode Http状态码
     * @param object 需要写入的JSON对象
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
