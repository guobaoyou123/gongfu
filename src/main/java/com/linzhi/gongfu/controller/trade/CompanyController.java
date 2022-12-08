package com.linzhi.gongfu.controller.trade;


import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.mapper.OperatorMapper;
import com.linzhi.gongfu.mapper.SceneMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.trade.BrandService;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.service.OperatorService;
import com.linzhi.gongfu.service.SceneService;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import com.linzhi.gongfu.vo.trade.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用于处理单位信息
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@RequiredArgsConstructor
@RestController
public class CompanyController {
    private final CompanyService companyService;
    private final OperatorService operatorService;
    private final OperatorMapper operatorMapper;
    private final SceneService sceneService;
    private final SceneMapper sceneMapper;
    private final BrandService brandService;
    private final BrandMapper brandMapper;

    /**
     * 本单位的公司详情
     *
     * @return 本单位的公司详情
     */
    @GetMapping("/company/detail")
    public VCompanyResponse companyDetail() throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var company = companyService.getCompany(session.getSession().getCompanyCode());
        return VCompanyResponse.builder()
            .code(200)
            .message("获取供应商详情成功")
            .company(company)
            .build();
    }

    /**
     * 修改本公司
     *
     * @param company 本公司信息
     * @return 成功或者失败信息
     */
    @PutMapping("/company/detail")
    public VBaseResponse modifyCompany(@RequestBody VCompanyRequest company
    ) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();

        var flag = companyService.shortNameRepeat(session.getSession().getCompanyCode(), company.getCompanyShortName());
        if (flag)
            return VBaseResponse.builder()
                .code(201)
                .message("公司简称重复")
                .build();
        var str = companyService.saveCompanyDetail(
            company,
            session.getSession().getCompanyCode()
        );
        return VBaseResponse.builder()
            .code(str != null ? 200 : 500)
            .message(str != null ? "数据修改成功" : "修改失败")
            .build();
    }

    /**
     * 设置格友可见
     *
     * @param visibleContent 设置是否可见信息
     * @return 返回成功信息
     */
    @PostMapping("/company/visible")
    public VBaseResponse setVisible(@RequestBody VCompanyVisibleRequest visibleContent) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = companyService.setVisible(session.getSession().getCompanyCode(), visibleContent);
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "设置成功" : "设置失败")
            .build();
    }

    /**
     * 获取人员列表分页
     *
     * @param pageNum  页码
     * @param pageSize 每页显示几条
     * @param state    状态
     * @return 返回人员信息列表
     */
    @GetMapping("/company/operators")
    public VOperatorPageResponse operatorPage(@RequestParam("pageNum") Optional<String> pageNum,
                                              @RequestParam("pageSize") Optional<String> pageSize,
                                              @RequestParam("state") Optional<String> state,
                                              @RequestParam("keyword") Optional<String> keyword) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var page = operatorService.pageOperators(
            PageRequest.of(pageNum.map(PageTools::verificationPageNum).orElse(0), pageSize.map(PageTools::verificationPageSize).orElse(10)),
            session.getSession().getCompanyCode(),
            state.orElse("1"),
            keyword.orElse("")
        );
        return VOperatorPageResponse.builder()
            .code(200)
            .message("数据成功")
            .current(page.getNumber() + 1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .operators(page.getContent())
            .build();
    }

    /**
     * 操作员详情
     *
     * @param code 操作员编码
     * @return 操作员详细信息
     */
    @GetMapping("/company/operator/detail/{code}")
    public VOperatorDetailResponse operatorDetail(@PathVariable String code) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var operator = operatorService.getOperatorDetail(session.getSession().getCompanyCode(), code)
            .map(operatorMapper::toOperatorDetailDTOs).orElseThrow();
        return VOperatorDetailResponse.builder()
            .code(200)
            .message("获取数据成功")
            .operator(operator)
            .build();
    }

    /**
     * 修改人员基本信息
     *
     * @param operator 操作员信息
     * @param code     操作员编码
     * @return 返回成功或者失败信息
     */
    @PutMapping("/company/operator/detail/{code}")
    public VBaseResponse modifyOperator(@RequestBody VOperatorRequest operator, @PathVariable String code) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = operatorService.modifyOperator(session.getSession().getCompanyCode(), code, operator);
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "修改成功" : "修改失败")
            .build();
    }

    /**
     * 添加人员信息
     *
     * @param operator 人员信息
     * @return 返回添加成功或者失败信息
     */
    @PostMapping("/company/operator/detail")
    public VResetPasswordResponse saveOperator(@RequestBody VOperatorRequest operator) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var password = operatorService.saveOperator(session.getSession().getCompanyCode(), operator);
        return VResetPasswordResponse.builder()
            .code(password != null ? 200 : 500)
            .message(password != null ? "添加成功" : "添加失败")
            .password(password)
            .build();
    }

    /**
     * 获取场景列表
     *
     * @return 返回场景列表
     */
    @GetMapping("/company/scenes")
    public VSceneListResponse scenes() throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var scenes = sceneService.listScenes(session.getSession().getCompanyCode())
            .stream().map(sceneMapper::toDTO)
            .map(sceneMapper::toVScene).toList();
        return VSceneListResponse.builder()
            .code(200)
            .message("获取数据成功")
            .scenes(scenes)
            .build();
    }

    /**
     * 修改人员场景
     *
     * @param operatorRequests 人员场景信息
     * @return 返回修改成功信息
     */
    @PutMapping("/company/operator/detail/scene")
    public VBaseResponse modifyOperatorScene(@RequestBody List<VOperatorSceneRequest> operatorRequests) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = operatorService.modifyOperatorScene(session.getSession().getCompanyCode(), operatorRequests);
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "操作成功" : "操作失败")
            .build();
    }

    /**
     * 重置密码
     *
     * @param code 操作员编码
     * @return 返回成功信息
     */
    @PostMapping("/company/operator/detail/{code}/password")
    public VResetPasswordResponse resetPassword(@PathVariable String code) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var newPassword = operatorService.resetPassword(
            session.getSession().getCompanyCode(),
            code, null
        ).orElseThrow(() -> new Exception("设置失败"));
        return VResetPasswordResponse.builder()
            .code(200)
            .message("操作成功")
            .password(newPassword)
            .build();
    }

    /**
     * 人员权限统计列表
     *
     * @return 返回人员权限列表
     */
    @GetMapping("/company/operator/scenes/statistics")
    public VOperatorListResponse authorityStatistics() {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var list = operatorService.listOperators(session.getSession().getCompanyCode())
            .stream().map(operatorMapper::toOperatorDTOs)
            .toList();
        return VOperatorListResponse.builder()
            .code(200)
            .message("获取数据成功")
            .operators(list)
            .build();
    }

    /**
     * 启用和禁用人员
     *
     * @param code  人员编码
     * @param state 状态
     * @return 返回成功或者失败信息
     */
    @PostMapping("/company/operator/detail/{code}")
    public VBaseResponse modifyOperatorState(@PathVariable String code, @RequestBody Optional<VOperatorRequest> state) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = operatorService.modifyOperatorState(
            code,
            session.getSession().getCompanyCode(),
            state.orElseThrow(() -> new NullPointerException("数据为空")).getState()
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "操作成功" : "操作失败")
            .build();
    }

    /**
     * 查询有采购权限的操作员列表
     *
     * @param privilege 权限类型 1-采购 2-销售
     * @return 操作员列表
     */
    @GetMapping("/company/operators/{privilege}")
    public VOperatorListResponse listOperatorsByPrivilege(@PathVariable Optional<String> privilege) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var list = operatorService.listOperatorsByPrivilege(
                session.getSession().getCompanyCode(),
                privilege.orElseThrow(() -> new NullPointerException("数据为空")))
            .stream().map(operatorMapper::toOperatorDTOs)
            .toList();
        return VOperatorListResponse.builder()
            .code(200)
            .message("获取数据成功")
            .operators(list)
            .build();
    }

    /**
     * 根据单位编码查找本单位经营品牌
     *
     * @return 返回经营品牌列表
     */
    @GetMapping("/company/brands")
    public VDcBrandResponse listBrandsByCompanyCode() {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var brands = brandService.listBrandsByCompanyCode(session.getSession().getCompanyCode()).stream()
            .map(brandMapper::toProductBrandPreload)
            .collect(Collectors.toSet());
        return VDcBrandResponse.builder()
            .code(200)
            .message("获取数据成功")
            .brands(brands)
            .build();
    }
}

