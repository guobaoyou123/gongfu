package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.mapper.MenuMapper;
import com.linzhi.gongfu.mapper.WordMapper;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.service.MenuService;
import com.linzhi.gongfu.service.OperatorService;
import com.linzhi.gongfu.service.WordService;
import com.linzhi.gongfu.util.URLTools;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用于处理前端应用需要预加载的内容。
 *
 * @author xutao
 * @create_at 2022-01-19
 */
@RequiredArgsConstructor
@RestController
public class PreloadController {
    private final CompanyService companyService;
    private final MenuService menuService;
    private final CompanyMapper companyMapper;
    private final MenuMapper menuMapper;
    private final WordService wordService;
    private final WordMapper wordMapper;
    private final OperatorService operatorService;

    /**
     * 通过给定的主机域名名称获取对应的公司基本信息接口
     *
     * @param domain   在请求头信息中包含的主机域名名称
     * @param hostname 在查询串中包含的主机域名名称
     * @return 对应的公司基本信息或者无法找到的提示
     */
    @GetMapping("/host")
    public VPreloadCompanyInfoResponse fetchCompanyInfoByHost(
            @RequestHeader("CompanyDomain") Optional<String> domain, @RequestParam("host") Optional<String> hostname) {
        return hostname.or(() -> domain)
                .map(URLTools::extractSubdomainName)
                .flatMap(companyService::findCompanyInformationByHostname)
                .map(companyMapper::toPreload)
                .orElse(VPreloadCompanyInfoResponse.builder()
                        .code(404)
                        .message("请求的公司信息没有找到。")
                        .companyName("UNKNOWN")
                        .companyShortName("UNKNOWN").build());
    }

    /**
     * 获取前端界面所需要使用的全部菜单结构
     *
     * @return 完整的前端功能菜单结构
     */
    @GetMapping("/menus")
    public VPreloadMenuResponse fetchFrontendMenus() {

        var mainMenus = menuService.fetchAllMenus().stream()
                .map(menuMapper::toPreloadMainMenu)
                .collect(Collectors.toSet());
        return VPreloadMenuResponse.builder()
                .code(200)
                .message("所有菜单结构已经获取，使用时请保证菜单顺序。")
                .menus(mainMenus)
                .build();
    }

    /**
     * 获取前端界面所需要使用的全部文案词汇
     *
     * @return 全部不分类的前端文案词汇
     */
    @GetMapping("/strings")
    public VPreloadWordsResponse fetchFrontendWords() {
        var words = wordService.fetchAllWords().stream()
                .map(wordMapper::toVO)
                .collect(Collectors.toSet());
        return VPreloadWordsResponse.builder()
                .code(200)
                .message("所有文案词汇已经获取，使用时请注意定位键。")
                .words(words)
                .build();
    }

    /**
     * 修改密码
     * @param password 密码信息
     * @param domain 域名
     * @return 返回成功信息
     * @throws Exception 异常
     */
    @PostMapping("/password")
    public VBaseResponse resetPassword( @RequestBody Optional<VResetPasswordRequest> password ,@RequestHeader("CompanyDomain") Optional<String> domain) throws Exception {
        //查询公司编码
        TCompanyBaseInformation tCompanyBaseInformation= domain
            .map(URLTools::extractSubdomainName)
            .flatMap(companyService::findCompanyInformationByHostname)
            .orElseThrow(()->new IOException("未从数据库中查到"));
        //验证旧密码是否正确
        var flag = operatorService.verifyPassword(
            tCompanyBaseInformation.getCode(),
            password.orElseThrow().getCode(),
            password.orElseThrow().getOldpassword()
        );
        if(!flag)
            throw new AuthenticationException("输入的原密码不正确");
        operatorService.resetPassword(
            tCompanyBaseInformation.getCode(),
                password.orElseThrow().getCode(),
                password.orElseThrow().getPassword()
            )
            .orElseThrow(()->new Exception("设置失败"));
        return VBaseResponse.builder()
            .code(200)
            .message("操作成功")
            .build();
    }
}
