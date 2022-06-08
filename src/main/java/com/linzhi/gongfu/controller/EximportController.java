package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.enumeration.TaxMode;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.ContractService;
import com.linzhi.gongfu.service.EximportService;
import com.linzhi.gongfu.service.InquiryService;
import com.linzhi.gongfu.vo.VBaseResponse;
import com.linzhi.gongfu.vo.VImportProductTempRequest;
import com.linzhi.gongfu.vo.VImportProductTempResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 用于处理导入导出信息
 *
 * @author zgh
 * @create_at 2022-06-08
 */
@RequiredArgsConstructor
@RestController
public class EximportController {

    private final InquiryService inquiryService;

    private final ContractService contractService;
    private final EximportService eximportService;
    /**
     * 导入产品
     * @param file 导入文件
     * @param id 询价单id
     * @return 导入产品列表
     */
    @PostMapping("/import/products/{id}/{type}")
    public VImportProductTempResponse importProduct(@RequestParam("products") MultipartFile file, @PathVariable String id, @PathVariable String type) throws IOException {
        TaxMode taxMode;String encode =null;
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        if(type.equals("1")){
            var inquiry = inquiryService.findInquiry(id).orElseThrow(()->new IOException("没有从数据库中找到该询价单"));
            taxMode=inquiry.getOfferMode();
            encode=inquiry.getCode();
        }else{
            var contract = contractService.getContractRevisionDetail(id,1)
                .orElseThrow(()->new IOException("未从数据库中找到该合同"));
            taxMode=contract.getOfferMode();
            encode=contract.getCode();
        }

        var map = eximportService.importProduct(
            file,
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            taxMode
        );
        if((int) map.get("code")!=200)
            return VImportProductTempResponse.builder()
                .code((int) map.get("code"))
                .message((String) map.get("message"))
                .build();
        return eximportService.getvImportProductTempResponse(id,  session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),encode,taxMode);
    }


}
