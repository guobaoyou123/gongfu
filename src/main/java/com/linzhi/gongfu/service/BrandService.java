package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.repository.BrandRepository;
import com.linzhi.gongfu.repository.CompAllowedBrandRepository;
import com.linzhi.gongfu.repository.CompBrandAuthRepository;
import com.linzhi.gongfu.repository.CompBrandOwnerRepository;
import com.linzhi.gongfu.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 品牌信息及处理业务服务
 *
 * @author zgh
 * @create_at 2022-02-07
 */
@RequiredArgsConstructor
@Service
public class BrandService {
    private final CompAllowedBrandRepository compAllowedBrandRepository;
    private final BrandRepository brandRepository;
    private final CompBrandAuthRepository compBrandAuthRepository;
    private final CompBrandOwnerRepository compBrandOwnerRepository;
    private final BrandMapper brandMapper;
    /**
     * 根据本单位id,页码，页数，获取供应商信息
     *
     * @param id 本单位id，页码 pageNum,页数 pageSize
     * @return 供应商信息列表
     */
    /*@Cacheable(value = "brandsPage;1800", unless = "#result == null")*/
    public PageUtil<TBrand> brandsPagebyId(String id, Optional<Integer> pageNum, Optional<Integer> pageSize) {

        //查询系统所有品牌
        Iterable<Brand> brandsIterable =brandRepository.findAll();
         //查询经营品牌
        Set<CompAllowedBrand> compAllowedBrandSet= compAllowedBrandRepository.findBrandsByCompAllowedBrandIdCompCode(id);
        //查询授权品牌
        Set<CompBrandAuth> compBrandAuthSet=compBrandAuthRepository.findCompBrandAuthByCompBrandAuthId_BeAuthComp(id);
        //查询拥有品牌
        Set<CompBrandOwner> compBrandOwnerSet=compBrandOwnerRepository.findCompBrandOwnerByCompBrandOwnerId_OwnerCode(id);
        Set<TBrand> tBrandSet =  StreamSupport.stream(brandsIterable.spliterator(), false)
            .map(brandMapper::toBrand)
            .collect(Collectors.toSet());
        tBrandSet.forEach(brands -> {
            brands.setHaveOwned(false);
            brands.setOwned(false);
            brands.setVending(false);
            brands.getCompBrandOwnerSet().forEach(compBrandOwner -> {
                brands.setHaveOwned(true);
                if(compBrandOwner.getCompBrandOwnerId().getOwnerCode().equals(id)){
                    brands.setOwned(true);
                }else{
                    compBrandAuthSet.forEach(compBrandAuth -> {
                        if(compBrandAuth.getCompBrandAuthId().getBrandCode().equals(compBrandOwner.getCompBrandOwnerId().getBrandCode())){
                            brands.setVending(true);
                        }
                    });
                }
            });
        });
        //筛选经营品牌
      Set<TBrand> allowedBrand = StreamSupport.stream(tBrandSet.spliterator(), false)
                                           .filter(brands -> {
                                               AtomicReference<Boolean> flag = new AtomicReference<>(false);
                                               compAllowedBrandSet.forEach(compAllowedBrand -> {
                                                   if(compAllowedBrand.getCompAllowedBrandId().getBrandCode().equals(brands.getCode()))
                                                       flag.set(true);
                                               });
                                               return flag.get();
                                           })
                                          .collect(Collectors.toSet());
      //过滤除经营品牌以外的其他品牌
        Set<TBrand> otherdBrand = StreamSupport.stream(tBrandSet.spliterator(), false)
            .filter(brands -> {
                AtomicReference<Boolean> flag = new AtomicReference<>(false);
                compAllowedBrandSet.forEach(compAllowedBrand -> {
                    if(!compAllowedBrand.getCompAllowedBrandId().getBrandCode().equals(brands.getCode()))
                        flag.set(true);
                });
                return flag.get();
            })
            .collect(Collectors.toSet());
        if(!Optional.of(allowedBrand).isPresent())
            allowedBrand = new HashSet<>();
        allowedBrand.addAll(otherdBrand);
        PageUtil<TBrand> brandsPage = new PageUtil<TBrand>(pageNum.orElse(1),pageSize.orElse(10),StreamSupport.stream(tBrandSet.spliterator(), false).collect(Collectors.toList()));
        return brandsPage;
    }
}
