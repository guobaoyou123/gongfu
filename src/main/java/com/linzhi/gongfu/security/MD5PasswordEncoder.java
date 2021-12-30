package com.linzhi.gongfu.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 采用MD5对密码进行散列的编码器
 *
 * @author xutao
 * @create_at 2021-12-30
 */
@Component
public class MD5PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        var rawBytes = rawPassword.toString().getBytes(StandardCharsets.UTF_8);
        return DigestUtils.md5DigestAsHex(rawBytes);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }
}
