package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.enumeration.TaxMode;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.EximportService;
import com.linzhi.gongfu.service.InquiryService;
import com.linzhi.gongfu.service.PurchaseContractService;
import com.linzhi.gongfu.service.SalesContractService;
import com.linzhi.gongfu.vo.VBaseResponse;
import com.linzhi.gongfu.vo.VImportProductTempRequest;
import com.linzhi.gongfu.vo.VImportProductTempResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
    private final PurchaseContractService contractService;
    private final SalesContractService salesContractService;
    private final EximportService eximportService;

    /**
     * 导入产品
     *
     * @param file 导入文件
     * @param id   询价单或者合同id
     * @return 导入产品列表
     */
    @PostMapping("/import/products/{id}/{type}")
    public VImportProductTempResponse importProduct(@RequestParam("products") MultipartFile file, @PathVariable String id, @PathVariable String type) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var maps = findTaxModelAndEnCode(id, type);
        var map = eximportService.importProduct(
            file,
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            (TaxMode) maps.get("taxMode")
        );
        if ((int) map.get("code") != 200)
            return VImportProductTempResponse.builder()
                .code((int) map.get("code"))
                .message((String) map.get("message"))
                .build();
        return eximportService.getVImportProductTempResponse(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            (String) maps.get("encode"),
            (TaxMode) maps.get("taxMode")
        );
    }

    /**
     * 查询导入的产品
     *
     * @param id 询价单或者合同id
     * @return 返回导入产品列表
     */
    @GetMapping("/import/products/{id}/{type}")
    public VImportProductTempResponse findImportProduct(@PathVariable String id, @PathVariable String type) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = findTaxModelAndEnCode(id, type);
        return eximportService.getVImportProductTempResponse(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            (String) map.get("encode"),
            (TaxMode) map.get("taxMode")
        );
    }

    /**
     * 修改导入产品
     *
     * @param id 询价单id或者采购合同主键
     * @return 成功或者失败的信息
     */
    @PutMapping("/import/products/{id}")
    public VBaseResponse modifyImportProduct(@PathVariable String id, @RequestBody List<VImportProductTempRequest> vImportProductTempRequest) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = eximportService.modifyImportProduct(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            vImportProductTempRequest
        );
        return VBaseResponse.builder()
            .code((int) map.get("code"))
            .message((String) map.get("message"))
            .build();
    }

    /**
     * 保存导入的产品
     *
     * @param id 询价单或者合同id
     * @return 成功或者失败的信息
     */
    @PostMapping("/import/products/{id}/{type}/save")
    public VBaseResponse saveImportProduct(@PathVariable String id, @PathVariable String type) {
        Map<String, Object> map;
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        if (type.equals("1")) {
            map = inquiryService.saveImportProducts(
                id,
                session.getSession().getCompanyCode(),
                session.getSession().getOperatorCode()
            );
        } else if (type.equals("2")) {
            map = contractService.saveImportProducts(
                id,
                session.getSession().getCompanyCode(),
                session.getSession().getOperatorCode(), 1
            );
        } else {
            map = salesContractService.saveImportProducts(
                id,
                session.getSession().getCompanyCode(),
                session.getSession().getOperatorCode(), 1
            );
        }
        return VBaseResponse.builder()
            .code((int) map.get("code"))
            .message((String) map.get("message"))
            .build();
    }

    /**
     * 清空暂存的导入产品数据
     *
     * @param id 询价单或者合同id
     * @return 返回成功或者失败信息
     */
    @DeleteMapping("/import/products/{id}")
    public VBaseResponse deleteImportProducts(@PathVariable("id") String id) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = eximportService.deleteImportProducts(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        if (flag)
            return VBaseResponse.builder()
                .code(200)
                .message("删除产品成功")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("删除产品失败")
            .build();
    }

    /**
     * 查找税模式和系统合同编码
     *
     * @param id   合同主键
     * @param type 类型
     * @return 税模式和系统合同编码
     * @throws IOException 异常
     */
    public Map<String, Object> findTaxModelAndEnCode(String id, String type) throws IOException {
        Map<String, Object> map = new HashMap<>();
        if (type.equals("1")) {
            var inquiry = inquiryService.getInquiry(id);
            map.put("taxMode", inquiry.getOfferMode());
            map.put("encode", inquiry.getCode());
        } else if (type.equals("2")) {
            map = contractService.findTaxModelAndEnCode(id, 1);
        } else {
            map = salesContractService.findTaxModelAndEnCode(id, 1);
        }
        return map;
    }
}
