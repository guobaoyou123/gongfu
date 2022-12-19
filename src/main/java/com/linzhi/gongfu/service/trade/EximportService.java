package com.linzhi.gongfu.service.trade;

import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.dto.TImportProductTemp;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.TaxMode;
import com.linzhi.gongfu.mapper.storage.ImportProductStockTempMapper;
import com.linzhi.gongfu.mapper.trade.ImportProductTempMapper;
import com.linzhi.gongfu.repository.trade.ImportProductTempRepository;
import com.linzhi.gongfu.repository.ProductRepository;
import com.linzhi.gongfu.repository.storage.ImportProductStockTempRepository;
import com.linzhi.gongfu.util.ExcelUtil;
import com.linzhi.gongfu.vo.VImportProductTempRequest;
import com.linzhi.gongfu.vo.VImportProductTempResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 产品导入信息处理及业务服务
 *
 * @author zgh
 * @create_at 2022-06-08
 */
@RequiredArgsConstructor
@Service
public class EximportService {
    private final ImportProductTempRepository importProductTempRepository;
    private final ProductRepository productRepository;
    private final ImportProductTempMapper importProductTempMapper;
    private final ImportProductStockTempRepository importProductStockTempRepository;
    private final ImportProductStockTempMapper importProductStockTempMapper;
    /**
     * 判断 字符串是否为数字
     *
     * @param str 字符串
     * @return 返回是或者否
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        if (str.indexOf(".") > 0) {//判断是否有小数点
            if (str.indexOf(".") == str.lastIndexOf(".") && str.split("\\.").length == 2) { //判断是否只有一个小数点
                return !pattern.matcher(str.replace(".", "")).matches();
            } else {
                return true;
            }
        } else {
            return !pattern.matcher(str).matches();
        }
    }

    /**
     * 导入产品
     *
     * @param file        导入文件
     * @param id          询价单id
     * @param companyCode 公司编码
     * @param operator    操作员编码
     * @return 返回成功或者失败信息
     */
    @Transactional
    public Map<String, Object> importProduct(MultipartFile file, String id, String companyCode, String operator, TaxMode taxMode) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            importProductTempRepository.deleteProduct(id, companyCode, operator);

