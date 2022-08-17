package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.entity.CompTradeApply;
import com.linzhi.gongfu.enumeration.TradeApply;
import com.linzhi.gongfu.mapper.BlacklistMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.CompTradeApplyService;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

/**
 * 用于格友申请采购流程等
 *
 * @author zgh
 * @create_at 2022-07-21
 */
@RestController
@RequiredArgsConstructor
public class EnrolledCompanyController {
    private final CompanyService companyService;
    private final CompTradeApplyService compTradeApplyService;
    private final BlacklistMapper blacklistMapper;
    /**
     * 查询入格单位信息列表分页
     * @param name 公司名称
     * @param pageNum 页数
     * @param pageSize 每页展示几条
     * @return 入格单位信息列表
     */
    @GetMapping("/enrolled/companies")
    public VEnrolledCompanyPageResponse pageCompanies(@RequestParam("name") Optional<String> name,
                                                        @RequestParam("pageNum") Optional<String> pageNum,
                                                        @RequestParam("pageSize") Optional<String> pageSize ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var page = companyService.pageEnrolledCompanies(
            name.orElse(""),
            PageRequest.of(pageNum.map(PageTools::verificationPageNum).orElse(0),pageSize.map(PageTools::verificationPageSize).orElse(10)),
            session.getSession().getCompanyCode()
        );
        return VEnrolledCompanyPageResponse.builder()
            .code(200)
            .message("获取数据成功")
            .current(page.getNumber()+1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .companies(page.getContent())
            .build();
    }

    /**
     * 查询格友可见详情
     * @param invitationCode 邀请码
     * @param code 格友编码
     * @return 格友可见详情
     * @throws IOException 异常
     */
    @GetMapping("/enrolled/company/detail")
    public VEnrolledCompanyResponse getEnrolledCompanyDetail(@RequestParam("invitationCode") Optional<String> invitationCode,
                                                              @RequestParam("code")Optional<String> code
    ) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        //判断邀请码是否不为空，找到邀请单位详情
        if(!invitationCode.orElse("").equals("")){
            code =   companyService.getECompanyCode(invitationCode.get());
        }
        if(code.orElse("").equals(""))
            return VEnrolledCompanyResponse.builder()
                .code(404)
                .message("邀请码已过期或邀请码错误")
                .build();
        //查询格友单位详情
        var company = companyService.getEnrolledCompany(
                code.get(),session.getSession().getCompanyCode())
            .orElseThrow(()->new IOException("未从数据库找到"));
        return VEnrolledCompanyResponse.builder()
            .code(200)
            .message("获取数据成功")
            .company(company)
            .build();
    }

