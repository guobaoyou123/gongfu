package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Company;

import org.springframework.data.repository.CrudRepository;

/**
 * 公司信息Repository
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public interface CompanyRepository extends CrudRepository<Company, String> {
}
