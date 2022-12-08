package com.linzhi.gongfu.repository.trade;


import com.linzhi.gongfu.entity.InquiryRecordDetail;
import com.linzhi.gongfu.entity.NotificationInquiryRecordId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


/**
 * 询价记录明细的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface NotificationInquiryRecordRepository extends CrudRepository<InquiryRecordDetail, NotificationInquiryRecordId>, QuerydslPredicateExecutor<InquiryRecordDetail> {


    @Query(value = "select  r.*,case b.offer_mode when '1' then (select top 1 price from sales_contract_record_rev\n" +
        " where product_id = r.product_id order by created_at desc)\n" +
        " else (select top 1 price_vat from sales_contract_record_rev\n" +
        " where product_id = r.product_id order by created_at desc)\n" +
        " end as preSalesPrice ,(select top 1 r1.price from message_inquiry_record r1  left join message_inquiry_base b1 on b1.mess_code = r1.mess_code\n" +
        " where r1.mess_code <>?1 and b1.inquiry_id = b.inquiry_id) as preOfferedPrice \n" +
        " from message_inquiry_record r\n" +
        " left join message_inquiry_base b on b.mess_code = r.mess_code\n" +
        " where r.mess_code=?1",nativeQuery = true)
    List<InquiryRecordDetail> findList(String messCode);
}
