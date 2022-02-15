package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TOperatorInfo;
import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.mapper.OperatorMapper;
import com.linzhi.gongfu.repository.OperatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 操作员相关信息处理及业务服务
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@RequiredArgsConstructor
@Service
public class OperatorService {
    private final OperatorRepository operatorRepository;
    private final OperatorMapper operatorMapper;

    /**
     * 根据所提供的操作员ID寻找制定的操作员信息。
     *
     * @param id 已经生成的操作员ID
     * @return 可能存在的操作员信息
     */
    @Cacheable(value = "Operator_Login;1800", key = "T(String).valueOf(#id.companyCode).concat(#id.operatorCode)")
    public Optional<TOperatorInfo> findOperatorByID(OperatorId id) {
        return operatorRepository.findById(id).map(operatorMapper::toDTO);
    }
}
