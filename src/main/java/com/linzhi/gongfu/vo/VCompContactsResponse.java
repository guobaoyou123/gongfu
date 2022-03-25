package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 *用于响应前端对于联系人列表的请求
 *
 * @author xutao
 * @create_at 2021-12-24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VCompContactsResponse extends VBaseResponse{

     private List<Contacts> contacts;

     @Data
     public static class  Contacts{
         private String code;
         private String companyName;
         private String name ;
         private String phone ;
         private String addressCode ;
         private String state;
     }

}
