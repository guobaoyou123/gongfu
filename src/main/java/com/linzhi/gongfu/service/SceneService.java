package com.linzhi.gongfu.service;

import com.linzhi.gongfu.entity.EnrolledCompany;
import com.linzhi.gongfu.entity.Scene;
import com.linzhi.gongfu.enumeration.CompanyRole;
import com.linzhi.gongfu.repository.EnrolledCompanyRepository;
import com.linzhi.gongfu.repository.SceneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 场景相关信息处理及业务服务
 *
 * @author zhangguanghhua
 * @create_at 2022-07-13
 */
@RequiredArgsConstructor
@Service
public class SceneService {
    private final SceneRepository sceneRepository;
    private final EnrolledCompanyRepository enrolledCompanyRepository;

    /**
     * 根据单位编码查找所属与本单位的场景列表
     * @param companyCode 单位编码
     * @return 返回场景列表信息
     * @throws IOException 异常
     */
    @Cacheable(value = "scene_List;1800", unless = "#result == null",key = "#companyCode")
    public List<Scene> listScenes(String companyCode) throws IOException {
        EnrolledCompany enrolledCompany = enrolledCompanyRepository.findById(companyCode).orElseThrow(()->new IOException("未从数据库中找到"));
        List<CompanyRole> companyRoles =new ArrayList<>();
        Arrays.stream(enrolledCompany.getDetails().getRole().split(",")).toList().forEach(s -> companyRoles.add(CompanyRole.valueBySign(s).get()));
        return sceneRepository.findSceneByRoleIn(companyRoles).stream().toList();
    }
}
