package com.linzhi.gongfu.security;

import com.linzhi.gongfu.dto.TScene;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.infrastructure.HttpServletJsonResponseWrapper;
import com.linzhi.gongfu.mapper.SessionMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.OperatorService;
import com.linzhi.gongfu.vo.VAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 处理用户登录成功响应的处理器
 *
 * @author xutao
 * @create_at 2021-12-24
 */
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    private final OperatorService operatorService;
    private final TokenStore tokenStore;
    private final SessionMapper sessionMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Assert.isInstanceOf(OperatorSessionToken.class, authentication, "需要确认登录认证成功处理器没有被使用在其他位置上。");
        var operatorSession = (OperatorSessionToken.Session) authentication.getPrincipal();
        var token = TokenStore.generateToken();
        var operator = operatorService
            .findOperatorByID(sessionMapper.toOperatorId(operatorSession))
            .orElseThrow(() -> new IOException("无法从数据库获取已经登录的操作员信息。"));
        var operatorScenes = operator.getScenes().stream().map(TScene::getCode).collect(Collectors.toSet());
        // TODO 暂定有效期两小时，未来需要根据KC提供的配置从数据库读取
        tokenStore.store(operator.getCode(), operatorSession.getDomain(), token, 2L,
            TimeUnit.HOURS);
        var loginResponse = VAuthenticationResponse.builder()
            .code(200)
            .message("登录成功，可使用令牌访问其他功能")
            .operatorCode(operator.getCode())
            .operatorName(operator.getName())
            .companyCode(operator.getCompanyCode())
            .companyName(operator.getCompanyName())
            .companyShortName(operator.getCompanyShortName())
            .admin(operator.getAdmin().equals(Whether.YES))
            .scenes(operatorScenes)
            // TODO 此处也有登录有效期设置，在修改为使用KC配置数据时，需要一并进行修改
            .expiresAt(LocalDateTime.now().plus(Duration.ofHours(2L)))
            .token(token)
            .build();
        HttpServletJsonResponseWrapper.wrap(response).write(200, loginResponse);
    }
}
