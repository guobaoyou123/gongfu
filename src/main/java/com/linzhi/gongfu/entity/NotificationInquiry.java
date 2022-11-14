package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.InquiryState;
import com.linzhi.gongfu.enumeration.InquiryType;
import com.linzhi.gongfu.enumeration.OfferType;
import com.linzhi.gongfu.enumeration.TaxMode;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message_inquiry_base")
public class NotificationInquiry {
    /**
     * 消息主键
     */
    @Id
    @Column(name = "mess_code",length = 50, nullable = false)
    @NotNull
    @NotBlank
    @NonNull
    private String messageCode;
    /**
     * 询价单编号
     */
    @Column(name = "inquiry_id",length = 50, nullable = false)
    @NotNull
    @NotBlank
    private String inquiryId;

    /**
     * 报价人
     */
    @Column(name = "offer_by", length = 20)
    private String offerBy;
    /**
     * 报价公司
     */
    @Column(name = "offer_comp_by", length = 40, nullable = false)
    private String offerCompBy;
    /**
     * 报价状态（待报价0-待报价 1-已报价 2-已废弃 3-已经生成销售合同）
     */
    @Column
    private OfferType state;
    /**
     * 税模式（0-未税 1-含税）
     */
    @Column(name = "offer_mode", length = 1)
    private TaxMode offerMode;
    /**
     * 报价时间
     */
    @Column(name = "offered_at")
    private LocalDateTime offeredAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "mess_code", referencedColumnName = "mess_code", insertable = true, updatable = true)
    List<NotificationInquiryRecord> records;

    /**
     * 报完价后对应推送给对方消息的消息编码
     */
    @Column(name = "offered_mess_code",length = 50)
    @NotNull
    @NotBlank
    private String offeredMessCode;
}