            List<Map<String, Object>> list = ExcelUtil.excelToList(file);
            List<ImportProductTemp> importProductTemps = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                ImportProductTemp importProductTemp = ImportProductTemp.builder().build();
                importProductTemp.setImportProductTempId(
                    ImportProductTempId.builder()
                        .dcCompId(companyCode)
                        .operator(operator)
                        .inquiryId(id)
                        .itemNo(i + 2)
                        .build()
                );
                if (map.get("产品代码") != null) {
                    String code = map.get("产品代码").toString();
                    importProductTemp.setCode(code);
                    //验证产品编码是否正确
                    List<Product> products = productRepository.findProductByCode(code);
                    if (products.size() == 1) {
                        importProductTemp.setProductId(products.get(0).getId());
                        importProductTemp.setBrandCode(products.get(0).getBrandCode());
                        importProductTemp.setBrandName(products.get(0).getBrand());
                    }
                }
                if (map.get("数量") != null) {
                    String amount = map.get("数量").toString();
                    importProductTemp.setAmount(amount);
                }
                if (map.get("单价（未税）") != null) {
                    String price = map.get("单价（未税）").toString();
                    importProductTemp.setPrice(price);
                    importProductTemp.setFlag(TaxMode.UNTAXED);
                } else if (map.get("单价（含税）") != null) {
                    String price = map.get("单价（含税）").toString();
                    importProductTemp.setPrice(price);
                    importProductTemp.setFlag(TaxMode.INCLUDED);
                } else {
                    importProductTemp.setFlag(taxMode);
                }
                importProductTemps.add(importProductTemp);
            }
            importProductTempRepository.saveAll(importProductTemps);
            resultMap.put("code", 200);
            resultMap.put("message", "导入产品成功！");
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("message", "导入产品失败！");
            return resultMap;
        }
    }

    /**
     * 查询暂存产品详情
     *
     * @param id          合同主键或者询价单主键
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     * @param code        合同编码或者询价单编码
     * @param taxMode     税模式
     * @return 返回暂存产品列
     */
    public VImportProductTempResponse getVImportProductTempResponse(String id, String companyCode, String operator, String code, TaxMode taxMode) {
        var map = findImportProductDetail(
            companyCode,
            operator,
            id, code, taxMode
        );
        var list = (List<VImportProductTempResponse.VProduct>) map.get("products");
        return VImportProductTempResponse.builder()
            .code(200)
            .message("产品导入临时表成功")
            .confirmable(list.stream().filter(vProduct -> vProduct.getMessages().size() > 0 || vProduct.getConfirmedBrand() == null).toList().size() == 0)
            .products(list)
            .enCode((String) map.get("enCode"))
            .build();
    }

    /**
     * 查询导入产品列表
     *
     * @param companyCode 单位id
     * @param operator    操作员编码
     * @param id          询价单id或者合同主键
     * @param code        询价单编码或者合同编码
     * @return 返回导入产品列表信息
     */
    public Map<String, Object> findImportProductDetail(String companyCode, String operator, String id, String code, TaxMode taxMode) {
        Map<String, Object> map = new HashMap<>();
        map.put("enCode", code);
        List<ImportProductTemp> list = importProductTempRepository.
            findImportProductTempsByImportProductTempId_DcCompIdAndImportProductTempId_OperatorAndImportProductTempId_InquiryId(companyCode, operator, id);
        List<TImportProductTemp> importProductTemps = list.stream()
            .map(importProductTempMapper::toTImportProductTemp)
            .toList();
        importProductTemps.forEach(tImportProductTemp -> {
            //错误数据
            List<String> errorList = new ArrayList<>();
            List<TBrand> tBrands = new ArrayList<>();
            if (tImportProductTemp.getProductId() == null && tImportProductTemp.getCode() == null) {
                errorList.add("产品代码不能为空");
            } else if (tImportProductTemp.getProductId() == null && tImportProductTemp.getCode() != null) {
                //验证产品编码是否正确
                List<Product> products = productRepository.findProductByCode(tImportProductTemp.getCode());
                if (products.size() == 0) {
                    errorList.add("产品代码错误或不存在于系统中");
                } else {
                    errorList.add("该产品代码在系统中存在多个，请选择品牌");
                    AtomicInteger i = new AtomicInteger();
                    products.forEach(product -> {
                        i.getAndIncrement();
                        tBrands.add(TBrand.builder()
                            .code(product.getBrandCode())
                            .name(product.getBrand())
                            .sort(i.get())
                            .build());
                    });
                }
            } else {
                tBrands.add(TBrand.builder()
                    .code(tImportProductTemp.getConfirmedBrand())
                    .name(tImportProductTemp.getConfirmedBrandName())
                    .sort(1)
                    .build());
            }
            tImportProductTemp.setBrand(tBrands);
            if (tImportProductTemp.getAmount() == null) {
                errorList.add("数量不能为空");
            } else {
                //验证 数量是否为数字
                if (isNumeric(tImportProductTemp.getAmount())) {
                    errorList.add("数量应为数字");
                }
            }

            if (tImportProductTemp.getPrice() != null) {
                //验证 数量是否为数字
                if (isNumeric(tImportProductTemp.getPrice())) {
                    errorList.add("单价应为数字");
                }
                if (!taxMode.equals(tImportProductTemp.getFlag())) {
                    //  String offerMode=taxMode.equals(TaxMode.UNTAXED)?"未税单价":"含税单价";
                    errorList.add("导入的单价与该目标单据的报价模式不一致");
                }
            }
            tImportProductTemp.setMessages(errorList);
        });
        map.put("products", importProductTemps.stream()
            .map(importProductTempMapper::toVProduct)
            .toList());
        return map;
    }

    /**
     * 修改暂存导入产品
     *
     * @param id          询价单id或者采购合同主键
     * @param companyCode 单位id
     * @param operator    操作员编码
     * @return 返回成功或者失败信息
     */
    @Transactional
    public Map<String, Object> modifyImportProduct(String id, String companyCode, String operator, List<VImportProductTempRequest> vImportProductTempRequests) {
        Map<String, Object> resultMap = new HashMap<>();
        List<ImportProductTemp> list = new ArrayList<>();
        try {
            for (VImportProductTempRequest vImport : vImportProductTempRequests) {
                ImportProductTemp temp = importProductTempRepository.findById(
                    ImportProductTempId.builder()
                        .inquiryId(id)
                        .itemNo(vImport.getItemNo())
                        .dcCompId(companyCode)
                        .operator(operator)
                        .build()
                ).orElseThrow(() -> new IOException("数据库中找不到该暂存产品"));
                Product product = productRepository.findProductByCodeAndBrandCode(temp.getCode(), vImport.getBrandCode())
                    .orElseThrow(() -> new IOException("数据库中找不到该产品"));
                temp.setProductId(product.getId());
                temp.setBrandCode(product.getBrandCode());
                temp.setBrandName(product.getBrand());
                list.add(temp);
            }
            importProductTempRepository.saveAll(list);
            resultMap.put("code", 200);
            resultMap.put("message", "修改成功");

            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("message", "保存失败");
            return resultMap;
        }
    }

    /**
     * 删除暂存产品
     *
     * @param id          询价单id
     * @param companyCode 公司编码
     * @param operator    操作员编码
     * @return 返回true或者false
     */
    @Transactional
    public Boolean deleteImportProducts(String id, String companyCode, String operator) {
        try {
            importProductTempRepository.deleteProduct(id, companyCode, operator);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 导入产品库存
     *
     * @param file        导入文件
     * @param id          仓库编码
     * @param companyCode 公司编码
     * @return 返回成功或者失败信息
     */
    @Transactional
    public Map<String, Object> importProductStock(MultipartFile file, String id, String companyCode,String operator,String type) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            importProductStockTempRepository.deleteProduct(id, companyCode);
            List<Map<String, Object>> list = ExcelUtil.excelToList(file);
            List<ImportProductStockTemp> importProductTemps = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                ImportProductStockTemp importProductTemp = ImportProductStockTemp.builder().build();
                importProductTemp.setImportProductStockTempId(
                    ImportProductStockTempId.builder()
                        .dcCompId(companyCode)
                        .operator(operator)
                        .werahouseCode(id)
                        .itemNo(i + 2)
                        .type(type)
                        .build()
                );
                if (map.get("产品代码") != null) {
                    String code = map.get("产品代码").toString();
                    importProductTemp.setCode(code);
                    //验证产品编码是否正确
                    List<Product> products = productRepository.findProductByCode(code);
                    if (products.size() == 1) {
                        importProductTemp.setProductId(products.get(0).getId());
                        importProductTemp.setBrandCode(products.get(0).getBrandCode());
                        importProductTemp.setBrandName(products.get(0).getBrand());
                    }
                }
                if (map.get("数量") != null) {
                    String amount = map.get("数量").toString();
                    importProductTemp.setAmount(amount);
                }
                importProductTemps.add(importProductTemp);
            }
            importProductStockTempRepository.saveAll(importProductTemps);
            resultMap.put("code", 200);
            resultMap.put("message", "导入产品成功！");
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code", 500);
            resultMap.put("message", "导入产品失败！");
            return resultMap;
        }
    }

    /**
     * 查询暂存产品详情
     *
     * @param companyCode 本单位编码
     * @param operator    操作员编码
     * @param code        合同编码或者询价单编码
     * @param type     类型
     * @return 返回暂存产品列
     */
    public VImportProductTempResponse getVImportProductStockTempResponse(String companyCode, String operator, String code, String type) {
        var map = findImportProductStockDetail(
            companyCode,
            operator,
             code, type
        );
        var list = (List<VImportProductTempResponse.VProduct>) map.get("products");
        return VImportProductTempResponse.builder()
            .code(200)
            .message("产品导入临时表成功")
            .confirmable(list.stream().filter(vProduct -> vProduct.getMessages().size() > 0 || vProduct.getConfirmedBrand() == null).toList().size() == 0)
            .products(list)
            .enCode((String) map.get("enCode"))
            .build();
    }

    /**
     * 查询导入产品列表
     *
     * @param companyCode 单位id
     * @param operator    操作员编码
     * @param id          仓库编码
     * @return 返回导入产品列表信息
     */
    public Map<String, Object> findImportProductStockDetail(String companyCode, String operator, String id,String type) {
        Map<String, Object> map = new HashMap<>();
        List<ImportProductStockTemp> list = importProductStockTempRepository.
            findImportProductStockTempByImportProductStockTempId_DcCompIdAndImportProductStockTempId_OperatorAndImpAndImportProductStockTempId_WerahouseCodeAndImportProductStockTempId_Type(companyCode, operator, id,type);
        List<TImportProductTemp> importProductTemps = list.stream()
            .map(importProductStockTempMapper::toTImportProductTemp)
            .toList();
        //将数据根据产品编码进行分组

        Map<String,List<ImportProductStockTemp>> maps= list.stream().collect(Collectors.groupingBy(i->i.getCode()+"_"+i.getBrandCode()));


        importProductTemps.forEach(tImportProductTemp -> {
            //错误数据
            List<String> errorList = new ArrayList<>();
            List<TBrand> tBrands = new ArrayList<>();
            List<ImportProductStockTemp> list1 =  maps.get(tImportProductTemp.getCode()+"_"+tImportProductTemp.getConfirmedBrand());
            if(list1!=null&&list1.size()>1){
               var str =  list1.stream().map(i->{
                   if(i.getImportProductStockTempId().getItemNo().intValue()==tImportProductTemp.getItemNo().intValue()){
                       return "";
                   }else {
                       return i.getImportProductStockTempId().getItemNo() +"";
                   }

               } ).collect(Collectors.joining(","));
               errorList.add("产品代码与第"+str+"行产品代码重复");
            }
            if (tImportProductTemp.getProductId() == null && tImportProductTemp.getCode() == null) {
                errorList.add("产品代码不能为空");
            } else if (tImportProductTemp.getProductId() == null && tImportProductTemp.getCode() != null) {
                //验证产品编码是否正确
                List<Product> products = productRepository.findProductByCode(tImportProductTemp.getCode());
                if (products.size() == 0) {
                    errorList.add("产品代码错误或不存在于系统中");
                } else {
                    errorList.add("该产品代码在系统中存在多个，请选择品牌");
                    AtomicInteger i = new AtomicInteger();
                    products.forEach(product -> {
                        i.getAndIncrement();
                        tBrands.add(TBrand.builder()
                            .code(product.getBrandCode())
                            .name(product.getBrand())
                            .sort(i.get())
                            .build());
                    });
                }
            } else {
                tBrands.add(TBrand.builder()
                    .code(tImportProductTemp.getConfirmedBrand())
                    .name(tImportProductTemp.getConfirmedBrandName())
                    .sort(1)
                    .build());
            }
            tImportProductTemp.setBrand(tBrands);
            if (tImportProductTemp.getAmount() == null) {
                errorList.add("数量不能为空");
            } else {
                //验证 数量是否为数字
                if (isNumeric(tImportProductTemp.getAmount())) {
                    errorList.add("数量应为数字");
                }
            }

            tImportProductTemp.setMessages(errorList);
        });
        map.put("products", importProductTemps.stream()
            .map(importProductTempMapper::toVProduct)
            .toList());
        return map;
    }

}
