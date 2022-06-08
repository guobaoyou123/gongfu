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
import java.util.Map;

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
     * @param id 询价单或者合同id
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

    /**
     * 查询导入的产品
     * @param id 询价单或者合同id
     * @return 返回导入产品列表
     */
    @GetMapping("/import/products/{id}/{type}")
    public VImportProductTempResponse findImportProduct(@PathVariable String id, @PathVariable String type) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        TaxMode taxMode;String encode =null;
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
        return eximportService.getvImportProductTempResponse(id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            encode,
            taxMode
        );
    }

    /**
     * 修改导入产品
     * @param id 询价单id或者采购合同主键
     * @return 成功或者失败的信息
     */
    @PutMapping("/import/products/{id}")
    public VBaseResponse modifyImportProduct(@PathVariable String id, @RequestBody List<VImportProductTempRequest> vImportProductTempRequest){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = eximportService.modifyImportProduct(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            vImportProductTempRequest
        );
        return VBaseResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .build();
    }

    /**
     * 保存导入的产品
     * @param id 询价单或者合同id
     * @return 成功或者失败的信息
     */
    @PostMapping("/import/products/{id}/{type}")
    public VBaseResponse saveImportProduct(@PathVariable String id,@PathVariable String type){
        Map<String,Object> map;
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        if(type.equals("1")){
             map = inquiryService.saveImportProducts(
                id,
                session.getSession().getCompanyCode(),
                session.getSession().getOperatorCode()
            );
        }else {
            map = contractService.saveImportProducts(
                id,
                session.getSession().getCompanyCode(),
                session.getSession().getOperatorCode()
            );
        }
        return VBaseResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .build();
    }

    /**
     * 清空暂存的导入产品数据
     * @param id 询价单或者合同id
     * @return 返回成功或者失败信息
     */
    @DeleteMapping("/import/products/{id}")
    public VBaseResponse deleteImportProducts(@PathVariable("id")String id){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = eximportService.deleteImportProducts(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        if(flag)
            return  VBaseResponse.builder()
                .code(200)
                .message("删除产品成功")
                .build();
        return  VBaseResponse.builder()
            .code(500)
            .message("删除产品失败")
            .build();
    }
}