    /**
     * 拒绝名单中的格友详情
     * @param code 格友编码
     * @return 格友详情
     * @throws IOException 异常
     */
    @GetMapping("/enrolled/company/apply/refused/detail/{code}")
    public VEnrolledCompanyResponse findRefuseEnrolledCompanyDetail(@PathVariable String code)
        throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        //查询格友单位详情
        var company = compTradeApplyService.getRefuseEnrolledCompanyDetail(
                code,session.getSession().getCompanyCode())
            .orElseThrow(()->new IOException("未从数据库找到"));
        return VEnrolledCompanyResponse.builder()
            .code(200)
            .message("获取数据成功")
            .company(company)
            .build();
    }

    /**
     * 申请采购
     * @param vTradeApplyRequest 申请采购信息
     * @return 返回成功信息或者失败信息
     */
    @PostMapping("/enrolled/company/apply")
    public VTradeApplyResponse tradeApply(@RequestBody Optional<VTradeApplyRequest> vTradeApplyRequest){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = compTradeApplyService.saveTradeApply(
            vTradeApplyRequest.orElseThrow(()->new NullPointerException("数据为空")),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            session.getSession().getCompanyName()
        );
        return VTradeApplyResponse.builder()
            .code(map.get("flag").equals("0")?500:map.get("flag").equals("1")?200:202)
            .message(map.get("flag").equals("0")?"操作失败":map.get("flag").equals("1")?"操作成功":"该格友已拒绝申请")
            .applyCode(map.get("code"))
            .build();
    }

    /**
     * 查看待处理列表
     * @param name 公司名称
     * @param pageNum 页数
     * @param pageSize 每页展示几条
     * @return 待处理申请列表
     */
    @GetMapping("/enrolled/company/apply")
    public VTradeApplyPageResponse pageTradeApplies(@RequestParam("name") Optional<String> name,
                                                  @RequestParam("pageNum") Optional<String> pageNum,
                                                  @RequestParam("pageSize") Optional<String> pageSize ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var page = compTradeApplyService.pageTradeApplies(
            session.getSession().getCompanyCode(),
            PageRequest.of(pageNum.map(PageTools::verificationPageNum).orElse(0),
                pageSize.map(PageTools::verificationPageSize).orElse(10)),
            name.orElse("")
        );
        return VTradeApplyPageResponse.builder()
            .code(200)
            .message("获取数据成功")
            .current(page.getNumber()+1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .applies(page.getContent())
            .build();
    }

    /**
     * 同意申请
     * @param code 申请记录编码
     * @return 返回成功或者失败信息
     */
    @PostMapping("/enrolled/company/apply/{code}/pass")
    public  VBaseResponse  consentApply(@PathVariable String code,@RequestBody Optional<VTradeApplyConsentRequest> vTradeApplyConsentRequest) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        CompTradeApply compTradeApply = compTradeApplyService.getCompInvitationCode(code);
        if(!compTradeApply.getState().equals(TradeApply.APPLYING))
            return VBaseResponse.builder()
                .code(500)
                .message("操作失败")
                .build();
        var flag = compTradeApplyService.consentApply(
            compTradeApply,
            session.getSession().getCompanyCode(),
            session.getSession().getCompanyName(),
            session.getSession().getOperatorCode(),
            vTradeApplyConsentRequest.orElseThrow(()->new NullPointerException("数据为空")));
        return VBaseResponse.builder()
            .code(flag?200:500)
            .message(flag?"操作成功":"操作失败")
            .build();
    }

    /**
     * 拒绝申请和始终拒绝申请
     * @param code 申请记录编码
     * @param vTradeApplyRefuseRequest  请求参数
     * @return  返回成功或者失败信息
     */
    @PostMapping("/enrolled/company/apply/{code}/refuse")
    public VBaseResponse tradeApplyRefuse(
        @PathVariable String code,
        @RequestBody Optional<VTradeApplyRefuseRequest> vTradeApplyRefuseRequest
    ) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        CompTradeApply compTradeApply = compTradeApplyService.getCompInvitationCode(code);

        var flag = compTradeApplyService.refuseApply(
            session.getSession().getCompanyCode(),
            session.getSession().getCompanyName(),
            session.getSession().getOperatorCode(),
            vTradeApplyRefuseRequest.orElseThrow().getRemark(),
            vTradeApplyRefuseRequest.orElseThrow().getState(),
            compTradeApply
        );
        return VBaseResponse.builder()
            .code(flag?200:500)
            .message(flag?"操作成功":"操作失败")
            .build();
    }

    /**
     * 生成邀请码
     * @return 邀请码
     */
    @PostMapping("/enrolled/company/invitation/code")
    public VInvitationCodeResponse getInvitationCode(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        String code = companyService.getInvitationCode(session.getSession().getCompanyCode());
        return VInvitationCodeResponse.builder()
            .code(code!=null?200:500)
            .message(code!=null?"操作成功":"操作失败")
            .invitationCode(code)
            .build();
    }

    /**
     * 申请采购历史记录
     * @param pageNum 页码
     * @param pageSize 每页几条
     * @param name 公司名称
     * @param startTime 开始时间
     * @param endTime  结束时间
     * @return 申请采购历史记录
     */
    @GetMapping("/enrolled/company/apply/history")
    public VTradeApplyHistoryResponse pageApplyHistories(@RequestParam("pageNum") Optional<String> pageNum ,
                                                       @RequestParam("pageSize") Optional<String> pageSize ,
                                                       @RequestParam("name") Optional<String> name,
                                                       @RequestParam("startTime") Optional<String> startTime,
                                                       @RequestParam("endTime") Optional<String> endTime,
                                                       @RequestParam("type") Optional<String> type){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var page = compTradeApplyService.pageApplyHistories(
            startTime.orElse(""),
            endTime.orElse(""),
            type.orElse("1"),
            name.orElse(""),
            PageRequest.of(pageNum.map(PageTools::verificationPageNum).orElse(0),pageSize.map(PageTools::verificationPageSize).orElse(10)),
            session.getSession().getCompanyCode()
        );
        return VTradeApplyHistoryResponse.builder()
            .code(200)
            .message("操作成功")
            .current(page.getNumber()+1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .applies(page.getContent())
            .build();
    }

    /**
     * 查找待处理申请或者历史申请记录详情
     * @param code 申请记录编码
     * @return 申请记录详情
     */
    @GetMapping("/enrolled/company/apply/{code}/detail")
    public  VTradeApplyDetailResponse getApplyDetail(@PathVariable String code) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var detail = compTradeApplyService.getTradeApplyDetail(code,session.getSession().getCompanyCode());
        return VTradeApplyDetailResponse.builder()
            .code(200)
            .message("获取数据成功")
            .record(detail.orElseThrow(()->new NullPointerException("数据为空")))
            .build();
    }

    /**
     * 始终拒绝名单
     * @return 返回始终拒绝名单
     */
    @GetMapping("/enrolled/company/apply/refused")
    public VRefusedListResponse listRefused(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var list= compTradeApplyService.listRefused(session.getSession().getCompanyCode());

        return VRefusedListResponse.builder()
            .code(200)
            .message("获取数据成功")
            .companies(list.stream()
                .map(blacklistMapper::toRefusedCompany)
                .toList()
            )
            .build();
    }

    /**
     * 取消始终拒绝
     * @param code 格友编码
     * @return 操作成功或失败信息
     */
    @PostMapping("/enrolled/company/apply/refused/{code}")
    public VBaseResponse removeRefused(@PathVariable String code){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = compTradeApplyService.removeRefused(session.getSession().getCompanyCode(),code,"1");
        return VBaseResponse.builder()
            .code(flag?200:500)
            .message(flag?"操作成功":"操作失败")
            .build();
    }
}
