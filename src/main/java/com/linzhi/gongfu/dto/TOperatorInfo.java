package com.linzhi.gongfu.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * 用于转移操作员基本信息
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TOperatorInfo implements Serializable {
    private String companyCode;
    private String code;
    private String name;
    private String password;
    private Availability state;
    private String companyName;
    private String companyShortName;
    private String companyDomain;
    private String phone;
    private Whether admin;
    private String LSCode;
    private Set<TScene> scenes;
    private String birthday;
    private String  sex;
    private String areaCode;
    private String areaName;
    private String address;
    private String entryAt;
    private String resignationAt;
}
