package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.enumeration.TaxMode;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.trade.EximportService;
import com.linzhi.gongfu.service.trade.InquiryService;
import com.linzhi.gongfu.service.trade.PurchaseContractService;
import com.linzhi.gongfu.service.trade.SalesContractService;
import com.linzhi.gongfu.vo.VBaseResponse;
import com.linzhi.gongfu.vo.VImportProductStockTempRequest;
import com.linzhi.gongfu.vo.VImportProductTempRequest;
import com.linzhi.gongfu.vo.VImportProductTempResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        String filename = file.getOriginalFilename();
        if(filename!=null){
            filename =  filename.substring(0,filename.lastIndexOf("."));
        }
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
        String sysName = (type.equals("1") ? "询价单" : type.equals("2") ? "采购合同" : "销售合同") + maps.get("encode");
        if (!sysName.equals(filename))
            return VImportProductTempResponse.builder()
                .code(203)
                .message("导入失败，导入文件名称错误，请以" + (type.equals("1") ? "询价单+询价单号" : type.equals("2") ? "采购合同+合同号" : "销售合同+销售合同号") + "格式命名，具体格式请仿照导出的产品模板")
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
        Map<String, Object> map;
        if (type.equals("1")) {
            map = inquiryService.findTaxModelAndEnCode(id);
        } else if (type.equals("2")) {
            map = contractService.findTaxModelAndEnCode(id, 1);
        } else {
            map = salesContractService.findTaxModelAndEnCode(id, 1);
        }
        return map;
    }

    /**
     * 初始化库存
     *
     * @param file 导入文件
     * @param id   库房编码
     * @return 导入产品列表
     */
    @PostMapping("/import/products/stock/{id}")
    public VImportProductTempResponse importProductStock(@RequestParam("products") MultipartFile file, @PathVariable String id) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();

        var map = eximportService.importProductStock(
            file,
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            "1"
        );
        if ((int) map.get("code") != 200)
            return VImportProductTempResponse.builder()
                .code((int) map.get("code"))
                .message((String) map.get("message"))
                .build();
        return eximportService.getVImportProductStockTempResponse(
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            id,
            "1"
        );
    }


    /**
     * 导入安全库存
     *
     * @param file 导入文件
     * @return 导入产品安全库存列表
     */
    @PostMapping("/import/products/safetystock")
    public VImportProductTempResponse importProductSafetyStock(@RequestParam("products") MultipartFile file){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();

        var map = eximportService.importProductStock(
            file,
            "0",
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            "2"
        );
        if ((int) map.get("code") != 200)
            return VImportProductTempResponse.builder()
                .code((int) map.get("code"))
                .message((String) map.get("message"))
                .build();
        return eximportService.getVImportProductStockTempResponse(
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
           "1",
            "2"
        );
    }

    /**
     * 查询导入的产品
     *
     * @param code 仓库编码
     * @param type 类型
     * @return 返回导入产品列表
     */
    @GetMapping("/import/products/stock")
    public VImportProductTempResponse findImportProductStock(@RequestParam("code") Optional<String> code, @RequestParam("type") Optional<String> type){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return eximportService.getVImportProductStockTempResponse(
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            code.orElse("0"),
            type.orElse("2")
        );
    }

    /**
     * 修改导入产品库存
     *
     * @return 成功或者失败的信息
     */
    @PutMapping("/import/products/stock")
    public VBaseResponse modifyImportProductStock(@RequestBody Optional<VImportProductStockTempRequest> vImportProductTempRequest) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = eximportService.modifyImportProductStock(
            vImportProductTempRequest.orElseThrow(),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code((int) map.get("code"))
            .message((String) map.get("message"))
            .build();
    }
}
